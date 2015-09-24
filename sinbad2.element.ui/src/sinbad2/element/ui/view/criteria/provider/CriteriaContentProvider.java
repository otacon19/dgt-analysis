package sinbad2.element.ui.view.criteria.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import sinbad2.element.IProblemElementsSetChangeListener;
import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ICriteriaChangeListener;
import sinbad2.element.ui.view.campaigns.dialog.AddCampaignsDialog;

public class CriteriaContentProvider implements IStructuredContentProvider, ICriteriaChangeListener, IProblemElementsSetChangeListener {

	private ProblemElementsManager _elementManager;
	private ProblemElementsSet _elementSet;
	private List<Criterion> _criteria;
	
	private TableViewer _tableViewer;
	
	public CriteriaContentProvider(TableViewer tableViewer) {
		_tableViewer = tableViewer;
		
		_elementManager = ProblemElementsManager.getInstance();
		_elementSet = _elementManager.getActiveElementSet();
		_criteria = new LinkedList<Criterion>();
		
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
				_criteria.clear();
				if(!AddCampaignsDialog.getCampaignsSelected().isEmpty()) {
					if(event.getNewValue() instanceof Boolean) {
						if((boolean) event.getNewValue()) {
							for(Criterion c: _elementSet.getCriteria()) {
								if(c.isDirect()) {
									_criteria.add(c);
								}
							}
						} else {
							for(Criterion c: _elementSet.getCriteria()) {
								if(!c.isDirect()) {
									_criteria.add(c);
								}
							}
						}
					} else {
						if(!checkCampaignsData()) {
							List<Criterion> criteriaNoDirect = new LinkedList<Criterion>();
							List<Criterion> criteriaDirect = new LinkedList<Criterion>();
							for(Criterion c: (List<Criterion>) event.getNewValue()) {
								if(!c.isDirect()) {
									criteriaNoDirect.add(c);
								} else {
									criteriaDirect.add(c);
								}
							}
							criteriaNoDirect.addAll(criteriaDirect);
							_criteria.addAll(criteriaNoDirect);
						} else {
							for(Criterion c: (List<Criterion>) event.getNewValue()) {
								if(c.isDirect()) {
									_criteria.add(c);
								}
							}
						}
					}
				}
				
				_tableViewer.refresh();
				pack();
				break;
			case ADD_CRITERION:
				addCriterion((Criterion) event.getNewValue());
				_tableViewer.refresh();
				pack();
				break;
			default: 
				break;
		}
	}

	private boolean checkCampaignsData() {
		boolean allCampaignsData = true;
		for(Campaign c: AddCampaignsDialog.getCampaignsSelected()) {
			if(!c.isACampaignData()) {
				allCampaignsData = false;
				break;
			}
		}
		return allCampaignsData;
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

	    Point preferredSize = table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	    int width = area.width - 2*table.getBorderWidth();

	    if (preferredSize.y > area.height + table.getHeaderHeight()) {
	        Point vBarSize = table.getVerticalBar().getSize();
	        width -= vBarSize.x;
	    }

	    if(lastColumn.getWidth() < width - columnsWidth) {
	        lastColumn.setWidth(width - columnsWidth);
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
