package plugins.ReadHelper;

import kd.bos.entity.ListboxItem;
import kd.bos.ext.form.control.Listbox;
import kd.bos.form.control.events.ListboxEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import org.apache.commons.lang3.StringUtils;
import kd.bos.form.control.events.ListboxClickListener;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;



/**
 * 动态表单插件
 */
public class NoteMenu extends AbstractFormPlugin implements ListboxClickListener {
    public void registerListener(EventObject event) {
        // 注册监听
        super.registerListener(event);
        Listbox box = this.getView().getControl("myg6_listboxap");
        box.addListboxClickListener(this);

    }
    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        this.getView().setVisible(true, "myg6_md");
        this.getView().setVisible(false, "myg6_note");
        this.getView().setVisible(false, "myg6_translate");

    }
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        Listbox box = this.getView().getControl("myg6_listboxap");
        ListboxItem listboxItem1 = new ListboxItem("1", "导读");
        ListboxItem listboxItem2 = new ListboxItem("2", "笔记");
        ListboxItem listboxItem3 = new ListboxItem("3", "翻译");
        List<ListboxItem> itemlist = new ArrayList<>();
        itemlist.add(listboxItem1);
        itemlist.add(listboxItem2);
        itemlist.add(listboxItem3);
        box.addItems(itemlist);
    }

    @Override
    public void listboxClick(ListboxEvent listboxEvent) {
        String cur = listboxEvent.getItemId();
        if (StringUtils.equals(cur, "1")) {
            this.getView().setVisible(true, "myg6_md");
            this.getView().setVisible(false, "myg6_note");
            this.getView().setVisible(false, "myg6_translate");
        } else if(StringUtils.equals(cur, "2")) {
            this.getView().setVisible(false, "myg6_md");
            this.getView().setVisible(true, "myg6_note");
            this.getView().setVisible(false, "myg6_translate");
        } else if(StringUtils.equals(cur, "3")) {
            this.getView().setVisible(false, "myg6_md");
            this.getView().setVisible(false, "myg6_note");
            this.getView().setVisible(true, "myg6_translate");
        }
    }
}