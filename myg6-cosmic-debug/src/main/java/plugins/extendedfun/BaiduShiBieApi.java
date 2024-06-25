package plugins.extendedfun;

import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

// 命令行代码: java -Dimg_url="d:\rjbimage.jpg" -jar d:\UseForRuanjianbei\ImageRec\Final.jar
public class BaiduShiBieApi extends AbstractFormPlugin implements Plugin {
    private static final String cmdin = "java -Dimg_url=\"d:\\rjbimage.jpg\" -jar d:\\UseForRuanjianbei\\ImageRec\\Final.jar";

    @Override
    public void registerListener(EventObject e) {
        // 注册点击事件
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }

    @Override
    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        if (e.getItemKey().equalsIgnoreCase("myg6_baritemap1")) {
            // 在命令行里面执行命令 cmdin
            try {
                Runtime.getRuntime().exec(cmdin);
            } catch (Exception exce) {
                exce.printStackTrace();
            }
            // 发送执行成功
            this.getView().showMessage("百度识别执行结束!");
        }
    }
}
