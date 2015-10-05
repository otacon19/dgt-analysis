package sinbad2.element.ui.view.campaigns.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import sinbad2.element.ui.Images;

public class CampaignSelectionLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return "";
	}
	
	@Override
	public Image getImage(Object element) {
		return Images.RadioButtonUnselected;
	}

}
