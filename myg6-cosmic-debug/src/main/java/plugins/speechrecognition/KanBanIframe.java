package plugins.speechrecognition;

import kd.bos.form.control.IFrame;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

import static myg6.cosmic.debug.DebugApplication.MY_IP;
/**
 * 动态表单插件
 */
public class KanBanIframe extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        IFrame iframe = this.getControl("myg6_kanbanniang");
        iframe.setSrc("http://"+ MY_IP +":12355/live.html");
    }
}