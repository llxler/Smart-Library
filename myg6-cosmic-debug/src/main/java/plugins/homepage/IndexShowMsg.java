package plugins.homepage;

import kd.bos.bill.AbstractBillPlugIn;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 单据界面插件
 */
public class IndexShowMsg extends AbstractBillPlugIn implements Plugin {
    @Override
    public void afterCreateNewData(EventObject e) {
        this.getView().showMessage("~欢迎来到我们的图书馆首页~");
    }
}