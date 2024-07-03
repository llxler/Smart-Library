package plugins.homepage;

import com.alibaba.druid.util.StringUtils;
import kd.bos.entity.IFrameMessage;
import kd.bos.form.control.IFrame;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;
import kd.bos.form.container.Container;

import java.util.EventObject;

/**
 * 动态表单插件
 */
public class ShowEchartsHomepg1 extends AbstractFormPlugin implements Plugin {
//    @Override
//    public void preOpenForm(PreOpenFormEventArgs e) {
//        super.preOpenForm(e);
//        // 要求触发TimerElapsed事件
//        ((FormShowParameter)e.getSource()).setListentimerElapsed(true);
//    }
//
//    @Override
//    public void TimerElapsed(TimerElapsedArgs e) {
//        IFrame iframe = this.getControl("myg6_frame_echarts");
//        IFrameMessage message = new IFrameMessage();
//        message.setType("home");
//        message.setOrigin("*");
//        iframe.postMessage(message);
//    }

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
        Container flex = this.getView().getControl("myg6_flexpanelap11");
        flex.addClickListener(this);
    }
    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Container) {
            Container button = (Container) source;
            String key = button.getKey();
            if (StringUtils.equals("myg6_flexpanelap11", key)) {
                IFrame iframe = this.getControl("myg6_frame_echarts");
                IFrameMessage message = new IFrameMessage();
                message.setType("home");
                message.setOrigin("*");
                iframe.postMessage(message);
            }
        }
    }
}