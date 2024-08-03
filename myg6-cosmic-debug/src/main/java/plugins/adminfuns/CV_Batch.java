package plugins.adminfuns;

import com.alibaba.fastjson.JSONObject;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.sdk.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EventObject;

import static myg6.cosmic.debug.DebugApplication.MY_IP;

/**
 * 标准单据列表插件
 */
public class CV_Batch extends AbstractListPlugin implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        // 注册点击事件
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }

    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        if (e.getItemKey().equalsIgnoreCase("myg6_baritemap2")) {
            cmd();
        }
    }

    public void cmd() {
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
            this.getView().showMessage("识别成功" + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}