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
public class AfterBorrowBook implements IWorkflowPlugin {
    @Override
    public void notify(AgentExecution execution) {
        String businessKey = execution.getBusinessKey();//单据的BusinessKey(业务ID)
        DynamicObject fatherDynamic = BusinessDataServiceHelper.loadSingle(businessKey, "myg6_book_subscribe");
        DynamicObject goalDynamic = (DynamicObject) fatherDynamic.get("myg6_nameofbook");

        // modify the value
        Integer curnum = Integer.valueOf(goalDynamic.getString("myg6_curnum")) - 1;
        goalDynamic.set("myg6_curnum", curnum.toString());
        Integer subcribe_cnt = Integer.valueOf(goalDynamic.getString("myg6_subcribe_cnt")) + 1;
        goalDynamic.set("myg6_subcribe_cnt", subcribe_cnt.toString());

        System.out.println("goalDynamic Info: " + goalDynamic);

        SaveServiceHelper.update(goalDynamic);

        IWorkflowPlugin.super.notify(execution);
    }
}