package plugins.MQ;

import kd.bos.bill.AbstractBillPlugIn;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.logging.Log;
import kd.bos.logging.LogFactory;

public class MulThreadsEdit extends AbstractBillPlugIn{
    Log Logger = LogFactory.getLog(MulThreadsEdit.class);
    @Override
    public void itemClick(ItemClickEvent evt) {
        if (evt.getItemKey().equals("myg6_concurrent")) {
            Runnable taskTemp = new BizCodeThread(false);
            LatchTest latchTest = new LatchTest();
            try {
                latchTest.startTaskAllInOnce(200, taskTemp);
            } catch (InterruptedException e) {
                Logger.error(e);
            }
        } else if (evt.getItemKey().equals("myg6_mq_concurrent")) {
            Runnable taskTemp = new BizCodeThread(true);
            LatchTest latchTest = new LatchTest();
            try {
                latchTest.startTaskAllInOnce(200, taskTemp);
            } catch (InterruptedException e) {
                Logger.error(e);
            }
        }
    }
}