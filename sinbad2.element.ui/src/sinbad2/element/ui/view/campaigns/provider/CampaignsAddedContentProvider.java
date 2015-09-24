package sinbad2.element.ui.view.campaigns.provider;

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

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ICampaignsChangeListener;

public class CampaignsAddedContentProvider implements IStructuredContentProvider, ICampaignsChangeListener {

	private TableViewer _tableViewer;
	
	private List<Campaign> _campaignsSelected;
	
	ProblemElementsSet _elementsSet;
	
	public CampaignsAddedContentProvider(TableViewer tableViewer) {
		_tableViewer = tableViewer;
		
		_campaignsSelected = new LinkedList<Campaign>();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_elementsSet.registerCampaignsChangesListener(this);
	}
	
	@Override
	public void dispose() {
		_elementsSet.unregisterCampaignsChangeListener(this);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	public Object getInput() {
		return _campaignsSelected;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Campaign>) inputElement).toArray();
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
	        lastColumn.setWidth(width - columnsWidth + 2);
	    }
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notifyCampaignsChange(CampaignsChangeEvent event) {
		switch(event.getChange()) {
			case ADD_CAMPAIGNS_SELECTED:
				List<Campaign> campaignsSelected = (List<Campaign>) event.getNewValue();
				for(Campaign c: campaignsSelected) {
					if(!_campaignsSelected.contains(c)) {
						_campaignsSelected.add(c);
					}
				}
				_tableViewer.refresh();
				pack();
				break;
			case REMOVE_CAMPAIGNS_SELECTED:
				if(!_tableViewer.getTable().isDisposed()) {
					_campaignsSelected.removeAll((List<Campaign>) event.getNewValue());
					_tableViewer.refresh();
					pack();
				}
				break;
			default:
				break;
		}
	}
}
