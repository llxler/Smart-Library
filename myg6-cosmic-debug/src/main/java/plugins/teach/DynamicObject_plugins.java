package plugins.teach;

import kd.bos.base.AbstractBasePlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

public class DynamicObject_plugins extends AbstractBasePlugIn implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        // 监听工具栏
        this.addItemClickListeners("tbmain");
    }

    // 工具栏里面的点击事件
    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        // 若检测到点击到提交按钮
        if (e.getItemKey().equalsIgnoreCase("myg6_baritemap")) {
            // 查看列表
            String fields = "number,name,myg6_author,myg6_type";
            QFilter[] filters = new QFilter[0];
            DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book", fields, filters);
            String output = "";
            for (DynamicObject obj : dys) {
                String number = obj.getString("number");
                String name = obj.getString("name");
                String author = obj.getString("myg6_author");
                String type = obj.getString("myg6_type");
                output += "number: " + number + ", name: " + name + ", author: " + author + ", type: " + type + "\n";
            }
            this.getView().showMessage(output);
        } else if (e.getItemKey().equalsIgnoreCase("myg6_baritemap1")) {
            // 更改值
            String fields = "number,name,myg6_author,myg6_type";
            QFilter qFilter = new QFilter("name", QCP.equals, "金蝶");
            DynamicObject dy = BusinessDataServiceHelper.loadSingle("myg6_book", fields, new QFilter[]{qFilter});

            dy.set("name", "我是金蝶高手");
            SaveServiceHelper.update(dy); // 这一步至关重要！
        }
    }
}