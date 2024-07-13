package plugins.ReadHelper;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.Button;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.bos.form.control.Html;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 动态表单插件
 */
public class ReadGpt extends AbstractFormPlugin implements Plugin {
    private final String htmlCode1 = "<!DOCTYPE html>\n" +
            "    <html lang=\"en\">\n" +
            "    <head>\n" +
            "        <meta charset=\"UTF-8\">\n" +
            "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "        <title>图书阅读页面</title>\n" +
            "        <style>\n" +
            "            body {\n" +
            "                font-family: Arial, sans-serif;\n" +
            "                margin: 0;\n" +
            "                padding: 20px;\n" +
            "                background-color: #f5f5f5;\n" +
            "            }\n" +
            "\n" +
            "            h1 {\n" +
            "                font-size: 24px;\n" +
            "                margin-bottom: 10px;\n" +
            "            }\n" +
            "            .content {\n" +
            "                padding: 30px;\n" +
            "                font-size: 18px;\n" +
            "                line-height: 1.6;\n" +
            "            }\n" +
            "        </style>\n" +
            "    </head>\n" +
            "    <body>\n" +
            "            <div class=\"content\">\n" +
            "                <p>";
    private final String htmlCode2 = " </p>\n" +
            "            </div>\n" +
            "\n" +
            "    </body>\n" +
            "    </html>";
    private String bookName;

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        // 添加按钮监听
        Button button1 = this.getView().getControl("myg6_startread");
        Button button2 = this.getView().getControl("myg6_conclude");
        Button button3 = this.getView().getControl("myg6_keyword");
        Button button4 = this.getView().getControl("myg6_showai1");
        Button button5 = this.getView().getControl("myg6_showai2");
        Button button6 = this.getView().getControl("myg6_showai3");
        Button button7 = this.getView().getControl("myg6_forward");
        Button button8 = this.getView().getControl("myg6_backward");

        // 监听
        button1.addClickListener(this);
        button2.addClickListener(this);
        button3.addClickListener(this);
        button4.addClickListener(this);
        button5.addClickListener(this);
        button6.addClickListener(this);
        button7.addClickListener(this);
        button8.addClickListener(this);
    }

    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();

        // 取出一些前置必要信息
        String pageId = this.getView().getMainView().getPageId();
        // Object pkValue = Long.parseLong("1992276822133310464");
        // 获取图书名字
        DynamicObject book = (DynamicObject) this.getModel().getValue("myg6_bookname");
        bookName = book.getString("name");
        // 获取缓存
        DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");

        // 开始书写按钮的业务逻辑
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();

            // 若是渲染界面按钮
            if (StringUtils.equals("myg6_startread", key)) {
                // 处理字符串给缓存
                solve();
                // 将当前pageId 标记为第0面
                cache.put("pageId", "0");
                render(0);
            }

            // 若是前进后退按钮
            if (StringUtils.equals("myg6_forward", key)) {
                String pg = cache.get("pageId");
                int pgInt = Integer.parseInt(pg) + 1;
                String nxtPg = String.valueOf(pgInt);
                if (StringUtils.equals(nxtPg, cache.get("allpage"))) {
                    this.getView().showMessage("已经是最后一页了");
                    return;
                }
                cache.put("pageId", nxtPg);
                render(pgInt);
            } else if (StringUtils.equals("myg6_backward", key)) {
                String pg = cache.get("pageId");
                int pgInt = Integer.parseInt(pg) - 1;
                if (pgInt < 0) {
                    this.getView().showMessage("已经是第一页了");
                    return;
                }
                cache.put("pageId", String.valueOf(pgInt));
                render(pgInt);
            }

            // 若是解析
            for (int i = 1; i <= 3; ++i) {
                String nowKey = "myg6_showai" + i;
                if (StringUtils.equals(nowKey, key)) {
                    // --------传入的json配置--------
                    JSONObject result = new JSONObject();
                    result.put("showIcon", true);
                    result.put("switchSide", true);
                    result.put("lockSide", false);
                    result.put("selectedProcessId", "1992276822133310464");
                    result.put("startProcessId", "1992276822133310464");
                    JSONObject startParams = new JSONObject();
                    startParams.put("txtResult", "我是无用的");
                    result.put("startParams", startParams);
                    // --------传入的json配置--------

                    // 利用缓存机制传参
                    String pg = cache.get("pageId");
                    int pgInt = Integer.parseInt(pg);
                    String txtid = "txt" + (i + pgInt * 3);
                    String txtContent = cache.get(txtid);
                    String info = "book:" + bookName + ", txt:" + txtContent;
                    cache.put("txtResult", info);

                    // 呼出gpt
                    DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService", "sideBarInit", pageId, result.toJSONString());

                    break;
                }
            }

            // 若是总结全文，则把当前三个全部传给它
            if (StringUtils.equals("myg6_conclude", key)) {
                // --------传入的json配置--------
                JSONObject result = new JSONObject();
                result.put("showIcon", true);
                result.put("switchSide", true);
                result.put("lockSide", false);
                result.put("selectedProcessId", "1992276822133310464");
                result.put("startProcessId", "1992276822133310464");
                JSONObject startParams = new JSONObject();
                startParams.put("txtResult", "我是无用的");
                result.put("startParams", startParams);
                // --------传入的json配置--------

                // 利用缓存机制传参
                String pg = cache.get("pageId");
                int pgInt = Integer.parseInt(pg);
                String txtContent = "";
                for (int i = 1; i <= 3; ++i) {
                    String txtid = "txt" + (i + pgInt * 3);
                    txtContent += cache.get(txtid);
                }

                String info = "book:" + bookName + ", txt:" + txtContent;
                cache.put("txtResult", info);
                // 呼出gpt
                DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService", "sideBarInit", pageId, result.toJSONString());
            }
        }
    }

    private void render(int page) {
        // 获取缓存
        DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");

        for (int i = 1; i <= 3; ++i) {
            // 获取当前页的文字内容
            String txtid = "txt" + (i + page * 3);
            String txtContent = cache.get(txtid);

            if (txtContent == null) {
                // 填入html信息
                String htmlId = "myg6_txt" + i;
                Html html = this.getView().getControl(htmlId);
                html.setConent("");
                break;
            }

            // 组装成html代码
            String htmlCode = htmlCode1 + txtContent + htmlCode2;

            // 填入html信息
            String htmlId = "myg6_txt" + i;
            Html html = this.getView().getControl(htmlId);
            html.setConent(htmlCode);
        }
    }

    private void solve() {
        // 获取缓存
        DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");

        System.out.println("book info " + bookName);

        QFilter qFilter = new QFilter("myg6_bookname", QCP.equals, bookName);
        DynamicObject booktxt = BusinessDataServiceHelper.loadSingle("myg6_txts", new QFilter[]{qFilter});

        String s = booktxt.getString("myg6_largetextfield_tag");

        // 去掉字符串中的回车符
        s = s.replace("\n", "").replace("\r", "");
        int all = 0, i = 0, idx = 0; // all为总页数，i为字符开始下标，idx为当前填入的txt缓存号
        int chunk = 310; // 310为上限
        while (i < s.length()) {
            int ed = i + chunk;
            if (ed >= s.length()) {
                ed = s.length();
            }
            while (ed > i && s.charAt(ed - 1) != '。' && s.charAt(ed - 1) != '？' && s.charAt(ed - 1) != '.' && s.charAt(ed - 1) != '?' && s.charAt(ed - 1) != '!' && s.charAt(ed - 1) != '！') {
                --ed;
            }
            if (ed == i) { // 没找到句号或问号，防止死循环
                ed = Math.min(i + chunk, s.length());
            }
            String txtContent = s.substring(i, ed);
            cache.put("txt" + (++idx), txtContent);
            i = ed;
            ++all;
        }
        all = (all + 2) / 3;

        // 放入总页数
        cache.put("allpage", String.valueOf(all));
    }
}