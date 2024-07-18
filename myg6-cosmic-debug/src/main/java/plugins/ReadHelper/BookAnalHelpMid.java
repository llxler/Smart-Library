package plugins.ReadHelper;

import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.form.gpt.IGPTAction;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态表单插件
 */
public class BookAnalHelpMid implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if ("GET_ANAL".equalsIgnoreCase(action)) {
            DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");
            String txtResult = cache.get("bookAnal");
            result.put("bookAnal", txtResult);
        }
        return result;
    }
}