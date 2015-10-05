package sinbad2.element.ui.view.campaigns.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import sinbad2.element.campaigns.Campaign;
import sinbad2.element.ui.Images;

public class CampaignIdLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object obj) {
		return ((Campaign) obj).getName();
	}

	@Override
	public Image getImage(Object element) {
		if(((Campaign) element).getCriteria().get(0).isDirect()) {
			return Images.Campaign_data;
		} else {
			return Images.Campaign;
		}
	}
	
}
