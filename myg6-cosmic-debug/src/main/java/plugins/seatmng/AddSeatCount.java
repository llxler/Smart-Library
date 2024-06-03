package plugins.seatmng;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.db.DB;
import kd.bos.db.DBRoute;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.operate.Operations;
import kd.bos.entity.plugin.AbstractOperationServicePlugIn;
import kd.bos.entity.plugin.PreparePropertysEventArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;
import kd.bos.entity.plugin.args.AfterOperationArgs;

/**
 * 单据操作插件
 */
public class AddSeatCount extends AbstractOperationServicePlugIn implements Plugin {

    private final static String SEAT = "tk_myg6_seat_seats";

    private final static String ROOM = "myg6_room";

    private final static String ROOM_ID = "number";

    @Override
    public void onPreparePropertys(PreparePropertysEventArgs e) {
        //加载自习室id字段
        e.getFieldKeys().add(ROOM_ID);
    }

    @Override
    public void afterExecuteOperationTransaction(AfterOperationArgs e) {
        //座位表
        //获取DynamicObject列表
        String fields = "myg6_basedatafield";
        // Create an empty filter array (no filters)
        QFilter[] filters = new QFilter[0];
        // Load the data
        DynamicObject entity = BusinessDataServiceHelper.loadSingle("myg6_seat", fields, filters);
        System.out.println("fuckinfo: " + entity);
        while (true) {
            DynamicObject o = (DynamicObject)entity.get("myg6_basedatafield");
            System.out.println("fuck" + o);
            if (o == null) {
                continue;
            }
            Integer num = Integer.valueOf(o.getString("myg6_integerfield1")) + 1;
            System.out.println("fuck" + num);
            o.set("myg6_integerfield1", num.toString());
            SaveServiceHelper.update(o);
            if (o != null) {
                break;
            }
        }
    }
}
