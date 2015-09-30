package sinbad2.element.ui.view.mecs.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.element.mec.MEC;

public class MECIdLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object obj) {
		return ((MEC) obj).getId();
	}
	
}
