//package plugins.homepage;
//
//import kd.bos.dataentity.entity.LocaleString;
//import kd.bos.form.control.Timeline;
//import kd.bos.form.control.TimelineContentOption;
//import kd.bos.form.control.TimelineLabelOption;
//import kd.bos.form.control.TimelineOption;
//import kd.bos.form.plugin.AbstractFormPlugin;
//
//import java.text.SimpleDateFormat;
//import java.util.*;
//
//public class NewsTimeLine extends AbstractFormPlugin {
//    private final static String KEY_TIMELINE = "myg6_timelineap";
////    private final static String KEY_DATE_FIELD = "kdec_datefield";
//
//    @Override
//    public void beforeBindData(EventObject e) {
//        // TODO Auto-generated method stub
//        super.beforeBindData(e);
//        //Date date = (Date) this.getModel().getValue(KEY_DATE_FIELD);
//        handTimeLine();
//    }
//
//    @Override
//    public void registerListener(EventObject e) {
//        // TODO Auto-generated method stub
//        super.registerListener(e);
//        this.addClickListeners(KEY_TIMELINE);
//    }
//
////    @Override
////    public void click(EventObject evt) {
////        // TODO Auto-generated method stub
////        super.click(evt);
////        System.out.println();
////    }
//    private void handTimeLine() {
//
//        Map<String, Object> dayTrip = this.getData();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//        List<Map<String, Object>> datas = (List<Map<String, Object>>) dayTrip.get(str);
//        if (datas != null && datas.size() > 0) {
//            Timeline timeline = this.getView().getControl(KEY_TIMELINE);
//            // 新的时间轴选项配置
//            List<TimelineOption> timelineOptions = new ArrayList<>(datas.size());
//            for (Map<String, Object> data : datas) {
//                TimelineOption timelineOption = new TimelineOption();
//                // 设置内容标题和内容详情
//                timelineOption.setContent(new TimelineContentOption(new LocaleString((String) data.get("title")),
//                        new LocaleString((String) data.get("content"))));
//                // 设置标签标题和标签内容
//                timelineOption.setLabel(new TimelineLabelOption(new LocaleString((String) data.get("day")),
//                        new LocaleString((String) data.get("time"))));
//                timelineOptions.add(timelineOption);
//            }
//            // 设置新的时间轴配置，发送给前端
//            timeline.setClientTimelineOptions(timelineOptions);
//        }
//
//    }
//
//    private Map<String, Object> getData() {
//        // 用户行程信息数据
//        Map<String, Object> dayTrip = new HashMap<String, Object>();
//        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
//        Map<String, Object> map = new HashMap<String, Object>();
//
//        map.put("day", "03-14");
//        map.put("time", "09:00");
//        map.put("title", "深圳-厦门");
//        map.put("content", "高铁，五人，预计3小时");
//        datas.add(map);
//        map = new HashMap<String, Object>();
//        map.put("day", "03-14");
//        map.put("time", "12:00");
//        map.put("title", "午餐");
//        map.put("content", "到达厦门，安排午餐");
//        datas.add(map);
//        map = new HashMap<String, Object>();
//        map.put("day", "03-14");
//        map.put("time", "14:00");
//        map.put("title", "入住");
//        map.put("content", "餐后到达预定酒店安排入住");
//        datas.add(map);
//        dayTrip.put("2022-03-14", datas);
//        datas = new ArrayList<Map<String, Object>>();
//        map = new HashMap<String, Object>();
//        map.put("day", "03-15");
//        map.put("time", "09:00");
//        map.put("title", "酒店-公司");
//        map.put("content", "参与XX会议");
//        datas.add(map);
//        map = new HashMap<String, Object>();
//        map.put("day", "03-15");
//        map.put("time", "12:00");
//        map.put("title", "午餐");
//        map.put("content", "会议午餐，会后统一安排在XX就餐");
//        datas.add(map);
//        map = new HashMap<String, Object>();
//        map.put("day", "03-15");
//        map.put("time", "14:00");
//        map.put("title", "会议");
//        map.put("content", "餐后进入会场");
//        datas.add(map);
//        map = new HashMap<String, Object>();
//        map.put("day", "03-15");
//        map.put("time", "18:00");
//        map.put("title", "酒店");
//        map.put("content", "酒店整理会议纪要");
//        datas.add(map);
//        dayTrip.put("2022-03-15", datas);
//        return dayTrip;
//    }
//
//}
