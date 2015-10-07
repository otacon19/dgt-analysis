package sinbad2.element.ui.view.alternatives.provider;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import sinbad2.element.alternative.Alternative;
import sinbad2.element.ui.Images;

public class AlternativeSelectedIdLabelProvider extends ColumnLabelProvider {
	
	@Override
	public String getText(Object obj) {
		return ((Alternative) obj).getId();
	}

	@Override
	public Image getImage(Object element) {
		if(((Alternative) element).getId().equals("Motivo")) { //$NON-NLS-1$
			return Images.Reason;
		} else {
			if(((Alternative) element).getId().equals("Tipo vehículo")) { //$NON-NLS-1$
				return Images.Kind_of_cars;
			} else {
				if(((Alternative) element).getId().equals("Franja horaria")) { //$NON-NLS-1$
					return Images.Time_zone;
				} else if(((Alternative) element).getId().equals("Periodo semanal")) { //$NON-NLS-1$
					return Images.Week;
				} else if(((Alternative) element).getId().equals("Permisos")){ //$NON-NLS-1$
					return Images.Permission;
				} else {
					return null;
				}
			}
		}
	}
	
}
