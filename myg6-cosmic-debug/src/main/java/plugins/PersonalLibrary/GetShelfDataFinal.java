package plugins.PersonalLibrary;

import com.alibaba.fastjson.JSONArray;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.gpt.IGPTAction;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import java.util.HashMap;
import java.util.Map;

public class GetShelfDataFinal implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String , String> result = new HashMap<>();
        if ("GET_USER_DATA".equalsIgnoreCase(action)) {
            System.out.println("fuck task begin");
            // 获取DynamicObject列表
            String fields = "myg6_basedatafield,myg6_textfield,myg6_datefield,myg6_datefield1,myg6_billstatusfield";
            // Create an empty filter array (no filters)
            QFilter[] filters = new QFilter[0];
            // Load the data
            DynamicObject[] dynamicObjectArray = BusinessDataServiceHelper.load("myg6_my_bookshelf", fields, filters);

            JSONArray jsonArrayTaskList = new JSONArray();
            //循环日任务表单
            for (DynamicObject single : dynamicObjectArray) {
                System.out.println("Fuck info" + single);
                JSONArray jsonArray = new JSONArray();
                // 添加数据
                DynamicObject book = single.getDynamicObject("myg6_basedatafield");
                jsonArray.add(book.getString("name")); // 获取书名
                jsonArray.add(single.getString("myg6_textfield")); // 书籍类别
                jsonArray.add(single.getString("myg6_datefield")); // 书籍借出时间
                jsonArray.add(single.getString("myg6_datefield1")); // 书籍归还时间
                jsonArray.add(single.getString("myg6_billstatusfield")); // 书籍状态
                jsonArrayTaskList.add(jsonArray);
            }
            // 将获取到的当日任务数据和近三日任务数据放入map
            result.put("statisticsData", jsonArrayTaskList.toJSONString());
            System.out.println("fuck result" + result);
        }
        return result;
    }
}
