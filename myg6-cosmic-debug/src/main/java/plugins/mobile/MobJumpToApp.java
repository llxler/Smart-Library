package plugins.mobile;

import com.alibaba.druid.util.StringUtils;
import kd.bos.bill.AbstractMobBillPlugIn;
import kd.bos.bill.MobileBillShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Button;
import kd.bos.list.MobileListShowParameter;
import kd.sdk.plugin.Plugin;
import kd.bos.form.control.Vector;

import java.util.EventObject;

/**
 * 单据界面插件(移动端)
 */
public class MobJumpToApp extends AbstractMobBillPlugIn implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Vector v1 = this.getView().getControl("myg6_kdtest_kdtest_temp17");
        v1.addClickListener(this);
        Vector v2 = this.getView().getControl("myg6_kdtest_kdtest_temp16");
        v2.addClickListener(this);
        Vector v3 = this.getView().getControl("myg6_kdtest_kdtest_temp18");
        v3.addClickListener(this);
        Vector v4 = this.getView().getControl("myg6_kdtest_kdtest_tempf5");
        v4.addClickListener(this);
        Vector v5 = this.getView().getControl("myg6_kdtest_kdtest_temp20");
        v5.addClickListener(this);
        Vector v6 = this.getView().getControl("myg6_kdtest_kdtest_tempf8");
        v6.addClickListener(this);
        Vector v7 = this.getView().getControl("myg6_kdtest_kdtest_temp22");
        v7.addClickListener(this);
        Vector v8 = this.getView().getControl("myg6_kdtest_kdtest_temp11");
        v8.addClickListener(this);
        Vector v9 = this.getView().getControl("myg6_kdtest_kdtest_temp24");
        v9.addClickListener(this);
        Vector v10 = this.getView().getControl("myg6_kdtest_kdtest_temp14");
        v10.addClickListener(this);
    }

    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            if (StringUtils.equals("myg6_kdtest_kdtest_temp17", key)) { // 图书库跳转
                MobileListShowParameter lsp = new MobileListShowParameter();
                lsp.setFormId("bos_moblist");
                lsp.setBillFormId("myg6_mobile_list");
                lsp.getOpenStyle().setShowType(ShowType.Floating);
                this.getView().showForm(lsp);
            } else if (StringUtils.equals("myg6_kdtest_kdtest_temp16", key)) { // 图书评论
                this.getView().showMessage("移动端图书评论模块正在开发中");
            } else if (StringUtils.equals("myg6_kdtest_kdtest_temp18", key)) { // 座位预约
                MobileBillShowParameter billShowParameter = new MobileBillShowParameter ();
                billShowParameter.setFormId("myg6_mobile_seat");
                billShowParameter.getOpenStyle().setShowType(ShowType.Floating);
                this.getView().showForm(billShowParameter);
            } else if (StringUtils.equals("myg6_kdtest_kdtest_tempf5", key)) { // 读书计划
                this.getView().showMessage("移动端读书计划模块正在开发中");
            } else if (StringUtils.equals("myg6_kdtest_kdtest_temp20", key)) { // 我的书架
                MobileListShowParameter lsp = new MobileListShowParameter();
                lsp.setFormId("bos_moblist");
                lsp.setBillFormId("myg6_mobile_bookshell");
                lsp.getOpenStyle().setShowType(ShowType.Floating);
                this.getView().showForm(lsp);
            } else if (StringUtils.equals("myg6_kdtest_kdtest_tempf8", key)) { // 图书订阅
                MobileListShowParameter lsp = new MobileListShowParameter();
                lsp.setFormId("bos_moblist");
                lsp.setBillFormId("myg6_mobile_subscribe");
                lsp.getOpenStyle().setShowType(ShowType.Floating);
                this.getView().showForm(lsp);
            } else if (StringUtils.equals("myg6_kdtest_kdtest_temp22", key)) { // 读书笔记
                this.getView().showMessage("移动端读书笔记模块正在开发中");
            } else if (StringUtils.equals("myg6_kdtest_kdtest_temp11", key)) { // 图书论坛
                this.getView().showMessage("移动端图书论坛模块正在开发中");
            } else if (StringUtils.equals("myg6_kdtest_kdtest_temp24", key)) { // 信誉系统
                this.getView().showMessage("移动端信誉系统模块正在开发中");
            } else if (StringUtils.equals("myg6_kdtest_kdtest_temp14", key)) { // 智慧阅读
                this.getView().showMessage("移动端智慧阅读功能模块正在开发中");
            }
        }
    }
}