package plugins.teach;

import com.alibaba.druid.util.StringUtils;
import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.exception.KDException;
import kd.bos.orm.query.QFilter;
import kd.bos.schedule.executor.AbstractTask;
import kd.bos.servicehelper.BusinessDataServiceHelper;
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

public class ReturnSeatSchedule extends AbstractTask {
    @Override
    public void execute(RequestContext requestContext, Map<String, Object> map) throws KDException {
        // 获取座位的预定信息
        String fields = "number,myg6_basedatafield,myg6_basedatafield_seat,myg6_timefield_end";
        QFilter[] filters = new QFilter[0];
        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_seat_apply", fields, filters);

        String info = "这些座位已经逾期了：\n";

        for (DynamicObject single : dys) {
            // 获取该座位名称
            DynamicObject seat = (DynamicObject) single.get("myg6_basedatafield_seat");
            String seatState = seat.getString("myg6_combofield");

            // 如果已经是空闲状态了, 则跳过
            if (StringUtils.equals(seatState, "0")) continue;

            // 获取该座位的结束时间（应为获取的edTime是秒数，所以需要转化为LocalTime）
            String edTime = single.getString("myg6_timefield_end");
            Duration duration = Duration.ofSeconds(Long.parseLong(edTime));
            LocalTime time = LocalTime.ofSecondOfDay(duration.getSeconds());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String endTime = time.format(formatter);

            // 同样的方式用来获取当前时间
            LocalTime now = LocalTime.now();
            String nowTime = now.format(formatter);

            // 比较两个时间，若结束时间在当前时间之后，则跳过
            LocalTime now_time = LocalTime.parse(nowTime);
            LocalTime end_time = LocalTime.parse(endTime);
            System.out.println("time info" + now_time + end_time);
            if (end_time.isAfter(now_time)) continue;

            String seatNumber = seat.getString("number");
            System.out.println("seatName info" + seatNumber);

            // 添加信息
            info += seatNumber + "\n";

            // 更改座位状态，并保存
            seat.set("myg6_combofield", 0);
            SaveServiceHelper.update(seat);
        }
        // 如果当前没有座位逾期，则直接返回
        if (info.equals("这些座位已经逾期了：\n")) return;

        // 开始发送站内消息
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