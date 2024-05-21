package plugins;

import kd.bos.bill.AbstractBillPlugIn;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

public class IndexMessage extends AbstractBillPlugIn implements Plugin {
    @Override
    public void afterCreateNewData(EventObject e) {
        this.getView().showMessage("欢迎来到图书管理首页!");
    }
}