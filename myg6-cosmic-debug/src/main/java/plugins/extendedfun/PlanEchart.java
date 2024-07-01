package plugins.extendedfun;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.IFrameMessage;
import kd.bos.form.control.IFrame;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EventObject;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 动态表单插件
 */
public class PlanEchart extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        IFrame iframe = this.getControl("myg6_frame_echarts");
        iframe.setSrc("http://localhost:12348/");
        // 渲染日历echart
        // 获取今天的日期给String today
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        String today1 = LocalDate.now().format(formatter);
        String txt = "今天日期:" + today1 + "\n";

        DynamicObjectCollection rows = this.getModel().getEntryEntity("myg6_per_plan_body");
        int idx = 1;
        for (DynamicObject single : rows) {
            String bookName = single.getString("myg6_bookname");
            int days = single.getInt("myg6_plan_time");
            txt += idx + ". " + "图书名称:" + bookName + " 阅读天数:" + days + "\n";
            ++idx;
        }
        System.out.println("fuck" + txt);

        // 解析日期
        String[] lines = txt.split("\n");
        String todayStr = lines[0].split(":")[1];
        LocalDate today = LocalDate.parse(todayStr, DateTimeFormatter.ofPattern("yyyy-M-d"));

        JSONArray booksArray = new JSONArray();

        // 开始时间
        LocalDate currentStartDate = today;

        // 解析每本书的信息
        for (int i = 1; i < lines.length; i++) {
            String line = lines[i];
            String[] parts = line.split("图书名称:|阅读天数:");
            String bookName = parts[1].trim();
            int readingDays = Integer.parseInt(parts[2].trim());

            // 计算结束时间
            LocalDate endDate = currentStartDate.plusDays(readingDays - 1);

            // 创建 JSON 对象
            JSONObject bookObject = new JSONObject();
            bookObject.put("书名", bookName);
            bookObject.put("开始时间", currentStartDate.toString());
            bookObject.put("结束时间", endDate.toString());

            // 添加到 JSON 数组
            booksArray.add(bookObject);

            // 更新开始时间为下一本书的开始时间
            currentStartDate = endDate.plusDays(1);
        }

        // 转换为 JSON 字符串
        String jsonString = JSON.toJSONString(booksArray, true);
        System.out.println("now info" + jsonString);

        IFrameMessage message = new IFrameMessage();
        message.setType("plan:begin");
        message.setContent(jsonString);
        message.setOrigin("*");
        iframe.postMessage(message);
    }
}