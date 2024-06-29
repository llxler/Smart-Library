package plugins.PersonalLibrary;

import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.workflow.api.AgentExecution;
import kd.bos.workflow.engine.extitf.IWorkflowPlugin;
import kd.bos.dataentity.entity.DynamicObject;
/**
 * 工作流插件
 */
public class Afterborrowbook implements IWorkflowPlugin {
    @Override
    public void notify(AgentExecution execution) {
        String businessKey = execution.getBusinessKey(); // 单据的BusinessKey(业务ID)
        DynamicObject fatherDynamic = BusinessDataServiceHelper.loadSingle(businessKey, "myg6_book_subscribe");

        // 修改图书订阅的状态
        fatherDynamic.set("myg6_billstatusfield", 1);
        SaveServiceHelper.update(fatherDynamic);

        DynamicObject midDynamic = (DynamicObject) fatherDynamic.get("myg6_nameofbook");
        String bookName = midDynamic.getString("name");

        QFilter qFilter = new QFilter("name", QCP.equals, bookName);
        DynamicObject goalDynamic = BusinessDataServiceHelper.loadSingle("myg6_book_list", new QFilter[]{qFilter});

        // modify the value
        Integer curnum = Integer.valueOf(goalDynamic.getString("myg6_curnum")) - 1;
        goalDynamic.set("myg6_curnum", curnum.toString());

        Integer subcribe_cnt = Integer.valueOf(goalDynamic.getString("myg6_subcribe_cnt")) + 1;
        goalDynamic.set("myg6_subcribe_cnt", subcribe_cnt.toString());

        SaveServiceHelper.update(goalDynamic);

        IWorkflowPlugin.super.notify(execution);
    }
}