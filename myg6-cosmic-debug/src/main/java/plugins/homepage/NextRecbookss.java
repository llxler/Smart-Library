package plugins.homepage;

import com.alibaba.druid.util.StringUtils;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.Button;
import kd.bos.form.control.Image;
import kd.bos.form.control.Label;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.sdk.plugin.Plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static myg6.cosmic.debug.DebugApplication.MY_IP;

/**
 * 动态表单插件
 */
public class NextRecbookss extends AbstractFormPlugin implements Plugin {

    private static final String CMD = "d://Code//Anaconda//python.exe";
    private static final String SCRIPT_PATH = "d://UseForRuanjianbei//Book-Recommendation//Implementation//main.py";

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Label button = this.getView().getControl("myg6_labelap9");
        button.addClickListener(this);
    }

    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            if (StringUtils.equals("myg6_labelap9", key)) {
                try {
                    // 使用 ProcessBuilder
                    ProcessBuilder processBuilder = new ProcessBuilder(CMD, SCRIPT_PATH);
                    processBuilder.directory(new File("d://UseForRuanjianbei//Book-Recommendation"));

                    // 启动进程
                    Process process = processBuilder.start();

                    // 读取标准输出和错误输出
                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                    BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

                    // 读取标准输出
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = stdInput.readLine()) != null) {
                        result.append(line).append("\n");
                    }

                    // 读取错误输出
                    StringBuilder error = new StringBuilder();
                    while ((line = stdError.readLine()) != null) {
                        error.append(line).append("\n");
                    }

                    // 等待进程完成
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        String output = result.toString();
                        List<Integer> bookId = extractBookIds(output);
                        render(bookId);
                        this.getView().showMessage("根据您的阅读习惯推荐如下: \n" + output);
                    } else {
                        this.getView().showMessage("命令执行失败，错误信息如下:\n" + error.toString());
                    }
                } catch (Exception exce) {
                    // 打印详细异常信息
                    exce.printStackTrace();
                    this.getView().showMessage("执行命令时发生异常：" + exce.getMessage());
                }
            }
        }
    }


    private static List<Integer> extractBookIds(String output) {
        List<Integer> bookId = new ArrayList<>();
        Pattern pattern = Pattern.compile("Recommended books for User 1: \\[(.*?)\\]");
        Matcher matcher = pattern.matcher(output);
        if (matcher.find()) {
            String[] ids = matcher.group(1).replace("'", "").split(", ");
            for (String id : ids) {
                bookId.add(Integer.parseInt(id));
            }
        }
        return bookId;
    }

    public void render(List<Integer> bookId) {
        String pic = "myg6_imageap", lb = "myg6_labelap";
        // 通过bookId查询对应的书籍信息
        // 获取DynamicObject列表
        String fields = "name,myg6_picturefield";
        // Create an empty filter array (no filters)
        QFilter[] filters = new QFilter[0];
        // Load the data
        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_list", fields, filters);

        int i = 0;
        for (int v : bookId) {
            ++i;
            if (i == 6) break;
            String nowPic = pic + i + i + i;
            String nowLb = lb + i + i + i;
            if (v >= dys.length) {
                continue;
            }
            DynamicObject single = dys[v];
            String bookName = single.getString("name");
            String url = single.getString("myg6_picturefield");
            // 设置标签
            Label labeltitle = this.getView().getControl(nowLb);
            labeltitle.setText(bookName);
            // 设置图片
            Image image = this.getView().getControl(nowPic);
            image.setUrl("http://" + MY_IP + ":8881/ierp/attachment/downloadImage/" + url);
        }
    }
}
