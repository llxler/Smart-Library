package plugins.homepage;

import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.ShowType;
import kd.bos.form.control.Button;
import kd.bos.form.control.Control;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;
import org.apache.commons.lang3.StringUtils;

import java.util.EventObject;

public class ShowRecInbooklist extends AbstractFormPlugin implements Plugin {
    private static final String pic = "myg6_imageap";
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        // 添加按钮监听
        Button button1 = this.getView().getControl("myg6_imageap111");
        button1.addClickListener(this);
        Button button2 = this.getView().getControl("myg6_imageap222");
        button2.addClickListener(this);
        Button button3 = this.getView().getControl("myg6_imageap333");
        button3.addClickListener(this);
        Button button4 = this.getView().getControl("myg6_imageap444");
        button4.addClickListener(this);
        Button button5 = this.getView().getControl("myg6_imageap555");
        button5.addClickListener(this);
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
        for (int i = 1; i <= 5; ++i) {
            String picName = pic + i + i + i;
            if (StringUtils.equals(picName, source.getKey())) {
                String bookId = cache.get("recbook" + i);
                // 通过bookId查询对应的书籍信息
                DynamicObject single = dys[Integer.parseInt(bookId)];
                BillShowParameter billShowParameter = new BillShowParameter();
                billShowParameter.setFormId("myg6_book_list");
                billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
                billShowParameter.setStatus(OperationStatus.VIEW);
                Long pkId = (Long) single.getPkValue();
                billShowParameter.setPkId(pkId);
                this.getView().showForm(billShowParameter);
            }
        }
    }
}