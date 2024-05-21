package plugins.bkmanagement;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.form.gpt.IGPTAction;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

import java.util.HashMap;
import java.util.Map;

public class GetBooks implements IGPTAction {
    @Override
    public Map<String, String> invokeAction(String action, Map<String, String> params) {
        Map<String , String> result = new HashMap<>();
        if ("GET_BOOK_INFO".equalsIgnoreCase(action)) {
            //获取DynamicObject列表
            String fields = "number,name,myg6_bookscore,myg6_bookcomment";
            // Create an empty filter array (no filters)
            QFilter[] filters = new QFilter[0];
            // Load the data
            DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_comment", fields, filters);

            //创建一个JsonArray
            JSONArray jsonArray = new JSONArray();
            for (DynamicObject dynamicObject : dys) {
                //将每一个评价信息加入JSONArray
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("number", dynamicObject.getString("number"));
                jsonObject.put("bookname", dynamicObject.getString("name"));
                jsonObject.put("score", dynamicObject.getString("myg6_bookscore"));
                jsonObject.put("comment", dynamicObject.getString("myg6_bookcomment"));
                jsonArray.add(jsonObject);
            }
            System.out.println(jsonArray.toString());
            //加入resultDynamicObject参数，将JsonArray加入到这个参数当中，然后返回
            result.put("resultDynamicObject", jsonArray.toString());
        }
        return result;
    }
}
