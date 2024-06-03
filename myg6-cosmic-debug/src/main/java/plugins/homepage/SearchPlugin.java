package plugins.homepage;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kd.bos.bill.BillOperationStatus;
import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.serialization.SerializationUtils;
import kd.bos.dataentity.utils.StringUtils;
import kd.bos.form.ShowType;
import kd.bos.form.control.Search;
import kd.bos.form.control.events.SearchEnterEvent;
import kd.bos.form.control.events.SearchEnterListener;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
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
     * @param searchText
     * @return    搜索到的数据
     */
    private List<String> doSearchList(String searchText) {
        // 模糊搜索结算方式
        // 构建取数条件
        String fields = "number,name";
        // Create an empty filter array (no filters)
        QFilter filter1 = new QFilter("name", QCP.like, "%" + searchText + "%");
        QFilter[] filter = new QFilter[] { filter1 };
        // Load the data
        DynamicObject[] collection = BusinessDataServiceHelper.load("myg6_book_list", fields, filter);

//        ORM orm = ORM.create();
        Map<String, String> searchList = new HashMap<String, String>();
        // 模糊搜索币别
//        QFilter filter1 = new QFilter("number", QCP.like, "%" + searchText + "%");
//        QFilter filter2 = new QFilter("name", QCP.like, "%" + searchText + "%");
//        QFilter filters = filter1.or(filter2);
//        DynamicObjectCollection collection = orm.query("bd_currency", new QFilter[] { filters });
        System.out.println("fuck1");
        for (DynamicObject obj : collection) {
            System.out.println("fuck" + obj.get("number").toString() + " " + obj.get("name"));
            searchList.put("myg6_book_list" + " " + obj.get("number").toString(), " " + obj.get("name"));
        }
        // 把本次搜索结果放到页面缓存：后续search事件要用到
        getPageCache().put("searchList", SerializationUtils.toJsonString(searchList));
        return new ArrayList<>(searchList.values());
    }

    /**
     * 用户录入搜索字符完毕，回车时触发：准确定位
     */
    @Override
    public void search(SearchEnterEvent evt) {
        Search search = (Search) evt.getSource();
        if (StringUtils.equals(KEY_SEARCH, search.getKey())){
            String searchText = evt.getText();
            this.doSearch(searchText);
        }
    }

    /**
     * 实现搜索
     * @param searchText    搜索文本
     */
    private void doSearch(String searchText) {
        // 从模糊查询结果中，匹配搜索文本：找到后，打开对应的币别详情界面
        if (getPageCache().get("searchList") != null) {
            Map<String, String> searchList = SerializationUtils.fromJsonString(getPageCache().get("searchList"), Map.class);
            searchList.forEach((key, value) -> {
                if (searchText.equals(value)) {
                    String[] arr = key.split(" ");
                    BillShowParameter param = new BillShowParameter();
                    param.setPkId(arr[1]);
                    param.setFormId(arr[0]);
                    param.getOpenStyle().setShowType(ShowType.Modal);
                    param.setBillStatus(BillOperationStatus.VIEW);
                    param.setStatus(OperationStatus.VIEW);
                    getView().showForm(param);
                }
            });
        }
    }
}
