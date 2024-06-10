package plugins.extendedfun;

import com.alibaba.druid.util.StringUtils;
import kd.bos.entity.IFrameMessage;
import kd.bos.form.control.Button;
import kd.bos.form.control.IFrame;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态表单插件
 */
public class MindMapGenerator extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        IFrame iframe = this.getControl("myg6_MindMap");
        iframe.setSrc("http://127.0.0.1:7000/");
    }
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        //添加按钮监听
        Button button = this.getView().getControl("myg6_buttonap");
        button.addClickListener(this);
    }
    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            //页面点击”向第三方发送“按钮向第三方发送消息
            if (StringUtils.equals("myg6_buttonap",key)){
//                Object txt = this.getModel().getValue("ur need");
                Object txt = "Hello, Kingdee!";
                IFrame iframe = this.getView().getControl("myg6_MindMap");
                IFrameMessage message = new IFrameMessage();
                message.setContent(txt);
                message.setOrigin("*");
                iframe.postMessage(message);
            }
        }
    }
}