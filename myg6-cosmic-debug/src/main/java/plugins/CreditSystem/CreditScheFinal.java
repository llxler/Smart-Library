package plugins.CreditSystem;

import kd.bos.context.RequestContext;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.dataentity.entity.LocaleString;
import kd.bos.exception.KDException;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.schedule.executor.AbstractTask;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import kd.bos.servicehelper.operation.SaveServiceHelper;
import kd.bos.servicehelper.user.UserServiceHelper;
import kd.bos.servicehelper.workflow.MessageCenterServiceHelper;
import kd.bos.workflow.engine.msg.info.MessageInfo;
import kd.sdk.plugin.Plugin;

import java.util.*;

// sch_taskdefine
public class CreditScheFinal extends AbstractTask implements Plugin {
    @Override
    public void execute(RequestContext requestContext, Map<String, Object> map) throws KDException {
        // 获取DynamicObject列表
        String fields = "billno,creator,myg6_nameofbook,myg6_date_ed";
        // Create an empty filter array   (no filters)
        QFilter[] filters = new QFilter[0];
        // Load the data
        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_subscribe", fields, filters);

        String info = "";

        // 获取今天的日期
        Date todayDate = new Date();
        System.out.println("todayDate: " + todayDate);
        // 获取昨天的日期
        Date yesterdayDate = new Date(todayDate.getTime() - 24 * 60 * 60 * 1000);

        for (DynamicObject o : dys) {
            // 得出图书的预定记录
            System.out.println("\n--------auto task info----------\n");
            QFilter qFilter = new QFilter("billno", QCP.equals, o.getString("billno"));
            DynamicObject single = BusinessDataServiceHelper.loadSingle("myg6_book_subscribe", new QFilter[]{qFilter});

            // 该预定记录的时间
            Date endDate = single.getDate("myg6_date_ed");
            // 如果没有endDate，跳过
            if (endDate == null) continue;

            System.out.println("endDate: " + endDate);
            DynamicObject creatorObj = (DynamicObject) single.get("creator");
            String creator = creatorObj.getString("name");
            System.out.println("creator: " + creator);

            DynamicObject book = (DynamicObject) single.get("myg6_nameofbook");
            String nameofbook = book.getString("name");
            System.out.println("nameofbook: " + nameofbook);

            // 如果endDate在今天之前，说明逾期了
            if (endDate.before(todayDate)) {
                // 添加信息
                info += "用户：" + creator + "，书名：" + nameofbook + "\n";

                // 获取这个人的信誉表单
                QFilter qFilter2 = new QFilter("myg6_textfield", QCP.equals, creator);
                String fields2 = "myg6_textfield,myg6_integerfield,myg6_shixin";
                DynamicObject credituser = BusinessDataServiceHelper.loadSingle("myg6_credibility_table", fields2, new QFilter[]{qFilter2});

                // 分数 -20
                String scoreString = credituser.getString("myg6_integerfield");
                int score = Integer.parseInt(scoreString);

                // 获取5-20的随机值subnum
                Random random = new Random();
                int subnum = random.nextInt(16) + 5; // 生成5到20之间的随机数
                credituser.set("myg6_integerfield", score - subnum);

                // 塞入信息到文本记录
                String newinfo = credituser.getString("myg6_shixin");
                newinfo += "bookname: " + nameofbook + "subnum: " + subnum + "*";
                credituser.set("myg6_shixin", newinfo);

                // 修改整个
                SaveServiceHelper.update(credituser);

            } else if (endDate.equals(yesterdayDate)) {
                // 如果明天就是endDate，提前一天提醒
                info += "用户：" + creator + "，书名：" + nameofbook + "，明天就要还书了！\n";
            }
            System.out.println("\n----------info over----------\n");
        }
        // 没有人逾期不归还
        if (info.equals("")) return;

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
        messageInfo.setTag("图书逾期相关信息");

        // 发送消息
        MessageCenterServiceHelper.sendMessage(messageInfo);
    }
}