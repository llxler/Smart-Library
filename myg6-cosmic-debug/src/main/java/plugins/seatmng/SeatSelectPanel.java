package plugins.seatmng;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.bill.BillShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.StyleCss;
import org.apache.commons.lang3.StringUtils;
import kd.bos.form.control.Control;
import java.util.EventObject;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

/**
 * 动态表单插件
 */
public class SeatSelectPanel extends AbstractFormPlugin implements Plugin {
    //定义标识
    private final String BLACK_SEAT = "myg6_black";
    private final String RED_SEAT = "myg6_red";
    private final String SEAT_APPLY = "myg6_seat_apply";

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        //注册工作交接安排控件的监听
        for(int i = 1;i <= 8;i++){
            this.addClickListeners(BLACK_SEAT + i);
            this.addClickListeners(RED_SEAT + i);
        }

    }

    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        for(int i = 1;i <= 8;i++){
            //这里的S00000是座位号的前缀，根据你在座位基础资料里的编码设置来修改
            String str = "S00000";
            QFilter qFilter = new QFilter("number", QCP.equals, str + i);
            //根据座位号查询座位基础资料
            DynamicObject goalSeat = BusinessDataServiceHelper.loadSingle("myg6_seat", new QFilter[]{qFilter});

            //判断座位是否被预定，然后设置座位的可见性
            if (goalSeat.getString("myg6_combofield").equals("1")) {
                this.getView().setVisible(false, BLACK_SEAT + i);
            } else{
                this.getView().setVisible(false, RED_SEAT + i);
            }
        }
    }

    @Override
    public void click(EventObject evt) {
        super.click(evt);
        //获取被点击的控件对象
        Control source = (Control) evt.getSource();
        if (source != null) {
            for(int i = 1;i <= 8;i++){
                //判断被点击的控件是哪个座位控件
                if (StringUtils.equals((BLACK_SEAT + i), source.getKey())) {

                    //设置子页面的参数
                    BillShowParameter parameter = new BillShowParameter();
                    parameter.setFormId(SEAT_APPLY);
                    parameter.getOpenStyle().setShowType(ShowType.Modal);
                    parameter.setCustomParam("myg6_basedatafield_seat","S00000" + i);

                    //设置弹出表单的样式，宽，高等
                    StyleCss styleCss = new StyleCss();
                    styleCss.setHeight("600");
                    styleCss.setWidth("900");
                    parameter.getOpenStyle().setInlineStyleCss(styleCss);
                    //弹出表单和本页面绑定
                    this.getView().showForm(parameter);

                    //将黑色座位设置为红色
                    this.getView().setVisible(false, BLACK_SEAT + i);
                    this.getView().setVisible(true, RED_SEAT + i);
                    break;
                } else if (StringUtils.equals(RED_SEAT + i, source.getKey())) {
                    //如果红色座位被点击，则提示该座位已被预定
                    this.getView().showMessage("该座位已被预定，请选择其他座位");
                }
            }
        }
    }
}