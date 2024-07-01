package plugins.extendedfun;

import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Control;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;
import org.apache.commons.lang3.StringUtils;

import java.util.EventObject;

/**
 * 动态表单插件
 */

public class ExtendedFunctionOpen extends AbstractFormPlugin implements Plugin {

    private final String FLEXID = "myg6_flexpanelapf";
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        //注册工作交接安排控件的监听
        this.addClickListeners();
        for(int i = 1;i <= 4;i++){
            this.addClickListeners(FLEXID + i);
        }
    }
    @Override
    public void click(EventObject evt) {
        super.click(evt);
        //获取被点击的控件对象
        Control source = (Control) evt.getSource();
        if (source != null) {
            for(int i = 1;i <= 4;i++) {
                if (StringUtils.equals(FLEXID + i, source.getKey())) {
                    FormShowParameter billShowParameter = new FormShowParameter();
                    if(i == 1) {
                        billShowParameter.setFormId("myg6_kgview");
                    } else if (i == 2) {
                        billShowParameter.setFormId("myg6_functions");
                    } else if (i == 3) {
                        this.getView().showMessage("功能暂未开放");
                    } else {
                        billShowParameter.setFormId("myg6_facerec");
                    }

                    billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
                    this.getView().showForm(billShowParameter);
                    break;
                }
            }
        }
    }
}