package plugins.seatmng;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.gpt.IGPTAction;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

import java.util.HashMap;
import java.util.Map;

public class GetSeatJsonString implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if ("GET_JSON_STRING".equalsIgnoreCase(action)) {
            // 将无效字符进行处理
            String jsonResult = params.get("jsonResult").replaceAll("\\s*|\r|\n|\t", "");
            JSONObject resultJsonObject = null;
            try {
                // 若全部生成JSON字符串，则不会进入catch
                resultJsonObject = JSON.parseObject(jsonResult);
            } catch (Exception ee) {
                // 将"roomid  "的上一个字符作为开始，以}]}字符作为结束，则最后需要+3
                jsonResult = jsonResult.substring(jsonResult.indexOf("\"roomid\"") - 1, jsonResult.indexOf("}") + 1);
                resultJsonObject = JSON.parseObject(jsonResult);
            }
            // new一个DynamicObject表单对象
            DynamicObject dynamicObject = BusinessDataServiceHelper.newDynamicObject("myg6_seat_apply");
            StringBuilder sb1 = new StringBuilder();
            for (int i = 1; i <= 10; ++i) {
                int ascii = 48 + (int) (Math.random() * 9);
                char c = (char) ascii;
                sb1.append(c);
            }

            System.out.println("fuck all info" + resultJsonObject);

            // 提炼出json里面的自习室dys
            String roomid = resultJsonObject.getString("roomid");
            QFilter qFilter = new QFilter("name", QCP.equals, roomid);
            DynamicObject dys_room = BusinessDataServiceHelper.loadSingle("myg6_room", new QFilter[]{qFilter});

            // 提炼出json里面的座位dys
            String seatid = resultJsonObject.getString("seatid");
            QFilter qFilter1 = new QFilter("number", QCP.equals, seatid);
            DynamicObject dys_seat = BusinessDataServiceHelper.loadSingle("myg6_seat", new QFilter[]{qFilter1});

            // 获取时间
            String beginnum = resultJsonObject.getString("beginTime");
            String endnum = resultJsonObject.getString("endTime");

            // Parse the time strings
            LocalTime beginTime = LocalTime.parse(beginnum);
            LocalTime endTime = LocalTime.parse(endnum);

            // Calculate the seconds from the start of the day
            long beginSeconds = ChronoUnit.SECONDS.between(LocalTime.MIDNIGHT, beginTime);
            long endSeconds = ChronoUnit.SECONDS.between(LocalTime.MIDNIGHT, endTime);

            // Assign the seconds back to the variables
            beginnum = String.valueOf(beginSeconds);
            endnum = String.valueOf(endSeconds);

            System.out.println("fuck room" + dys_room);
            System.out.println("fuck seat" + dys_seat);


            // 设置对应属性
            dynamicObject.set("number", sb1.toString());
            dynamicObject.set("creator", RequestContext.get().getCurrUserId());
            dynamicObject.set("myg6_basedatafield", dys_room);
            dynamicObject.set("myg6_basedatafield_seat", dys_seat);
            dynamicObject.set("myg6_timefield_start", beginnum);
            dynamicObject.set("myg6_timefield_end", endnum);

            SaveServiceHelper.saveOperate("myg6_seat_apply", new DynamicObject[]{dynamicObject}, null);
            Long pkId = (Long) dynamicObject.getPkValue();
            // 拼接URL字符串
            String targetForm = "bizAction://currentPage?gaiShow=1&selectedProcessNumber=processNumber&gaiAction=showBillForm&gaiParams={\"appId\":\"myg6_seat_reservation\",\"billFormId\":\"myg6_seat_apply\",\"billPkId\":\"" + pkId + "\"}&title=座位申请生成表单 &iconType=bill&method=bizAction";
            result.put("formUrl", targetForm);
            result.put("resultJsonObject", resultJsonObject.toJSONString());
        }
        return result;
    }
}
