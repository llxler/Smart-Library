
package plugins.ReadHelper;

import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.form.gpt.IGPTAction;

import java.util.HashMap;
import java.util.Map;

/**
 * 动态表单插件
 */
public class KeyWordMid implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String, String> result = new HashMap<>();
        if ("GET_KEY".equalsIgnoreCase(action)) {
            DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");
            String txtResult = cache.get("keyword");
            String htmlurl = cache.get("htmlurl");
            result.put("keyword", txtResult);
            result.put("htmlurl", htmlurl);
            System.out.println("jiba" + htmlurl);
        }
        return result;
    }
}