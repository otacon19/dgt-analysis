package sinbad2.element.ui.view.criteria.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

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
				return Images.Time;
			}
		}
	}
}
