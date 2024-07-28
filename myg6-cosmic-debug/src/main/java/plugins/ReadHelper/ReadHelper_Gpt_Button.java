package plugins.ReadHelper;

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
public class ReadHelper_Gpt_Button extends AbstractFormPlugin implements Plugin {

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        // 添加按钮监听
        Button button = this.getView().getControl("myg6_startread");
        button.addClickListener(this);
    }

    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        // 开始书写按钮的业务逻辑
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            // 若是渲染界面按钮
            if (StringUtils.equals("myg6_startread", key)) {
                // ----------- begin -----------

                JSONObject jsonResultObject = new JSONObject();
                DynamicObject book = (DynamicObject) this.getModel().getValue("myg6_bookname");
                if (book == null) {
                    this.getView().showMessage("请选择书籍");
                    return;
                }
                String bookName = book.getString("name");

                jsonResultObject.put("bookName", bookName);
                Map<String, String> variableMap = new HashMap<>();
                variableMap.put("bookInfo", jsonResultObject.toJSONString());

                Object[] params = new Object[]{
                        //GPT提示编码
                        getPromptFid("prompt-2407138C01A31C"),
                        "",
                        variableMap
                };
                Map<String, Object> result = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", params);
                JSONObject jsonObjectResult = new JSONObject(result);
                JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");
                // 设置值
                this.getModel().setValue("myg6_txt", jsonObjectData.getString("llmValue"));
                Markdown mk = this.getView().getControl("myg6_md");
                mk.setText(jsonObjectData.getString("llmValue"));
                // ----------- over -----------
            }
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
        Markdown mk = this.getView().getControl("myg6_md");
        mk.setText(this.getModel().getValue("myg6_txt").toString());
    }
}