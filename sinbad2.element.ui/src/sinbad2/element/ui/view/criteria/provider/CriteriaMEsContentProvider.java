package sinbad2.element.ui.view.criteria.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Button;

import sinbad2.element.criterion.Criterion;
import sinbad2.element.ui.view.criteria.CriteriaView;

public class CriteriaMEsContentProvider implements IStructuredContentProvider {
	
	private TableViewer _tableViewer;
	
	private List<Criterion> _criteria;

	public CriteriaMEsContentProvider(TableViewer tableViewer) {
		_tableViewer = tableViewer;
		
		_criteria = new LinkedList<Criterion>();
		
		for(Button b: CriteriaView.getButtons()) {
			_criteria.add((Criterion) b.getData("criterion"));
		}
	}

	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Criterion>) inputElement).toArray();
	}

	public Object getInput() {
		 return _criteria;
	}

	public void pack() {
		for (int i = 0; i < _tableViewer.getTable().getColumns().length; ++i) {
			if(i != 1) {
				_tableViewer.getTable().getColumn(i).pack();
			}
		}
	}

}
