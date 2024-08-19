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
                    "        <div id=\"mermaid-diagram\" class=\"mermaid\">" +
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
                    "</div>\n" +
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
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
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
