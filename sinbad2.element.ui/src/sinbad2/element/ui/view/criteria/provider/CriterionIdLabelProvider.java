package sinbad2.element.ui.view.criteria.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableItem;

import sinbad2.element.criterion.Criterion;
import sinbad2.element.ui.Images;

public class CriterionIdLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object element) {
		return ((Criterion) element).getId();
	}
	
	@Override
	public Image getImage(Object element) {
		if(((Criterion) element).getId().equals("Desplazamientos")) {
			return Images.Journey;
		} else {
			if(((Criterion) element).getId().equals("Distancia")) {
				return Images.Distance;
			} else {
				if(((Criterion) element).getId().equals("Tiempo")) {
					return Images.Time;
				} else {
					if(((Criterion) element).getId().equals("Habitantes")) {
						return Images.People;
					} else {
						if(((Criterion) element).getId().equals("Censo")) {
							return Images.Park;	
						} else {
							return Images.Licences;
						}
					}
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
