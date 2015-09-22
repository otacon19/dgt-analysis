package sinbad2.element.ui.view.mecs.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.element.alternative.Alternative;

public class MECIdDialogLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return ((Alternative) ((Object[]) element)[0]).getId();
	}

}
