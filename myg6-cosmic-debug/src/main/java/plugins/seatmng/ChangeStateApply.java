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
public class ChangeStateApply extends AbstractOperationServicePlugIn implements Plugin {
    @Override
    public void onPreparePropertys(PreparePropertysEventArgs e) {
        // 加载自习室id字段
        e.getFieldKeys().add("myg6_basedatafield_seat");
    }

    @Override
    public void afterExecuteOperationTransaction(AfterOperationArgs e) {
        List<ExtendedDataEntity> rows = e.getSelectedRows();
        for (ExtendedDataEntity row : rows) {
            DynamicObject dataEntity = row.getDataEntity();
            DynamicObject o = (DynamicObject) dataEntity.get("myg6_basedatafield_seat");
            DynamicObject room = (DynamicObject) dataEntity.get("myg6_basedatafield");
            int seatNum = room.getInt("myg6_integerfield1");
            room.set("myg6_integerfield1", seatNum - 1);
            o.set("myg6_combofield", 1);
            SaveServiceHelper.update(o);
            SaveServiceHelper.update(room);
        }
    }
}