package plugins.homepage;

import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.Label;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

import java.util.*;
/**
 * 动态表单插件
 */
public class ActivityRendering16 extends AbstractFormPlugin implements Plugin {
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        //todo: 父页面传参部分
        //FormShowParameter showParameter = this.getView().getFormShowParameter();
       //String seatId = showParameter.getCustomParam("myg6_basedatafield_seat");
        //if (seatId == null) return;
        //获取DynamicObject列表

        String fields = "creator,myg6_title,myg6_datefield,myg6_viewcount,myg6_largetextfield1,myg6_largetextfield2,myg6_largetextfield3,myg6_largetextfield4,myg6_picturefield1,myg6_picturefield2,myg6_picturefield3";
        //String fields = "creator,myg6_title,myg6_datefield,myg6_viewcount,myg6_largetextfield1,myg6_picturefield1";
        // Create an empty filter array (no filters)
        QFilter[] filters = new QFilter[0];
        // Load the data

        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_avtivity_pub", fields, filters);
        int i = 0;
        for (DynamicObject dy : dys) {
            if(i == 1)  break;
            String title = dy.getString("myg6_title");
            String date = dy.getString("myg6_datefield");
            DynamicObject creatorObj = (DynamicObject) dy.get("creator");
            String creator = creatorObj.getString("name");
            Integer view_cnt = Integer.valueOf(dy.getString("myg6_viewcount")) + 1;
            dy.set("myg6_viewcount", view_cnt.toString());
            String largetextfield1 = dy.getString("myg6_largetextfield1");
            String largetextfield2 = dy.getString("myg6_largetextfield2");
            String largetextfield3 = dy.getString("myg6_largetextfield3");
            String largetextfield4 = dy.getString("myg6_largetextfield4");
            String picturefield1 = dy.getString("myg6_picturefield1");
            String picturefield2 = dy.getString("myg6_picturefield2");
            String picturefield3 = dy.getString("myg6_picturefield3");
            SaveServiceHelper.update(dy);
            System.out.println("maple到此一游");
            Label labeltitle = this.getView().getControl("myg6_labeltitle");
            labeltitle.setText(title);
            Label labeldate = this.getView().getControl("myg6_labeldate");
            labeldate.setText("发布时间:" + date.substring(0, 10));
            Label labelcreator = this.getView().getControl("myg6_labecreator");
            labelcreator.setText("作者:" + creator);
            Label labelviewtimes = this.getView().getControl("myg6_labelviewtimes");

            labelviewtimes.setText("浏览次数:" + view_cnt);
//            文字部分赋值
            if (!largetextfield1.isEmpty()) {

                this.getModel().setValue("myg6_textfield1", largetextfield1);
            }
            else{
                this.getView().setVisible(false, "myg6_labelapt1");
                this.getView().setVisible(false, "myg6_textfield1");
            }
            if (!largetextfield2.isEmpty()) {

                this.getModel().setValue("myg6_textfield2", largetextfield2);
            }
            else{
                this.getView().setVisible(false, "myg6_labelapt2");
                this.getView().setVisible(false, "myg6_textfield2");
            }
            if (!largetextfield3.isEmpty()) {

                this.getModel().setValue("myg6_textfield3", largetextfield3);
            }
            else{
                this.getView().setVisible(false, "myg6_labelapt3");
                this.getView().setVisible(false, "myg6_textfield3");
            }
            if (!largetextfield4.isEmpty()) {

                this.getModel().setValue("myg6_textfield4", largetextfield4);
            }
            else{
                this.getView().setVisible(false, "myg6_labelapt4");
                this.getView().setVisible(false, "myg6_textfield4");
            }
            //图片赋值
            if (!picturefield1.isEmpty()) {
                this.getModel().setValue("myg6_picturefield1", picturefield1);
            }
            else{
                this.getView().setVisible(false, "myg6_picturefield1");
            }
            if (!picturefield2.isEmpty()) {
                this.getModel().setValue("myg6_picturefield2", picturefield2);
            }
            else{
                this.getView().setVisible(false, "myg6_picturefield2");
            }
            if (!picturefield3.isEmpty()) {
                this.getModel().setValue("myg6_picturefield3", picturefield3);
            }
            else{
                this.getView().setVisible(false, "myg6_picturefield3");
            }
            i++;
        }

    }
}