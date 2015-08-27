package sinbad2.element.ui.view.criteria.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TableColumn;

import sinbad2.element.IProblemElementsSetChangeListener;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ICriteriaChangeListener;

public class CriteriaContentProvider implements IStructuredContentProvider, ICriteriaChangeListener, IProblemElementsSetChangeListener {

	private ProblemElementsManager _elementManager;
	private ProblemElementsSet _elementSet;
	private List<Criterion> _criteria;
	private TableViewer _tableViewer;
	
	public CriteriaContentProvider() {
		_criteria = null;
	}
	
	public CriteriaContentProvider(TableViewer tableViewer) {
		this();
		_tableViewer = tableViewer;
		
		_elementManager = ProblemElementsManager.getInstance();
		_elementSet = _elementManager.getActiveElementSet();
		
		_criteria = new LinkedList<Criterion>();
		for(Criterion c: _elementSet.getCriteria()) {
			if(!c.getIsDirect()) {
				_criteria.add(c);
			}
		}
		
		_elementSet.registerCriteriaChangesListener(this);
		_elementManager.registerElementsSetChangeListener(this);
	}
	
	@Override
	public void dispose() {
		_elementSet.unregisterCriteriaChangeListener(this);
		_elementManager.unregisterElementsSetChangeListener(this);
		
	}

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
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyCriteriaChange(CriteriaChangeEvent event) {
		
		switch(event.getChange()) {
			case CRITERIA_CHANGES:
				_criteria = (List<Criterion>) event.getNewValue();
				_tableViewer.setInput(_criteria);
				break;
			case ADD_CRITERION:
				addCriterion((Criterion) event.getNewValue());
				break;
			default: 
				break;
		}
		pack();
	}
	
	public void pack() {
		for(TableColumn tc: _tableViewer.getTable().getColumns()) {
			tc.pack();
		}
		
	}

	private void addCriterion(Criterion criterion) {
		int pos = 0;
		boolean find = false;
		
		do {
			if(_criteria.get(pos) == criterion) {
				find = true;
			} else {
				pos++;
			}
		} while (!find);
		_tableViewer.insert(criterion, pos);
		
	}
	
	@Override
	public void notifyNewProblemElementsSet(ProblemElementsSet elementSet) {
		
		if(_elementSet != elementSet) {
			_elementManager.unregisterElementsSetChangeListener(this);
			_elementSet = elementSet;
			_criteria = _elementSet.getCriteria();
			_elementSet.registerCriteriaChangesListener(this);
			_tableViewer.setInput(_criteria);
		}
		
	}

}
