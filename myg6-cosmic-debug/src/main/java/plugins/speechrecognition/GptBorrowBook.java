package plugins.speechrecognition;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.gpt.IGPTAction;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 动态表单插件
 */
public class GptBorrowBook implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if ("GET_JSON_STRING".equalsIgnoreCase(action)) {
            String targetForm = "";
            String jsonResult = params.get("jsonResult").replaceAll("\\s*|\r|\n|\t", "");
            System.out.println("Fuck fuck" + jsonResult);
            JSONObject resultJsonObject = JSON.parseObject(jsonResult);
            // new一个DynamicObject表单对象
            DynamicObject dynamicObject = BusinessDataServiceHelper.newDynamicObject("myg6_book_subscribe");
            StringBuilder sb1 = new StringBuilder();
            for (int i = 1; i <= 10; ++i) {
                int ascii = 48 + (int) (Math.random() * 9);
                char c = (char) ascii;
                sb1.append(c);
            }

            // 判断找没找到书
            String bookName = resultJsonObject.getString("bookName");
            if (!StringUtils.equals(bookName, "-1") && !StringUtils.equals(bookName, "-400")) {
                String fields = "name,myg6_picturefield,myg6_textfield";
                // Create an empty filter array (no filters)
                QFilter[] filters = new QFilter[0];
                // Load the data
                DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_list", fields, filters);

                for (DynamicObject single : dys) {
                    if (single.getString("name").replaceAll("\\s+", "").equals(bookName.replaceAll("\\s+", ""))) {

                        String url = single.getString("myg6_picturefield");
                        String bookType = single.getString("myg6_textfield");
                        String beginnum = resultJsonObject.getString("beginTime");
                        String endnum = resultJsonObject.getString("endTime");
                        String duringTime = resultJsonObject.getString("duringTime");

                        dynamicObject.set("myg6_nameofbook", single);
                        dynamicObject.set("myg6_booktype", bookType);
                        dynamicObject.set("myg6_picturefield", url);
                        if (!StringUtils.equals(duringTime, "-1")) {
                            // 定义日期格式
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");
                            LocalDate beginDate, endDate;

                            if(StringUtils.equals(beginnum, "-1") || StringUtils.equals(endnum, "-1")) {
                                beginDate = LocalDate.now();
                                endDate = beginDate.plusDays(Integer.parseInt(duringTime));
                            }
                            // 将字符串转换为 LocalDate
                            else {
                                beginDate = LocalDate.parse(beginnum, formatter);
                                endDate = LocalDate.parse(endnum, formatter);
                                int duringDays = Integer.parseInt(duringTime);

                                // 获取当前日期
                                LocalDate currentDate = LocalDate.now();

                                // 判断beginDate是否小于当前日期
                                if (beginDate.isBefore(currentDate)) {
                                    beginDate = currentDate;
                                    endDate = beginDate.plusDays(Integer.parseInt(duringTime));
                                }
                            }

                            // 将 LocalDate 转换为 Date
                            Date beginDateFormatted = Date.from(beginDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                            Date endDateFormatted = Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                            System.out.println("beginDateFormatted: " + beginDateFormatted);
                            System.out.println("endDateFormatted: " + endDateFormatted);

                            dynamicObject.set("myg6_date_bg", beginDateFormatted);
                            dynamicObject.set("myg6_date_ed", endDateFormatted);
                        }
                        break;
                    }
                }
                SaveServiceHelper.saveOperate("myg6_book_subscribe", new DynamicObject[]{dynamicObject}, null);
                Long pkId = (Long) dynamicObject.getPkValue();
                // 拼接URL字符串
                targetForm = "bizAction://currentPage?gaiShow=1&selectedProcessNumber=processNumber&gaiAction=showBillForm&gaiParams={\"appId\":\"myg6_mylibrary\",\"billFormId\":\"myg6_book_subscribe\",\"billPkId\":\"" + pkId + "\"}&title=借书单据 &iconType=bill&method=bizAction";
            }
            result.put("formUrl", targetForm);
            result.put("resultJsonObject", resultJsonObject.toJSONString());
            result.put("state", bookName);
        } // 我要借一本呐喊，从7.20号开始借20天
        return result;
    }
}