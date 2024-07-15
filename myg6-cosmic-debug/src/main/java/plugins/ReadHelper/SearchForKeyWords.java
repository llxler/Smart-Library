package plugins.ReadHelper;

import kd.bos.form.control.Button;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.EventObject;

/**
 * 动态表单插件
 */
public class SearchForKeyWords extends AbstractFormPlugin implements Plugin {

    private static final String cmdin = "python D:\\UseForRuanjianbei\\keyword_search\\main.py ";
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Button button = this.getView().getControl("myg6_keyword");
        button.addClickListener(this);
    }

    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            if (StringUtils.equals("myg6_keyword", key)) {
                String keyword = (String) this.getModel().getValue("myg6_keytxt");
                System.out.println("keyword: " + keyword);
                if (StringUtils.isBlank(keyword)) {
                    this.getView().showMessage("请输入关键字");
                    return;
                }
                // 在命令行里面执行命令 cmdin
                try {
                    Runtime.getRuntime().exec(cmdin + keyword);
                    // 过一会儿再接受返回的数据
                    Thread.sleep(5000);
                    BufferedReader in = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(cmdin + keyword).getInputStream(), "UTF-8"));
                    String line = null, result = "";
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                        result += line + "\n";
                    }
                    this.getView().showMessage("关键字联网搜寻: \n" + result);
                } catch (Exception exce) {
                    exce.printStackTrace();
                }

            }
        }
    }
}
