package plugins.homepage;

import kd.bos.bill.BillShowParameter;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Control;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.list.ListShowParameter;
import kd.sdk.plugin.Plugin;
import org.apache.commons.lang3.StringUtils;

import java.util.EventObject;

/**
 * 动态表单插件
 */
public class Jumpapp1 extends AbstractFormPlugin implements Plugin {
    public void registerListener(EventObject event) {
        // 注册监听
        super.registerListener(event);
        this.addClickListeners("myg6_image_books");
        this.addClickListeners("myg6_image_seats");
        this.addClickListeners("myg6_image_shelf");
        this.addClickListeners("myg6_image_notes");
        this.addClickListeners("myg6_image_plan");
        this.addClickListeners("myg6_image_subcribe");

    }

    public void click(EventObject evt) {
        super.click(evt);
        //获取被点击的控件对象
        Control source = (Control) evt.getSource();
        if (StringUtils.equals("myg6_image_notes", source.getKey())) {
            // 初始化一个新表单
            FormShowParameter billShowParameter = new FormShowParameter();
            billShowParameter.setFormId("myg6_read_notes");
            billShowParameter.getOpenStyle().setShowType(ShowType.InCurrentForm);
            //弹出表单和本页面绑定
            this.getView().showForm(billShowParameter);
        }
        else if(StringUtils.equals("myg6_image_books", source.getKey())){
            ListShowParameter billShowParameter = new ListShowParameter();
            billShowParameter.setFormId("bos_list");
            billShowParameter.setBillFormId("myg6_book_list");
            billShowParameter.getOpenStyle().setShowType(ShowType.InCurrentForm);
            this.getView().showForm(billShowParameter);
        }
        else if(StringUtils.equals("myg6_image_seats", source.getKey())){
            ListShowParameter billShowParameter = new ListShowParameter();
            billShowParameter.setFormId("bos_list");
            billShowParameter.setBillFormId("myg6_room");
            billShowParameter.getOpenStyle().setShowType(ShowType.InCurrentForm);
            this.getView().showForm(billShowParameter);
        }
        else if(StringUtils.equals("myg6_image_shelf", source.getKey())){
            ListShowParameter billShowParameter = new ListShowParameter();
            billShowParameter.setFormId("bos_list");
            billShowParameter.setBillFormId("myg6_my_bookshelf");
            billShowParameter.getOpenStyle().setShowType(ShowType.InCurrentForm);
            this.getView().showForm(billShowParameter);
        }
        else if(StringUtils.equals("myg6_image_plan", source.getKey())){
            BillShowParameter billShowParameter = new BillShowParameter();
            billShowParameter.setFormId("myg6_planlist");
            billShowParameter.getOpenStyle().setShowType(ShowType.InCurrentForm);
            this.getView().showForm(billShowParameter);
        }
        else if(StringUtils.equals("myg6_image_subcribe", source.getKey())){
            BillShowParameter billShowParameter = new BillShowParameter();
            billShowParameter.setFormId("myg6_book_subscribe");
            billShowParameter.getOpenStyle().setShowType(ShowType.InCurrentForm);
            this.getView().showForm(billShowParameter);
        }
    }
}