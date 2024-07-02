package plugins.extendedfun;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Button;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.sdk.plugin.Plugin;

import java.math.BigDecimal;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态表单插件
 */
public class QAbot3 extends AbstractFormPlugin implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        //添加按钮监听
        Button button = this.getView().getControl("btnok");
        button.addClickListener(this);
    }
    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            if (StringUtils.equals("btnok", key)) {
                // 获取图书名称
                JSONObject jsonResultObject = new JSONObject();
                DynamicObject book = (DynamicObject) this.getModel().getValue("myg6_basedatafield");
                String bookName = book.getString("name");
                System.out.println("bookName" + bookName);
                jsonResultObject.put("bookName", bookName);
                // 获取图书数量
                BigDecimal questionCount = (BigDecimal) this.getModel().getValue("myg6_stepperfield1");
                System.out.println("图书数量" + questionCount);
                jsonResultObject.put("questionCount", questionCount);

                // 调用GPT开发平台微服务
                Map<String, String> variableMap = new HashMap<>();
                variableMap.put("questionResult", jsonResultObject.toJSONString());

                Object[] params = new Object[]{
                        //GPT提示编码
                        getPromptFid("prompt-24062984974936"),
                        "",
                        variableMap
                };
                Map<String, Object> result = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", params);
                JSONObject jsonObjectResult = new JSONObject(result);
                JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");
                // 设置值
                String getInfo = jsonObjectData.getString("llmValue");

                System.out.println("fuck info" + getInfo);

                FormShowParameter billShowParameter = new FormShowParameter();
                billShowParameter.setFormId("myg6_questions_render");
                billShowParameter.setCaption("请开始答题~");
                billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
                billShowParameter.setCustomParam("questionData",getInfo);
                //弹出表单和本页面绑定
                this.getView().showForm(billShowParameter);
            }
        }
    }
    public long getPromptFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_prompt",
                "number," + "id",
                (new QFilter( "number", QCP.equals, billNo)).toArray());
        return (dynamicObject).getLong("id");
    }
}