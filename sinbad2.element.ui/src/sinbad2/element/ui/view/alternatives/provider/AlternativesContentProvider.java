package sinbad2.element.ui.view.alternatives.provider;

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
import sinbad2.element.alternative.Alternative;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.IAlternativesChangeListener;
import sinbad2.element.campaigns.Campaign;
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
					if(event.getNewValue() instanceof Boolean) {
						if((boolean) event.getNewValue()) {
							for(Alternative a: _elementSet.getAlternatives()) {
								if(a.isDirect()) {
									_alternatives.add(a);
								}
							}
						} else {
							for(Alternative a: _elementSet.getAlternatives()) {
								if(!a.isDirect()) {
									_alternatives.add(a);
								}
							}
						}
					} else {
						if(!checkCampaignsData()) {
							List<Alternative> alternativesNoDirect = new LinkedList<Alternative>();
							List<Alternative> alternativesDirect = new LinkedList<Alternative>();
							for(Alternative a: (List<Alternative>) event.getNewValue()) {
								if(!a.isDirect()) {
									alternativesNoDirect.add(a);
								} else {
									alternativesDirect.add(a);
								}
							}
							alternativesNoDirect.addAll(alternativesDirect);
							_alternatives.addAll(alternativesNoDirect);
						} else {
							for(Alternative a: (List<Alternative>) event.getNewValue()) {
								if(a.isDirect()) {
									_alternatives.add(a);
								}
							}
						}
					}
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
