package plugins.extendedfun;

import com.alibaba.druid.util.StringUtils;
import kd.bos.entity.IFrameMessage;
import kd.bos.form.FormShowParameter;
import kd.bos.form.container.Container;
import kd.bos.form.control.IFrame;
import kd.bos.form.events.ClientCallBackEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 动态表单插件
 */
public class QAanal extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        IFrame iframe = this.getControl("myg6_frame_echarts");
        iframe.setSrc("http://localhost:12348/");
        this.getView().addClientCallBack("10", 200);
    }
    @Override
    public void clientCallBack(ClientCallBackEvent e) {
        super.clientCallBack(e);
        FormShowParameter showParameter = this.getView().getFormShowParameter();
        String data = showParameter.getCustomParam("qaResult");
        System.out.println("now data: " + data);
        IFrame iframe = this.getControl("myg6_frame_echarts");
        IFrameMessage message = new IFrameMessage();
        message.setType("qabot");
        message.setOrigin("*");
        message.setContent(data);
        iframe.postMessage(message);
    }
}