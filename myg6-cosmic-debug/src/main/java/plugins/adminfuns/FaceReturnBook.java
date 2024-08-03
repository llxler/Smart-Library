package plugins.adminfuns;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import kd.bos.form.control.Button;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EventObject;

import static myg6.cosmic.debug.DebugApplication.MY_IP;

/**
 * 动态表单插件
 */
public class FaceReturnBook extends AbstractFormPlugin implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        //添加按钮监听
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
                    this.getView().showMessage("识别成功，当前您还的书籍是 " + bookName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}