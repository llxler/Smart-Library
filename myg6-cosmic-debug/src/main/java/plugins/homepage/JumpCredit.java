package plugins.homepage;

import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.ShowType;
import kd.bos.form.control.Control;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;
import org.apache.commons.lang3.StringUtils;

import java.util.EventObject;

/**
 * 动态表单插件
 */
public class JumpCredit extends AbstractFormPlugin implements Plugin {
    public void registerListener(EventObject event) {
        // 注册监听
        super.registerListener(event);
        this.addClickListeners("myg6_image_credit");
    }

    public void click(EventObject evt) {
        super.click(evt);
        //获取被点击的控件对象
        Control source = (Control) evt.getSource();
        if (StringUtils.equals("myg6_image_credit", source.getKey())) {
            // 初始化一个新表单
            BillShowParameter billShowParameter = new BillShowParameter();
            billShowParameter.setFormId("myg6_credibility_table");
            billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
            billShowParameter.setStatus(OperationStatus.VIEW);
            // 打开新表单
            RequestContext rc = RequestContext.get();
            String nowUser = rc.getUserName();
            QFilter qFilter = new QFilter("myg6_textfield", QCP.equals, nowUser);
            DynamicObject credititself = BusinessDataServiceHelper.loadSingle("myg6_credibility_table", new QFilter[]{qFilter});
            Long pkId = (Long) credititself.getPkValue();
            billShowParameter.setPkId(pkId);
            this.getView().showForm(billShowParameter);
        }
    }
}