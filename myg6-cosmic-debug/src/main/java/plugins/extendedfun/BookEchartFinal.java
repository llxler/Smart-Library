package plugins.extendedfun;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.OrmLocaleValue;
import kd.bos.entity.IFrameMessage;
import kd.bos.form.control.IFrame;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static myg6.cosmic.debug.DebugApplication.MY_IP;

/**
 * 动态表单插件
 */
public class BookEchartFinal extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        IFrame iframe = this.getControl("myg6_frame_echarts");
        iframe.setSrc("http://"+ MY_IP +":12348/");
        IFrame iframekg = this.getControl("myg6_frame_kg");
        iframekg.setSrc("http://"+ MY_IP +":12356/");
    }
}