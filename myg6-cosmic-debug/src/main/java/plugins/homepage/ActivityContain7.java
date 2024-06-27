package plugins.homepage;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kd.bos.dataentity.serialization.SerializationUtils;
import kd.bos.ext.form.container.MessageCarouselContainer;
import kd.bos.form.control.events.BeforeClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;

public class ActivityContain7 extends AbstractFormPlugin {

    public void registerListener(EventObject event) {
        // 注册监听
        MessageCarouselContainer mcc = this.getView().getControl("myg6_messagecarouselconta");
        mcc.addClickListener(this);
    }
    // 行点击事件 发生之前，可以取消事件继续
    @Override
    public void beforeClick(BeforeClickEvent evt) {
//        super.beforeClick(evt);
//        Control ctrl = (Control) evt.getSource();
//        String key = ctrl.getKey();
//        if("kdec_messagecarouselcontainerap".equals(key))
//        {
//            int index = ((Integer)evt.getParamsMap().get("rowKey")).intValue();
//            String data = getPageCache().get("data");
//            if(StringUtils.isEmpty(data))
//                return;
//
//            List<Map<String, Object>> msgList = SerializationUtils.fromJsonString(data, List.class);
//            Map<String, Object> dataMap = msgList.get(index);
//            Map<String, Object> propMap = (Map<String, Object>)dataMap.get("kdec_labelap");
//            if(propMap == null || propMap.get("text") == null || kd.bos.util.StringUtils.isEmpty(propMap.get("text").toString()))
//                return;
//            this.getView().showTipNotification(propMap.get("text").toString());
////   this.getView().openUrl(propMap.get("text").toString());
//        }
    }
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

        // 图片控件url数组
        String[] urls = new String[] {"/images/pc/cardbackground/hr_jtyh_taiwan.png",
                "/images/pc/cardbackground/hr_jtyh_hz1.png",
                "/images/pc/cardbackground/card_xingyebank_280_150.png"};
        // 标签控件显示内容数组
        String[] texts = new String[] {"点击我！！！！！！---1", "点击我！！！！！！---2", "点击我！！！！！！---3"};
        String[] dates = new String[] {"2020-01-01", "2020-01-02", "2020-01-03"};
        // 放内容map
        Map<String, Object> propsMap = null;
        // 控件map，key为控件类型
        Map<String, Object> controlsMap = null;

        for (int i = 0; i < urls.length; i++) {
            propsMap = new HashMap<>();
            controlsMap = new HashMap<>();

            propsMap.put("text", texts[i]);
            controlsMap.put("myg6_labeltext", propsMap);

            propsMap = new HashMap<>();
            propsMap.put("text", dates[i]);
            System.out.println("fuck" + dates[i] + i);
            controlsMap.put("myg6_labeltime", propsMap);
//    String url = UrlService.getImageFullUrl( urls[i]);
            propsMap = new HashMap<>();
            propsMap.put("imageKey", urls[i]);
            controlsMap.put("myg6_imageap", propsMap);

            data.add(controlsMap);
        }
        mcc.setData(data);
        getPageCache().put("data", SerializationUtils.toJsonString(data));


    }
    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);

    }
}