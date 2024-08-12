package plugins.bkmanagement;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.OrmLocaleValue;
import kd.bos.entity.IFrameMessage;
import kd.bos.ext.form.control.Markdown;
import kd.bos.form.ShowType;
import kd.bos.form.control.IFrame;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.events.CustomEventArgs;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.bos.threads.ThreadPool;
import kd.bos.threads.ThreadPools;
import kd.sdk.plugin.Plugin;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static myg6.cosmic.debug.DebugApplication.MY_IP;

/**
 * 动态表单插件
 */
public class BookAnal extends AbstractFormPlugin implements Plugin {
    private static ThreadPool pool = ThreadPools.newCachedThreadPool("CustomThreadPool", 3, 5);
    @Override
    public void registerListener(EventObject e) {
        // 注册点击事件
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }

    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        if (e.getItemKey().equalsIgnoreCase("myg6_smart_helper")) {

            List<Future<?>> futures = new ArrayList<>(); // 用于存储 Future 对象
            //将图谱生成加入多线程
            Future<?> future = pool.submit(() -> {
                String fields = "name,myg6_picturefield";
                QFilter[] filters = new QFilter[0];
                DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_list", fields, filters);
                String bookName = this.getModel().getValue("name").toString();

                //调用GPT开发平台微服务
                //Integer count = (Integer) this.getModel().getValue("myg6_bookname");

                Map<String, String> variableMap = new HashMap<>();
                Object[] params = new Object[]{
                        //GPT提示编码
                        getPromptFid("prompt-240812A3DEA324"),
                        bookName,
                        variableMap
                };
                Map<String, Object> result = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", params);

                try {
                    JSONObject jsonObjectResult = new JSONObject(result);
                    JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");
                    //设置值
                    //this.getModel().setValue("ozwe_evaluate_all", jsonObjectData.getString("llmValue"));

                    String jsonResult = jsonObjectData.getString("llmValue").replaceAll("\\s*|\r|\n|\t", "");
                    this.getView().showSuccessNotification(jsonResult);

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
                    //定义matchingDataList
                    List<Map<String, String>> matchingDataList = new ArrayList<>();
                    matchingDataList = JSON.parseObject(jsonString, new com.alibaba.fastjson.TypeReference<List<Map<String, String>>>() {
                    });

                    //塞入url
                    for (Map<String, String> map : matchingDataList) {
                        String sourceName = map.get("source");
                        String targetName = map.get("target");

                        // 为 source 和 target 添加图片URL
                        for(DynamicObject single : dys) {
                            if (single.getString("name").replaceAll("\\s+", "").equals(sourceName.replaceAll("\\s+", ""))) {

                                String url = single.getString("myg6_picturefield");

                                // 设置图片
                                map.put("surl", "http://"+ MY_IP +":8881/ierp/attachment/downloadImage/" + url);
                            }
                            if (single.getString("name").replaceAll("\\s+", "").equals(targetName.replaceAll("\\s+", ""))) {

                                String url = single.getString("myg6_picturefield");
                                // 设置图片
                                map.put("turl", "http://"+ MY_IP +":8881/ierp/attachment/downloadImage/" + url);
                            }
                        }
                    }
                    System.out.println("生成了多少：" + matchingDataList.size());
                    //更新储存的图谱内容\

                    String data = JSON.toJSONString(matchingDataList);
                    System.out.println("jiba" + data);
                    IFrame iframe = this.getControl("myg6_frame_kg");
                    IFrameMessage message = new IFrameMessage();
                    message.setType("KGViewbook");
                    message.setOrigin("*");
                    message.setContent(data);

                    iframe.postMessage(message);

                } catch (Exception emaple) {
                    emaple.printStackTrace();
                }
                return null; // 必须返回一个值，因为 Callable 需要一个返回值
            });
            futures.add(future); // 将 Future 对象添加到列表中


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
                    "开始评价该图书",
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

            IFrame iframe = this.getView().getControl("myg6_frame_echarts");
            IFrameMessage message = new IFrameMessage();

            String bookName;
            Object value1 = this.getModel().getValue("name");
            if (value1 instanceof OrmLocaleValue) {
                OrmLocaleValue ormValue = (OrmLocaleValue) value1;
                bookName = ormValue.toString(); // 或者使用适当的方法获取字符串表示
            } else if (value1 instanceof String) {
                bookName = (String) value1;
            } else {
                throw new IllegalArgumentException("Unexpected value type: " + value1.getClass().getName());
            }
            message.setType("book:" + bookName);
            message.setContent(jsonString);
            message.setOrigin("*");
            System.out.println("fuck " + message);
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

    @Override
    public void customEvent(CustomEventArgs e) {
        String args = e.getEventArgs();//数据
        if (args.startsWith("{\"pageId\"")) return;
        //提取人物名称
        Map<String, Object> jsonMap = JSON.parseObject(args);
        Map<String, Object> contentMap = (Map<String, Object>) jsonMap.get("content");
        if (StringUtils.equals((String) contentMap.get("type"), "click")) {
            //从图书库中加载图书
            String fields = "name,myg6_picturefield,myg6_textfield";
            QFilter[] filters = new QFilter[0];
            DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_list", fields, filters);
            String bookName = (String) contentMap.get("msg");
            DynamicObject single = null;
            for (DynamicObject result : dys) {
                if (result.getString("name").replaceAll("\\s+", "").equals(bookName.replaceAll("\\s+", ""))) {
                    single = result;
                    BillShowParameter billShowParameter = new BillShowParameter();
                    billShowParameter.setFormId("myg6_book_list");
                    billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
                    billShowParameter.setStatus(OperationStatus.VIEW);
                    Long pkId = (Long) single.getPkValue();
                    billShowParameter.setPkId(pkId);
                    this.getView().showForm(billShowParameter);
                    break;
                }
            }
        }
    }
}