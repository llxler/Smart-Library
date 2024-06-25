package plugins.homepage;

import kd.bos.entity.ListboxItem;
import kd.bos.ext.form.control.Listbox;
import kd.bos.form.control.events.ListboxClickListener;
import kd.bos.form.control.events.ListboxEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class HomepageBookShow extends AbstractFormPlugin implements ListboxClickListener {

    private final String LISTBOX = "myg6_listboxap";
    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        this.getView().setVisible(true, "myg6_hot");
        this.getView().setVisible(false, "myg6_new");
        this.getView().setVisible(false, "myg6_small");
    }

    public void registerListener(EventObject event) {
        // 注册监听
        Listbox box = this.getView().getControl(LISTBOX);
        box.addListboxClickListener(this);
    }

    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        Listbox box = this.getView().getControl(LISTBOX);
        ListboxItem listboxItem1 = new ListboxItem("1", "近期热门");
        ListboxItem listboxItem2 = new ListboxItem("2", "新书上架");
        ListboxItem listboxItem3 = new ListboxItem("3", "小众热门");
        List<ListboxItem> itemlist = new ArrayList<>();
        itemlist.add(listboxItem1);
        itemlist.add(listboxItem2);
        itemlist.add(listboxItem3);
        box.addItems(itemlist);
    }

    @Override
    public void listboxClick(ListboxEvent listboxEvent) {
        String cur = listboxEvent.getItemId();
        this.getView().showSuccessNotification("当前点击菜单:" + listboxEvent.getItemId());
        System.out.println("fuck cur" + cur);
        if(StringUtils.equals(cur, "1")) {
            this.getView().setVisible(true, "myg6_hot");
            this.getView().setVisible(false, "myg6_new");
            this.getView().setVisible(false, "myg6_small");
        } else if(StringUtils.equals(cur, "2")) {
            this.getView().setVisible(false, "myg6_hot");
            this.getView().setVisible(true, "myg6_new");
            this.getView().setVisible(false, "myg6_small");
        } else if(StringUtils.equals(cur, "3")) {
            this.getView().setVisible(false, "myg6_hot");
            this.getView().setVisible(false, "myg6_new");
            this.getView().setVisible(true, "myg6_small");
        }

    }
}