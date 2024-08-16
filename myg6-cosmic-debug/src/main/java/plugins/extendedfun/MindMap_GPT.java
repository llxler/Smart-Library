package plugins.extendedfun;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.ext.form.control.Markdown;
import kd.bos.form.control.Button;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态表单插件
 */
public class MindMap_GPT extends AbstractFormPlugin implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        //添加按钮监听
        Button button = this.getView().getControl("myg6_buttonap_gpt");
        button.addClickListener(this);
    }

    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            if (StringUtils.equals("myg6_buttonap_gpt", key)) {
                // 获取日任务信息，并且以JSON字符串的形式展现
                JSONObject jsonResultObject = new JSONObject();
                DynamicObject book = (DynamicObject) this.getModel().getValue("myg6_bookinfo");
                if (book == null) {
                    this.getView().showMessage("请先选择图书！");
                    return;
                }
                jsonResultObject.put("bookName", book.getString("name"));

                // 调用GPT开发平台微服务
                Map<String, String> variableMap = new HashMap<>();
                variableMap.put("taskResult", jsonResultObject.toJSONString());

                Object[] params = new Object[]{
                        //GPT提示编码
                        getPromptFid("prompt-2406105F429646"),
                        "开始生成图书内容的框架",
                        variableMap
                };
                Map<String, Object> result = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", params);
                JSONObject jsonObjectResult = new JSONObject(result);
                JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");

                // 设置值
                this.getModel().setValue("myg6_txt", jsonObjectData.getString("llmValue"));
                Markdown mk = this.getView().getControl("myg6_md_txt");
                mk.setText(jsonObjectData.getString("llmValue"));
            }
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
        Markdown mk = this.getView().getControl("myg6_md_txt");
        mk.setText(this.getModel().getValue("myg6_txt").toString());
    }
}