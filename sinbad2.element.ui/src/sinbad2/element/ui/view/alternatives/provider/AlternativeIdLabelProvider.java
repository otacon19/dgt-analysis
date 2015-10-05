package sinbad2.element.ui.view.alternatives.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

import sinbad2.element.alternative.Alternative;
import sinbad2.element.ui.Images;

public class AlternativeIdLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object obj) {
		return ((Alternative) obj).getId();
	}

	@Override
	public Image getImage(Object element) {
		if(((Alternative) element).getId().equals("Motivo")) {
			return Images.Reason;
		} else {
			if(((Alternative) element).getId().equals("Tipo vehículo")) {
				return Images.Kind_of_cars;
			} else {
				if(((Alternative) element).getId().equals("Franja horaria")) {
					return Images.Time_zone;
				} else if(((Alternative) element).getId().equals("Periodo semanal")) {
					return Images.Week;
				} else {
					return Images.Permission;
				}
			}
		}
	}
	
	@Override
	public void update(ViewerCell cell) {
		super.update(cell);
		
		TableItem item = (TableItem) cell.getItem();
		item.setForeground(new Color(Display.getCurrent(), 211, 211, 211));	
	}
}
