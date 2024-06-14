package plugins.extendedfun;

import kd.bos.form.control.IFrame;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 动态表单插件
 */
public class ForumIFrame extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        IFrame iframe = this.getControl("myg6_frame_forum");
        iframe.setSrc("http://localhost:12347/");
    }
}