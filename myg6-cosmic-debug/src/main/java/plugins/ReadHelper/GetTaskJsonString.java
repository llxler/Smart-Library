package plugins.ReadHelper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.form.gpt.IGPTAction;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.dataentity.entity.DynamicObject;

import java.util.Map;
import java.util.HashMap;

public class GetTaskJsonString implements IGPTAction {
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
                // 将"dayname"的上一个字符作为开始，以}]}字符作为结束，则最后需要+3
                jsonResult = jsonResult.substring(jsonResult.indexOf("\"TaskName\"") - 1, jsonResult.indexOf("}]}") + 3);
                resultJsonObject = JSON.parseObject(jsonResult);
            }
            // new一个DynamicObject表单对象
            DynamicObject dynamicObject = BusinessDataServiceHelper.newDynamicObject("myg6_planlist");
            StringBuilder sb1 = new StringBuilder();
            for (int i = 1; i <= 10; ++i) {
                int ascii = 48 + (int) (Math.random() * 9);
                char c = (char) ascii;
                sb1.append(c);
            }
            // 设置对应属性
            dynamicObject.set("number", sb1.toString());
            dynamicObject.set("name", resultJsonObject.getString("TaskName"));
            dynamicObject.set("status", "C");
            dynamicObject.set("creator", RequestContext.get().getCurrUserId());
            dynamicObject.set("myg6_created_time", RequestContext.get().getLoginTime());
            // 操作单据体
            DynamicObjectCollection dynamicObjectCollection = dynamicObject.getDynamicObjectCollection("myg6_per_plan_body");
            for (Object object : resultJsonObject.getJSONArray("TaskList")) {
                JSONObject jsonObjectSingle = (JSONObject) object;
                DynamicObject dynamicObjectEntry = dynamicObjectCollection.addNew();
                dynamicObjectEntry.set("myg6_bookname", jsonObjectSingle.getString("bookName"));
                dynamicObjectEntry.set("myg6_plan_time", jsonObjectSingle.getString("plan_time"));
                dynamicObjectEntry.set("myg6_diff", jsonObjectSingle.getString("diff"));
            }
            SaveServiceHelper.saveOperate("myg6_planlist", new DynamicObject[]{dynamicObject}, null);
            Long pkId = (Long) dynamicObject.getPkValue();
            // 拼接URL字符串
            String targetForm = "bizAction://currentPage?gaiShow=1&selectedProcessNumber=processNumber&gaiAction=showBillForm&gaiParams={\"appId\":\"myg6_readplan\",\"billFormId\":\"myg6_planlist\",\"billPkId\":\"" + pkId + "\"}&title=读书计划生成表单 &iconType=bill&method=bizAction";
            result.put("formUrl", targetForm);
            result.put("resultJsonObject", resultJsonObject.toJSONString());
        }
        return result;
    }
}
