package plugins.MQ;

import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.mq.MQFactory;
import kd.bos.mq.MessagePublisher;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;

public class BizCodeThread implements Runnable {
    private boolean isUseMQ = false;

    public BizCodeThread(boolean isUseMQ) {
        super();
        this.isUseMQ = isUseMQ;
    }

    @Override
    public void run() {
        if (isUseMQ) {
            MessagePublisher mp = MQFactory.get().createSimplePublisher("kded_tc", "erkai_queue");
            try {
                mp.publish("hello");
            } finally{
                mp.close();
            }
        } else {
            DynamicObject obj = BusinessDataServiceHelper.newDynamicObject("myg6_mq");
            obj.set("billstatus", "A");
            obj.set("myg6_textfield", "17299999999");
            OperationResult result = OperationServiceHelper.executeOperate("submit", "myg6_mq", new DynamicObject[] {obj}, OperateOption.create());
            //---------发消息通知用户申请成功或失败
            //--------MessageCenterServiceHelper.sendMessage(messageInfo)
        }
    }
}