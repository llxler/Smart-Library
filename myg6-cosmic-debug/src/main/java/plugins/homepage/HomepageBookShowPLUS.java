package plugins.homepage;

import kd.bos.bill.BillShowParameter;
import kd.bos.bill.OperationStatus;
import kd.bos.dataentity.entity.DynamicObject;
import kd.bos.entity.ListboxItem;
import kd.bos.ext.form.control.Listbox;
import kd.bos.form.ShowType;
import kd.bos.form.control.Control;
import kd.bos.form.control.Label;
import kd.bos.form.control.events.ListboxClickListener;
import kd.bos.form.control.events.ListboxEvent;
import kd.bos.form.plugin.AbstractFormPlugin;
import kd.bos.orm.query.QCP;
import kd.bos.orm.query.QFilter;
import kd.bos.servicehelper.BusinessDataServiceHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.*;


public class HomepageBookShowPLUS extends AbstractFormPlugin implements ListboxClickListener {

    private final String LISTBOX = "myg6_listboxap";
    private List<String> bookTitles = new ArrayList<>();
    private final String IMAGEID = "myg6_picturefield";
    private final String LABELID = "myg6_labelap";
    private Map<String, Map<String, String>> booksInfo = new HashMap<>();

    private void init() {
        //获取DynamicObject列表
        String fields = "name,myg6_picturefield,myg6_subcribe_cnt";
        // Create an empty filter array (no filters)
        QFilter[] filters = new QFilter[0];
        // Load the data

        DynamicObject[] dys = BusinessDataServiceHelper.load("myg6_book_list", fields, filters);
        for (DynamicObject dy : dys) {
            String bookName = dy.getString("name");
            String picture = dy.getString("myg6_picturefield");
            String cnt = dy.getString("myg6_subcribe_cnt");
            if (bookName == null || picture == null || cnt == null) {
                continue;
            }
            booksInfo.put(bookName, new HashMap<String, String>() {{
                put(picture, cnt);
            }});
        }
        // 根据借阅次数排序书籍列表
        bookTitles.addAll(booksInfo.keySet());
        bookTitles.sort((title1, title2) -> {
            String count1Str = booksInfo.get(title1).values().iterator().next();
            String count2Str = booksInfo.get(title2).values().iterator().next();
            int count1 = Integer.parseInt(count1Str);
            int count2 = Integer.parseInt(count2Str);
            return count2 - count1; // 降序排列
        });
        // 输出排序后的书籍信息
        System.out.println("assinfo---------------\n");
        for (String title : bookTitles) {
            Map<String, String> info = booksInfo.get(title);
            String coverUrl = info.keySet().iterator().next();
            String borrowCountStr = info.values().iterator().next();
            System.out.println("书名: " + title +
                    ", 图像URL: " + coverUrl +
                    ", 借阅次数: " + borrowCountStr);
        }
        System.out.println("\nassinfo---------------");
    }

    @Override
    public void afterCreateNewData(EventObject e) {
        super.afterCreateNewData(e);
        this.getView().setVisible(true, "myg6_hot");
        this.getView().setVisible(false, "myg6_new");
        this.getView().setVisible(false, "myg6_small");
        // 数据库信息初始化到booksinfo
        init();
        // 设置热门书籍
        setHot();
        // 设置新书
        setNew();
        // 设置小众
        setSmall();
    }

    public void registerListener(EventObject event) {
        // 注册监听
        super.registerListener(event);
        Listbox box = this.getView().getControl(LISTBOX);
        box.addListboxClickListener(this);
        for (int i = 1;i <= 5;i++) {
            this.addClickListeners(LABELID + "h" + i);
            this.addClickListeners(LABELID + "n" + i);
            this.addClickListeners(LABELID + "s" + i);
        }
    }

    @Override
    public void afterBindData(EventObject e) {
        super.afterBindData(e);
        Listbox box = this.getView().getControl(LISTBOX);
        ListboxItem listboxItem1 = new ListboxItem("1", "近期热门");
        ListboxItem listboxItem2 = new ListboxItem("2", "新书上架");
        ListboxItem listboxItem3 = new ListboxItem("3", "小众热门");
        List<ListboxItem> itemlist = new ArrayList<>();
        itemlist.add(listboxItem1);
        itemlist.add(listboxItem2);
        itemlist.add(listboxItem3);
        box.addItems(itemlist);
    }

    public void click(EventObject evt) {
        init();
        super.click(evt);
        //获取被点击的控件对象
        Control source = (Control) evt.getSource();
        if (source != null) {
            // 初始化一个新表单
            BillShowParameter billShowParameter = new BillShowParameter();
            billShowParameter.setFormId("myg6_book_list");
            billShowParameter.getOpenStyle().setShowType(ShowType.Modal);
            billShowParameter.setStatus(OperationStatus.VIEW);

            for (int j = 0; j < 3; ++j) {
                boolean flag = false;
                for (int i = 0;i < 5;++i) {
                    String label = "myg6_labelap" + (j == 0 ? "h" : j == 1 ? "n" : "s") + (i + 1);
                    if (StringUtils.equals(label, source.getKey())) {
                        flag = true;
                        String bookname = "";
                        // 分类讨论
                        if (j == 0) {
                            int idx = 0;
                            for (String title : bookTitles) {
                                // 设置标签 -> url -> 借阅次数
                                if (idx == i) {
                                    bookname = title;
                                    break;
                                }
                                ++idx;
                            }
                        } else if (j == 1) {
                            // 倒序遍历bookTitles
                            int idx = 0;
                            ListIterator<String> iterator = bookTitles.listIterator(bookTitles.size());
                            while (iterator.hasPrevious())  {
                                String title = iterator.previous();
                                // 设置标签 -> url -> 借阅次数
                                if (idx == i) {
                                    bookname = title;
                                    break;
                                }
                                ++idx;
                            }
                        } else {
                            // 直接暴力讨论
                            if (i == 0) {
                                // 先set一个末尾
                                ListIterator<String> iterator = bookTitles.listIterator(bookTitles.size());
                                while (iterator.hasPrevious())  {
                                    String title = iterator.previous();
                                    bookname = title;
                                    break;
                                }
                            } else if (i == 1) {
                                bookname = bookTitles.get(1);
                            } else if (i == 2) {
                                bookname = bookTitles.get(3);
                            } else if (i == 3) {
                                bookname = bookTitles.get(5);
                            } else {
                                bookname = bookTitles.get(7);
                            }
                        }
                        // 找到了bookname
                        QFilter qFilter = new QFilter("name", QCP.equals, bookname);
                        DynamicObject bookitself = BusinessDataServiceHelper.loadSingle("myg6_book_list", new QFilter[]{qFilter});
                        Long pkId = (Long) bookitself.getPkValue();
                        billShowParameter.setPkId(pkId);
                        this.getView().showForm(billShowParameter);
                    }
                    if (flag) break;
                }
                if (flag) break;
            }

        }
    }
    @Override
    public void listboxClick(ListboxEvent listboxEvent) {
        String cur = listboxEvent.getItemId();
        if (StringUtils.equals(cur, "1")) {
            this.getView().setVisible(true, "myg6_hot");
            this.getView().setVisible(false, "myg6_new");
            this.getView().setVisible(false, "myg6_small");
        } else if(StringUtils.equals(cur, "2")) {
            this.getView().setVisible(false, "myg6_hot");
            this.getView().setVisible(true, "myg6_new");
            this.getView().setVisible(false, "myg6_small");
        } else if(StringUtils.equals(cur, "3")) {
            this.getView().setVisible(false, "myg6_hot");
            this.getView().setVisible(false, "myg6_new");
            this.getView().setVisible(true, "myg6_small");
        }

    }
    // bookName -> label, Url -> model, 借阅次数 -> 次数
    private void setHot() {
        int i = 0;
        for (String title : bookTitles) {
            Map<String, String> info = booksInfo.get(title);
            String coverUrl = info.keySet().iterator().next();
            String borrowCountStr = info.values().iterator().next();
            // 设置标签 -> url -> 借阅次数
            if (i == 0) {
                Label label1 = this.getView().getControl("myg6_labelaph1");
                Label label2 = this.getView().getControl("myg6_labelaph11");
                label1.setText(title);
                label2.setText("借阅次数:" + borrowCountStr);
                this.getModel().setValue("myg6_picturefield0", coverUrl);
            } else if (i == 1) {
                Label label1 = this.getView().getControl("myg6_labelaph2");
                Label label2 = this.getView().getControl("myg6_labelaph22");
                label1.setText(title);
                label2.setText("借阅次数:" + borrowCountStr);
                this.getModel().setValue("myg6_picturefield1", coverUrl);
            } else if (i == 2) {
                Label label1 = this.getView().getControl("myg6_labelaph3");
                Label label2 = this.getView().getControl("myg6_labelaph33");
                label1.setText(title);
                label2.setText("借阅次数:" + borrowCountStr);
                this.getModel().setValue("myg6_picturefield2", coverUrl);
            } else if (i == 3) {
                Label label1 = this.getView().getControl("myg6_labelaph4");
                Label label2 = this.getView().getControl("myg6_labelaph44");
                label1.setText(title);
                label2.setText("借阅次数:" + borrowCountStr);
                this.getModel().setValue("myg6_picturefield3", coverUrl);
            } else if (i == 4) {
                Label label1 = this.getView().getControl("myg6_labelaph5");
                Label label2 = this.getView().getControl("myg6_labelaph55");
                label1.setText(title);
                label2.setText("借阅次数:" + borrowCountStr);
                this.getModel().setValue("myg6_picturefield4", coverUrl);
            } else {
                break;
            } ++i;
        }
    }

    private void setNew() {
        int i = 0;
        // 倒序遍历bookTitles
        ListIterator<String> iterator = bookTitles.listIterator(bookTitles.size());
        while (iterator.hasPrevious())  {
            String title = iterator.previous();
            Map<String, String> info = booksInfo.get(title);
            String coverUrl = info.keySet().iterator().next();
            String borrowCountStr = info.values().iterator().next();
            // 设置标签 -> url -> 借阅次数
            if (i == 0) {
                Label label1 = this.getView().getControl("myg6_labelapn1");
                Label label2 = this.getView().getControl("myg6_labelapn11");
                label1.setText(title);
                label2.setText("借阅次数:" + borrowCountStr);
                this.getModel().setValue("myg6_picturefield01", coverUrl);
            } else if (i == 1) {
                Label label1 = this.getView().getControl("myg6_labelapn2");
                Label label2 = this.getView().getControl("myg6_labelapn22");
                label1.setText(title);
                label2.setText("借阅次数:" + borrowCountStr);
                this.getModel().setValue("myg6_picturefield02", coverUrl);
            } else if (i == 2) {
                Label label1 = this.getView().getControl("myg6_labelapn3");
                Label label2 = this.getView().getControl("myg6_labelapn33");
                label1.setText(title);
                label2.setText("借阅次数:" + borrowCountStr);
                this.getModel().setValue("myg6_picturefield03", coverUrl);
            } else if (i == 3) {
                Label label1 = this.getView().getControl("myg6_labelapn4");
                Label label2 = this.getView().getControl("myg6_labelapn44");
                label1.setText(title);
                label2.setText("借阅次数:" + borrowCountStr);
                this.getModel().setValue("myg6_picturefield04", coverUrl);
            } else if (i == 4) {
                Label label1 = this.getView().getControl("myg6_labelapn5");
                Label label2 = this.getView().getControl("myg6_labelapn55");
                label1.setText(title);
                label2.setText("借阅次数:" + borrowCountStr);
                this.getModel().setValue("myg6_picturefield05", coverUrl);
            } else {
                break;
            } ++i;
        }
    }

    private void setSmall() {
        int i = 0;
        // 先set一个末尾
        ListIterator<String> iterator = bookTitles.listIterator(bookTitles.size());
        while (iterator.hasPrevious())  {
            String title = iterator.previous();
            Map<String, String> info = booksInfo.get(title);
            String coverUrl = info.keySet().iterator().next();
            String borrowCountStr = info.values().iterator().next();
            Label label1 = this.getView().getControl("myg6_labelaps1");
            Label label2 = this.getView().getControl("myg6_labelaps11");
            label1.setText(title);
            label2.setText("借阅次数:" + borrowCountStr);
            this.getModel().setValue("myg6_picturefield06", coverUrl);
            break;
        }

        // 再顺序游走set
        for (int j = 1; ; j += 2)  {
            String title = bookTitles.get(j);
            Map<String, String> info = booksInfo.get(title);
            String coverUrl = info.keySet().iterator().next();
            String borrowCountStr = info.values().iterator().next();
            // 设置标签 -> url -> 借阅次数
            if (i == 0) {
                Label label1 = this.getView().getControl("myg6_labelaps2");
                Label label2 = this.getView().getControl("myg6_labelaps22");
                label1.setText(title);
                label2.setText("借阅次数:" + borrowCountStr);
                this.getModel().setValue("myg6_picturefield07", coverUrl);
            } else if (i == 1) {
                Label label1 = this.getView().getControl("myg6_labelaps3");
                Label label2 = this.getView().getControl("myg6_labelaps33");
                label1.setText(title);
                label2.setText("借阅次数:" + borrowCountStr);
                this.getModel().setValue("myg6_picturefield08", coverUrl);
            } else if (i == 2) {
                Label label1 = this.getView().getControl("myg6_labelaps4");
                Label label2 = this.getView().getControl("myg6_labelaps44");
                label1.setText(title);
                label2.setText("借阅次数:" + borrowCountStr);
                this.getModel().setValue("myg6_picturefield09", coverUrl);
            } else if (i == 3) {
                Label label1 = this.getView().getControl("myg6_labelaps5");
                Label label2 = this.getView().getControl("myg6_labelaps55");
                label1.setText(title);
                label2.setText("借阅次数:" + borrowCountStr);
                this.getModel().setValue("myg6_picturefield010", coverUrl);
            } else {
                break;
            } ++i;
        }
    }

}