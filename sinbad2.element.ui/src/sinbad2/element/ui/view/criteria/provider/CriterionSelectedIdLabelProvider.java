package sinbad2.element.ui.view.criteria.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import sinbad2.element.criterion.Criterion;
import sinbad2.element.ui.Images;

public class CriterionSelectedIdLabelProvider extends ColumnLabelProvider {
	
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
	
	
}
