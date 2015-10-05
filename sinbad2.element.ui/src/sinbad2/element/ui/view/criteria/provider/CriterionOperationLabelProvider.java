package sinbad2.element.ui.view.criteria.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.element.criterion.Criterion;

public class CriterionOperationLabelProvider extends ColumnLabelProvider{
	
	@Override
	public String getText(Object element) {
		if(((Criterion) element).isDirect()) {
			return "Media";
		} else {
			return "Accumulate";
		}
	}
}
