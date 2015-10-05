package sinbad2.element.ui.view.alternatives.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import sinbad2.element.IProblemElementsSetChangeListener;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.IAlternativesChangeListener;
import sinbad2.element.ui.view.campaigns.dialog.AddCampaignsDialog;

public class AlternativesContentProvider implements IStructuredContentProvider, IAlternativesChangeListener, IProblemElementsSetChangeListener {
	
	private ProblemElementsManager _elementManager;
	private ProblemElementsSet _elementSet;
	private List<Alternative> _alternatives;
	
	private TableViewer _tableViewer;
	
	
	public AlternativesContentProvider(TableViewer tableViewer) {
		_tableViewer = tableViewer;
		
		_elementManager = ProblemElementsManager.getInstance();
		_elementSet = _elementManager.getActiveElementSet();
		_alternatives = new LinkedList<Alternative>();
		
		_elementSet.registerAlternativesChangesListener(this);
		_elementManager.registerElementsSetChangeListener(this);
	}
	
	@Override
	public void dispose() {
		_elementSet.unregisterAlternativesChangeListener(this);
		_elementManager.unregisterElementsSetChangeListener(this);
	}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Alternative>)inputElement).toArray();
	}
	
	public Object getInput() {
		return _alternatives;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyAlternativesChange(AlternativesChangeEvent event) {
		
		switch(event.getChange()) {
			case ALTERNATIVES_CHANGES:
				_alternatives.clear();
				if(!AddCampaignsDialog.getCampaignsSelected().isEmpty()) {
					_alternatives.addAll((List<Alternative>) event.getNewValue()); 
				}
				
				_tableViewer.refresh();
				pack();
				break;
			case ADD_ALTERNATIVE:
				addAlternative((Alternative) event.getNewValue());
				_tableViewer.refresh();
				pack();
				break;
			default:
				break;
		}
	}

	public void pack() {
		for(int i = 0; i < _tableViewer.getTable().getColumnCount() - 1; ++i) {
			_tableViewer.getTable().getColumn(i).pack();
		}
		
		Table table = _tableViewer.getTable();
	    int columnsWidth = 0;
	    
	    for (int i = 0; i < table.getColumnCount() - 1; i++) {
	        columnsWidth += table.getColumn(i).getWidth();
	    }
	    TableColumn lastColumn = table.getColumn(table.getColumnCount() - 1);
	    lastColumn.pack();

	    Rectangle area = table.getClientArea();

	    int width = area.width - 2*table.getBorderWidth();

	    if(lastColumn.getWidth() < width - columnsWidth) {
	        lastColumn.setWidth(width - columnsWidth + 3);
	    }
	}

	@Override
	public void notifyNewProblemElementsSet(ProblemElementsSet elementSet) {
		
		if(_elementSet != elementSet) {
			_elementSet.unregisterAlternativesChangeListener(this);
			_elementSet = elementSet;
			_alternatives = _elementSet.getAlternatives();
			_elementSet.registerAlternativesChangesListener(this);
			_tableViewer.setInput(_alternatives);
		}
	}

	private void addAlternative(Alternative newAlternative) {
		int pos = 0;
		boolean find = false;
		
		do {
			if(_alternatives.get(pos) == newAlternative) {
				find = true;
			} else {
				pos++;
			}
		} while (!find);
		_tableViewer.insert(newAlternative, pos);
	}
}
