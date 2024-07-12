package plugins.ReadHelper;

import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.form.gpt.IGPTAction;

import java.util.HashMap;
import java.util.Map;


public class ReadGptMid1 implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if ("GET_TXT_INFO".equalsIgnoreCase(action)) {
            DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");
            String txtResult = cache.get("txtResult");
            result.put("txtResult", txtResult);
        }
        return result;
    }
}
