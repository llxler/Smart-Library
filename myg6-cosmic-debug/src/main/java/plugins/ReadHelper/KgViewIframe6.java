package plugins.ReadHelper;
import com.alibaba.fastjson.JSON;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.IFrame;
import kd.bos.form.events.ClientCallBackEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;
import   kd.bos.form.events.CustomEventArgs;
import com.alibaba.fastjson.JSONObject;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.util.regex.*;

/**
 * 动态表单插件
 */
public class KgViewIframe6 extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        IFrame iframe = this.getControl("myg6_kgview");
        iframe.setSrc("http://localhost:12351/");
        this.getView().addClientCallBack("10", 200);
    }

    //接受参数
    @Override
    public void customEvent(CustomEventArgs e) {
        String key = e.getKey();//自定义控件标识
        String args = e.getEventArgs();//数据
        String ename = e.getEventName();//事件名称:这里默认是invokeCustomEvent
        System.out.println("fuck" + args.getClass());

        String bookName;
        DynamicObject book = (DynamicObject) this.getModel().getValue("myg6_bookname");
        bookName = book.getString("name");
        QFilter qFilter = new QFilter("myg6_bookname", QCP.equals, bookName);
        DynamicObject bookData = BusinessDataServiceHelper.loadSingle("myg6_txts", new QFilter[]{qFilter});
        //调用GPT开发平台微服务
        //Integer count = (Integer) this.getModel().getValue("myg6_bookname");

        Map<String , String> variableMap = new HashMap<>();
        variableMap.put("bookName", "红楼梦");
        variableMap.put("roleName", "薛宝钗");
        variableMap.put("count", "5");
        Object[] params = new Object[] {
                //GPT提示编码
                getPromptFid("prompt-24072668AAE0F1"),
                "",
                variableMap
        };
        Map<String, Object> result = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", params);
        JSONObject jsonObjectResult = new JSONObject(result);
        JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");
        //设置值
        //this.getModel().setValue("ozwe_evaluate_all", jsonObjectData.getString("llmValue"));
        this.getView().showSuccessNotification(key+":"+args+":"+ename);
        String jsonResult = jsonObjectData.getString("llmValue").replaceAll("\\s*|\r|\n|\t","");

        Pattern pattern = Pattern.compile("\\[.*?\\]");
        Matcher matcher = pattern.matcher(jsonResult);
        String jsonString = "";
        if (matcher.find()) {
            jsonString = matcher.group();
            System.out.println("Extracted JSON Array: " + jsonString);
        } else {
            System.out.println("No match found.");
        }
        // 使用FastJSON解析JSON字符串
        List<Map<String, String>> dataList = JSON.parseObject(jsonString, new com.alibaba.fastjson.TypeReference<List<Map<String, String>>>(){});

        // 打印解析结果
        String s = bookData.getString("myg6_tupu");
        System.out.println("shit" + s);
        //更新储存的图谱内容
        String jsonStringToStore = JSON.toJSONString(dataList);
        System.out.println("JSON String to store: " + jsonStringToStore);
        if (s.isEmpty()) {
            s += ",";
        }
        s += jsonStringToStore;
        bookData.set("myg6_tupu_tag", s);
        SaveServiceHelper.update(bookData);
    }


    @Override
    public void clientCallBack(ClientCallBackEvent e) {
        super.clientCallBack(e);
        IFrame iframe = this.getControl("myg6_kgview");
//        IFrameMessage message = new IFrameMessage();
//        message.setType("qabot");
//        message.setOrigin("*");
//        message.setContent(data);
//        iframe.postMessage(message);

    }
    public long getPromptFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_prompt",
                "number," +
                        "id",
                (new QFilter("number", QCP.equals, billNo)).toArray());
        return dynamicObject.getLong("id");
    }
}