package plugins.CreditSystem;

import java.util.ArrayList;
import kd.bos.entity.datamodel.IDataModel;
import kd.bos.form.control.events.ItemClickEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.sdk.plugin.Plugin;

import java.util.EventObject;

/**
 * 动态表单插件
 */
public class LookDishonesty extends AbstractFormPlugin implements Plugin {
    @Override
    public void registerListener(EventObject e) {
        // 注册点击事件
        super.registerListener(e);
        this.addItemClickListeners("tbmain");
    }

    public void itemClick(ItemClickEvent e) {
        super.itemClick(e);
        if (e.getItemKey().equalsIgnoreCase("myg6_buttonshixin")) {
            // 首先得到失信档案记录
            String txt = (String) this.getModel().getValue("myg6_shixin");
            if (txt == null) return;
            // 开始处理失信档案记录
            entryentitySetVal(txt);
        }
    }

    private void entryentitySetVal(String s) {
        // ------------先把s处理好------------

        // Split the string by the delimiter *
        String[] entries = s.split("\\*");

        // Create lists to hold book names and sub numbers
        ArrayList<String> bookNames = new ArrayList<>();
        ArrayList<Integer> subNums = new ArrayList<>();

        // Iterate over each entry
        for (String entry : entries) {
            if (entry.isEmpty()) continue;

            // Extract book name
            int booknameStart = entry.indexOf("bookname: ") + "bookname: ".length();
            int booknameEnd = entry.indexOf("subnum: ");
            String bookname = entry.substring(booknameStart, booknameEnd).trim();

            // Extract sub number
            int subnumStart = booknameEnd + "subnum: ".length();
            String subnum = entry.substring(subnumStart).trim();

            // Add to lists
            bookNames.add(bookname);
            subNums.add(Integer.parseInt(subnum));
        }

        // Convert lists to arrays
        String[] bookNameArray = bookNames.toArray(new String[0]);
        int[] subNumArray = subNums.stream().mapToInt(i -> i).toArray();

        // ------------s处理结束------------

        // 待新增分录的行数
        int size = subNumArray.length;
        IDataModel dataModel = this.getModel();
        int currRowCnt = dataModel.getEntryRowCount("entryentity");
        dataModel.batchCreateNewEntryRow("entryentity", size);
        for (int i = currRowCnt; i < (currRowCnt + size); ++i) {
            int idx = i - currRowCnt;
            dataModel.setValue("myg6_largetextfield", "图书逾期: " + bookNameArray[idx], i);
            dataModel.setValue("myg6_integerfield1", subNumArray[idx], i);
        }
    }
}