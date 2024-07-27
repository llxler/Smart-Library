package plugins.extendedfun;

import kd.bos.form.control.IFrame;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;
import static myg6.cosmic.debug.DebugApplication.MY_IP;

/**
 * 动态表单插件
 */
public class ForumIFrame extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        IFrame iframe = this.getControl("myg6_frame_forum");
        iframe.setSrc("http://"+ MY_IP +":12347/");
    }
}