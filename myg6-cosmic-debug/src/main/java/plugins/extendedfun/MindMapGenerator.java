package plugins.extendedfun;

import com.alibaba.druid.util.StringUtils;
import kd.bos.entity.IFrameMessage;
import kd.bos.form.control.Button;
import kd.bos.form.control.IFrame;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

/**
 * 动态表单插件
 */
public class MindMapGenerator extends AbstractFormPlugin implements Plugin {
    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        IFrame iframe = this.getControl("myg6_MindMap");
        iframe.setSrc("http://10.21.207.149:7000/");
    }
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        //添加按钮监听
        Button button = this.getView().getControl("myg6_buttonap");
        button.addClickListener(this);
    }
    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            if (StringUtils.equals("myg6_buttonap", key)) {
                // todo: 从数据模型中获取数据
//                Object txt = this.getModel().getValue("myg6_txt");
                Object txt = "# 如何读好一本书\n" +
                        "\n" +
                        "## 读书前的准备\n" +
                        "\n" +
                        "### 确定读书目的\n" +
                        "- 明确为何读书，是获取知识、陶冶情操还是其他目的。\n" +
                        "- 了解自己的兴趣和需求，选择与之相关的书籍。\n" +
                        "\n" +
                        "### 选择合适的书籍\n" +
                        "- 根据读书目的，挑选题材、难度、风格与自己匹配的书籍。\n" +
                        "- 参考书评、书单或向他人请教推荐。\n" +
                        "\n" +
                        "## 读书中的方法\n" +
                        "\n" +
                        "### 整体把握\n" +
                        "- 通览全书，了解作者意图、结构和主要观点。\n" +
                        "- 标记重点段落，写下笔记或摘要。\n" +
                        "\n" +
                        "### 细致阅读\n" +
                        "- 逐字逐句阅读，理解字面意思和逻辑关系。\n" +
                        "- 标记生词、难句，查阅资料深入理解。\n" +
                        "- 思考作者观点，提出自己的见解。\n" +
                        "\n" +
                        "### 批判性阅读\n" +
                        "- 分析作者观点，与自己的知识和经验进行比较。\n" +
                        "- 找出证据和论据，检验其合理性和可靠性。\n" +
                        "- 提出自己的质疑和反驳意见。\n" +
                        "\n" +
                        "## 读书后的反思\n" +
                        "\n" +
                        "### 总结并应用\n" +
                        "- 回顾所读内容，总结重点和要点。\n" +
                        "- 将学到的知识或道理应用到实际生活中。\n" +
                        "\n" +
                        "### 分享和讨论\n" +
                        "- 与他人讨论书本内容，交流心得体会。\n" +
                        "- 写书评或读书笔记，记录自己的理解和收获。\n";
                System.out.println("打印txt内容" + txt);

                IFrame iframe = this.getView().getControl("myg6_MindMap");
                IFrameMessage message = new IFrameMessage();
                message.setContent(txt);
                message.setOrigin("*");
                iframe.postMessage(message);
            }
        }
    }
}