package plugins.teach;

import com.alibaba.druid.util.StringUtils;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.ShowType;
import kd.bos.form.control.Button;
import kd.bos.form.control.Label;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.list.ListShowParameter;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 动态表单插件
 */
public class PluginSkills extends AbstractFormPlugin implements Plugin {

    // 注册点击事件
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        // 监听工具栏
        this.addItemClickListeners("tbmain");
        // 监听控件
        Label label = this.getView().getControl("myg6_labelap");
        label.addClickListener(this);
    }

    // 工具栏里面的点击事件
    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        // 若检测到点击到提交按钮
        if (e.getItemKey().equalsIgnoreCase("myg6_baritemap")) {
            // 1. 弹出消息我是谁
            RequestContext rc = RequestContext.get();
            String nowUser = rc.getUserName();
            this.getView().showMessage("我是：" + nowUser);
        } else if (e.getItemKey().equalsIgnoreCase("myg6_baritemap1")) {
            // 2. 从数据库中得到评论
            String fields = "billno,creator,myg6_nameofbook,myg6_date_ed";
            QFilter[] filters = new QFilter[0];
            DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_subscribe", fields, filters);
            String output = "";
            int i = 0;
            for (DynamicObject single : dys) {
               String billno = single.getString("billno");
               DynamicObject book = (DynamicObject) single.get("myg6_nameofbook");
               String bookName = book.getString("name");
               output += ++i + ". " + billno + " book: " + bookName + "\n";
            }
            this.getView().showMessage(output);
        } else if (e.getItemKey().equalsIgnoreCase("myg6_baritemap2")) {
            // 3. 填写字段
            String iWantToWrite = "I love kingdee!";
            this.getModel().setValue("myg6_textfield", iWantToWrite);
        } else if (e.getItemKey().equalsIgnoreCase("myg6_baritemap3")) {
            // 4. 跳转到评论
            ListShowParameter nxtList = new ListShowParameter();
            nxtList.setFormId("bos_list");
            nxtList.setBillFormId("myg6_book_subscribe");
            nxtList.getOpenStyle().setShowType(ShowType.Modal);
            this.getView().showForm(nxtList);
        }
    }

    // 其他控件的点击事件
    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            if (StringUtils.equals("myg6_labelap", key)) {
                this.getView().showMessage("我点击了标签！");
            }
        }
    }
}