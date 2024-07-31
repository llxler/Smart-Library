package plugins.ReadHelper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
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
import kd.bos.dataentity.entity.DynamicObject;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态表单插件
 */
public class buttom_Gpt_evaluate extends AbstractFormPlugin implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        // 注册点击事件
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }

    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        if (e.getItemKey().equalsIgnoreCase("myg6_rate_button")) {
            // 获取日任务信息，并且以JSON字符串的形式展现
            JSONObject jsonResultObject = new JSONObject();
            jsonResultObject.put("taskName", this.getModel().getValue("name").toString());
            jsonResultObject.put("createTime", this.getModel().getValue("myg6_created_time").toString());
            DynamicObjectCollection dynamicObjectCollection = this.getModel().getEntryEntity("myg6_per_plan_body");
            JSONArray jsonTaskArray = new JSONArray();
            for (DynamicObject dynamicObjectSingle : dynamicObjectCollection) {
                JSONObject jsonObjectSingle = new JSONObject();
                jsonObjectSingle.put("taskContent", dynamicObjectSingle.getString("myg6_bookname"));
                jsonObjectSingle.put("expectTime", dynamicObjectSingle.getString("myg6_plan_time"));
                jsonObjectSingle.put("diff", dynamicObjectSingle.getString("myg6_diff"));
                jsonObjectSingle.put("finishTime", dynamicObjectSingle.getString("myg6_completed_time"));
                jsonObjectSingle.put("finishSituation", dynamicObjectSingle.getString("myg6_summary"));
                jsonTaskArray.add(jsonObjectSingle);
            }
            jsonResultObject.put("taskIntroduction", jsonTaskArray);

            // 调用GPT开发平台微服务
            Map<String, String> variableMap = new HashMap<>();
            variableMap.put("taskResult", jsonResultObject.toJSONString());

            Object[] params = new Object[]{
                    //GPT提示编码
                    getPromptFid("prompt-2405264A15D752"),
                    "开始评价任务完成情况",
                    variableMap
            };
            Map<String, Object> result = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", params);
            JSONObject jsonObjectResult = new JSONObject(result);
            JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");
            // 设置值
            this.getModel().setValue("myg6_evaluate_all", jsonObjectData.getString("llmValue"));
            Markdown mk = this.getView().getControl("myg6_md_evaluate");
            mk.setText(jsonObjectData.getString("llmValue"));

            // 渲染echart
            // 获取单据体里面的信息到txt
            String txt = "";
            DynamicObjectCollection rows = this.getModel().getEntryEntity("myg6_per_plan_body");
            int idx = 1;
            for (DynamicObject single : rows) {
                String bookName = single.getString("myg6_bookname");
                int days = single.getInt("myg6_plan_time");
                int realDays = single.getInt("myg6_completed_time");
                txt += idx + ". " + "图书名称:" + bookName + " 阅读天数:" + days + " 实际阅读天数:" + realDays + "\n";
                ++idx;
            }

            JSONArray booksArray = new JSONArray();
            // 解析每本书的信息
            String[] lines = txt.split("\n");
            for (String line : lines) {
                String[] parts = line.split("图书名称:|阅读天数:|实际阅读天数:");
                String bookName = parts[1].trim();
                int plannedReadingDays = Integer.parseInt(parts[2].trim());
                int actualReadingDays = Integer.parseInt(parts[3].trim());

                // 创建 JSON 对象
                JSONObject bookObject = new JSONObject();
                bookObject.put("书名", bookName);
                bookObject.put("计划天数", plannedReadingDays);
                bookObject.put("实际天数", actualReadingDays);

                // 添加到 JSON 数组
                booksArray.add(bookObject);
            }

            // 转换为 JSON 字符串
            String jsonString = JSON.toJSONString(booksArray, true);
            System.out.println("info json" + jsonString);

            IFrame iframe = this.getView().getControl("myg6_frame_echarts");
            IFrameMessage message = new IFrameMessage();
            message.setType("plan:over");
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
        Markdown mk = this.getView().getControl("myg6_md_evaluate");
        mk.setText(this.getModel().getValue("myg6_evaluate_all").toString());
    }
}