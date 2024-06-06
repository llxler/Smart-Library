package plugins.PersonalLibrary;

import com.alibaba.druid.util.StringUtils;
import com.kingdee.bos.qing.filesystem.manager.model.QingFileInfo;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;

/**
 * 标准单据列表插件
 */
public class AutoReturnBook extends AbstractListPlugin implements Plugin {
    @Override
    public void itemClick(ItemClickEvent e) {
        if (StringUtils.equals("myg6_auto_return", e.getItemKey())){
            this.getSelectedRows().forEach(row -> {
                // 获取billid
                String id = row.getBillNo();

                // 获取书架单据
                QFilter qFilter = new QFilter("billno", QCP.equals, id);
                DynamicObject shelfDynamic = BusinessDataServiceHelper.loadSingle("myg6_my_bookshelf", new QFilter[]{qFilter});

                // 获取图书基础资料
                DynamicObject bookDynamic = (DynamicObject) shelfDynamic.get("myg6_basedatafield");
                String bookName = bookDynamic.getString("name");
                QFilter qFilter1 = new QFilter("name", QCP.equals, bookName);
                DynamicObject goalDynamic = BusinessDataServiceHelper.loadSingle("myg6_book_list", new QFilter[]{qFilter1});

                // modify the num
                Integer curnum = Integer.valueOf(goalDynamic.getString("myg6_curnum")) + 1;
                goalDynamic.set("myg6_curnum", curnum.toString());

                // modify the state
                shelfDynamic.set("myg6_billstatusfield", 1);

                // update
                SaveServiceHelper.update(shelfDynamic);
                SaveServiceHelper.update(goalDynamic);
            });
        }
    }

}