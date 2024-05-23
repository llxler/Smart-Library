package plugins.bkmanagement;

import kd.bos.form.gpt.IGPTAction;
import kd.bos.param.facade.ISysParamServiceFacade;

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

            String htmlString = params.get("htmlResult");
            System.out.println(htmlString);

            //以防万一，在代码中我们再次截取一下字符串中的HTML片段
            int indexBegin = htmlString.indexOf("<!DOCTYPE html>"), indexEnd = htmlString.indexOf("</html>");
            if (indexBegin != -1 && indexEnd != -1) {
                try {
                    htmlString = htmlString.substring(indexBegin, indexEnd + 7);
                } catch (StringIndexOutOfBoundsException exception) {
                    htmlString = htmlString.substring(indexBegin);
                }
            }
            //获取当前时间，我们可以在isv文件夹中根据时间来对相应的文件夹创建HTML文件
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String directoryName = simpleDateFormat.format(new Date());
            //获取一个10位的随机文件名称
            StringBuffer sb = new StringBuffer();
            for (int i = 1; i <= 10; ++i) {
                int ascii = 48 + (int)(Math.random() * 9);
                char c = (char) ascii;
                sb.append(c);
            }
            File directoryGetQing = new File(System.getProperty("JETTY_WEBRES_PATH") + "/isv/gptQing", directoryName);
            //如果文件夹不存在就创建文件夹
            if (!directoryGetQing.exists()) {
                directoryGetQing.mkdirs();
            }
            File targetFile = new File(System.getProperty("JETTY_WEBRES_PATH") + "/isv/gptQing/" + directoryName,sb + ".html");
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
            //返回URL，其中gaiIframeSize = {"height":380,"width":1000} 用于调整展示窗口宽高
            result.put("resultHtmlUrl", System.getProperty("domain.contextUrl") + "/isv/gptQing/" + directoryName + "/" + sb + ".html?" + "gaiIframeSize={\"height\":380,\"width\":1000}");
            result.put("htmlNewString", htmlString);
        }
        return result;
    }
}
