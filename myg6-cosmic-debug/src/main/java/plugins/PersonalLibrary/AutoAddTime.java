package plugins.PersonalLibrary;

import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.EventObject;
import java.util.Random;

/**
 * 动态表单插件
 */
public class AutoAddTime extends AbstractFormPlugin implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        // 注册点击事件
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }

    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        if (e.getItemKey().equalsIgnoreCase("myg6_add_time")) {
            Date todayDate = new Date();
            this.getModel().setValue("myg6_date_bg", todayDate);

            LocalDate localTodayDate = todayDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Random rand = new Random();
            int daysToAdd = rand.nextInt(21) + 20; // Generates a random number between 20 and 40

            LocalDate localNextDate = localTodayDate.plusDays(daysToAdd);
            Date nextDate = Date.from(localNextDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
            this.getModel().setValue("myg6_date_ed", nextDate);
        }
    }
}