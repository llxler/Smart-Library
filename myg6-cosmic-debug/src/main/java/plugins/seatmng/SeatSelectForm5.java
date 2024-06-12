package plugins.seatmng;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.bill.BillShowParameter;
import kd.bos.form.CloseCallBack;
import kd.bos.form.ShowType;
import kd.bos.form.StyleCss;
import org.apache.commons.lang3.StringUtils;
import kd.bos.form.control.Control;
import java.util.EventObject;

/**
 * 动态表单插件
 */



public class SeatSelectForm5 extends AbstractFormPlugin {

    //父页面的请假天数标识
    private final String KEY_LEAVE_DAYS = "leavedays";
    //子页面的标识
    private final String KEY_APPEND_BILL = "myg6_seat_apply";
    //父页面的工作交接安排标识
    private final String KEY_WORK_ARRANGE = "myg6_imageaps";
    private final String redseat = "myg6_imageapr";
    //父页面的备注信息标识
    private final String KEY_REMARK = "remark";
    //子页面的主键标识
    private final String KEY_PKID = "son_pkid";
    private  String KEY_PARAM = "S0000";
    /**
     * 修改字段值之后触发
     * 通知插件字段发生了改变，可以同步调整其他字段值
     * 特别说明：
     * 界面数据初始时，不触发此事件，即在afterCreateNewData事件中，修改字段值，不会触发此事件
     * @param e
     */

    /**
     * 用户与界面上的控件进行交互时，即会触发此事件。
     * 建议在此事件，侦听各个控件的插件事件。
     * @param e
     */
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        //注册工作交接安排控件的监听
        for(int i = 1;i <= 76;i++){
            this.addClickListeners(KEY_WORK_ARRANGE + i);
            this.addClickListeners(redseat + i);
        }

    }
    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        for(int i = 1;i <= 76;i++){
            String str = KEY_PARAM;
            if(i < 10) str += '0';
            QFilter qFilter = new QFilter("number", QCP.equals, str + i);
            DynamicObject goalSeat = BusinessDataServiceHelper.loadSingle("myg6_seat", new QFilter[]{qFilter});
            if (goalSeat == null) {
                this.getView().setVisible(false, KEY_WORK_ARRANGE + i);
                System.out.println("goalSeat is null");
                continue;
            }
            if (goalSeat.getString("myg6_combofield").equals("1")) {
                this.getView().setVisible(false, KEY_WORK_ARRANGE + i);
            } else{
                this.getView().setVisible(false, redseat + i);
            }
        }
    }
    @Override
    public void click(EventObject evt) {
        super.click(evt);
        //获取被点击的控件对象
        Control source = (Control) evt.getSource();
        System.out.println("shit" + source.getKey());
        if (source != null) {
            for(int i = 1;i <= 76;i++){
                if (StringUtils.equals(KEY_WORK_ARRANGE + i, source.getKey())) {
                    //当工作交接安排控件被点击的时候，创建弹出表单页面的对象
                    BillShowParameter billShowParameter = new BillShowParameter();
                    //设置弹出表单页面的标识
                    billShowParameter.setFormId(KEY_APPEND_BILL);
                    //设置弹出表单页面数据的主键id
                    //billShowParameter.setPkId(this.getModel().getValue(KEY_PKID));
                    //设置弹出表单页面的标题
                    billShowParameter.setCaption("请补充座位预定时间段~");
                    //设置弹出表单页面的打开方式
                    billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
                    //设置弹出表单页面的关闭回调对象，使用本插件，标识为常量KEY_WORK_ARRANGE对应的值
                    billShowParameter.setCloseCallBack(new CloseCallBack(this, KEY_WORK_ARRANGE));
                    //设置弹出表单页面的座位号参数

                    if (i < 10) KEY_PARAM += '0';
                    System.out.println(KEY_PARAM + i);
                    billShowParameter.setCustomParam("myg6_basedatafield_seat",KEY_PARAM + i);

                    StyleCss styleCss = new StyleCss();
                    styleCss.setHeight("600");
                    styleCss.setWidth("900");
                    //设置弹出表单的样式，宽，高等
                    billShowParameter.getOpenStyle().setInlineStyleCss(styleCss);
                    //弹出表单和本页面绑定
                    this.getView().showForm(billShowParameter);
                    this.getView().setVisible(false, KEY_WORK_ARRANGE + i);
                    this.getView().setVisible(true, redseat + i);
                    break;
                } else if (StringUtils.equals(redseat + i, source.getKey())) {
                    this.getView().showMessage("该座位已被预定，请选择其他座位");
                }
            }

        }
    }
}
