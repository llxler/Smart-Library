package plugins.adminfuns;

import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
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

/**
 * 动态表单插件
 */
public class NewsPolish1 extends AbstractFormPlugin implements Plugin {
        @Override
        public void registerListener(EventObject e) {
            // 注册点击事件
            super.registerListener(e);
            this.addItemClickListeners("tbmain");
        }

        public void itemClick(ItemClickEvent e) {
            super.itemClick(e);
            if (e.getItemKey().equalsIgnoreCase("myg6_runse")) {
                // 赋值
                JSONObject jsonResultObject = new JSONObject();
                jsonResultObject.put("text1", this.getModel().getValue("myg6_largetextfield1").toString());
                jsonResultObject.put("text2", this.getModel().getValue("myg6_largetextfield2").toString());
                jsonResultObject.put("text3", this.getModel().getValue("myg6_largetextfield3").toString());
                jsonResultObject.put("text4", this.getModel().getValue("myg6_largetextfield4").toString());
                System.out.println(jsonResultObject.toJSONString());

                // 调用GPT开发平台微服务
                Map<String, String> variableMap = new HashMap<>();
                variableMap.put("taskResult", jsonResultObject.toJSONString());

                Object[] params = new Object[]{
                        //GPT提示编码
                        getPromptFid("prompt-24062763DAA2B4"),
                        "开始润色新闻填写内容",
                        variableMap
                };
                Map<String, Object> result = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", params);
                JSONObject jsonObjectResult = new JSONObject(result);
                JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");
                // 设置值
                String s = jsonObjectData.getString("llmValue");
                // 处理s
                String[] paragraphs = s.split("\n");

                String txt1 = paragraphs[0].substring(paragraphs[0].indexOf('.') + 1).trim();
                String txt2 = paragraphs[1].substring(paragraphs[1].indexOf('.') + 1).trim();
                String txt3 = paragraphs[2].substring(paragraphs[2].indexOf('.') + 1).trim();
                String txt4 = paragraphs[3].substring(paragraphs[3].indexOf('.') + 1).trim();

                System.out.println("txt1: " + txt1);
                System.out.println("txt2: " + txt2);
                System.out.println("txt3: " + txt3);
                System.out.println("txt4: " + txt4);

                // 若txt1不是"XXX"
                if (!"XXX".equals(txt1)) {
                    this.getModel().setValue("myg6_largetextfield1", txt1);
                }
                if (!"XXX".equals(txt2)) {
                    this.getModel().setValue("myg6_largetextfield2", txt2);
                }
                if (!"XXX".equals(txt3)) {
                    this.getModel().setValue("myg6_largetextfield3", txt3);
                }
                if (!"XXX".equals(txt4)) {
                    this.getModel().setValue("myg6_largetextfield4", txt4);
                }
                // 更新当前modelview
                this.getView().updateView("myg6_largetextfield1");
                this.getView().updateView("myg6_largetextfield2");
                this.getView().updateView("myg6_largetextfield3");
                this.getView().updateView("myg6_largetextfield4");
            }
        }

    // 获取GPT提示的Fid
    public long getPromptFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_prompt",
                "number," + "id",
                (new QFilter("number", QCP.equals, billNo)).toArray());
        return (dynamicObject).getLong("id");
    }
}