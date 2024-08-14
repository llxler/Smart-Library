package plugins.adminfuns;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.Button;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.EventObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static myg6.cosmic.debug.DebugApplication.MY_IP;

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
        Button button1 = this.getView().getControl("myg6_buttonap1");
        button1.addClickListener(this);
    }
    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            if (StringUtils.equals("myg6_buttonap", key)) {
                cmd(2);
            } else if (StringUtils.equals("myg6_buttonap1", key)) {
                cmd(1);
            }
        }
    }

    public void cmd(int type) {
        String urlString = "http://" + MY_IP + ":12350/cv/openCamera";

        String jsonInputString = "";
        if (type == 2) {
            jsonInputString = String.format("{\n" +
                    "  \"groupName\": \"kingdee\",\n" +
                    "  \"itemName\": \"redundant\",\n" +
                    "  \"opeCode\": %d\n" +
                    "}", type); // 2是识别人脸信息
        } else {
            String name = (String) this.getModel().getValue("myg6_name");
            jsonInputString = String.format("{\n" +
                    "  \"groupName\": \"kingdee\",\n" +
                    "  \"itemName\": \"%s\",\n" +
                    "  \"opeCode\": %d\n" +
                    "}", name, type); // 1是注册人脸信息
        }
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
            if (type == 2) {
                getInfo = response.toString();
                // 解析JSON响应并提取userId
                JSONObject jsonResponse = JSONObject.parseObject(response.toString());
                JSONObject data = jsonResponse.getJSONObject("data");
                String userId = data.getString("userId");
                // 打印该人的信息
                 render(userId);
            } else if (type == 1) { // 人脸注册
                this.getView().showMessage("恭喜注册成功！欢迎新同学来到华科智能图书馆!!!\n");
                this.getModel().setValue("myg6_textareafield", "欢迎新同学来到华科智能图书馆!!!\n您可以去借阅感兴趣的书籍。\n记得去学生中心上传照片！！！");
                // 得出学生照片
                QFilter qFilterr = new QFilter("myg6_student_name", QCP.equals, "陌生人");
                DynamicObject faceInfo = BusinessDataServiceHelper.loadSingle("myg6_student_info", new QFilter[]{qFilterr});
                String imgurl = faceInfo.getString("myg6_student_photo");
                this.getModel().setValue("myg6_picturefield", imgurl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void render(String userId) {
        String name = "";
        if (StringUtils.equals(userId, "lxler")) {
            name = "雷翔麟";
        } else if (StringUtils.equals(userId, "maple")) {
            name = "金枫淋";
        } else if (StringUtils.equals(userId, "yuzengqi")) {
            name = "余曾祺";
        } else {
            this.getView().showMessage("欢迎" + userId + "来到华科智能图书馆!!!\n");
            this.getModel().setValue("myg6_textareafield", "欢迎新同学来到华科智能图书馆!!!\n您可以去借阅感兴趣的书籍。");
            // 得出学生照片
            QFilter qFilterr = new QFilter("myg6_student_name", QCP.equals, "陌生人");
            DynamicObject faceInfo = BusinessDataServiceHelper.loadSingle("myg6_student_info", new QFilter[]{qFilterr});
            String imgurl = faceInfo.getString("myg6_student_photo");
            this.getModel().setValue("myg6_picturefield", imgurl);
            return;
        }

        // 查询该人借阅的书籍
        String fields = "creator,myg6_nameofbook,myg6_billstatusfield";
        // Create an empty filter array   (no filters)
        QFilter[] filters = new QFilter[0];
        // Load the data
        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_subscribe", fields, filters);
        String infoBook = "借阅的书籍有:\n";
        for (DynamicObject o : dys) {
            DynamicObject creatorObj = (DynamicObject) o.get("creator");
            if (creatorObj == null) continue;
            String creator = creatorObj.getString("name");
            if (creator == null) continue;
            if (StringUtils.equals(creator, name)) {
                DynamicObject book = (DynamicObject) o.get("myg6_nameofbook");
                if (book == null) continue;
                String nameofbook = book.getString("name");
                if (nameofbook == null) continue;
                String status = o.getString("myg6_billstatusfield");
                if (status == null) continue;
                if (StringUtils.equals(status, "0")) {
                    status = "未处理";
                } else if (StringUtils.equals(status, "1")) {
                    status = "阅读中";
                } else if (StringUtils.equals(status, "2")) {
                    status = "已逾期";
                } else{
                    // status = "已归还";
                    continue;
                }
                infoBook += nameofbook + "  图书状态:" + status + "\n";
            }
        }

        // 查询该人订阅过的座位
        String fields2 = "number,creator,myg6_basedatafield,myg6_basedatafield_seat,myg6_datefield,myg6_timefield_start,myg6_timefield_end";
        // Create an empty filter array   (no filters)
        QFilter[] filters2 = new QFilter[0];
        // Load the data
        DynamicObject[] dys2 = BusinessDataServiceHelper.load("myg6_seat_apply", fields2, filters2);
        String infoSeat = "订阅的座位有:\n";
        for (DynamicObject o : dys2) {
            DynamicObject creatorObj = (DynamicObject) o.get("creator");
            if (creatorObj == null) continue;
            String creator = creatorObj.getString("name");
            if (creator == null) continue;
            if (StringUtils.equals(creator, name)) {
                System.out.println("o info" + o);
                System.out.println("------------------------------");
                DynamicObject room = (DynamicObject) o.get("myg6_basedatafield");
                DynamicObject seat = (DynamicObject) o.get("myg6_basedatafield_seat");
                if (room == null || seat == null) continue;
                String roomName = room.getString("name");
                String seatName = seat.getString("number");
                String startTime = o.getString("myg6_timefield_start");
                String endTime = o.getString("myg6_timefield_end");
                // 获取今天的日期
                String date = o.getString("myg6_datefield");
                if (date == null) continue;
                date = date.substring(0, 10);
                // 如果不是今天的日期, 则跳过
                DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                String today = LocalDate.now().format(fmt);
                System.out.println("today info" + today);
                if (!StringUtils.equals(date, today)) continue;
                System.out.println("---------是今天---------");
                if (roomName == null || seatName == null || startTime == null || endTime == null) continue;
                // Parse the time strings
                Duration duration = Duration.ofSeconds(Long.parseLong(endTime));
                LocalTime time = LocalTime.ofSecondOfDay(duration.getSeconds());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String edTime = time.format(formatter);
                Duration duration2 = Duration.ofSeconds(Long.parseLong(startTime));
                LocalTime time2 = LocalTime.ofSecondOfDay(duration2.getSeconds());
                String beginTime = time2.format(formatter);

                infoSeat += "自习室: " + roomName + "  座位号:" + seatName + "  开始与结束时间: " + beginTime + "  " + edTime + "\n";
            }
        }
        if (StringUtils.equals(infoSeat, "订阅的座位有:\n")) {
            infoSeat = "该同学未订阅座位";
        }
        // 显示识别结果
        this.getView().showMessage("识别结果:\n" + getInfo + "---------------" + "欢迎" + name + "同学来到华科智能图书馆!!!\n");
        this.getModel().setValue("myg6_textareafield", infoBook + "\n" + infoSeat);

        // 得出学生照片
        QFilter qFilterr = new QFilter("myg6_student_name", QCP.equals, name);
        DynamicObject faceInfo = BusinessDataServiceHelper.loadSingle("myg6_student_info", new QFilter[]{qFilterr});
        String imgurl = faceInfo.getString("myg6_student_photo");
        this.getModel().setValue("myg6_picturefield", imgurl);
    }
}