package plugins.seatmng;


import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.PreparePropertysEventArgs;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;
import kd.bos.entity.plugin.args.AfterOperationArgs;

import java.util.List;

/**
 * 单据操作插件
 */
public class AddSeatCount extends AbstractOperationServicePlugIn implements Plugin {

    private final static String SEAT = "tk_myg6_seat_seats";

    private final static String ROOM = "myg6_basedatafield";

    private final static String ROOM_ID = "number";

    @Override
    public void onPreparePropertys(PreparePropertysEventArgs e) {
        //加载自习室id字段
        e.getFieldKeys().add(ROOM);
    }

    @Override
    public void afterExecuteOperationTransaction(AfterOperationArgs e) {
        //座位表
        List<ExtendedDataEntity> rows = e.getSelectedRows();
        for (ExtendedDataEntity row : rows) {
            DynamicObject dataEntity = row.getDataEntity();
            DynamicObject o = (DynamicObject)dataEntity.get("myg6_basedatafield");
            Integer num = Integer.valueOf(o.getString("myg6_integerfield1")) + 1;
            o.set("myg6_integerfield1", num.toString());
            SaveServiceHelper.update(o);
        }
    }
}
