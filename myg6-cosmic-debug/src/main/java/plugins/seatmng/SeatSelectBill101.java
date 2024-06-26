package plugins.seatmng;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.form.FormShowParameter;
import kd.bos.form.events.AfterDoOperationEventArgs;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.EventObject;

import kd.bos.form.plugin.AbstractFormPlugin;

/**
 * 动态表单插件
 */

public class SeatSelectBill101 extends AbstractFormPlugin {

    //子页面的申请人标识
    private final String KEY_CREATOR = "creator";
    //子页面的所属部门标识
    private final String KEY_ORG = "org";
    //子页面的请假天数标识
    private final String KEY_LEAVE_DAYS = "leavedays";
    //子页面的工作交接安排标识
    private final String KEY_WORK_ARRANGE = "workarrange";
    //子页面的备注信息标识
    private final String KEY_REMARK = "remark";
    //子页面的主键标识
    private final String KEY_PKID = "son_pkid";
    //页面保存操作
    private final String KEY_SAVE_OPERATION = "save";

    /**
     * 界面初始化或刷新，新建表单数据包成功，并给字段填写了默认值之后，触发此事件；
     * 插件可以在此事件，重设字段的默认值。
     * 部分字段的默认值难以通过设计器配置出来，如需要计算的值、根据系统参数选项决定的值，必须写插件实现。
     * @param e
     */
    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        //获取父页面的数据模型
        FormShowParameter showParameter = this.getView().getFormShowParameter();

        // 获取座位基础资料
        String seatId = showParameter.getCustomParam("myg6_basedatafield_seat");
        if (seatId == null) return;
        QFilter qFilter = new QFilter("number", QCP.equals, seatId);
        DynamicObject goalSeat = BusinessDataServiceHelper.loadSingle("myg6_seat", new QFilter[]{qFilter});
        DynamicObject goalRoom = (DynamicObject) goalSeat.get("myg6_basedatafield");

        IDataModel parentModel = this.getView().getParentView().getModel();
        //给子页面相关属性赋值
        this.getModel().setValue("myg6_basedatafield_seat", goalSeat);
        this.getModel().setValue("myg6_basedatafield", goalRoom);

//        this.getModel().setValue(KEY_ORG, parentModel.getValue(KEY_ORG));
//        this.getModel().setValue(KEY_LEAVE_DAYS, parentModel.getValue(KEY_LEAVE_DAYS));
    }
    /**
     * 用户点击按钮、菜单，执行完绑定的操作后，不论成功与否，均会触发此事件；
     * 插件可以在此事件，根据操作结果控制界面。
     * 特别说明：
     * 这个事件，是在表单界面层执行的，没有事务保护。
     * 不允许在此事件同步修改数据库数据，以免同步失败导致数据不一致。
     * 这里使用了监控save操作，也可以监控按钮点击事件或者工具栏按钮点击事件
     *
     * @param e
     */
    @Override
    public void afterDoOperation(AfterDoOperationEventArgs e) {
        super.afterDoOperation(e);
        //如果执行了save操作
        if (StringUtils.equals(KEY_SAVE_OPERATION, e.getOperateKey())) {
            //并且save操作的结果是success
            if (e.getOperationResult().isSuccess()) {
                //那么就组装子页面数据返回给父页面,最后关闭子页面
                //HashMap<String, String> map = new HashMap<>();
                //map.put(KEY_WORK_ARRANGE, String.valueOf(this.getModel().getValue(KEY_WORK_ARRANGE)));
                //map.put(KEY_REMARK, String.valueOf(this.getModel().getValue(KEY_REMARK)));
                //map.put(KEY_PKID, String.valueOf(this.getModel().getDataEntity().getPkValue()));
                //this.getView().returnDataToParent(map);
                //this.getView().close();
            } else {
                // 如果save操作失败则弹窗提示
                this.getView().showErrMessage("保存数据失败，请重试或联系管理员~", "数据保存失败");
            }
        }
    }
}

