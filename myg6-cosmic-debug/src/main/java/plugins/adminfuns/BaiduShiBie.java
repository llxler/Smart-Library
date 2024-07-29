package plugins.adminfuns;

import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.EventObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 命令行代码: java -Dimg_url="d:\rjbimage.jpg" -jar d:\UseForRuanjianbei\ImageRec\Final.jar
public class BaiduShiBie extends AbstractFormPlugin implements Plugin {
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

            // 开始填入信息
            // 在这里等待一段时间，等待百度识别完成
            try {
                Thread.sleep(5000);
            } catch (InterruptedException exce) {
                exce.printStackTrace();
            }

            // 打开电脑中的文件D:\isbndata.txt将文件里面的内容赋值给String s
            String filePath = "D:\\isbndata.txt";
            String title = "";
            String author = "";
            String publisher = "";
            String isbn = "";
            String keyword = "";
            String type = "";
            String summary = "";
            String img = "";

            try {
                // 使用指定的编码读取文件内容，假设文件是以 GBK 编码保存的
                String content = new String(Files.readAllBytes(Paths.get(filePath)), Charset.forName("GBK"));

                // 定义正则表达式模式
                Pattern pattern = Pattern.compile("title:(.*?) author:(.*?) publisher:(.*?) isbn:(.*?) keyword:(.*?) type:(.*?) summary:(.*?) img:(.*)");
                Matcher matcher = pattern.matcher(content);
                if (matcher.find()) {
                    title = matcher.group(1).trim();
                    author = matcher.group(2).trim();
                    publisher = matcher.group(3).trim();
                    isbn = matcher.group(4).trim();
                    keyword = matcher.group(5).trim();
                    type = matcher.group(6).trim();
                    summary = matcher.group(7).trim();
                    img = matcher.group(8).trim();
                }
            } catch (IOException exce) {
                exce.printStackTrace();
            }

            // 设置值
            this.getModel().setValue("name", title);
            this.getModel().setValue("myg6_author", author);
            this.getModel().setValue("myg6_publisher", publisher);
            this.getModel().setValue("myg6_isbn", isbn);
            this.getModel().setValue("myg6_abstract", summary);

            // 特殊处理
            if (type == null) {
                if (keyword != null) {
                    this.getModel().setValue("myg6_textfield", keyword);
                }
            } else {
                this.getModel().setValue("myg6_textfield", type);
            }

            // 将图片保存到本地D盘,名字为rjb封面
            if (!img.isEmpty()) {
                try {
                    URL imageUrl = new URL(img); // 将字符串 URL 转换为 URL 对象
                    String fileName = "rjb封面.jpg"; // 设置文件名

                    // 下载图片到本地
                    Path destinationPath = Paths.get("D:\\" + fileName);
                    Files.copy(imageUrl.openStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                    System.out.println("Image saved to D:\\" + fileName);

                    this.getModel().setValue("myg6_picturefield", "D:\\" + fileName);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            // 填入over
        }
    }
}
