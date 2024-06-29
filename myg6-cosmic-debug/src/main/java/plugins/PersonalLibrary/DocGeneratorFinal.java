package plugins.PersonalLibrary;

import com.alibaba.fastjson.JSONArray;
import kd.bos.fileservice.FileItem;
import kd.bos.fileservice.FileService;
import kd.bos.fileservice.FileServiceFactory;
import kd.bos.form.gpt.IGPTAction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DocGeneratorFinal implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if ("GENERATE_FILE_DOC".equalsIgnoreCase(action)) {
            System.out.println("Generation begin!!!");
            try {
                // 获取文件字符串
                String fileContent = getFileContent();

                // 获取传入参数
                String statisticsData = params.get("statisticsData");

                // 将statisticsData转为JSONArray
                JSONArray jsonArrayData = JSONArray.parseArray(statisticsData);

                //将数据加入图表
                for (int i = 0 ;i < jsonArrayData.size() && i < 6; i++) {
                    JSONArray jsonArraySingle = (JSONArray) jsonArrayData.get(i);
                    // 填写书名
                    String l1 = "Book" + i;
                    fileContent = fileContent.replace(l1, jsonArraySingle.getString(0));

                    // 填写类别
                    String l2 = "Type" + i;
                    fileContent = fileContent.replace(l2, jsonArraySingle.getString(1));

                    // 填写借阅时间
                    String l3 = "Borrow" + i;
                    fileContent = fileContent.replace(l3, jsonArraySingle.getString(2).substring(0, 10));

                    // 填写归还时间
                    String l4 = "Return" + i;
                    fileContent = fileContent.replace(l4, jsonArraySingle.getString(3).substring(0, 10));
                }
                // 替代总结
                fileContent = fileContent.replace("dayEvaluate", params.get("dayPrompt"));
                System.out.println("fileContent: " + fileContent);

                // 随机生成文件名称
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i <= 12; i++) {
                    int ascii = 48 + (int) (Math.random() * 9);
                    char c = (char) ascii;
                    sb.append(c);
                }
                // 创建一个临时文件，这里可以直接命名为docx文档
                File targetFile = File.createTempFile(sb.toString(), ".xml");
                if (!targetFile.exists()) {
                    try {
                        targetFile.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                // 将字符串写入文件
                byte[] bytes = fileContent.getBytes(StandardCharsets.UTF_8);
                try (FileOutputStream fos = new FileOutputStream(targetFile)) {
                    fos.write(bytes);
                    fos.flush();
                    // 获取到文件服务器，并将文件上传至文件服务器
                    FileService fs = FileServiceFactory.getAttachmentFileService();
                    String path = "/User/DayEvaluate/" + targetFile.getName();
                    FileItem fi = new FileItem(targetFile.getName(), path, new FileInputStream(targetFile));
                    fi.setCreateNewFileWhenExists(true);
                    // 获取到文件路径
                    path = fs.upload(fi);
                    // 拼接URL，将最终的URL输出
                    result.put("endUrl", System.getProperty("domain.contextUrl") + "/attachment/download.do?path=" + path + "&method=autoJump&title=我的读书报告.xml&iconType=document");

                    targetFile.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static String getFileContent() throws Exception {
        File file = new File("D:/我的读书报告.xml");
        if (!file.exists()) {
            return null;
        }
        try (FileInputStream inputStream = new FileInputStream(file)) {
            int length = inputStream.available();
            byte[] bytes = new byte[length];
            inputStream.read(bytes);
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}
