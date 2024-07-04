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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.EventObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 动态表单插件
 */
public class FaceRec extends AbstractFormPlugin implements Plugin {

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
                    String jsonInputString = "{\n" + // lxlerimg.jpg 可以更改
                            "  \"groupIdList\": \"kingdee\",\n" +
                            "  \"img\": \"D:\\\\Desktop\\\\mapleimg.jpg\",\n" +
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
                            System.out.println("Response: " + response);
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
                            System.out.println("assuser name: " + name);
                            // 查询该人借阅的书籍
                            String fields = "creator,myg6_nameofbook,myg6_billstatusfield";
                            // Create an empty filter array   (no filters)
                            QFilter[] filters = new QFilter[0];
                            // Load the data
                            DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_subscribe", fields, filters);
                            String infoBook = "借阅的书籍有:\n";
                            for (DynamicObject o : dys) {
                                DynamicObject creatorObj = (DynamicObject) o.get("creator");
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
                                        status = "已归还";
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
                                    date = date.substring(0, 10);
                                    System.out.println("date info" + date);
                                    if (date == null) continue;
                                    // 如果不是今天的日期, 则跳过
                                    DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                                    String today = LocalDate.now().format(fmt);
                                    System.out.println("today info" + today);
                                    if (!StringUtils.equals(date, today)) continue;
                                    System.out.println("---------是今天---------");
                                    if (!StringUtils.equals(date, today)) continue;

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
                            this.getView().showMessage("识别结果\n" + getInfo + "\n" + "欢迎" + name + "来到华科智能图书馆\n");
                            this.getModel().setValue("myg6_textareafield", infoBook + "\n" + infoSeat);
                            // 得出学生照片
                            QFilter qFilterr = new QFilter("myg6_student_name", QCP.equals, name);
                            DynamicObject faceInfo = BusinessDataServiceHelper.loadSingle("myg6_student_info", new QFilter[]{qFilterr});
                            System.out.println("student info" + faceInfo);
                            String imgurl = faceInfo.getString("myg6_student_photo");
                            this.getModel().setValue("myg6_picturefield", imgurl);
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