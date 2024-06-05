package plugins.seatmng;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.ExtendedDataEntity;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.PreparePropertysEventArgs;
import kd.bos.entity.plugin.args.AfterOperationArgs;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;
import java.util.List;

/**
 * 单据操作插件
 */
public class SubSearCountByUnaudit extends AbstractOperationServicePlugIn implements Plugin {
    private final static String ROOM = "myg6_basedatafield";

    private final static String SEAT_NUM = "myg6_integerfield1";

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
            DynamicObject o = (DynamicObject) dataEntity.get(ROOM);
            Integer num = Integer.valueOf(o.getString(SEAT_NUM)) - 1;
            if (num < 0) return;
            o.set(SEAT_NUM, num.toString());
            SaveServiceHelper.update(o);
        }
    }
}
