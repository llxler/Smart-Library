package plugins.homepage;

import dm.jdbc.util.StringUtil;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.Label;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 动态表单插件
 */
public class GetSeatinfoSuper extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);

        // 获取DynamicObject列表
        String fields = "number,myg6_combofield";
        // Create an empty filter array   (no filters)
        QFilter[] filters = new QFilter[0];
        // Load the data
        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_seat", fields, filters);

        int sum = 0, count = 0;

        for (DynamicObject dy : dys) {
            String seatnumber = dy.getString("number");
            if (!seatnumber.startsWith("S0000")) continue;
            String state = dy.getString("myg6_combofield");
            if (StringUtil.equals(state, "0")) {
                count++;
            }
            sum++;
        }

        String title = "座位预约 " + count + "/" + sum + " 座";
        Label labeltitle = this.getView().getControl("myg6_labelap51");
        labeltitle.setText(title);
    }
}