package plugins.adminfuns;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.Button;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import static myg6.cosmic.debug.DebugApplication.MY_IP;

/**
 * 动态表单插件
 */
public class CVReturnBook extends AbstractFormPlugin implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        // 添加按钮监听
        Button button = this.getView().getControl("myg6_returnbook");
        button.addClickListener(this);
    }

    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            if (StringUtils.equals("myg6_returnbook", key)) {
                String txt = (String) this.getModel().getValue("myg6_textareafield");
                // 调用百度识别
                String urlString = "http://" + MY_IP + ":12350/cv/openCamera";

                String jsonInputString = String.format("{\n" +
                        "  \"groupName\": \"kingdee\",\n" +
                        "  \"itemName\": \"redundant\",\n" +
                        "  \"opeCode\": %d\n" +
                        "}", 0); // 0表示识别书籍

                try {
                    // 创建URL对象
                    URL url = new URL(urlString);
                    // 打开连接
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    // 设置请求方法为POST
                    connection.setRequestMethod("POST");
                    // 设置请求头属性
                    connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                    connection.setRequestProperty("Accept", "application/json");
                    // 允许写入请求体
                    connection.setDoOutput(true);

                    // 写入URL编码的数据到请求体
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // 获取响应代码
                    int responseCode = connection.getResponseCode();
                    System.out.println("Response Code: " + responseCode);

                    // 读取响应数据
                    StringBuilder response = new StringBuilder();
                    try (BufferedReader br = new BufferedReader(
                            new InputStreamReader(connection.getInputStream(), "utf-8"))) {
                        String responseLine;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                    }
                    JSONObject jsonResponse = JSONObject.parseObject(response.toString());
                    JSONObject data = jsonResponse.getJSONObject("data");
                    String bookName = data.getString("title");

                    // 更新txt内容
                    String updatedTxt = updateBookStatus(txt, bookName);
                    System.out.println("updatedTxt: " + updatedTxt);

                    this.getView().showMessage("识别成功，当前您还的书籍是 " + bookName);
                    this.getModel().setValue("myg6_textareafield", updatedTxt);

                    solve(bookName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String updateBookStatus(String txt, String bookName) {
        if (txt == null) {
            return "";
        }
        String[] lines = txt.split("\n");
        List<String> updatedLines = new ArrayList<>();
        for (String line : lines) {
            if (line.contains(bookName)) {
                updatedLines.add("--------------该本图书已经归还--------------\n");
            } else {
                updatedLines.add(line);
            }
        }
        return String.join("\n", updatedLines);
    }

    // 处理我的书架还书，
    private void solve(String bookName) {
        // 改变我的书架里面的书籍状态
        String fields = "myg6_basedatafield,myg6_billstatusfield";
        QFilter[] filters = new QFilter[0];
        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_my_bookshelf", fields, filters);
        for (DynamicObject dy : dys) {
            DynamicObject book = (DynamicObject) dy.get("myg6_basedatafield");
            String bookNameField = book.getString("name");
            if (bookNameField.equals(bookName)) {
                dy.set("myg6_billstatusfield", 1);
                SaveServiceHelper.update(dy);
            }
        }
        // 改变借书单据中的书籍状态
        String fields1 = "myg6_nameofbook,myg6_billstatusfield";
        QFilter[] filters1 = new QFilter[0];
        DynamicObject[] dys1 = BusinessDataServiceHelper.load("myg6_book_subscribe", fields1, filters1);
        for (DynamicObject dy : dys1) {
            DynamicObject book = (DynamicObject) dy.get("myg6_nameofbook");
            String bookNameField = book.getString("name");
            if (bookNameField.equals(bookName)) {
                System.out.println("找到你了！");
                dy.set("myg6_billstatusfield", 3);
                SaveServiceHelper.update(dy);
            }
        }
    }
}
