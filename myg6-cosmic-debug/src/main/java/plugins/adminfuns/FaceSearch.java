package plugins.adminfuns;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import kd.bos.form.control.Button;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.EventObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 动态表单插件
 */
public class FaceSearch extends AbstractFormPlugin implements Plugin {

    private static String getInfo;

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        //添加按钮监听
        Button button = this.getView().getControl("myg6_buttonap");
        button.addClickListener(this);
    }
    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            if (StringUtils.equals("myg6_buttonap", key)) {
                // 给http://localhost:12350/doc.html#/default/face-rec/searchUserUsingPOST传输数据
                try {
                    // 创建 URL 对象
                    URL url = new URL("http://localhost:12350/face/search");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    // 设置请求方法为 POST
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json; utf-8");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.setDoOutput(true);

                    // JSON 数据
                    String jsonInputString = "{\n" +
                            "  \"groupIdList\": \"kingdee\",\n" +
                            "  \"img\": \"D:\\\\Desktop\\\\lxlerimg1.jpg\",\n" +
                            "  \"imgType\": \"\"\n" +
                            "}";

                    // 发送 JSON 数据
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = jsonInputString.getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // 读取响应
                    int responseCode = connection.getResponseCode();
                    System.out.println("POST Response Code :: " + responseCode);
                    if (responseCode == HttpURLConnection.HTTP_OK) { // 成功
                        try (InputStream is = connection.getInputStream();
                             BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"))) {
                            StringBuilder response = new StringBuilder();
                            String responseLine;
                            while ((responseLine = br.readLine()) != null) {
                                response.append(responseLine.trim());
                            }
                            System.out.println("Response: " + response.toString());
                            getInfo = response.toString();
                            JSONObject jsonObject = JSONObject.parseObject(response.toString());
                            String userId = jsonObject.getJSONObject("data").getString("userId");
                            String name = "";
                            if (StringUtils.equals(userId, "lxler")) {
                                name = "雷翔麟";
                            } else if (StringUtils.equals(userId, "maple")) {
                                name = "金枫淋";
                            } else {
                                name = "余曾祺";
                            }
                            this.getView().showMessage("识别结果\n" + getInfo + "\n" + "欢迎" + name + "来到华科智能图书馆");
                        }
                    } else {
                        System.out.println("POST request not worked");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}