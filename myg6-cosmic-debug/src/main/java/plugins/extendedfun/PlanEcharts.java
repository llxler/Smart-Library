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


/**
 * 动态表单插件
 */
public class PlanEcharts extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        IFrame iframe = this.getControl("myg6_frame_echarts");
        iframe.setSrc("http://localhost:12348/");
        // 渲染日历echart
        // 获取今天的日期给String today
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        String today = LocalDate.now().format(formatter);
        String txt = "今天日期:" + today + "\n";

        DynamicObjectCollection rows = this.getModel().getEntryEntity("myg6_per_plan_body");
        int idx = 1;
        for (DynamicObject single : rows) {
            String bookName = single.getString("myg6_bookname");
            int days = single.getInt("myg6_plan_time");
            txt += idx + ". " + "图书名称:" + bookName + "阅读天数:" + days + "\n";
            ++idx;
        }
        System.out.println("fuck" + txt);
        IFrameMessage message = new IFrameMessage();
        message.setType("plan:begin");
        message.setContent(txt);
        message.setOrigin("*");
        iframe.postMessage(message);
    }
}