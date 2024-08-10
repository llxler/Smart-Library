package plugins.ReadHelper;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.IFrameMessage;
import kd.bos.form.control.Button;
import kd.bos.form.control.Control;
import kd.bos.form.control.IFrame;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;
import kd.bos.form.events.CustomEventArgs;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.*;
import java.util.regex.*;

import static myg6.cosmic.debug.DebugApplication.MY_IP;

/**
 * 动态表单插件
 */
public class KgViewIframe extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        IFrame iframe = this.getControl("myg6_kgview");
        iframe.setSrc("http://"+ MY_IP +":12351/");

        this.getView().addClientCallBack("10", 200);
    }
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        // 添加按钮监听
        Button button1 = this.getView().getControl("myg6_remake");
        button1.addClickListener(this);

    }
    public void click(EventObject evt) {
        super.click(evt);
        //获取被点击的控件对象
        Control source = (Control) evt.getSource();
        if (StringUtils.equals("myg6_remake", source.getKey())) {
            String empty = "";
            String bookName;
            DynamicObject book = (DynamicObject) this.getModel().getValue("myg6_bookname");
            bookName = book.getString("name");
            QFilter qFilter = new QFilter("myg6_bookname", QCP.equals, bookName);
            DynamicObject bookData = BusinessDataServiceHelper.loadSingle("myg6_txts", new QFilter[]{qFilter});
            bookData.set("myg6_tupu_tag", empty);
            SaveServiceHelper.update(bookData);
            IFrame iframe = this.getControl("myg6_kgview");
            this.getView().setVisible(false, "myg6_kgview");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.getView().setVisible(true, "myg6_kgview");
        }
    }
    //接受参数
    @Override
    public void customEvent(CustomEventArgs e) {
        String key = e.getKey();//自定义控件标识
        String args = e.getEventArgs();//数据
        String ename = e.getEventName();//事件名称:这里默认是invokeCustomEvent
        //提取人物名称
        Map<String, Object> jsonMap = JSON.parseObject(args);
        Map<String, Object> contentMap = (Map<String, Object>) jsonMap.get("content");
        String roleName = (String) contentMap.get("msg");
        String type = (String) contentMap.get("type");
        System.out.println("matuo" + roleName);
        String bookName;
        DynamicObject book = (DynamicObject) this.getModel().getValue("myg6_bookname");

        Boolean ispdftxt = (Boolean) this.getModel().getValue("myg6_isupload");

        DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");
        if (book == null && !ispdftxt) {
            this.getView().showMessage("请选择书籍");
            return;
        } else if (ispdftxt) {
            bookName = cache.get("fileName");
        } else {
            bookName = book.getString("name");
        }

        Integer count = (Integer) this.getModel().getValue("myg6_integerfield");
        System.out.println("goushi" + count);
        System.out.println("goushu" + bookName);
        QFilter qFilter = new QFilter("myg6_bookname", QCP.equals, bookName);
        DynamicObject bookData = BusinessDataServiceHelper.loadSingle("myg6_txts", new QFilter[]{qFilter});

        String s;
        if (ispdftxt) {
            s = "";
        } else {
            // 取出图谱内容，判断数据有没有现成的
            s = bookData.getString("myg6_tupu_tag");
        }

        List<Map<String, String>> storedDataList = JSON.parseObject(s, new TypeReference<List<Map<String, String>>>(){});
        boolean isDuplicate = false;
        if(!s.isEmpty())
            for (Map<String, String> map : storedDataList) {
                if (map.containsKey("source") && roleName.equals(map.get("source"))) {
                    isDuplicate = true;
                    break;
                }
            }
        List<Map<String, String>> matchingDataList = new ArrayList<>();

        if(!isDuplicate) {
            //调用GPT开发平台微服务
            //Integer count = (Integer) this.getModel().getValue("myg6_bookname");

            Map<String, String> variableMap = new HashMap<>();
            variableMap.put("bookName", bookName);
            variableMap.put("roleName", roleName);
            variableMap.put("count", count.toString());
            Object[] params = new Object[]{
                    //GPT提示编码
                    getPromptFid("prompt-24072668AAE0F1"),
                    "开始生成关系图谱",
                    variableMap
            };
            Map<String, Object> result = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", params);
            JSONObject jsonObjectResult = new JSONObject(result);
            JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");
            //设置值
            //this.getModel().setValue("ozwe_evaluate_all", jsonObjectData.getString("llmValue"));
            this.getView().showSuccessNotification(key + ":" + args + ":" + ename);
            String jsonResult = jsonObjectData.getString("llmValue").replaceAll("\\s*|\r|\n|\t", "");

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
            matchingDataList = JSON.parseObject(jsonString, new com.alibaba.fastjson.TypeReference<List<Map<String, String>>>() {
            });
            System.out.println("生成了多少：" + matchingDataList.size());

            if (ispdftxt) {
                String data = JSON.toJSONString(matchingDataList);
                IFrame iframe = this.getControl("myg6_kgview");
                IFrameMessage message = new IFrameMessage();
                message.setType(type);
                message.setOrigin("*");
                message.setContent(data);

                iframe.postMessage(message);
                return;
            }

            //更新储存的图谱内容\
            String updatedJsonString = "";
            if(!s.isEmpty()) {
                storedDataList.addAll(matchingDataList);
                // 将更新后的列表转换回JSON字符串
                updatedJsonString = JSON.toJSONString(storedDataList);
            }
            else {
                updatedJsonString = JSON.toJSONString(matchingDataList);
            }
            System.out.println("JSON String to store: " + updatedJsonString);

            bookData.set("myg6_tupu_tag", updatedJsonString);
            SaveServiceHelper.update(bookData);
        }
        else {
            // 遍历dataList，找到所有source字段相同的部分
            for (Map<String, String> map : storedDataList) {
                if (map.containsKey("source") && roleName.equals(map.get("source"))) {
                    matchingDataList.add(map);
                }
            }
            System.out.println("fuckyou" + "有一样的");
        }

        String data = JSON.toJSONString(matchingDataList);
        System.out.println("jiba" + data);
        IFrame iframe = this.getControl("myg6_kgview");
        IFrameMessage message = new IFrameMessage();
        message.setType(type);
        message.setOrigin("*");
        message.setContent(data);

        iframe.postMessage(message);
    }

    public long getPromptFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_prompt",
                "number," +
                        "id",
                (new QFilter("number", QCP.equals, billNo)).toArray());
        return dynamicObject.getLong("id");
    }
}