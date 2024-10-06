package plugins.MQ;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.mq.MQFactory;
import kd.bos.mq.MessagePublisher;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class MulThreadsEdit extends AbstractBillPlugIn{
    Log Logger = LogFactory.getLog(MulThreadsEdit.class);
    @Override
    public void itemClick(ItemClickEvent evt) {
        if (evt.getItemKey().equals("myg6_concurrent")) {
            // 普通处理
            for (int i = 1; i < 10000; i++) {
                QFilter qFilter = new QFilter("billno", QCP.equals, "MQ8888888");
                DynamicObject obj = BusinessDataServiceHelper.loadSingle("myg6_mq", new QFilter[]{qFilter});
                obj.set("myg6_integerfield", i);
                SaveServiceHelper.update(obj);
            }
        } else if (evt.getItemKey().equals("myg6_mq_concurrent")) {
            // 使用MQ处理
            MessagePublisher mp = MQFactory.get().createSimplePublisher("myg6_Intelligent_lib", "erkai_queue");
            for (int i = 1; i < 10000; i++) {
                mp.publish(i);
            }
        }
    }
}