package plugins.homepage;

import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.form.control.Timeline;
import kd.bos.form.control.TimelineContentOption;
import kd.bos.form.control.TimelineLabelOption;
import kd.bos.form.control.TimelineOption;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;

import java.text.SimpleDateFormat;
import java.util.*;

public class NewsTimeLine extends AbstractFormPlugin {
    private final static String KEY_TIMELINE = "myg6_timelineap";
    //    private final static String KEY_DATE_FIELD = "kdec_datefield";
    private List<Date> newsTitle = new ArrayList<>();
    private Map<Date, String> newsInfo = new HashMap<>();
    @Override
    public void beforeBindData(EventObject e) {
        // TODO Auto-generated method stub
        super.beforeBindData(e);
        //Date date = (Date) this.getModel().getValue(KEY_DATE_FIELD);
        handTimeLine();
    }

    @Override
    public void registerListener(EventObject e) {
        // TODO Auto-generated method stub
        super.registerListener(e);
        this.addClickListeners(KEY_TIMELINE);
    }

    //    @Override
//    public void click(EventObject evt) {
//        // TODO Auto-generated method stub
//        super.click(evt);
//        System.out.println();
//    }
    private void handTimeLine() {
        String fields = "myg6_title,myg6_datefield";
        //String fields = "creator,myg6_title,myg6_datefield,myg6_viewcount,myg6_largetextfield1,myg6_picturefield1";
        // Create an empty filter array (no filters)
        QFilter[] filters = new QFilter[0];
        // Load the data

        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_avtivity_pub", fields, filters);
        for (DynamicObject dy : dys) {
            String title = dy.getString("myg6_title");
            Date date = dy.getDate("myg6_datefield");
            if (title == null || date == null) {
                continue;
            }
            newsInfo.put(date, title);

        }

        newsTitle.addAll(newsInfo.keySet());

        newsTitle.sort((date1, date2) -> {


            return Long.compare(date2.getTime(), date1.getTime());
        });
        // 输出排序后的书籍信息
        System.out.println("assinfo---------------\n");
        for (Date da : newsTitle) {
            String info = newsInfo.get(da);

            System.out.println(info);
        }
        System.out.println("\nassinfo---------------");

        Timeline timeline = this.getView().getControl(KEY_TIMELINE);
        List<TimelineOption> timelineOptions = new ArrayList<>(Math.max(4, newsInfo.size()));
        int j = 0;
        for(Date da : newsTitle) {

            if(j == 4) {
                break;
            }
            TimelineOption timelineOption = new TimelineOption();
            // 设置内容标题和内容详情
            timelineOption.setContent(new TimelineContentOption(new LocaleString(newsInfo.get(da)),
                    new LocaleString((String) "")));
            // 设置标签标题和标签内容
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String strDate = formatter.format(da);
            timelineOption.setLabel(new TimelineLabelOption(new LocaleString((strDate).substring(0, 10)),
                    new LocaleString((String) (strDate).substring(11))));
            timelineOptions.add(timelineOption);

            // 设置新的时间轴配置，发送给前端
            j ++;
        }
        timeline.setClientTimelineOptions(timelineOptions);

    }
}