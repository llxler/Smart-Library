package plugins.extendedfun;

import com.alibaba.druid.util.StringUtils;
import kd.bos.entity.IFrameMessage;
import kd.bos.form.FormShowParameter;
import kd.bos.form.container.Container;
import kd.bos.form.control.IFrame;
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
    }

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        //添加按钮监听
        Container flex = this.getView().getControl("myg6_flexpanelap");
        flex.addClickListener(this);
    }
    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Container) {
            Container button = (Container) source;
            String key = button.getKey();
            if (StringUtils.equals("myg6_flexpanelap", key)) {
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
    }
}