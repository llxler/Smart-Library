package plugins.extendedfun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.dataentity.entity.OrmLocaleValue;
import kd.bos.entity.IFrameMessage;
import kd.bos.form.control.EntryGrid;
import kd.bos.form.control.IFrame;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 动态表单插件
 */
public class PlanEchart extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        IFrame iframe = this.getControl("myg6_frame_echarts");
        iframe.setSrc("http://localhost:12348/");
    }
}