package plugins.extendedfun;

import kd.bos.form.control.IFrame;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 动态表单插件
 */
public class OpenWebUrl extends AbstractFormPlugin implements Plugin {

    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        IFrame iframe2 = this.getControl("myg6_tongyi_deeplearning");
        iframe2.setSrc("https://www.scopus.com/");


    }
}