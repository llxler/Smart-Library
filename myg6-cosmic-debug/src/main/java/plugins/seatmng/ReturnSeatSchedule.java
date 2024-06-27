package plugins.seatmng;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.exception.KDException;
import kd.bos.orm.query.QFilter;
import kd.bos.schedule.executor.AbstractTask;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.DeleteServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;
import kd.bos.servicehelper.workflow.MessageCenterServiceHelper;
import kd.bos.workflow.engine.msg.info.MessageInfo;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// sch_taskdefine
public class ReturnSeatSchedule extends AbstractTask {
    @Override
    public void execute(RequestContext requestContext, Map<String, Object> map) throws KDException {
        // 获取DynamicObject列表
        String fields = "number,myg6_basedatafield,myg6_basedatafield_seat,myg6_timefield_end";
        // Create an empty filter array   (no filters)
        QFilter[] filters = new QFilter[0];
        // Load the data
        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_seat_apply", fields, filters);

        String info = "这些座位已经逾期了：\n";

        for (DynamicObject single : dys) {
            System.out.println("auto task info");
            String billNo = single.getString("number");
            if (billNo.startsWith("A00") && !billNo.equals("A000037")) {
                // 获取该座位的结束时间
                String edTime = single.getString("myg6_timefield_end");
                System.out.println("edTime info" + edTime);
                Duration duration = Duration.ofSeconds(Long.parseLong(edTime));
                LocalTime time = LocalTime.ofSecondOfDay(duration.getSeconds());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
                String endTime = time.format(formatter);

                // 获取当前时间并转化成XX:XX:XX格式 给 String nowTime
                DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("HH:mm:ss");
                LocalTime now = LocalTime.now();
                String nowTime = now.format(formatter1);

                // 比较两个时间
                LocalTime nowyeah = LocalTime.parse(nowTime);
                LocalTime endyeah = LocalTime.parse(endTime);
                System.out.println("time info" + nowyeah + endyeah);

                if (endyeah.isAfter(nowyeah)) continue;

                // 获取该座位名称
                DynamicObject seat = (DynamicObject) single.get("myg6_basedatafield_seat");
                String seatState = seat.getString("myg6_combofield");
                
                // 如果已经是空闲状态了, 则跳过
                if (seatState == "0") continue;

                String seatNumber = seat.getString("number");
                System.out.println("seatName info" + seatNumber);

                // 添加信息, 并修改座位的状态为可用
                info += seatNumber + "\n";
                seat.set("myg6_combofield", 0);
                SaveServiceHelper.update(seat);

                // TODO: 删除申请单

            } else {
                System.out.println("跳过了这些");
                System.out.println(single.getString("myg6_basedatafield"));
                System.out.println(single.getString("myg6_basedatafield_seat"));
                System.out.println(single.getString("myg6_timefield_end"));
            }
        }
        if (info.equals("这些座位已经逾期了：\n")) return;
        // 发送站内消息
        MessageInfo messageInfo = new MessageInfo();
        LocaleString title = new LocaleString();
        title.setLocaleValue_zh_CN("座位逾期提示");
        messageInfo.setMessageTitle(title);
        LocaleString content = new LocaleString();
        content.setLocaleValue_zh_CN(info);
        messageInfo.setMessageContent(content);

        //获取当前的业务单元
        long orgId = requestContext.getOrgId();

        // 获取业务单元中所有的用户
        List<Long> allUsersOfOrg = UserServiceHelper.getAllUsersOfOrg(orgId);
        ArrayList<Long> ids = new ArrayList<Long>();

        // 获取当前登录用户id
        ids.add(requestContext.getCurrUserId());
        messageInfo.setUserIds(ids);
        messageInfo.setType(MessageInfo.TYPE_MESSAGE);
        messageInfo.setTag("座位逾期");

        // 发送消息
        MessageCenterServiceHelper.sendMessage(messageInfo);
    }
}