package plugins.ReadHelper;

import com.alibaba.druid.util.StringUtils;
import com.twelvemonkeys.io.FileUtil;
import kd.bos.form.control.Button;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;
import kd.bos.ext.form.control.Markdown;

import java.util.EventObject;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 动态表单插件
 */
public class DownLoadMd extends AbstractFormPlugin implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Button button = this.getView().getControl("myg6_dlnote");
        button.addClickListener(this);
    }

    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            if (StringUtils.equals("myg6_dlnote", key)) {
                Markdown mk = this.getView().getControl("myg6_note");
                String md = mk.getText();
                if (md == null) {
                    this.getView().showMessage("笔记为空，无法下载");
                    return;
                }
                // 另存为本地文件 D:\我的读书笔记.md
                String path = "D:\\Desktop\\我的读书笔记.md";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
                    writer.write(md);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
