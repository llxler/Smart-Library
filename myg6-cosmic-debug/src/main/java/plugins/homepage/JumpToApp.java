package plugins.homepage;

import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Control;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.list.ListShowParameter;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;
import org.apache.commons.lang3.StringUtils;

import java.util.EventObject;

/**
 * 动态表单插件
 */
public class JumpToApp extends AbstractFormPlugin implements Plugin {
    public void registerListener(EventObject event) {
        // 注册监听
        super.registerListener(event);
        this.addClickListeners("myg6_image_books");
        this.addClickListeners("myg6_image_seats");
        this.addClickListeners("myg6_image_shelf");
        this.addClickListeners("myg6_image_notes");
        this.addClickListeners("myg6_image_credit");
        this.addClickListeners("myg6_forum");
        this.addClickListeners("myg6_image_exfun");
    }

    public void click(EventObject evt) {
        super.click(evt);
        //获取被点击的控件对象
        Control source = (Control) evt.getSource();
        if (StringUtils.equals("myg6_image_notes", source.getKey())) {
            FormShowParameter formShowParameter = new FormShowParameter();
            formShowParameter.setFormId("myg6_read_notes");
            formShowParameter.getOpenStyle().setShowType(ShowType.MainNewTabPage);
            this.getView().showForm(formShowParameter);
        } else if(StringUtils.equals("myg6_image_books", source.getKey())){
            ListShowParameter billShowParameter = new ListShowParameter();
            billShowParameter.setFormId("bos_list");
            billShowParameter.setBillFormId("myg6_book_list");
            billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
            this.getView().showForm(billShowParameter);
        } else if(StringUtils.equals("myg6_image_seats", source.getKey())){
            ListShowParameter billShowParameter = new ListShowParameter();
            billShowParameter.setFormId("bos_list");
            billShowParameter.setBillFormId("myg6_room");
            billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
            this.getView().showForm(billShowParameter);
        } else if(StringUtils.equals("myg6_image_shelf", source.getKey())){
            ListShowParameter billShowParameter = new ListShowParameter();
            billShowParameter.setFormId("bos_list");
            billShowParameter.setBillFormId("myg6_my_bookshelf");
            billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
            this.getView().showForm(billShowParameter);
        } else if (StringUtils.equals("myg6_image_credit", source.getKey())) {
            BillShowParameter billShowParameter = new BillShowParameter();
            billShowParameter.setFormId("myg6_credibility_table");
            billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
            billShowParameter.setStatus(OperationStatus.VIEW);
            RequestContext rc = RequestContext.get();
            String nowUser = rc.getUserName();
            QFilter qFilter = new QFilter("myg6_textfield", QCP.equals, nowUser);
            DynamicObject credititself = BusinessDataServiceHelper.loadSingle("myg6_credibility_table", new QFilter[]{qFilter});
            Long pkId = (Long) credititself.getPkValue();
            billShowParameter.setPkId(pkId);
            this.getView().showForm(billShowParameter);
        } else if (StringUtils.equals("myg6_forum", source.getKey())) {
            FormShowParameter formShowParameter = new FormShowParameter();
            formShowParameter.setFormId("myg6_forum");
            formShowParameter.getOpenStyle().setShowType(ShowType.Modal);
            this.getView().showForm(formShowParameter);
        } else if (StringUtils.equals("myg6_image_exfun", source.getKey())) {
            FormShowParameter formShowParameter = new FormShowParameter();
            formShowParameter.setFormId("myg6_funs");
            formShowParameter.getOpenStyle().setShowType(ShowType.Modal);
            this.getView().showForm(formShowParameter);
        }
    }
}