package plugins.homepage;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.gpt.IGPTAction;

import java.util.HashMap;
import java.util.Map;

public class SearchPluginMid7 implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if ("GET_JSON_STRING".equalsIgnoreCase(action)) {
            // 将无效字符进行处理
            String jsonResult = params.get("needResult").replaceAll("\\s*|\r|\n|\t", "");
            System.out.println("jsonResult: " + jsonResult);
            JSONObject resultJsonObject = JSON.parseObject(jsonResult);
            System.out.println("resultJsonObject: " + resultJsonObject);

            // 创立一个String 变长数组 bookNames
            //List<String> bookNames = new ArrayList<>();
            // 遍历resultJsonObject的TaskList数组
            String cnt_str = resultJsonObject.get("cnt").toString();
            int cnt = Integer.parseInt(cnt_str);

            DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");

            for (int i = 1; i <= cnt; ++i) {
                //bookNames.add(String.valueOf(resultJsonObject.getString("book" + i)));
                System.out.println("shit" + resultJsonObject.getString("book" + i));
                cache.put("bookSearch" + i, resultJsonObject.getString("book" + i));
            }

            cache.put("bookSearchCnt", "" + cnt);

            String property = System.getProperty("domain.contextUrl");
            String url = property + "/index.html?formId=myg6_minishelf";
            result.put("formUrl", url);
            result.put("cnt", cnt + "");
            System.out.println("goushi" + params.get("request").replaceAll("\\s*|\r|\n|\t", "") + params.get("needResult").replaceAll("\\s*|\r|\n|\t", ""));
            result.put("anatext", params.get("request").replaceAll("\\s*|\r|\n|\t", "") + params.get("needResult").replaceAll("\\s*|\r|\n|\t", ""));
            cache.put("openFlag", "true");
        }
        return result;
    }
}
