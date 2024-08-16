package plugins.speechrecognition;

import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.gpt.IGPTAction;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * 动态表单插件
 */
public class MermaidRender implements IGPTAction {

    public static final String htmlStringPart1 = "<!DOCTYPE html>\n" +
            "<html lang=\"en\">\n" +
            "\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <title>Interactive Mermaid Diagram</title>\n" +
            "    <script type=\"module\">\n" +
            "        import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';\n" +
            "        mermaid.initialize({ startOnLoad: true });\n" +
            "    </script>\n" +
            "    <style>\n" +
            "        #mermaid-container {\n" +
            "            width: 100%;\n" +
            "            max-width: 500px;\n" +
            "            border: 1px solid #ccc;\n" +
            "            overflow: hidden;\n" +
            "            height: 500px;\n" +
            "            position: relative;\n" +
            "        }\n" +
            "\n" +
            "        #mermaid-diagram {\n" +
            "            transform-origin: center center;\n" +
            "            cursor: grab;\n" +
            "        }\n" +
            "\n" +
            "        #mermaid-diagram:active {\n" +
            "            cursor: grabbing;\n" +
            "        }\n" +
            "    </style>\n" +
            "</head>\n" +
            "\n" +
            "<body>\n" +
            "    <div id=\"mermaid-container\">\n" +
            "        <div id=\"mermaid-diagram\" class=\"mermaid\">";
    public static final String[] PROMPTS = {
            "请你给出围绕用户输入内容的逻辑关系图，使用mermaid语法，注意需要使用双引号将代码中的内容括起来，如下面的例子所示。\n" +
                    "mermaid语法举例：\n" +
                    "mermaid\n" +
                    "graph TD\n" +
                    "    P(\"编程\") --> L1(\"Python\")\n" +
                    "    P(\"编程\") --> L2(\"C\")\n" +
                    "    P(\"编程\") --> L3(\"C++\")\n" +
                    "    P(\"编程\") --> L4(\"Javascipt\")\n" +
                    "    P(\"编程\") --> L5(\"PHP\")\n",

            "请你给出围绕用户输入内容的序列图，使用mermaid语法,注意需要使用双引号将内容括起来。\n" +
                    "mermaid语法举例：\n" +
                    "mermaid\n" +
                    "sequenceDiagram\n" +
                    "    participant A as 用户\n" +
                    "    participant B as 系统\n" +
                    "    A->>B: 登录请求\n" +
                    "    B->>A: 登录成功\n" +
                    "    A->>B: 获取数据\n" +
                    "    B->>A: 返回数据\n",

            "请你给出围绕用户输入内容的类图，使用mermaid语法。\n" +
                    "mermaid语法举例：\n" +
                    "mermaid\n" +
                    "classDiagram\n" +
                    "    Class01 <|-- AveryLongClass : Cool\n" +
                    "    Class03 *-- Class04\n" +
                    "    Class05 o-- Class06\n" +
                    "    Class07 .. Class08\n" +
                    "    Class09 --> C2 : Where am i?\n" +
                    "    Class09 --* C3\n" +
                    "    Class09 --|> Class07\n" +
                    "    Class07 : equals()\n" +
                    "    Class07 : Object[] elementData\n" +
                    "    Class01 : size()\n" +
                    "    Class01 : int chimp\n" +
                    "    Class01 : int gorilla\n" +
                    "    Class08 <--> C2: Cool label\n",

            "请你给出围绕用户输入内容的饼图，使用mermaid语法，注意需要使用双引号将内容括起来。\n" +
                    "mermaid语法举例：\n" +
                    "mermaid\n" +
                    "pie title Pets adopted by volunteers\n" +
                    "    \"狗\" : 386\n" +
                    "    \"猫\" : 85\n" +
                    "    \"兔子\" : 15\n",

            "请你给出围绕用户输入内容的甘特图，使用mermaid语法，注意需要使用双引号将内容括起来。\n" +
                    "mermaid语法举例：\n" +
                    "mermaid\n" +
                    "gantt\n" +
                    "    title \"项目开发流程\"\n" +
                    "    dateFormat  YYYY-MM-DD\n" +
                    "    section \"设计\"\n" +
                    "    \"需求分析\" :done, des1, 2024-01-06,2024-01-08\n" +
                    "    \"原型设计\" :active, des2, 2024-01-09, 3d\n" +
                    "    \"UI设计\" : des3, after des2, 5d\n" +
                    "    section \"开发\"\n" +
                    "    \"前端开发\" :2024-01-20, 10d\n" +
                    "    \"后端开发\" :2024-01-20, 10d\n",

            "请你给出围绕用户输入内容的状态图，使用mermaid语法，注意需要使用双引号将内容括起来。\n" +
                    "mermaid语法举例：\n" +
                    "mermaid\n" +
                    "stateDiagram-v2\n" +
                    "   [*] --> \"Still\"\n" +
                    "    \"Still\" --> [*]\n" +
                    "    \"Still\" --> \"Moving\"\n" +
                    "    \"Moving\" --> \"Still\"\n" +
                    "    \"Moving\" --> \"Crash\"\n" +
                    "    \"Crash\" --> [*]\n",

            "请你给出围绕用户输入内容的实体关系图，使用mermaid语法。\n" +
                    "mermaid语法举例：\n" +
                    "mermaid\n" +
                    "erDiagram\n" +
                    "    CUSTOMER ||--o{ ORDER : places\n" +
                    "    ORDER ||--|{ LINE-ITEM : contains\n" +
                    "    CUSTOMER {\n" +
                    "        string name\n" +
                    "        string id\n" +
                    "    }\n" +
                    "    ORDER {\n" +
                    "        string orderNumber\n" +
                    "        date orderDate\n" +
                    "        string customerID\n" +
                    "    }\n" +
                    "    LINE-ITEM {\n" +
                    "        number quantity\n" +
                    "        string productID\n" +
                    "    }\n",

            "请你给出围绕用户输入内容的象限图，使用mermaid语法，注意需要使用双引号将内容括起来。\n" +
                    "mermaid语法举例：\n" +
                    "mermaid\n" +
                    "graph LR\n" +
                    "    A[\"Hard skill\"] --> B(\"Programming\")\n" +
                    "    A[\"Hard skill\"] --> C(\"Design\")\n" +
                    "    D[\"Soft skill\"] --> E(\"Coordination\")\n" +
                    "    D[\"Soft skill\"] --> F(\"Communication\")\n"
    };
    public static final String htmlStringPart2 = "</div>\n" +
            "    </div>\n" +
            "\n" +
            "    <script src=\"https://cdn.jsdelivr.net/npm/interactjs@1.10.11/dist/interact.min.js\"></script>\n" +
            "    <script>\n" +
            "        // Initial scale\n" +
            "        let scale = 1;\n" +
            "\n" +
            "        // Make the diagram draggable\n" +
            "        interact('#mermaid-diagram')\n" +
            "            .draggable({\n" +
            "                listeners: {\n" +
            "                    move(event) {\n" +
            "                        const target = event.target;\n" +
            "                        const x = (parseFloat(target.getAttribute('data-x')) || 0) + event.dx;\n" +
            "                        const y = (parseFloat(target.getAttribute('data-y')) || 0) + event.dy;\n" +
            "                        target.style.transform = `translate(${x}px, ${y}px) scale(${scale})`;\n" +
            "                        target.setAttribute('data-x', x);\n" +
            "                        target.setAttribute('data-y', y);\n" +
            "                    }\n" +
            "                }\n" +
            "            });\n" +
            "\n" +
            "        // Add mouse wheel event for zooming\n" +
            "        document.getElementById('mermaid-container').addEventListener('wheel', function (event) {\n" +
            "            event.preventDefault();\n" +
            "            if (event.deltaY < 0) {\n" +
            "                // Zoom in\n" +
            "                scale = Math.min(scale * 1.1, 10); // Limit max scale\n" +
            "            } else {\n" +
            "                // Zoom out\n" +
            "                scale = Math.max(scale * 0.9, 0.1); // Limit min scale\n" +
            "            }\n" +
            "            const target = document.getElementById('mermaid-diagram');\n" +
            "            target.style.transform = `translate(${parseFloat(target.getAttribute('data-x')) || 0}px, ${parseFloat(target.getAttribute('data-y')) || 0}px) scale(${scale})`;\n" +
            "        });\n" +
            "    </script>\n" +
            "</body>\n" +
            "\n" +
            "</html>";

    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if ("GET_QING_HTML".equalsIgnoreCase(action)) {

            Map<String, String> variableMap = new HashMap<>();
            String type = params.get("type");

            Object[] gptParams = new Object[]{
                    //GPT提示编码
                    getPromptFid("prompt-240816EFC321D8"),
                    params.get("userInput") + PROMPTS[Integer.parseInt(type) - 1],
                    variableMap
            };
            Map<String, Object> mResult = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", gptParams);
            JSONObject jsonObjectResult = new JSONObject(mResult);
            JSONObject mermaidResult = jsonObjectResult.getJSONObject("data");
            //设置值
            //this.getModel().setValue("ozwe_evaluate_all", jsonObjectData.getString("llmValue"));

            String mermaidCode = mermaidResult.getString("llmValue");
            System.out.println("fuckyou" + mermaidCode);


            String codeToHtml = "";

            Pattern pattern = Pattern.compile("==(.+?)==", Pattern.DOTALL);
            Matcher matcher = pattern.matcher(mermaidCode);
            if (matcher.find()) {
                codeToHtml = matcher.group(1); // 提取到的内容
                System.out.println(codeToHtml);
            } else {
                System.out.println("No match found.");
            }
            String htmlString = htmlStringPart1 + codeToHtml + htmlStringPart2;
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
            result.put("mermaidUrl", System.getProperty("domain.contextUrl") + "/isv/gptQing/" + directoryName + "/" + sb + ".html?" + "gaiIframeSize={\"height\":510,\"width\":550}");
            result.put("userInput", params.get("userInput"));
            result.put("mermaidCode", codeToHtml);
        }
        return result;
    }

    public long getPromptFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_prompt",
                "number," +
                        "id",
                (new QFilter("number", QCP.equals, billNo)).toArray());
        return dynamicObject.getLong("id");
    }
}