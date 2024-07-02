package plugins.extendedfun;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.serialization.SerializationUtils;
import kd.bos.form.FormShowParameter;
import kd.bos.form.control.Button;
import kd.bos.form.control.Control;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;
import kd.bos.form.control.Label;

import java.util.*;
/**
 * 动态表单插件
 */
public class QuestionsRendering16 extends AbstractFormPlugin implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        //添加按钮监听
        Button button = this.getView().getControl("myg6_btnok");
        button.addClickListener(this);
        Button button1 = this.getView().getControl("btnok");
        button1.addClickListener(this);
    }

    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);

        FormShowParameter showParameter = this.getView().getFormShowParameter();
        String data = showParameter.getCustomParam("questionData");
        System.out.println("fuck data" + data);
        // 去掉固定标识符部分
        String jsonString= data.replace("```json\n", "").replace("```", "").trim();

        // 解析JSON字符串
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        // 获取问题数量
        int questionCount = 0;
        while (jsonObject.containsKey("问题" + (questionCount + 1))) {
            questionCount++;
        }

        // 提取问题和答案字段
        String[] questions = new String[questionCount];
        String[] answers = new String[questionCount];
        for (int i = 1; i <= questionCount; i++) {
            questions[i - 1] = jsonObject.getString("问题" + i);
            answers[i - 1] = jsonObject.getString("答案" + i);
        }
        // 打印问题数组
        System.out.println("fuck问题数组:");
        for (String question : questions) {
            System.out.println(question);
        }
        // 打印答案数组
        System.out.println("fuck答案数组:");
        for (String answer : answers) {
            System.out.println(answer);
        }
        System.out.println("Fuckfuckfuck" + questions[0]);
        getPageCache().put("questions", SerializationUtils.toJsonString(questions));
        getPageCache().put("answers", SerializationUtils.toJsonString(answers));
        getPageCache().put("index", "1");
        Label questionLabel = this.getView().getControl("myg6_question");
        questionLabel.setText(questions[0]);

        if (questions.length == 1) {
            this.getView().setVisible(false, "btnok");
            this.getView().setVisible(true, "myg6_btnok");
        } else {
            this.getView().setVisible(true, "btnok");
            this.getView().setVisible(false, "myg6_btnok");
        }
    }
    public void click(EventObject evt) {
        super.click(evt);
        //获取被点击的控件对象
        Control source = (Control) evt.getSource();

        String ques = getPageCache().get("questions");
        List<String> quesList = SerializationUtils.fromJsonString(ques, List.class);

        String ans = getPageCache().get("answers");
        List<String> ansList = SerializationUtils.fromJsonString(ans, List.class);

        String myans = getPageCache().get("myans");
        System.out.println("fuck myans" + myans);

        String[] myansarr = new String[1];

        List<String> myansList = new ArrayList<>();

        if (myans == null) {

        } else if (myans.startsWith("[")) {
            //System.out.println("shit" + myans.getClass().getName());
            myansList = SerializationUtils.fromJsonString(myans, List.class);
        } else {
            myansarr[0] = myans;
            myansList = SerializationUtils.fromJsonString(SerializationUtils.toJsonString(myansarr), List.class);
        }
        System.out.println("fuck" + myansList);
        String idxString = getPageCache().get("index");
        int idx = Integer.parseInt(idxString);
        //System.out.println("index value" + idx);
        if (idx == quesList.size() - 1) {
            this.getView().setVisible(false, "btnok");
            this.getView().setVisible(true, "myg6_btnok");
        } else {
            this.getView().setVisible(true, "btnok");
            this.getView().setVisible(false, "myg6_btnok");
        }

        System.out.println("--------------------");
        if (StringUtils.equals("btnok", source.getKey())) {
            // 塞旧答案
            String nowans = (String) this.getModel().getValue("myg6_largetextfield");

            myansList.add(nowans);
            getPageCache().put("myans", SerializationUtils.toJsonString(myansList));
            this.getModel().setValue("myg6_largetextfield","");
            // 拿出新问题
            String newque = quesList.get(idx);
            Label questionLabel = this.getView().getControl("myg6_question");
            questionLabel.setText(newque);
            // 更新序号
            getPageCache().put("index", String.valueOf(idx + 1));

        } else if (StringUtils.equals("myg6_btnok", source.getKey())) {
            String nowans = (String) this.getModel().getValue("myg6_largetextfield");
            myansList.add(nowans);
            System.out.println("_________data_init_______________");
            System.out.println(myansList);
            System.out.println(ansList);
            System.out.println(quesList);
        }
    }
}