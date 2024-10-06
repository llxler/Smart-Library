package plugins.teach;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.gpt.IGPTAction;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态表单插件
 */
public class Cosmic_borrowbook implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if ("GET_JSON_STRING".equalsIgnoreCase(action)) {
            // 获取Json字符串
            String jsonResult = params.get("jsonResult").replaceAll("\\s*|\r|\n|\t", "");
            JSONObject resultJsonObject = null;
            try {
                resultJsonObject = JSON.parseObject(jsonResult);
            } catch (Exception ee) {
                jsonResult = jsonResult.substring(jsonResult.indexOf("\"bookName\"") - 1, jsonResult.indexOf("}") + 1);
                resultJsonObject = JSON.parseObject(jsonResult);
            }
            System.out.println("resultJsonObject: " + resultJsonObject);

            // 随机一个单据编号
            StringBuilder sb1 = new StringBuilder();
            for (int i = 1; i <= 10; ++i) {
                int ascii = 48 + (int) (Math.random() * 9);
                char c = (char) ascii;
                sb1.append(c);
            }

            // 提炼出json信息
            String bookName = resultJsonObject.getString("bookName");
            String borrowTime = resultJsonObject.getString("borrowTime");

            // 筛出该图书的基础资料，方便后续set到借书单据中
            QFilter qFilter = new QFilter("name", QCP.equals, bookName);
            DynamicObject book = BusinessDataServiceHelper.loadSingle("myg6_book", new QFilter[]{qFilter});

            // 获取该书籍的对应信息
            String myg6_picture = book.getString("myg6_picturefield");
            String myg6_isbn = book.getString("number");
            String myg6_name = book.getString("name");
            String myg6_author = book.getString("myg6_author");
            String myg6_type = book.getString("myg6_type");

            // 获取当前日期
            LocalDate today = LocalDate.now();
            long daysToAdd = Long.parseLong(borrowTime);
            LocalDate returnDay = today.plusDays(daysToAdd);
            Date todayDate = Date.valueOf(today);
            Date ReturnDay = Date.valueOf(returnDay);

            // new 一个 DynamicObject 表单对象
            DynamicObject dynamicObject = BusinessDataServiceHelper.newDynamicObject("myg6_borrowbook");

            // 设置对应属性
            dynamicObject.set("billno", sb1.toString());
            dynamicObject.set("myg6_basedatafield", book);
            dynamicObject.set("myg6_picture", myg6_picture);
            dynamicObject.set("myg6_isbn", myg6_isbn);
            dynamicObject.set("myg6_name", myg6_name);
            dynamicObject.set("myg6_author", myg6_author);
            dynamicObject.set("myg6_type", myg6_type);
            dynamicObject.set("myg6_day", borrowTime);
            dynamicObject.set("createtime", todayDate);
            dynamicObject.set("myg6_endtime", ReturnDay);

            SaveServiceHelper.saveOperate("myg6_borrowbook", new DynamicObject[]{dynamicObject}, null);
            Long pkId = (Long) dynamicObject.getPkValue();
            // 拼接URL字符串
            String targetForm = "bizAction://currentPage?gaiShow=1&selectedProcessNumber=processNumber&gaiAction=showBillForm&gaiParams={\"appId\":\"myg6_booksmanage\",\"billFormId\":\"myg6_borrowbook\",\"billPkId\":\"" + pkId + "\"}&title=借阅单据 &iconType=bill&method=bizAction";
            result.put("formUrl", targetForm);
        }
        return result;
    }
}