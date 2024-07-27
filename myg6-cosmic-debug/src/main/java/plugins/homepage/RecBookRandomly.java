package plugins.homepage;

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
        super.afterBindData(e);
        // 生成随机数五个不同的数字放入列表bookId中
        List<Integer> bookId = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            int random = (int) (Math.random() * 15);
            if (!bookId.contains(random)) {
                bookId.add(random);
            } else {
                i--;
            }
        }
        render(bookId);
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