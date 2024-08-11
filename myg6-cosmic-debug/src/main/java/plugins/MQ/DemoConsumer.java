package plugins.MQ;

import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.mq.MessageAcker;
import kd.bos.mq.MessageConsumer;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;

public class DemoConsumer implements MessageConsumer {
    Log log = LogFactory.getLog(getClass());
    @Override
    public void onMessage(Object message, String messageId, boolean resend, MessageAcker acker) {
        log.info("自定义DemoConsumer开始消费");
        try {
            System.out.println("i am here" + message);

            QFilter qFilter = new QFilter("billno", QCP.equals, "MQ8888888");
            DynamicObject obj = BusinessDataServiceHelper.loadSingle("myg6_mq", new QFilter[]{qFilter});
            obj.set("myg6_integerfield", (int) message);

            SaveServiceHelper.update(obj);
//            OperationResult result = SaveServiceHelper.saveOperate("myg6_mq", new DynamicObject[] {obj}, null);
            // ---------发消息通知用户申请成功或失败
            // --------MessageCenterServiceHelper.sendMessage(messageInfo)
        } catch (Throwable e) {
            boolean discard = false;
            if (discard){
                acker.discard(messageId);
            } else{
                acker.deny(messageId);
            }
        }
    }
}