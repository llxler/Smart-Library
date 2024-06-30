package plugins.bkmanagement;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.OrmLocaleValue;
import kd.bos.entity.IFrameMessage;
import kd.bos.ext.form.control.Markdown;
import kd.bos.form.control.IFrame;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 动态表单插件
 */
public class BookAnal extends AbstractFormPlugin implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        // 注册点击事件
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }

    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        if (e.getItemKey().equalsIgnoreCase("myg6_smart_helper")) {
            // 获取日任务信息，并且以JSON字符串的形式展现
            JSONObject jsonResultObject = new JSONObject();
            jsonResultObject.put("bookName", this.getModel().getValue("name").toString());
            jsonResultObject.put("bookType", this.getModel().getValue("myg6_textfield").toString());
            jsonResultObject.put("bookAuthor", this.getModel().getValue("myg6_author").toString());
            jsonResultObject.put("bookAbstract", this.getModel().getValue("myg6_abstract").toString());

            // 调用GPT开发平台微服务
            Map<String, String> variableMap = new HashMap<>();
            variableMap.put("bookResult", jsonResultObject.toJSONString());

            Object[] params = new Object[]{
                    //GPT提示编码
                    getPromptFid("prompt-240530DBBA674F"),
                    "",
                    variableMap
            };

            Map<String, Object> result = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", params);
            JSONObject jsonObjectResult = new JSONObject(result);
            JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");
            // 设置值
            this.getModel().setValue("myg6_txt_helper", jsonObjectData.getString("llmValue"));
            Markdown mk = this.getView().getControl("myg6_md_helper");
            mk.setText(jsonObjectData.getString("llmValue"));
            Object value = this.getModel().getValue("myg6_txt_helper");

            String txt;
            if (value instanceof OrmLocaleValue) {
                OrmLocaleValue ormValue = (OrmLocaleValue) value;
                txt = ormValue.toString(); // 或者使用适当的方法获取字符串表示
            } else if (value instanceof String) {
                txt = (String) value;
            } else {
                throw new IllegalArgumentException("Unexpected value type: " + value.getClass().getName());
            }

            // 处理这个txt
            // Define the pattern to extract the ratings
            Pattern pattern = Pattern.compile("\\*\\*([^\\*]+)\\*\\*：([0-9]+)");
            Matcher matcher = pattern.matcher(txt);

            // Create a JSONObject to store the extracted ratings
            JSONObject jsonObject = new JSONObject();

            // Extract and store ratings
            while (matcher.find()) {
                String category = matcher.group(1).trim();
                int rating = Integer.parseInt(matcher.group(2).trim());
                jsonObject.put(category, rating);
            }

            // Convert the JSONObject to a JSON string
            String jsonString = JSON.toJSONString(jsonObject, true);

            // Print the JSON string
            System.out.println(jsonString);

            IFrame iframe = this.getView().getControl("myg6_frame_echarts");
            IFrameMessage message = new IFrameMessage();
            message.setType("book");
            message.setContent(jsonString);
            message.setOrigin("*");
            iframe.postMessage(message);
        }
    }

    // 获取GPT提示的Fid
    public long getPromptFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_prompt",
                "number," + "id",
                (new QFilter("number", QCP.equals, billNo)).toArray());
        return (dynamicObject).getLong("id");
    }

    @Override
    public void afterBindData(EventObject eventObject) {
        Markdown mk = this.getView().getControl("myg6_md_helper");
        mk.setText(this.getModel().getValue("myg6_txt_helper").toString());
    }
}