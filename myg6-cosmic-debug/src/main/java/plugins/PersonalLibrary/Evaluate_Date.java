package plugins.PersonalLibrary;

import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;

import kd.bos.ext.form.control.Markdown;
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

public class Evaluate_Date extends AbstractFormPlugin implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        // 注册点击事件
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }

    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        if (e.getItemKey().equalsIgnoreCase("myg6_add_time")) {
            // 获取日任务信息，并且以JSON字符串的形式展现
            JSONObject jsonResultObject = new JSONObject();
            jsonResultObject.put("bookName", this.getModel().getValue("myg6_nameofbook").toString());
            jsonResultObject.put("booktype", this.getModel().getValue("myg6_booktype").toString());
            jsonResultObject.put("borrowTime", this.getModel().getValue("myg6_date_bg").toString());
            jsonResultObject.put("returnTime", this.getModel().getValue("myg6_date_ed").toString());

            // 调用GPT开发平台微服务
            Map<String, String> variableMap = new HashMap<>();
            variableMap.put("timeResult", jsonResultObject.toJSONString());

            Object[] params = new Object[]{
                    //GPT提示编码
                    getPromptFid("prompt-2406050AC4DA89"),
                    "",
                    variableMap
            };
            Map<String, Object> result = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", params);
            JSONObject jsonObjectResult = new JSONObject(result);
            JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");
            // 设置值
            this.getModel().setValue("myg6_txt_borrow", jsonObjectData.getString("llmValue"));
            Markdown mk = this.getView().getControl("myg6_md_borrow");
            mk.setText(jsonObjectData.getString("llmValue"));
        }

    }

    // 获取GPT提示的Fid
    public long getPromptFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_prompt",
                "number," + "id",
                (new QFilter( "number", QCP.equals, billNo)).toArray());
        return (dynamicObject).getLong("id");
    }

    @Override
    public void afterBindData(EventObject eventObject) {
        Markdown mk = this.getView().getControl("myg6_md_borrow");
        mk.setText(this.getModel().getValue("myg6_txt_borrow").toString());
    }
}
