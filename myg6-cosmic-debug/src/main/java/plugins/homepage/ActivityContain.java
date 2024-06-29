package plugins.homepage;

import java.text.SimpleDateFormat;
import java.util.*;

import com.alibaba.dubbo.common.utils.StringUtils;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.serialization.SerializationUtils;
import kd.bos.ext.form.container.MessageCarouselContainer;
import kd.bos.form.FormShowParameter;
import kd.bos.form.ShowType;
import kd.bos.form.control.Control;
import kd.bos.form.control.events.BeforeClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

public class ActivityContain extends AbstractFormPlugin {

    private Map<Date, Map<String, String>> newsInfo = new HashMap<>();
    private List<Date> newsDate = new ArrayList<>();

    public void registerListener(EventObject event) {
        // 注册监听
        MessageCarouselContainer mcc = this.getView().getControl("myg6_messagecarouselconta");
        mcc.addClickListener(this);
    }
    // 行点击事件 发生之前，可以取消事件继续
    @Override
    public void beforeClick(BeforeClickEvent evt) {
        super.beforeClick(evt);
        Control ctrl = (Control) evt.getSource();
        String key = ctrl.getKey();
        if ("myg6_messagecarouselconta".equals(key)) {
            int index = ((Integer) evt.getParamsMap().get("rowKey")).intValue();
            String data = getPageCache().get("data");
            if (StringUtils.isEmpty(data))
                return;

            List<Map<String, Object>> msgList = SerializationUtils.fromJsonString(data, List.class);
            Map<String, Object> dataMap = msgList.get(index);
            Map<String, Object> propMap = (Map<String, Object>) dataMap.get("myg6_labeltext");
            if (propMap == null || propMap.get("text") == null || kd.bos.util.StringUtils.isEmpty(propMap.get("text").toString()))
                return;
            FormShowParameter billShowParameter = new FormShowParameter();
            billShowParameter.setFormId("myg6_activity_rendering");
            billShowParameter.getOpenStyle().setShowType(ShowType.Modal);

            billShowParameter.setCustomParam("openTitle", propMap.get("text"));
            System.out.println("woshisb" + propMap.get("text"));
            //弹出表单和本页面绑定
            this.getView().showForm(billShowParameter);


        }
    }

    private static final String MY_IP = "10.21.204.111";
    @Override
    public void beforeBindData(EventObject e) {
        // TODO Auto-generated method stub
        super.beforeBindData(e);
        /**
         * 向轮播容器中动态添加三条数据
         * 包含 图片、标签控件。
         */
        MessageCarouselContainer mcc = this.getControl("myg6_messagecarouselconta");
        List<Map<String, Object>> data = new ArrayList<>();
        String fields = "myg6_title,myg6_datefield,myg6_picturefield1";
        QFilter[] filters = new QFilter[0];
        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_avtivity_pub", fields, filters);
        for (DynamicObject dy : dys) {
            String title = dy.getString("myg6_title");
            Date date = dy.getDate("myg6_datefield");
            String url = dy.getString("myg6_picturefield1");
            if (title == null || date == null) {
                continue;
            }
            newsInfo.put(date, new HashMap<String, String>() {{
                put(title, url);
            }});
        }
        newsDate.addAll(newsInfo.keySet());
        newsDate.sort((date1, date2) -> {
            return Long.compare(date2.getTime(), date1.getTime());
        });
        if (newsDate.size() > 3) {
            newsDate = newsDate.subList(0, 3);
        }
        Collections.reverse(newsDate);
        // 图片控件url数组
        //String[] urls = new String[] {"isv/myg6/globalimgs/images/书香筑梦图片.png?v=1.0","isv/myg6/globalimgs/images/书香筑梦图片.png?v=1.0","isv/myg6/globalimgs/images/书香筑梦图片.png?v=1.0","isv/myg6/globalimgs/images/书香筑梦图片.png?v=1.0","isv/myg6/globalimgs/images/书香筑梦图片.png?v=1.0","isv/myg6/globalimgs/images/书香筑梦图片.png?v=1.0"};
        // 标签控件显示内容数组
        //String[] texts = new String[] {"字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字", "点击我！！！！！！---2", "点击我！！！！！！---3","点击我！！！！！！---4", "点击我！！！！！！---5", "点击我！！！！！！---6"};
        //String[] dates = new String[] {"2020-01-01", "2020-01-02", "2020-01-03","2020-01-01", "2020-01-02", "2020-01-03"};
        // 放内容map
        Map<String, Object> propsMap = null;
        // 控件map，key为控件类型
        Map<String, Object> controlsMap = null;
        int j = 0;
        for (Date da : newsDate) {
            if(j == 3)  break;
            propsMap = new HashMap<>();
            controlsMap = new HashMap<>();
            String texti = newsInfo.get(da).keySet().iterator().next();

//            texti = "." + texti + "     ";
//            int t = (44 - texti.length()) * 3;
//            for(int p = 0; p < texti.length(); p++) {
//                if(!isChinese(texti.charAt(p))) t += 2;
//            }
//            for(int k = 0;k < t - 1;k++)    texti += " ";
//            texti += ".";
//            String tt = "字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字字";
//            System.out.println("shit" + tt + "shit");
//            System.out.println(tt.length());
//            System.out.println("shit" + texti + "shit");

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = formatter.format(da);
            String urli = newsInfo.get(da).values().iterator().next();
            propsMap.put("text", texti);
            controlsMap.put("myg6_labeltext", propsMap);

            propsMap = new HashMap<>();
            propsMap.put("text", strDate.substring(0, 10));
//            System.out.println("fuck" + dates[i] + i);
            controlsMap.put("myg6_labeltime", propsMap);

            propsMap = new HashMap<>();
            propsMap.put("imageKey","http://"+ MY_IP +":8881/ierp/attachment/downloadImage/" + urli);
            controlsMap.put("myg6_imageap", propsMap);

            data.add(controlsMap);
            j ++;
        }
        mcc.setData(data);
        getPageCache().put("data", SerializationUtils.toJsonString(data));


    }
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
    }
}