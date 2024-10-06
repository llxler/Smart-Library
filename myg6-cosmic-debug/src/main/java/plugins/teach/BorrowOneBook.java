package plugins.teach;

import kd.bos.base.AbstractBasePlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;

/**
 * 基础资料插件
 */
public class BorrowOneBook extends AbstractBasePlugIn implements Plugin {
    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        // 若检测到点击到审核按钮
        if (e.getItemKey().equalsIgnoreCase("bar_audit")) {
            // 查看列表
            String fields = "name,myg6_bookinfo,myg6_combo";
            QFilter[] filters = new QFilter[0];
            DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book", fields, filters);
            String borrowBookName = (String) this.getModel().getValue("myg6_name");


            Boolean haveBook = false;
            for (DynamicObject obj : dys) {

                String bookName = obj.getString("name");
                if(bookName.equals(borrowBookName)){

                    DynamicObjectCollection rows = obj.getDynamicObjectCollection("myg6_bookinfo");
                    for(DynamicObject row: rows){
                        if(row.getString("myg6_combo").equals("0")){
                            haveBook = true;
                            row.set("myg6_combo", "1");
                            SaveServiceHelper.update(obj);
                            break;
                        }
                    }
                }

            }
            //如果haveBook为false，说明没有库存
            if(!haveBook) {
                this.getView().showMessage("没有库存");
            }
        }
    }
}