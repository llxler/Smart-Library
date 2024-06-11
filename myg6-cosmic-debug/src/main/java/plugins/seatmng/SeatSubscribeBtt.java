package plugins.seatmng;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.operate.Donothing;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.list.BillList;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.servicehelper.BusinessDataServiceHelper;

public class SeatSubscribeBtt extends AbstractListPlugin {

    @Override
    public void afterDoOperation(AfterDoOperationEventArgs afterDoOperationEventArgs) {
        super.afterDoOperation(afterDoOperationEventArgs);
        if ("donothing".equals(afterDoOperationEventArgs.getOperateKey())) {
            BillList list = this.getControl("billlistap");
            Donothing donothing = (Donothing) afterDoOperationEventArgs.getSource();
            Long id = (Long)donothing.getListFocusRow().getPrimaryKeyValue();
            DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle(id,"myg6_room");
            String roomname = dynamicObject.getString("name");
            if (roomname.equals("一号自习室")) {
                System.out.println("fuckyou");
            }
            FormShowParameter baseShowParameter = new FormShowParameter();

            baseShowParameter.setFormId("myg6_house1_choose"); // 弹窗的对象
            //baseShowParameter.setPkId(dynamicObject.get("kdec_basedatafield_id")); // 设置弹窗具体的页面
            baseShowParameter.getOpenStyle().setShowType(ShowType.Modal);// 打开方式
            //设置弹出子单据页面的样式，高600宽800
//            StyleCss inlineStyleCss = new StyleCss();
//            inlineStyleCss.setHeight("600");
//            inlineStyleCss.setWidth("800");
//            baseShowParameter.getOpenStyle().setInlineStyleCss(inlineStyleCss);
            this.getView().showForm(baseShowParameter);
        }
    }
}