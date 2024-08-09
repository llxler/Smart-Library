package plugins.ReadHelper;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kd.bos.cache.CacheFactory;
import kd.bos.cache.tempfile.TempFileCacheDownloadable;
import kd.bos.cache.tempfile.TempFileCacheDownloadable.Content;
import kd.bos.form.control.AttachmentPanel;
import kd.bos.form.control.events.UploadEvent;
import kd.bos.form.control.events.UploadListener;
import kd.bos.form.plugin.AbstractFormPlugin;

public class BookUpload1 extends AbstractFormPlugin implements UploadListener {

    private final static String KEY_ATTACHMENTPANEL1 = "myg6_attachmentpanelap";

    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);

        // 侦听附件面板控件的文件上传事件
        AttachmentPanel attachmentPanel = this.getView().getControl(KEY_ATTACHMENTPANEL1);
        attachmentPanel.addUploadListener(this);
    }

    @Override
    public void afterUpload(UploadEvent evt) {
        List<String> fileUrls = new ArrayList<>();

        for (Object url : evt.getUrls()) {
            fileUrls.add((String) ((Map<String, Object>) url).get("url"));
        }

        // 从临时目录，读取已上传文件内容
        for (String fileUrl : fileUrls) {
            String text = this.loadTextFileString(fileUrl);
            System.out.println("fuck" + text);
        }
    }

    private String loadTextFileString(String fileUrl) {

        TempFileCacheDownloadable downLoad = (TempFileCacheDownloadable) CacheFactory.getCommonCacheFactory().getTempFileCache();

        int temp;
        int len = 0;
        byte[] bt = new byte[1024 * 1024 * 5];
        InputStream inStream = null;
        try {
            System.out.println("fuckurl:" + fileUrl);
            String[] queryParams = new URL(fileUrl).getQuery().split("&");
            Map<String, String> downloadFileParams = new HashMap<>();
            for (String queryParam : queryParams) {
                String[] p = queryParam.split("=");
                downloadFileParams.put(p[0], p[1]);
            }
            Content content = downLoad.get(downloadFileParams.get("configKey"), downloadFileParams.get("id"));
            inStream = content.getInputStream();

            // 获取文件名
            String fileName = content.getFilename();
            System.out.println("fileName : " + fileName);
            String fileExtension = getFileExtension(fileName);
//            System.out.println("fuckkey:" + downloadFileParams.get("configKey"));
//            System.out.println("fuckid" + downloadFileParams.get("id"));
            if ("txt".equalsIgnoreCase(fileExtension)) {
                while ((temp = inStream.read()) != -1) {
                    bt[len] = (byte) temp;
                    len++;
                }
            } else if ("pdf".equalsIgnoreCase(fileExtension)) {
                // 保存文件以便后续处理
                String pdfFilePath = "temp_file.pdf";
                try (FileOutputStream outputStream = new FileOutputStream(pdfFilePath)) {
                    while ((temp = inStream.read()) != -1) {
                        outputStream.write(temp);
                    }
                }

                // 调用 Python 脚本转换 PDF 为 TXT
                String txtFilePath = "converted_file.txt";
                convertPdfToTxtWithPython(pdfFilePath, txtFilePath);

                // 读取转换后的 TXT 文件
                try (BufferedReader reader = new BufferedReader(new FileReader(txtFilePath))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    return sb.toString();
                }
            } else {
                return "Unsupported file format.";
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (IOException e) {
                return e.toString();
            }
        }
        return new String(bt, 0, len);
    }

    private static String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    private static void convertPdfToTxtWithPython(String pdfPath, String txtPath) {
        try {
            // 构建 Python 命令
            String[] command = {"python", "convert_pdf_to_txt.py", pdfPath, txtPath};

            // 执行 Python 脚本
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            // 读取 Python 脚本的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.out.println("PDF 转换为 TXT 失败。");
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}