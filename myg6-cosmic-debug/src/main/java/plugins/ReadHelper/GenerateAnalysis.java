package plugins.ReadHelper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.form.gpt.IGPTAction;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GenerateAnalysis implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if ("GET_USER_DATA".equalsIgnoreCase(action)) {
            // 获取今日时间
            Date todayDate = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String todayDateFormat = simpleDateFormat.format(todayDate);

            DynamicObject[] dynamicObjectArray = BusinessDataServiceHelper.load("myg6_planlist",
                    "name," +
                            "myg6_created_time", new QFilter[]{new QFilter("name", QCP.is_notnull, null)});
            // 新建日历对象，One代表前一天，Two代表前两天
//            Calendar calendarOne = Calendar.getInstance();
//            Calendar calendarTwo = Calendar.getInstance();
//            calendarOne.setTime(new Date());
//            calendarTwo.setTime(new Date());
//            calendarOne.add(Calendar.DAY_OF_MONTH, -1);
//            calendarTwo.add(Calendar.DAY_OF_MONTH, -2);
            JSONObject dayDataObject = new JSONObject();
            JSONArray jsonArrayTaskList = new JSONArray();
            // 循环日任务表单
            for (DynamicObject single : dynamicObjectArray) {
                String objectDate = simpleDateFormat.format(single.getDate("myg6_created_time"));
                // 如果日期等于这一天
                if (objectDate.equalsIgnoreCase(todayDateFormat)) {
                    DynamicObjectCollection dynamicObjectCollection = single.getDynamicObjectCollection("myg6_per_plan_body");
                    int sumExpect = 0;
                    int sumFinish = 0;
                    // 统计任务时间总数以及日任务数据
                    for (DynamicObject entryObject : dynamicObjectCollection) {
                        JSONArray jsonArray = new JSONArray();
                        String bookName = entryObject.getString("myg6_bookname");
                        jsonArray.add(bookName);
                        jsonArray.add(entryObject.getString("myg6_plan_time")); // 阅读天数
                        jsonArray.add(entryObject.getString("myg6_completed_time")); // 实际天数
                        jsonArrayTaskList.add(jsonArray);
                        sumExpect += entryObject.getInt("myg6_plan_time");
                        sumFinish += entryObject.getInt("myg6_completed_time");
                    }
                    // 加入到JSONObject对象
                    getDayObject(1, dayDataObject, objectDate, sumExpect, sumFinish);
                    // 若表单日期等于前一天
                }
//                else if (objectDate.equalsIgnoreCase(simpleDateFormat.format(calendarOne.getTime()))) {
//                    DynamicObjectCollection dynamicObjectCollection = single.getDynamicObjectCollection("ozwe_entryentity_daytask");
//                    // 获取时间总数
//                    int sumExpect = 0;
//                    int sumFinish = 0;
//                    for (DynamicObject entryObject : dynamicObjectCollection) {
//                        sumExpect += entryObject.getInt("ozwe_expect_minutes");
//                        sumFinish += entryObject.getInt("ozwe_finish_minute");
//                    }
//                    // 加入到JSONObject对象
//                    getDayObject(2, dayDataObject, objectDate, sumExpect, sumFinish);
//                    // 若表单日期等于前两天
//                } else if (objectDate.equalsIgnoreCase(simpleDateFormat.format(calendarTwo.getTime()))) {
//                    DynamicObjectCollection dynamicObjectCollection = single.getDynamicObjectCollection("ozwe_entryentity_daytask");
//                    int sumExpect = 0;
//                    int sumFinish = 0;
//                    for (DynamicObject entryObject : dynamicObjectCollection) {
//                        sumExpect += entryObject.getInt("ozwe_expect_minutes");
//                        sumFinish += entryObject.getInt("ozwe_finish_minute");
//                    }
//                    getDayObject(3, dayDataObject, objectDate, sumExpect, sumFinish);
//                }
            }
            //将获取到的当日任务数据和近三日任务数据放入map
            result.put("statisticsData", jsonArrayTaskList.toJSONString());
            result.put("dayDataObject", dayDataObject.toJSONString());
            System.out.println(result);
        }
        return result;
    }

    private void getDayObject(int day, JSONObject dayDataObject, String objectDate, int sumExpect, int sumFinish) {
        JSONObject dayObject = new JSONObject();
        dayObject.put("day" + day, objectDate);
        dayObject.put("day" + day + "Expect", sumExpect);
        dayObject.put("day" + day + "Finish", sumFinish);
        dayObject.put("day" + day + "IsOvertime", ((sumExpect > sumFinish) ? "否" : "是"));
        dayDataObject.put("day" + day + "Data", dayObject);
    }
}
