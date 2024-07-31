package plugins.extendedfun;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.serialization.SerializationUtils;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Button;
import kd.bos.form.control.Control;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.sdk.plugin.Plugin;
import kd.bos.form.control.Label;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 动态表单插件
 */
public class QuestionsRenderFinal extends AbstractFormPlugin implements Plugin {

    // Class to hold the question, user's answer, correct answer, score, and reason
    public static class QA {
        private String question;
        private String userAnswer;
        private String correctAnswer;
        private String score;
        private String reason;

        public QA(String question, String userAnswer, String correctAnswer, String score, String reason) {
            this.question = question;
            this.userAnswer = userAnswer;
            this.correctAnswer = correctAnswer;
            this.score = score;
            this.reason = reason;
        }

        public String getQuestion() {
            return question;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public String getCorrectAnswer() {
            return correctAnswer;
        }

        public String getScore() {
            return score;
        }

        public String getReason() {
            return reason;
        }
    }

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
        System.out.println("data: " + data);

        // 使用正则表达式提取问题和答案
        Pattern questionPattern = Pattern.compile("问题\\d+:\\s*([\\s\\S]+?)\\s*答案\\d+:");
        Pattern answerPattern = Pattern.compile("答案\\d+:\\s*([\\s\\S]+?)(?=问题\\d+:|$)");

        ArrayList<String> questionsList = new ArrayList<>();
        ArrayList<String> answersList = new ArrayList<>();

        Matcher questionMatcher = questionPattern.matcher(data);
        Matcher answerMatcher = answerPattern.matcher(data);

        while (questionMatcher.find()) {
            questionsList.add(questionMatcher.group(1).trim());
        }

        while (answerMatcher.find()) {
            answersList.add(answerMatcher.group(1).trim());
        }

        String[] questions = questionsList.toArray(new String[0]);
        String[] answers = answersList.toArray(new String[0]);

        // 打印结果
        System.out.println("Questions:");
        for (String question : questions) {
            System.out.println(question);
        }

        System.out.println("\nAnswers:");
        for (String answer : answers) {
            System.out.println(answer);
        }

        System.out.println("First Question: " + questions[0]);

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
        System.out.println("myans: " + myans);

        List<String> myansList = new ArrayList<>();

        if (myans != null) {
            if (myans.startsWith("[")) {
                myansList = SerializationUtils.fromJsonString(myans, List.class);
            } else {
                myansList.add(myans);
            }
        }

        System.out.println("myansList: " + myansList);
        String idxString = getPageCache().get("index");
        int idx = Integer.parseInt(idxString);

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
            this.getModel().setValue("myg6_largetextfield", "");
            // 拿出新问题
            String newque = quesList.get(idx);
            Label questionLabel = this.getView().getControl("myg6_question");
            questionLabel.setText(newque);
            // 更新序号
            getPageCache().put("index", String.valueOf(idx + 1));

        } else if (StringUtils.equals("myg6_btnok", source.getKey())) {
            String nowans = (String) this.getModel().getValue("myg6_largetextfield");
            myansList.add(nowans);

            StringBuilder qaJson = new StringBuilder("[");
            for (int i = 0; i < quesList.size(); i++) {
                String userAnswer = i < myansList.size() ? myansList.get(i) : "";
                String correctAnswer = i < ansList.size() ? ansList.get(i) : "";
                qaJson.append("{")
                        .append("\"userAnswer\":\"").append(userAnswer).append("\",")
                        .append("\"correctAnswer\":\"").append(correctAnswer).append("\"")
                        .append("}");

                if (i < quesList.size() - 1) {
                    qaJson.append(",");
                }
            }
            qaJson.append("]");
            System.out.println("Final JSON: " + qaJson);

            // 调用GPT开发平台微服务
            Map<String, String> variableMap = new HashMap<>();
            variableMap.put("ansResult", String.valueOf(qaJson));

            Object[] params = new Object[] {
                    //GPT提示编码
                    getPromptFid("prompt-240703B474C02A"),
                    "开始根据学生的每个问题的回答来进行打分",
                    variableMap
            };
            Map<String, Object> result = DispatchServiceHelper.invokeBizService("ai", "gai", "GaiPromptService", "syncCall", params);
            JSONObject jsonObjectResult = new JSONObject(result);
            JSONObject jsonObjectData = jsonObjectResult.getJSONObject("data");
            String finalGet = jsonObjectData.getString("llmValue");
            System.out.println("finalGet: " + finalGet);

            // Combine finalGet with quesList, ansList, and myansList
            List<QA> qaList = new ArrayList<>();

            // Parse finalGet into QA objects
            // 去掉固定标识符部分
            finalGet = finalGet.replace("```json\n", "").replace("```", "").trim();
            JSONArray jsonArray = JSONArray.parseArray(finalGet);
            for (int i = 0; i < quesList.size(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String question = quesList.get(i);
                String userAnswer = myansList.get(i);
                String correctAnswer = ansList.get(i);
                String score = jsonObject.getString("评分");
                String reason = jsonObject.getString("评分理由");
                QA qa = new QA(question, userAnswer, correctAnswer, score, reason);
                qaList.add(qa);
            }

            // Convert qaList to JSON manually
            StringBuilder qaJson1 = new StringBuilder("[");
            for (int i = 0; i < qaList.size(); i++) {
                QA qa = qaList.get(i);
                qaJson1.append("{")
                        .append("\"index\":").append(i + 1).append(",")
                        .append("\"question\":\"").append(qa.getQuestion()).append("\",")
                        .append("\"userAnswer\":\"").append(qa.getUserAnswer()).append("\",")
                        .append("\"correctAnswer\":\"").append(qa.getCorrectAnswer()).append("\",")
                        .append("\"score\":\"").append(qa.getScore()).append("\",")
                        .append("\"reason\":\"").append(qa.getReason()).append("\"")
                        .append("}");

                if (i < qaList.size() - 1) {
                    qaJson1.append(",");
                }
            }
            qaJson1.append("]");

            // Pass to next page
            FormShowParameter billShowParameter = new FormShowParameter();
            billShowParameter.setFormId("myg6_question_judge");
            billShowParameter.setCaption("开始分析~");
            billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
            billShowParameter.setCustomParam("qaResult", qaJson1.toString());
            this.getView().showForm(billShowParameter);
        }
    }

    // 获取GPT提示的Fid
    public long getPromptFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_prompt",
                "number," + "id",
                (new QFilter( "number", QCP.equals, billNo)).toArray());
        return (dynamicObject).getLong("id");
    }
}
