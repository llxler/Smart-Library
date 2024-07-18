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

import java.util.EventObject;

/**
 * 动态表单插件
 */
public class BookAnalHelper extends AbstractFormPlugin implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        super.registerListener(e);
        Button button = this.getView().getControl("myg6_notehelper");
        button.addClickListener(this);
    }

    @Override
    public void beforeClick(BeforeClickEvent evt) {
        super.beforeClick(evt);
        Object source = evt.getSource();
        if (source instanceof Button) {
            Button button = (Button) source;
            String key = button.getKey();
            String pageId = this.getView().getMainView().getPageId();
            String pkValue = getProcessFid("process-24071781856E77");
            if (StringUtils.equals("myg6_notehelper", key)) {
                // 获取缓存
                DynamicObject book = (DynamicObject) this.getModel().getValue("myg6_bookname");
                String bookName = book.getString("name");
                // 获取缓存
                DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");
                // gpt 呼出
                // --------传入的json配置--------
                JSONObject need = new JSONObject();
                need.put("showIcon", true);
                need.put("switchSide", true);
                need.put("lockSide", false);
                need.put("selectedProcessId", pkValue);
                need.put("startProcessId", pkValue);
                JSONObject startParams = new JSONObject();
                startParams.put("txtResult", "我是无用的");
                need.put("startParams", startParams);
                // --------传入的json配置--------
                // 呼出gpt
                cache.put("bookAnal", bookName);
                DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService", "sideBarInit", pageId, need.toJSONString());
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