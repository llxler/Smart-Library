package plugins.homepage;

import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.ext.form.control.Listbox;
import kd.bos.form.ShowType;
import kd.bos.form.control.Button;
import kd.bos.form.control.Control;
import kd.bos.form.control.Image;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.generator.common.cache.DistributeCache;
import kd.sdk.plugin.Plugin;
import kd.bos.form.FormShowParameter;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.sdk.plugin.Plugin;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.Label;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import static myg6.cosmic.debug.DebugApplication.MY_IP;

/**
 * 动态表单插件
 */
public class MiniShelfRender extends AbstractFormPlugin implements Plugin {
    public void registerListener(EventObject event) {
        // 注册监听
        super.registerListener(event);
        DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");


        Button button1 = this.getView().getControl("myg6_image1");
        button1.addClickListener(this);
        Button button2 = this.getView().getControl("myg6_image2");
        button2.addClickListener(this);
        Button button3 = this.getView().getControl("myg6_image3");
        button3.addClickListener(this);
        Button button4 = this.getView().getControl("myg6_image4");
        button4.addClickListener(this);
        Button button5 = this.getView().getControl("myg6_image5");
        button5.addClickListener(this);
        Button button6 = this.getView().getControl("myg6_image6");
        button6.addClickListener(this);
        Button button7 = this.getView().getControl("myg6_image7");
        button7.addClickListener(this);
        Button button8 = this.getView().getControl("myg6_image8");
        button8.addClickListener(this);
        Button button9 = this.getView().getControl("myg6_image9");
        button9.addClickListener(this);
    }
    public void click(EventObject evt) {
        super.click(evt);
        //获取被点击的控件对象
        Control source = (Control) evt.getSource();
        DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");
        //获取DynamicObject列表
        String fields = "name";
        // Create an empty filter array (no filters)
        QFilter[] filters = new QFilter[0];
        // Load the data
        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_list", fields, filters);


        String bookName = cache.get("bookSearch" + source.getKey().substring(10));
        System.out.println("jiba" + source.getKey().substring(10));

        String bookNameTrimmed = bookName.replaceAll("\\s+", "");
        QFilter qFilter = new QFilter("name", QCP.equals, bookNameTrimmed);


        DynamicObject single = null;
        for (DynamicObject result : dys) {
            if (result.getString("name").replaceAll("\\s+", "").equals(bookNameTrimmed)) {
                single = result;
                BillShowParameter billShowParameter = new BillShowParameter();
                billShowParameter.setFormId("myg6_book_list");
                billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
                billShowParameter.setStatus(OperationStatus.VIEW);
                Long pkId = (Long) single.getPkValue();
                billShowParameter.setPkId(pkId);
                this.getView().showForm(billShowParameter);
                break;
            }
        }




    }
    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);

        DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");
        int cnt = Integer.parseInt(cache.get("bookSearchCnt"));
        if (cnt >= 9) {
            cnt = 9;
        }
        for (int i = 1;i <= 9;i++) {
            this.addClickListeners("myg6_image" + i);
            if(i > cnt) {
                this.getView().setVisible(false, "myg6_image" + i);
            }
        }
        List<String> bookArray = new ArrayList<>();
        for(int i = 1;i <= cnt;i++) {
            System.out.println("motherfucker" + cache.get("bookSearch" + i));
            bookArray.add(cache.get("bookSearch" + i));
        }


        String fields = "name,myg6_picturefield";
        // Create an empty filter array (no filters)
        QFilter[] filters = new QFilter[0];
        // Load the data
        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_list", fields, filters);
        int index = 0;
        for (String v : bookArray) {
            index ++;
            for(DynamicObject single : dys) {
                if(single.getString("name").replaceAll("\\s+", "").equals(v.replaceAll("\\s+", ""))) {

                    String url = single.getString("myg6_picturefield");

                    // 设置图片
                    Image image = this.getView().getControl("myg6_image" + index);
                    image.setUrl("http://"+ MY_IP +":8881/ierp/attachment/downloadImage/" + url);
                    break;
                }
            }

        }

    }
}