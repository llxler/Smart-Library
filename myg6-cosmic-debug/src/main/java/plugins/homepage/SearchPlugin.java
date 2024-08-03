package plugins.homepage;


import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import kd.bos.cache.CacheFactory;
import kd.bos.cache.DistributeSessionlessCache;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.serialization.SerializationUtils;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Search;
import kd.bos.form.control.events.SearchEnterEvent;
import kd.bos.form.control.events.SearchEnterListener;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.list.ListFilterParameter;
import kd.bos.list.ListShowParameter;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.DispatchServiceHelper;

public class SearchPlugin extends AbstractFormPlugin implements SearchEnterListener {

    // 搜索控件标识
    private final static String KEY_SEARCH = "myg6_searchap";

    @Override
    public void registerListener(EventObject e) {
        // 侦听搜索控件的搜索事件
        Search search = getView().getControl(KEY_SEARCH);
        search.addEnterListener(this);
        super.registerListener(e);
    }

    /**
     * 用户敲入搜索字符时，即时触发：输出模糊搜索结果
     */
    @Override
    public List<String> getSearchList(SearchEnterEvent evt) {
        Search search = (Search) evt.getSource();
        if (StringUtils.equals(KEY_SEARCH, search.getKey())) {
            String searchText = evt.getText();
            if (StringUtils.isNotBlank(searchText))
                return this.doSearchList(searchText);
        }
        return null;
    }

    /**
     * 模糊搜索币别中编码和名字包含搜索关键字的结果
     * 实现搜索列表，在搜索框输入后列举出搜索到的数组
     *
     * @param searchText
     * @return 搜索到的数据
     */
    private List<String> doSearchList(String searchText) {
        // 模糊搜索结算方式
        // 构建取数条件
        String fields = "number,name";
        // Create an empty filter array (no filters)
        QFilter filter1 = new QFilter("name", QCP.like, "%" + searchText + "%");
        QFilter[] filter = new QFilter[]{filter1};
        // Load the data
        DynamicObject[] collection = BusinessDataServiceHelper.load("myg6_book_list", fields, filter);
        Map<String, String> searchList = new HashMap<String, String>();
        for (DynamicObject obj : collection) {
            searchList.put("myg6_book_list" + " " + obj.get("number").toString(), "" + obj.get("name"));
        }
        // 把本次搜索结果放到页面缓存：后续search事件要用到
        getPageCache().put("searchList", SerializationUtils.toJsonString(searchList));
        return new ArrayList<>(searchList.values());
    }
    @Override
    public void search(SearchEnterEvent evt) {
        Search search = (Search) evt.getSource();
        if (StringUtils.equals(KEY_SEARCH, search.getKey())) {
            String searchText = evt.getText();
            this.doSearch(searchText);
        }
    }
    /**
     * 实现搜索
     * @param searchText 搜索文本
     */
    private void doSearch(String searchText) {
        Boolean AI = (Boolean) this.getModel().getValue("myg6_smartsearch");
        if (AI) {
            // 呼出gpt对话框
            String pageId = this.getView().getMainView().getPageId();
            Object pkValue = getProcessFid("process-24080158231F7B");
            JSONObject needJson = new JSONObject();
            // 从数据库中获取书籍列表
            String bookList = "";
            String fields = "name";
            QFilter[] filters = new QFilter[0];
            DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_list", fields, filters);
            for (DynamicObject dy : dys) {
                bookList += dy.getString("name") + ",";
            }
            needJson.put("bookList", bookList);
            needJson.put("userNeed", searchText);
            DistributeSessionlessCache cache = CacheFactory.getCommonCacheFactory().getDistributeSessionlessCache("customRegion");
            cache.put("openFlag", "false");
            DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService","selectProcessInSideBar",pkValue, pageId, "----------------------正在搜索----------------------\n");
            DispatchServiceHelper.invokeBizService("ai", "gai", "GaiService","startProcessInSideBar", pkValue, pageId, new HashMap(), needJson.toJSONString());
            for (int i = 1; i <= 10; ++i) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                String openFlag = cache.get("openFlag");
                if (openFlag != null && openFlag.equals("true")) {
                    FormShowParameter formShowParameter = new FormShowParameter();
                    formShowParameter.setFormId("myg6_minishelf");
                    formShowParameter.getOpenStyle().setShowType(ShowType.Modal);
                    this.getView().showForm(formShowParameter);
                    break;
                }
            }
        } else {
            ListShowParameter lsp = new ListShowParameter();
            lsp.setFormId("bos_list");
            lsp.setBillFormId("myg6_book_list");
            lsp.getOpenStyle().setShowType(ShowType.Modal);
            ListFilterParameter listFilterParameter = new ListFilterParameter();
            QFilter qFilter = new QFilter("name", QCP.like, "%" + searchText + "%");
            listFilterParameter.setFilter(qFilter);
            lsp.setListFilterParameter(listFilterParameter);
            this.getView().showForm(lsp);
        }
    }

    public Object getProcessFid(String billNo) {
        DynamicObject dynamicObject = BusinessDataServiceHelper.loadSingle("gai_process",
                "number," +
                        "id",
                (new QFilter("number", QCP.equals, billNo)).toArray());
        long idd = dynamicObject.getLong("id");
        return Long.parseLong(String.valueOf(idd));
    }
}
