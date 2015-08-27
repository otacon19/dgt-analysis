package sinbad2.element.ui.view.campaigns.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.element.campaigns.Campaign;

public class CampaignDateLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return ((Campaign) element).getDate();
	}

}
