package plugins.seatmng;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.form.FormShowParameter;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.EventObject;

import kd.bos.form.plugin.AbstractFormPlugin;

/**
 * 动态表单插件
 */

public class SeatSelectBill101 extends AbstractFormPlugin {

    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        //获取父页面的数据模型
        FormShowParameter showParameter = this.getView().getFormShowParameter();

        // 获取座位基础资料
        String seatId = showParameter.getCustomParam("myg6_basedatafield_seat");
        if (seatId == null) return;
        QFilter qFilter = new QFilter("number", QCP.equals, seatId);
        DynamicObject goalSeat = BusinessDataServiceHelper.loadSingle("myg6_seat", new QFilter[]{qFilter});
        DynamicObject goalRoom = (DynamicObject) goalSeat.get("myg6_basedatafield");

        IDataModel parentModel = this.getView().getParentView().getModel();
        //给子页面相关属性赋值
        this.getModel().setValue("myg6_basedatafield_seat", goalSeat);
        this.getModel().setValue("myg6_basedatafield", goalRoom);

    }

}

