package plugins.seatmng.Alert;

import kd.bos.data.BusinessDataWriter;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.DynamicObjectCollection;
import kd.bos.entity.EntityMetadataCache;
import kd.bos.entity.MainEntityType;
import kd.bos.entity.earlywarn.EarlyWarnContext;
import kd.bos.entity.earlywarn.warn.plugin.IEarlyWarnWriteOut;
import kd.bos.servicehelper.BusinessDataServiceHelper;

public class WriteOut implements IEarlyWarnWriteOut {
    @Override
    public void writeOut(DynamicObjectCollection list, EarlyWarnContext context) {
        if (list.isEmpty()) {
            return;
        }
        DynamicObject[] saveArr = new DynamicObject[list.size()];
        for (int i = 0; i < list.size(); i++) {
            DynamicObject obj = list.get(i);
            DynamicObject newObj = BusinessDataServiceHelper.newDynamicObject("myg6_seat_apply");
            newObj.set("number", obj.get("number"));
            saveArr[i] = newObj;
        }

        MainEntityType entityType = EntityMetadataCache.getDataEntityType("myg6_seat_apply");
        BusinessDataWriter.save(entityType, saveArr);
    }
}
