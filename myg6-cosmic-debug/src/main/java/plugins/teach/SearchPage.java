package plugins.teach;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.serialization.SerializationUtils;
import kd.bos.dataentity.utils.StringUtils;
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

import java.util.*;


public class SearchPage extends AbstractFormPlugin implements SearchEnterListener {
    public void registerListener(EventObject e) {


        Search search = this.getControl("myg6_searchap");
        search.addEnterListener(this);
    }

    @Override
    public void search(SearchEnterEvent evt) {
        Search search = (Search) evt.getSource();
        if (StringUtils.equals("myg6_searchap", search.getKey())){
            String searchText = evt.getText();
            this.doSearch(searchText);
        }
    }

    /**
     * 实现搜索
     * @param searchText	搜索文本
     */
    private void doSearch(String searchText) {
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



    @Override
    public List<String> getSearchList(SearchEnterEvent evt) {
        Search search = (Search) evt.getSource();
        if (StringUtils.equals("myg6_searchap", search.getKey())) {
            String searchText = evt.getText();
            if (StringUtils.isNotBlank(searchText))
                return this.doSearchList(searchText);
        }
        return null;
    }


    private List<String> doSearchList(String searchText) {
        // 模糊搜索结算方式
        // 构建取数条件
        String fields = "name";

        Map<String, String> searchList = new HashMap<String, String>();
        // 模糊搜索币别

        QFilter filter2 = new QFilter("name", QCP.like, "%" + searchText + "%");
        QFilter[] filters = new QFilter[] { filter2 };
        DynamicObject[] collection = BusinessDataServiceHelper.load("myg6_book_list", fields, filters);
        for (DynamicObject obj : collection) {
            searchList.put("myg6_book_list" + " " + obj.get("id").toString(), obj.get("name").toString());
        }
        return new ArrayList<>(searchList.values());
    }


}