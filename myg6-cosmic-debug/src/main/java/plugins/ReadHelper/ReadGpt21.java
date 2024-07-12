package plugins.ReadHelper;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.form.control.Button;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 动态表单插件
 */
public class ReadGpt21 extends AbstractFormPlugin implements Plugin {
    private final String id = "myg6_analyse";
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        // 添加按钮监听
        Button button = this.getView().getControl("myg6_showai");
        // 分析按钮
        Button button1 = this.getView().getControl("myg6_analyse1");
        Button button2 = this.getView().getControl("myg6_analyse2");
        Button button3 = this.getView().getControl("myg6_analyse3");
        Button button4 = this.getView().getControl("myg6_analyse4");
        Button button5 = this.getView().getControl("myg6_analyse5");
        // 监听
        button.addClickListener(this);
        button1.addClickListener(this);
        button2.addClickListener(this);
        button3.addClickListener(this);
        button4.addClickListener(this);
        button5.addClickListener(this);
    }

    @Override
    public void click(EventObject evt) {
        super.click(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            String pageId = this.getView().getMainView().getPageId();
            // Object pkValue = Long.parseLong("1992276822133310464");
            if (StringUtils.equals("myg6_showai", key)) {
                JSONObject result = new JSONObject();
                result.put("showIcon", true);
                result.put("switchSide", true);
                result.put("lockSide", true);
                result.put("selectedProcessId", "1992276822133310464");
                result.put("startProcessId", "1992276822133310464");

                JSONObject startParams = new JSONObject();
                startParams.put("txtResult", "我是天才我是天才，我是天才，我是天才");
                result.put("startParams", startParams);
                DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");
                cache.put("txtResult", "yeah 成功！！！");

                DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService", "sideBarInit", pageId, result.toJSONString());
            }
        }
    }
}