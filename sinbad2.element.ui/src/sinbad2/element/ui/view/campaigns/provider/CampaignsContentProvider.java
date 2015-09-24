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

public class CampaignsContentProvider implements IStructuredContentProvider, ICampaignsChangeListener {
	
	private TableViewer _tableViewer;
	
	private List<Campaign> _campaignsSelected;
	private static List<Campaign> _campaignsAlreadyAdded;
	
	private ProblemElementsSet _elementsSet;

	public CampaignsContentProvider(TableViewer tableViewer) {
		_tableViewer = tableViewer;
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();	
		
		_campaignsSelected = new LinkedList<Campaign>();
		_campaignsAlreadyAdded = new LinkedList<Campaign>();
		
		_elementsSet.registerCampaignsChangesListener(this);
	}

	@Override
	public void dispose() {
		_elementsSet.unregisterCampaignsChangeListener(this);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Campaign>) inputElement).toArray();
	}

	public Object getInput() {
		return _campaignsSelected;
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
			case SEARCH_CAMPAIGNS:
				_campaignsSelected.clear();
				if(event.getNewValue() instanceof List<?>) {
					List<String> dataCampaigns = (List<String>) event.getNewValue();
					searchCampaignsByProvinceAndDate(dataCampaigns);
					
					_tableViewer.refresh();
					pack();
				}
				break;
			default:
				break;
		}	
	}

	private void searchCampaignsByProvinceAndDate(List<String> dataCampaigns) {
		String date1, date2, ano1, ano2, month1, month2;
		List<Campaign> campaigns = _elementsSet.getCampaigns();
		for(Campaign c : campaigns) {
			if(c.getProvince().equals(dataCampaigns.get(0))) {
				if(dataCampaigns.size() > 1) {
					date1 = c.getDate();
					date2 = dataCampaigns.get(1);
					ano1 = date1.substring(date1.length() - 2, date1.length());
					ano2 = date2.substring(date2.length() - 2, date2.length());
					if(ano1.equals(ano2)) {
						month1 = date1.substring(date1.length() - 5, date1.length() - 3);
						month2 = date2.substring(date2.length() - 5, date2.length() - 3);
						if(month1.equals(month2)) {
							_campaignsSelected.add(c);
						}
					}
				} else {
					_campaignsSelected.add(c);
				}
			}
		}
	}
	
	public static List<Campaign> getCampaignsAlreadyAdded() {
		return _campaignsAlreadyAdded;
	}
}
