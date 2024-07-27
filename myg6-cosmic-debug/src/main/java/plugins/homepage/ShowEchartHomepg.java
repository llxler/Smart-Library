package plugins.homepage;

import kd.bos.entity.IFrameMessage;
import kd.bos.form.control.IFrame;
import kd.bos.form.events.ClientCallBackEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

import static myg6.cosmic.debug.DebugApplication.MY_IP;

/**
 * 动态表单插件
 */
public class ShowEchartHomepg extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        IFrame iframe = this.getControl("myg6_frame_echarts");
        iframe.setSrc("http://"+ MY_IP +":12348/");
        this.getView().addClientCallBack("10", 1500);
    }
    @Override
    public void clientCallBack(ClientCallBackEvent e) {
        super.clientCallBack(e);
        IFrame iframe = this.getControl("myg6_frame_echarts");
        IFrameMessage message = new IFrameMessage();
        message.setType("home");
        message.setOrigin("*");
        iframe.postMessage(message);
    }
}