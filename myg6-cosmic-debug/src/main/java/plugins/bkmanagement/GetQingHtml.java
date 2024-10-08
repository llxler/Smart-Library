package plugins.bkmanagement;

import kd.bos.form.gpt.IGPTAction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GetQingHtml implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if ("GET_QING_HTML".equalsIgnoreCase(action)) {

            String htmlString = params.get("htmlResult"); // 自动生成的质量较低,换成现成的

            // 以防万一，在代码中再次截取一下字符串中的HTML片段
//            int indexBegin = htmlString.indexOf("<!DOCTYPE html>"), indexEnd = htmlString.indexOf("</html>");
//            if (indexBegin != -1 && indexEnd != -1) {
//                try {
//                    htmlString = htmlString.substring(indexBegin, indexEnd + 7);
//                } catch (StringIndexOutOfBoundsException exception) {
//                    htmlString = htmlString.substring(indexBegin);
//                }
//            }
            // 设置一个默认值
            htmlString = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>Book Ratings Pie Chart</title>\n" +
                    "    <script src=\"https://cdn.jsdelivr.net/npm/echarts/dist/echarts.min.js\"></script>\n" +
                    "    <style>\n" +
                    "        body,\n" +
                    "        html {\n" +
                    "            margin: 0;\n" +
                    "            padding: 0;\n" +
                    "            width: 100%;\n" +
                    "            height: 100%;\n" +
                    "            display: flex;\n" +
                    "            justify-content: center;\n" +
                    "        }\n" +
                    "\n" +
                    "        #main {\n" +
                    "            width: 600px;\n" +
                    "            height: 400px;\n" +
                    "\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "    <div id=\"main\"></div>\n" +
                    "    <script type=\"text/javascript\">\n" +
                    "        var chart = echarts.init(document.getElementById('main'));\n" +
                    "        var data = [\n" +
                    "            { name: \"高等数学1\", value: 4 },\n" +
                    "            { name: \"高等数学1\", value: 4 },\n" +
                    "            { name: \"高等数学1\", value: 5 },\n" +
                    "            { name: \"游戏学导论\", value: 3 },\n" +
                    "            { name: \"游戏学导论\", value: 4 },\n" +
                    "            { name: \"游戏学导论\", value: 3 },\n" +
                    "            { name: \"计算机导论 专著\", value: 4 },\n" +
                    "            { name: \"计算机导论 专著\", value: 4 },\n" +
                    "            { name: \"计算机导论 专著\", value: 3 },\n" +
                    "            { name: \"法律与文学\", value: 5 },\n" +
                    "            { name: \"法律与文学\", value: 2 },\n" +
                    "            { name: \"法律与文学\", value: 2 },\n" +
                    "            { name: \"金蝶K/3\", value: 5 },\n" +
                    "            { name: \"金蝶K/3\", value: 5 },\n" +
                    "            { name: \"金蝶K/3\", value: 5 },\n" +
                    "            { name: \"深度学习入门\", value: 5 },\n" +
                    "            { name: \"深度学习入门\", value: 4 }\n" +
                    "        ];\n" +
                    "        var option = {\n" +
                    "            title: {\n" +
                    "                text: '图书评论一览',\n" +
                    "                subtext: '基于评价数据库',\n" +
                    "                left: 'center'\n" +
                    "            },\n" +
                    "            tooltip: {\n" +
                    "                trigger: 'item'\n" +
                    "            },\n" +
                    "            legend: {\n" +
                    "                orient: 'vertical',\n" +
                    "                left: 'left'\n" +
                    "            },\n" +
                    "            series: [\n" +
                    "                {\n" +
                    "                    name: 'Rating',\n" +
                    "                    type: 'pie',\n" +
                    "                    radius: '50%',\n" +
                    "                    data: data.map(item => ({ name: item.name, value: item.value })),\n" +
                    "                    emphasis: {\n" +
                    "                        itemStyle: {\n" +
                    "                            shadowBlur: 10,\n" +
                    "                            shadowOffsetX: 0,\n" +
                    "                            shadowColor: 'rgba(0, 0, 0, 0.5)'\n" +
                    "                        }\n" +
                    "                    }\n" +
                    "                }\n" +
                    "            ]\n" +
                    "        };\n" +
                    "        chart.setOption(option);\n" +
                    "    </script>\n" +
                    "</body>\n" +
                    "\n" +
                    "</html>";

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
            // 返回URL，其中gaiIframeSize = {"height":380, "width":1000} 用于调整展示窗口宽高
            result.put("resultHtmlUrl", System.getProperty("domain.contextUrl") + "/isv/gptQing/" + directoryName + "/" + sb + ".html?" + "gaiIframeSize={\"height\":380,\"width\":1000}");
            result.put("htmlNewString", htmlString);
        }
        return result;
    }
}
