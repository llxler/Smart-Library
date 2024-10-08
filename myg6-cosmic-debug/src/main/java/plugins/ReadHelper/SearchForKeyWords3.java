package plugins.ReadHelper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.Button;
import kd.bos.form.control.events.BeforeClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.sdk.plugin.Plugin;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventObject;

/**
 * 动态表单插件
 */
public class SearchForKeyWords3 extends AbstractFormPlugin implements Plugin {

    private static final String cmdin = "d://Code//Anaconda//python.exe D:\\UseForRuanjianbei\\keyword_search\\main.py ";
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Button button = this.getView().getControl("myg6_keyword");
        button.addClickListener(this);
    }

    @Override
    public void beforeClick(BeforeClickEvent evt) {
        super.beforeClick(evt);
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
                    Process process = Runtime.getRuntime().exec(cmdin + keyword);
                    System.out.println("fuckyou" + cmdin + keyword);

                    // 等待进程完成
                    process.waitFor();
                    System.out.println("eat shit");
                    // 读取进程的输出流
                    BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream(), "UTF-8"));
                    StringBuilder ss = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        ss.append(line);
                    }
                    in.close();
                    String result = ss.toString();
                    JSONArray jsonArray = JSONArray.parseArray(result);
                    System.out.println("search result: " + jsonArray.toJSONString());
                    System.out.println("asshole");
                    // 组装 HTML
                    StringBuilder htmlBuilder = new StringBuilder();
                    htmlBuilder.append("<!DOCTYPE html>\n");
                    htmlBuilder.append("<html lang=\"zh-CN\">\n");
                    htmlBuilder.append("<head>\n");
                    htmlBuilder.append("<meta charset=\"UTF-8\">\n");
                    htmlBuilder.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
                    htmlBuilder.append("<title>搜索结果</title>\n");
                    htmlBuilder.append("<style>\n");
                    htmlBuilder.append("body { font-family: Arial, sans-serif; margin: 5px; }\n");
                    htmlBuilder.append(".result { display: block; text-decoration: none; color: inherit; margin-bottom: 20px; margin: 5px; padding: 10px; width:500px; border: 1px solid #ddd; border-radius: 5px; transition: background-color 0.3s; }\n");
                    htmlBuilder.append(".result:hover { background-color: #f0f0f0; }\n");
                    htmlBuilder.append(".image-row { display: flex; align-items: center; justify-content: space-between; margin-bottom: 5px; }\n");
                    htmlBuilder.append(".image-row .title { font-size: 18px; font-weight: bold; margin-right: 10px; }\n");
                    htmlBuilder.append(".image-row img { max-height: 100px; max-width: 100px; object-fit: cover; }\n");
                    htmlBuilder.append(".icon-row { display: flex; align-items: center; }\n");
                    htmlBuilder.append(".icon { width: 16px; height: 16px; margin-right: 5px; }\n");
                    htmlBuilder.append(".link { font-size: 12px; color: #666;max-width:485px; word-wrap: break-word; overflow-wrap: break-word; }\n");
                    htmlBuilder.append("</style>\n");
                    htmlBuilder.append("</head>\n");
                    htmlBuilder.append("<body>\n");

                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject item = jsonArray.getJSONObject(i);
                        String title = item.getString("title");
                        String link = item.getString("link");
                        String iconLink = item.getString("icon_link");
                        String imageLink = item.getString("img_link");

                        htmlBuilder.append("<a class=\"result\" href=\"").append(link).append("\">\n");
                        htmlBuilder.append("<div class=\"image-row\">\n");
                        htmlBuilder.append("<div class=\"title\">").append(title).append("</div>\n");
                        htmlBuilder.append("<img src=\"").append(imageLink).append("\" alt=\"Image\" onerror=\"this.style.display='none'\">\n");
                        htmlBuilder.append("</div>\n");
                        htmlBuilder.append("<div class=\"icon-row\">\n");
                        htmlBuilder.append("<img class=\"icon\" src=\"").append(iconLink).append("\" onerror=\"this.src='//th.bing.com/th?id=ODLS.A2450BEC-5595-40BA-9F13-D9EC6AB74B9F&amp;w=32&amp;h=32&amp;qlt=90&amp;pcl=fffffa&amp;o=6&amp;pid=1.2'\">\n");
                        htmlBuilder.append("<span class=\"link\">").append(link).append("</span>\n");
                        htmlBuilder.append("</div>\n");
                        htmlBuilder.append("</a>\n");
                    }

                    htmlBuilder.append("</body>\n");
                    htmlBuilder.append("</html>\n");

                    String htmlString = htmlBuilder.toString();


                    System.out.println("htmlString" + htmlString); // 输出生成的HTML代码

                    // 获取当前时间，我们可以在isv文件夹中根据时间来对相应的文件夹创建HTML文件
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    String directoryName = simpleDateFormat.format(new Date());
                    // 获取一个10位的随机文件名称
                    StringBuffer sb = new StringBuffer();
                    for (int i = 1; i <= 10; ++i) {
                        int ascii = 48 + (int) (Math.random() * 9);
                        char c = (char) ascii;
                        sb.append(c);
                    }
                    File directoryGetQing = new File(System.getProperty("JETTY_WEBRES_PATH") + "/isv/gptQing", directoryName);
                    // 如果文件夹不存在就创建文件夹
                    if (!directoryGetQing.exists()) {
                        directoryGetQing.mkdirs();
                    }
                    File targetFile = new File(System.getProperty("JETTY_WEBRES_PATH") + "/isv/gptQing/" + directoryName, sb + ".html");
                    if (!targetFile.exists()) {
                        try {
                            targetFile.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    byte[] bytes = htmlString.getBytes();
                    try {
                        FileOutputStream fos = new FileOutputStream(targetFile);
                        fos.write(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //this.getView().showMessage("关键字联网搜寻: \n" + result);
                    // 获取缓存
                    DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");
                    cache.put("keyword", keyword);
                    cache.put("htmlurl",System.getProperty("domain.contextUrl") + "/isv/gptQing/" + directoryName + "/" + sb + ".html?" + "gaiIframeSize={\"height\":500,\"width\":540}");
                    // gpt 呼出
                    // --------传入的json配置--------
                    // process-24071613FA0518
                    String id = getProcessFid("process-24071613FA0518");
                    JSONObject need = new JSONObject();
                    need.put("showIcon", true);
                    need.put("switchSide", true);
                    need.put("lockSide", false);
                    need.put("selectedProcessId", id);
                    need.put("startProcessId", id);
                    JSONObject startParams = new JSONObject();
                    startParams.put("txtResult", "我是无用的");
                    need.put("startParams", startParams);
                    // --------传入的json配置--------
                    String pageId = this.getView().getMainView().getPageId();
                    // 呼出gpt
                    DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService", "sideBarInit", pageId, need.toJSONString());

                } catch (Exception exce) {
                    exce.printStackTrace();
                }
            }
        }
    }
    public String getProcessFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_process",
                "number," +
                        "id",
                (new QFilter("number", QCP.equals, billNo)).toArray());
        long idd = dynamicObject.getLong("id");
        return String.valueOf(idd);
    }
}
