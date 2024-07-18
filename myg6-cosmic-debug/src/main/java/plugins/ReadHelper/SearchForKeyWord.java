package plugins.ReadHelper;

import com.alibaba.fastjson.JSONObject;
import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.control.Button;
import kd.bos.form.control.events.BeforeClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;
import kd.sdk.plugin.Plugin;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.EventObject;

/**
 * 动态表单插件
 */
public class SearchForKeyWord extends AbstractFormPlugin implements Plugin {

    private static final String cmdin = "python D:\\UseForRuanjianbei\\keyword_search\\main.py ";
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Button button = this.getView().getControl("myg6_keyword");
        button.addClickListener(this);
    }

    @Override
    public void beforeClick(BeforeClickEvent evt) {
        super.beforeClick(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            if (StringUtils.equals("myg6_keyword", key)) {
                String keyword = (String) this.getModel().getValue("myg6_keytxt");
                System.out.println("keyword: " + keyword);
                if (StringUtils.isBlank(keyword)) {
                    this.getView().showMessage("请输入关键字");
                    return;
                }
                // 在命令行里面执行命令 cmdin
                try {
                    Runtime.getRuntime().exec(cmdin + keyword);
                    // 过一会儿再接受返回的数据
                    Thread.sleep(5000);
                    BufferedReader in = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(cmdin + keyword).getInputStream(), "UTF-8"));
                    String line = null, result = "";
                    while ((line = in.readLine()) != null) {
                        System.out.println(line);
                        result += line + "\n";
                    }
                    this.getView().showMessage("关键字联网搜寻: \n" + result);
                    // 获取缓存
                    DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");
                    cache.put("keyword", keyword + ": " + result);

                    // gpt 呼出
                    // --------传入的json配置--------
                    // process-24071613FA0518
                    String id = getProcessFid("process-24071613FA0518");
                    JSONObject need = new JSONObject();
                    need.put("showIcon", true);
                    need.put("switchSide", true);
                    need.put("lockSide", false);
                    need.put("selectedProcessId", id);
                    need.put("startProcessId", id);
                    JSONObject startParams = new JSONObject();
                    startParams.put("txtResult", "我是无用的");
                    need.put("startParams", startParams);
                    // --------传入的json配置--------
                    String pageId = this.getView().getMainView().getPageId();
                    // 呼出gpt
                    DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService", "sideBarInit", pageId, need.toJSONString());

                } catch (Exception exce) {
                    exce.printStackTrace();
                }

            }
        }
    }
    public String getProcessFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_process",
                "number," +
                        "id",
                (new QFilter("number", QCP.equals, billNo)).toArray());
        long idd = dynamicObject.getLong("id");
        return String.valueOf(idd);
    }
}
