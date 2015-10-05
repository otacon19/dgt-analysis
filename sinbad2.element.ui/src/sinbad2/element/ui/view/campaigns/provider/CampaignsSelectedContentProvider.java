package sinbad2.element.ui.view.campaigns.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ICampaignsChangeListener;

public class CampaignsSelectedContentProvider implements IStructuredContentProvider, ICampaignsChangeListener{

	TableViewer _tableViewer;
	
	ProblemElementsSet _elementsSet;
	
	List<Campaign> _campaignsSelected;
	
	public CampaignsSelectedContentProvider(TableViewer tableViewer) {
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

	    int width = area.width - 2*table.getBorderWidth();

	    if(lastColumn.getWidth() < width - columnsWidth) {
	        lastColumn.setWidth(width - columnsWidth + 3);
	    }
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notifyCampaignsChange(CampaignsChangeEvent event) {
		switch(event.getChange()) {
		case FINAL_CAMPAIGNS:
			List<Campaign> addedCampaigns = (List<Campaign>) event.getNewValue();
			for(Campaign c: addedCampaigns) {
				if(!_campaignsSelected.contains(c)) {
					_campaignsSelected.add(c);
				}
			}
			
			_tableViewer.refresh();
			pack();
			
			break;
		case REMOVE_CAMPAIGNS_SELECTED:
			if(!_campaignsSelected.isEmpty()) {
				List<Campaign> removeCampaigns = (List<Campaign>) event.getNewValue();
				_campaignsSelected.removeAll(removeCampaigns);
				_tableViewer.refresh();
				pack();
			}
			break;
		default:
			break;
		}
	}

}
