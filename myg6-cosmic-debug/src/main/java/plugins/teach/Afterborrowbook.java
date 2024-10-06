package plugins.teach;

import kd.bos.dataentity.entity.DynamicObjectCollection;
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
        DynamicObject fatherDynamic = BusinessDataServiceHelper.loadSingle(businessKey, "myg6_borrowbook");
        String borrowBookName = fatherDynamic.getString("myg6_name");

        System.out.println("fuck you" + borrowBookName);

        String fields = "name,myg6_bookinfo,myg6_combo";
        QFilter[] filters = new QFilter[0];
        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book", fields, filters);

        for (DynamicObject obj : dys) {
            String bookName = obj.getString("name");
            if (bookName.equals(borrowBookName)) {
                System.out.println("fuckfuck");
                DynamicObjectCollection rows = obj.getDynamicObjectCollection("myg6_bookinfo");
                for (DynamicObject row: rows){
                    System.out.println("rowrow" + row);
                    if (row.getString("myg6_combo").equals("0")) {
                        row.set("myg6_combo", "1");
                        SaveServiceHelper.update(obj);
                        break;
                    }
                }
            }
        }

        IWorkflowPlugin.super.notify(execution);
    }
}