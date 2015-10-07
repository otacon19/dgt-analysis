package sinbad2.element.ui;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;

import sinbad2.element.campaigns.Campaign;

public class ComparatorCampaigns extends ViewerComparator {
	private int propertyIndex;
	private static final int DESCENDING = 1;
	private int direction = DESCENDING;

	public ComparatorCampaigns() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}

	public int getDirection() {
		return direction == 1 ? SWT.DOWN : SWT.UP;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			direction = 1 - direction;
		} else {
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	@Override
	public int compare(Viewer viewer, Object e1, Object e2) {
		Campaign c1 = (Campaign) e1;
		Campaign c2 = (Campaign) e2;
		int rc = 0;
		switch (propertyIndex) {
		case 0:
			rc = c1.getName().compareTo(c2.getName());
			break;
		case 1:
			rc = c1.getProvince().compareTo(c2.getProvince());
			break;
		case 2:
			rc = c1.getInitialDate().compareTo(c2.getInitialDate());
			break;
		case 3:
			rc = c1.getFinalDate().compareTo(c2.getFinalDate());
			break;
		default:
			rc = 0;
		}

		if (direction == DESCENDING) {
			rc = -rc;
		}
		return rc;
	}

}
