package plugins.speechrecognition;

import com.alibaba.fastjson.JSONObject;
import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Button;
import kd.bos.form.control.Control;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.list.ListShowParameter;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.sdk.plugin.Plugin;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 动态表单插件
 */
public class HomepageSpeechJump2 extends AbstractFormPlugin implements Plugin {
    private static final String CMD = "d://Code//Anaconda//python.exe";
    private static final String SCRIPT_PATH = "D:\\UseForRuanjianbei\\Speech-Recognition\\main.py";

    String output;

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        // 添加按钮监听
        Button button = this.getView().getControl("myg6_yuyin");
        button.addClickListener(this);
    }
    public void click(EventObject evt) {
        super.click(evt);
        //获取被点击的控件对象
        Control source = (Control) evt.getSource();
        if (StringUtils.equals("myg6_yuyin", source.getKey())) {
            //获取DynamicObject列表
            String fields = "name,myg6_picturefield";
            // Create an empty filter array (no filters)
            QFilter[] filters = new QFilter[0];
            // Load the data
            DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_list", fields, filters);
            for (int i = 0; i < dys.length; i++) {
                DynamicObject single = dys[i];
                String bookName = single.getString("name");
                System.out.println("book" + (i + 1) + ":" + bookName);
            }
            // 调用python脚本
            try {
                // 使用 ProcessBuilder
                ProcessBuilder processBuilder = new ProcessBuilder(CMD, SCRIPT_PATH);
                processBuilder.directory(new File("D:\\UseForRuanjianbei\\Speech-Recognition"));

                // 启动进程
                Process process = processBuilder.start();

                // 读取标准输出和错误输出
                BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
                BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8));

                // 读取标准输出
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = stdInput.readLine()) != null) {
                    result.append(line).append("\n");
                }

                // 读取错误输出
                StringBuilder error = new StringBuilder();
                while ((line = stdError.readLine()) != null) {
                    error.append(line).append("\n");
                }
                output = result.toString();

                // Split the output into lines
                String[] lines = output.split("\n");
                output = lines[0];
                String type_str = lines[1];

                // 处理这个output
                this.getView().showMessage(output);

                // 定义正则表达式模式
                Pattern pattern = Pattern.compile("识别码\\s*(\\d+)");
                Matcher matcher = pattern.matcher(type_str);

                int type = -1;
                // 查找匹配
                if (matcher.find()) {
                    // 提取识别码并转换为 int 类型
                    String typeString = matcher.group(1);
                    type = Integer.parseInt(typeString);
                    // 输出结果
                    System.out.println("识别码: " + type);
                } else {
                    System.out.println("识别码未找到");
                }
                render(type);
            } catch (Exception exce) {
                // 打印详细异常信息
                exce.printStackTrace();
                this.getView().showMessage("执行命令时发生异常：" + exce.getMessage());
            }
        }
    }

    public void render(int type) {
        // 用于gpt
        String pageId = this.getView().getMainView().getPageId();
        // 根据图书类型进行操作
        if (type == 0) { // 介绍图书馆概况
            Object pkValue = getProcessFid("process-2405212738F418");
            DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService", "selectProcessInSideBar", pkValue, pageId, "您好，欢迎来到我们的华科智慧图书馆");
            DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService", "startProcessInSideBar", pkValue, pageId, new HashMap(), "介绍一下华科图书馆的历史情况，以及图书库里面的相关图书");
        } else if (type == 1) { // 打开图书库
            ListShowParameter billShowParameter = new ListShowParameter();
            billShowParameter.setFormId("bos_list");
            billShowParameter.setBillFormId("myg6_book_list");
            billShowParameter.getOpenStyle().setShowType(ShowType.NewTabPage);
            billShowParameter.setAppId("myg6_mylibrary");
            billShowParameter.getOpenStyle().setTargetKey("tabap");
            this.getView().showForm(billShowParameter);
        } else if (type == 2) { // 打开座位预约
            ListShowParameter billShowParameter = new ListShowParameter();
            billShowParameter.setFormId("bos_list");
            billShowParameter.setBillFormId("myg6_room");
            billShowParameter.getOpenStyle().setShowType(ShowType.NewTabPage);
            billShowParameter.setAppId("myg6_mylibrary");
            billShowParameter.getOpenStyle().setTargetKey("tabap");
            this.getView().showForm(billShowParameter);
        } else if (type == 3) { // 我要看读书笔记
            FormShowParameter formShowParameter = new FormShowParameter();
            formShowParameter.setFormId("myg6_read_notes");
            formShowParameter.getOpenStyle().setShowType(ShowType.NewTabPage);
            formShowParameter.setAppId("myg6_mylibrary");
            formShowParameter.getOpenStyle().setTargetKey("tabap");
            this.getView().showForm(formShowParameter);
        } else if (type == 4) { // 打开我的书架
            ListShowParameter billShowParameter = new ListShowParameter();
            billShowParameter.setFormId("bos_list");
            billShowParameter.setBillFormId("myg6_my_bookshelf");
            billShowParameter.getOpenStyle().setShowType(ShowType.NewTabPage);
            billShowParameter.setAppId("myg6_mylibrary");
            billShowParameter.getOpenStyle().setTargetKey("tabap");
            this.getView().showForm(billShowParameter);
        } else if (type == 5) { // 查看信誉分数
            BillShowParameter billShowParameter = new BillShowParameter();
            billShowParameter.setFormId("myg6_credibility_table");
            billShowParameter.getOpenStyle().setShowType(ShowType.NewTabPage);
            billShowParameter.setAppId("myg6_mylibrary");
            billShowParameter.getOpenStyle().setTargetKey("tabap");
            billShowParameter.setStatus(OperationStatus.VIEW);
            RequestContext rc = RequestContext.get();
            String nowUser = rc.getUserName();
            QFilter qFilter = new QFilter("myg6_textfield", QCP.equals, nowUser);
            DynamicObject credititself = BusinessDataServiceHelper.loadSingle("myg6_credibility_table", new QFilter[]{qFilter});
            Long pkId = (Long) credititself.getPkValue();
            billShowParameter.setPkId(pkId);
            this.getView().showForm(billShowParameter);
        } else if (type == 6) { // 打开图书论坛
            FormShowParameter formShowParameter = new FormShowParameter();
            formShowParameter.setFormId("myg6_forum");
            formShowParameter.getOpenStyle().setShowType(ShowType.NewTabPage);
            formShowParameter.setAppId("myg6_mylibrary");
            formShowParameter.getOpenStyle().setTargetKey("tabap");
            this.getView().showForm(formShowParameter);
        } else if (type == 7) { // 打开智慧功能
            FormShowParameter formShowParameter = new FormShowParameter();
            formShowParameter.setFormId("myg6_funs");
            formShowParameter.getOpenStyle().setShowType(ShowType.NewTabPage);
            formShowParameter.setAppId("myg6_mylibrary");
            formShowParameter.getOpenStyle().setTargetKey("tabap");
            this.getView().showForm(formShowParameter);
        } else if (type == 8) { // 用gpt来推荐图书
            Object pkValue = getProcessFid("process-2405212738F418");
            DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService", "selectProcessInSideBar", pkValue, pageId, "您好，我将根据评价来为您推荐图书");
            DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService", "startProcessInSideBar", pkValue, pageId, new HashMap(), "根据评价信息，来为我们推荐图书馆中的图书");
        } else if (type == 9) { // 呼出gpt来预定座位
            Object pkValue = getProcessFid("process-240603565909B7");
            DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService", "selectProcessInSideBar", pkValue, pageId, "您好，我将根据您的需求来预定座位");
            DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService", "startProcessInSideBar", pkValue, pageId, new HashMap(), output);
        } else if (type == 10) {
            // TODO 继续完善逻辑
        } else {
            this.getView().showMessage("NLP短文本匹配失败，现交由AI处理");
            // 调用GPT开发平台微服务
            Map<String, String> variableMap = new HashMap<>();
            Object[] params = new Object[]{
                    //GPT提示编码
                    getPromptFid("prompt-2408073AD851D4"),
                    output,
                    variableMap
            };
            Map<String, Object> result = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", params);
            JSONObject jsonObjectResult = new JSONObject(result);
            JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");

            String txt = jsonObjectData.getString("llmValue");
            System.out.println("识别结果码：" + txt);
            aisolve(Integer.parseInt(txt));
        }
    }

    public void aisolve(int type) {
        String pageId = this.getView().getMainView().getPageId();
        if (type == 10 || type == 8) { // 非模糊借书
            Object pkValue = getProcessFid("process-24080844EC8655");
            DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService", "selectProcessInSideBar", pkValue, pageId, "您好，我将根据您的需求来借书");
            DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService", "startProcessInSideBar", pkValue, pageId, new HashMap(), output);
        } else if (type == 11) { // 模糊匹配一群书
            Object pkValue = getProcessFid("process-24080158231F7B");
            JSONObject needJson = new JSONObject();
            // 从数据库中获取书籍列表
            String bookList = "";
            String fields = "name";
            QFilter[] filters = new QFilter[0];
            DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_list", fields, filters);
            for (DynamicObject dy : dys) {
                bookList += dy.getString("name") + ",";
            }
            needJson.put("bookList", bookList);
            needJson.put("userNeed", output);
            DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");
            cache.put("openFlag", "false");
            DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService","selectProcessInSideBar",pkValue, pageId, "----------------------正在搜索----------------------\n");
            DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService","startProcessInSideBar", pkValue, pageId, new HashMap(), needJson.toJSONString());
            for (int i = 1; i <= 10; ++i) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                String openFlag = cache.get("openFlag");
                if (openFlag != null && openFlag.equals("true")) {
                    FormShowParameter formShowParameter = new FormShowParameter();
                    formShowParameter.setFormId("myg6_minishelf");
                    formShowParameter.getOpenStyle().setShowType(ShowType.Modal);
                    this.getView().showForm(formShowParameter);
                    break;
                }
            }
        }
    }

    public Object getProcessFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_process",
                "number," +
                        "id",
                (new QFilter("number", QCP.equals, billNo)).toArray());
        long idd = dynamicObject.getLong("id");
        return Long.parseLong(String.valueOf(idd));
    }

    public long getPromptFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_prompt",
                "number," + "id",
                (new QFilter("number", QCP.equals, billNo)).toArray());
        return (dynamicObject).getLong("id");
    }
}