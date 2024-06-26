package plugins.CreditSystem;

import kd.bos.context.RequestContext;
import kd.bos.form.events.SetFilterEvent;
import kd.bos.list.plugin.AbstractListPlugin;
import kd.bos.orm.query.QFilter;
import kd.sdk.plugin.Plugin;

public class ListFilter extends AbstractListPlugin implements Plugin {
    @Override
    public void setFilter(SetFilterEvent e) {
        RequestContext rc = RequestContext.get();
        String nowUser = rc.getUserName();
        e.addCustomQFilter(new QFilter("myg6_textfield", "=", nowUser));
    }
}