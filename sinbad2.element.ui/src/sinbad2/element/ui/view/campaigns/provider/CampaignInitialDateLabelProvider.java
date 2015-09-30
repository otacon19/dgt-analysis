package sinbad2.element.ui.view.campaigns.provider;


import org.eclipse.jface.viewers.ColumnLabelProvider;

import sinbad2.element.campaigns.Campaign;

public class CampaignInitialDateLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		String date = ((Campaign) element).getInitialDate();
		String initialDate = date.substring(date.length() - 5, date.length());
		return initialDate;
	}

}
