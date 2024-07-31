package plugins.MQ;

import kd.bos.dataentity.OperateOption;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.operate.result.OperationResult;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;
import kd.bos.mq.MessageAcker;
import kd.bos.mq.MessageConsumer;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.OperationServiceHelper;

public class DemoConsumer implements MessageConsumer {
    Log log = LogFactory.getLog(getClass());
    @Override
    public void onMessage(Object message, String messageId, boolean resend, MessageAcker acker) {
        log.info("自定义DemoConsumer开始消费");
        try {
            DynamicObject obj = BusinessDataServiceHelper.newDynamicObject("myg6_mq");
            obj.set("billstatus", "A");
            obj.set("myg6_textfield", "17299999999");
            OperationResult result = OperationServiceHelper.executeOperate("submit", "myg6_mq", new DynamicObject[] {obj}, OperateOption.create());
            // ---------发消息通知用户申请成功或失败
            // --------MessageCenterServiceHelper.sendMessage(messageInfo)
        } catch (Throwable e) {
            boolean discard = false; // 是否废弃这条消息，根据具体场景判断
            if (discard){
                acker.discard(messageId);// 废弃
                // 记录废弃原因，并写业务日志
            } else{
                acker.deny(messageId);// 告诉mq重发这条消息
                // 记录异常原因，并写业务日志
            }
        }
    }

}