package plugins.homepage;

import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.Label;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;
import kd.bos.form.control.Image;
import static myg6.cosmic.debug.DebugApplication.MY_IP;

import java.util.*;

/**
 * 动态表单插件
 */
public class RecBookRandomly extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterBindData(EventObject e) {
        List<Integer> RecBookId = new ArrayList<>();
        super.afterBindData(e);
        // 获取DynamicObject列表
        String fields = "name,myg6_picturefield";
        // Create an empty filter array (no filters)
        QFilter[] filters = new QFilter[0];
        // Load the data
        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_list", fields, filters);
        // 生成随机数五个不同的数字放入列表bookId中
        DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");
        for (int i = 1; i <= 5; i++) {
            int random = (int) (Math.random() * dys.length);
            if (!RecBookId.contains(random)) {
                RecBookId.add(random);
            } else {
                i--;
                continue;
            }
            cache.put("recbook" + i, String.valueOf(random));
        }
        render(RecBookId);
    }
    public void render(List<Integer> bookId) {
        String pic = "myg6_imageap", lb = "myg6_labelap";
        // 通过bookId查询对应的书籍信息
        //获取DynamicObject列表
        String fields = "name,myg6_picturefield";
        // Create an empty filter array (no filters)
        QFilter[] filters = new QFilter[0];
        // Load the data
        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_list", fields, filters);

        int i = 0;
        for (int v : bookId) {
            ++i;
            String nowPic = pic + i + i + i;
            String nowLb = lb + i + i + i;
            DynamicObject single = dys[v];
            String bookName = single.getString("name");
            if (bookName.length() >= 9) {
                bookName = bookName.substring(0, 8) + "...";
            }
            String url = single.getString("myg6_picturefield");
            // 设置标签
            Label labeltitle = this.getView().getControl(nowLb);
            labeltitle.setText(bookName);
            // 设置图片
            Image image = this.getView().getControl(nowPic);
            image.setUrl("http://"+ MY_IP +":8881/ierp/attachment/downloadImage/" + url);
        }
    }
}