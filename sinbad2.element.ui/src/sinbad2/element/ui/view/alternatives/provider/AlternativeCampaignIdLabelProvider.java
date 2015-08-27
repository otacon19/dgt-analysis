package sinbad2.element.ui.view.alternatives.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.element.alternative.Alternative;

public class AlternativeCampaignIdLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return (((Alternative) element).getId());
	}

}
