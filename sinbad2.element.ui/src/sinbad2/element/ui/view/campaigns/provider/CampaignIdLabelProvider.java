package sinbad2.element.ui.view.campaigns.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import sinbad2.element.campaigns.Campaign;
import sinbad2.element.ui.Images;

public class CampaignIdLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object obj) {
		return ((Campaign) obj).getId();
	}

	@Override
	public Image getImage(Object element) {
		return Images.Campaign;
	}
	
}
