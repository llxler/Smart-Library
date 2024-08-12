package plugins.speechrecognition;

import kd.bos.form.gpt.IGPTAction;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LibraryQA implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if ("GET_QING_HTML".equalsIgnoreCase(action)) {
            String htmlString = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                    "    <title>Mermaid Diagram with Auto-fit and Zoom Controls</title>\n" +
                    "    <style>\n" +
                    "        .mermaid-container {\n" +
                    "            max-width: 500px;\n" +
                    "            height: 300px;\n" +
                    "            background-color: #ffffff;\n" +
                    "            overflow: auto;\n" +
                    "            position: relative;\n" +
                    "        }\n" +
                    "\n" +
                    "        .mermaid {\n" +
                    "            transform-origin: top left;\n" +
                    "            position: absolute;\n" +
                    "        }\n" +
                    "\n" +
                    "        .zoom-controls {\n" +
                    "            /* width: 100%; */\n" +
                    "            /* 将按钮容器的宽度设为 100% */\n" +
                    "            /* text-align: center; */\n" +
                    "            margin-top: 10px;\n" +
                    "            margin-left: 205px;\n" +
                    "            /* 确保按钮紧跟容器 */\n" +
                    "        }\n" +
                    "\n" +
                    "        .zoom-controls button {\n" +
                    "            padding: 5px 10px;\n" +
                    "            margin: 0 5px;\n" +
                    "            cursor: pointer;\n" +
                    "            width: 30px;\n" +
                    "            height: 30px;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "    <script type=\"module\">\n" +
                    "        import mermaid from 'https://cdn.jsdelivr.net/npm/mermaid@10/dist/mermaid.esm.min.mjs';\n" +
                    "        mermaid.initialize({\n" +
                    "            startOnLoad: true,\n" +
                    "            theme: 'base',\n" +
                    "\n" +
                    "            flowchart: {\n" +
                    "                useMaxWidth: false,\n" +
                    "            }\n" +
                    "        });\n" +
                    "\n" +
                    "        document.addEventListener('DOMContentLoaded', () => {\n" +
                    "            const container = document.querySelector('.mermaid-container');\n" +
                    "            const mermaidElement = container.querySelector('.mermaid');\n" +
                    "            let scale = 1;\n" +
                    "\n" +
                    "            const updateScale = () => {\n" +
                    "                const svg = mermaidElement.querySelector('svg');\n" +
                    "                const svgWidth = svg.getBBox().width;\n" +
                    "                const svgHeight = svg.getBBox().height;\n" +
                    "\n" +
                    "                const containerWidth = container.clientWidth;\n" +
                    "                const containerHeight = container.clientHeight;\n" +
                    "\n" +
                    "                const scaleX = containerWidth / svgWidth;\n" +
                    "                const scaleY = containerHeight / svgHeight;\n" +
                    "                const minScale = Math.min(scaleX, scaleY);\n" +
                    "\n" +
                    "                // Apply initial scale\n" +
                    "                mermaidElement.style.transform = `scale(${scale * minScale})`;\n" +
                    "\n" +
                    "                // Adjust position to center the diagram\n" +
                    "                const offsetX = (containerWidth - svgWidth * scale * minScale) / 2;\n" +
                    "                const offsetY = (containerHeight - svgHeight * scale * minScale) / 2;\n" +
                    "\n" +
                    "                mermaidElement.style.left = `${offsetX}px`;\n" +
                    "                mermaidElement.style.top = `${offsetY}px`;\n" +
                    "            };\n" +
                    "\n" +
                    "            const initialFit = () => {\n" +
                    "                setTimeout(() => {\n" +
                    "                    updateScale();\n" +
                    "                }, 100);\n" +
                    "            };\n" +
                    "\n" +
                    "            // 放大按钮\n" +
                    "            document.getElementById('zoom-in').addEventListener('click', () => {\n" +
                    "                scale += 0.1;\n" +
                    "                updateScale();\n" +
                    "            });\n" +
                    "\n" +
                    "            // 缩小按钮\n" +
                    "            document.getElementById('zoom-out').addEventListener('click', () => {\n" +
                    "                scale = Math.max(0.1, scale - 0.1);\n" +
                    "                updateScale();\n" +
                    "            });\n" +
                    "\n" +
                    "            initialFit();\n" +
                    "        });\n" +
                    "    </script>\n" +
                    "</head>\n" +
                    "\n" +
                    "<body>\n" +
                    "    <div class=\"mermaid-container\">\n" +
                    "        <div class=\"mermaid\">\n" +
                    "graph TD\n" +
                    "    A[华中科技大学主校区] --> B[光谷广场]\n" +
                    "    A --> C[鲁巷]\n" +
                    "    A --> D[华科西门]\n" +
                    "    A --> E[华中科技大学东门]\n" +
                    "\n" +
                    "    B --> F[武汉火车站]\n" +
                    "    B --> G[光谷步行街]\n" +
                    "    B --> H[光谷软件园]\n" +
                    "    B --> I[光谷同济医院]\n" +
                    "    B --> J[东湖高新]\n" +
                    "    \n" +
                    "    C --> K[鲁磨路]\n" +
                    "    C --> L[民族大道]\n" +
                    "    C --> M[光谷金融港]\n" +
                    "    C --> N[江夏]\n" +
                    "\n" +
                    "    D --> O[关山大道]\n" +
                    "    D --> P[喻家山南路]\n" +
                    "    D --> Q[武汉东湖]\n" +
                    "    D --> R[汤逊湖]\n" +
                    "    \n" +
                    "    E --> S[光谷一路]\n" +
                    "    E --> T[华中科技大学南三门]\n" +
                    "    E --> U[光谷大道]\n" +
                    "    E --> V[未来城]\n" +
                    "\n" +
                    "    %% 交互线路\n" +
                    "    G --> I\n" +
                    "    F --> M\n" +
                    "    N --> V\n" +
                    "    K --> R\n" +
                    "    H --> S\n" +
                    "    P --> O\n" +
                    "    Q --> J\n" +
                    "    L --> T\n" +
                    "    U --> L\n" +
                    "    T --> G\n" +
                    "    M --> R\n" +
                    "        </div>\n" +
                    "    </div>\n" +
                    "    <div class=\"zoom-controls\">\n" +
                    "        <button id=\"zoom-in\">+</button>\n" +
                    "        <button id=\"zoom-out\">-</button>\n" +
                    "    </div>\n" +
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
