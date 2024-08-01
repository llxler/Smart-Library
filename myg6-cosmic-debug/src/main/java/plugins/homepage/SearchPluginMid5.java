package plugins.homepage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.bos.form.gpt.IGPTAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchPluginMid5 implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if ("GET_JSON_STRING".equalsIgnoreCase(action)) {
            // 将无效字符进行处理
            String jsonResult = params.get("needResult").replaceAll("\\s*|\r|\n|\t", "");
            System.out.println("jsonResult: " + jsonResult);
            JSONObject resultJsonObject = JSON.parseObject(jsonResult);
            System.out.println("resultJsonObject: " + resultJsonObject);

            // 创立一个String 变长数组 bookNames
            List<String> bookNames = new ArrayList<>();
            // 遍历resultJsonObject的TaskList数组
            String cnt_str = resultJsonObject.get("cnt").toString();
            int cnt = Integer.parseInt(cnt_str);
            for (int i = 1; i <= cnt; ++i) {
                bookNames.add(String.valueOf(resultJsonObject.getString("book" + i)));
            }
            for (String bookName : bookNames) {
                System.out.println("fuck bookName: " + bookName);
            }
//            // new一个DynamicObject表单对象
//            DynamicObject dynamicObject = BusinessDataServiceHelper.newDynamicObject("myg6_planlist");
//            StringBuilder sb1 = new StringBuilder();
//            for (int i = 1; i <= 10; ++i) {
//                int ascii = 48 + (int) (Math.random() * 9);
//                char c = (char) ascii;
//                sb1.append(c);
//            }
//            // 设置对应属性
//            dynamicObject.set("number", sb1.toString());
//            dynamicObject.set("name", resultJsonObject.getString("TaskName"));
//            dynamicObject.set("status", "C");
//            dynamicObject.set("creator", RequestContext.get().getCurrUserId());
//            Date todayDate = new Date(); // 设置时间
//            dynamicObject.set("myg6_created_time", todayDate);
//
//            SaveServiceHelper.saveOperate("myg6_planlist", new DynamicObject[]{dynamicObject}, null);
//            Long pkId = (Long) dynamicObject.getPkValue();
//            // 拼接URL字符串
//            String targetForm = "bizAction://currentPage?gaiShow=1&selectedProcessNumber=processNumber&gaiAction=showBillForm&gaiParams={\"appId\":\"myg6_readplan\",\"billFormId\":\"myg6_planlist\",\"billPkId\":\"" + pkId + "\"}&title=读书计划生成表单 &iconType=bill&method=bizAction";
//            result.put("formUrl", targetForm);
////            result.put("resultJsonObject", resultJsonObject.toJSONString());
        }
        return result;
    }
}
