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

	    int width = area.width - 2*table.getBorderWidth();

	    if(lastColumn.getWidth() < width - columnsWidth) {
	        lastColumn.setWidth(width - columnsWidth + 3);
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
		String dateInitial, dateFinal, dateCalendar, anoInitial, anoFinal, ano, month;
		List<Campaign> campaigns = _elementsSet.getCampaigns();
		for(Campaign c : campaigns) {
			if(c.getProvince().equals(dataCampaigns.get(0))) {
				if(dataCampaigns.size() > 1) {
					dateInitial = c.getInitialDate();
					dateFinal = c.getFinalDate();
					anoInitial = dateInitial.substring(dateInitial.length() - 2, dateInitial.length());
					anoFinal = dateInitial.substring(dateFinal.length() - 2, dateFinal.length());
					dateCalendar = dataCampaigns.get(1);
					ano = dateCalendar.substring(dateCalendar.length() - 2, dateCalendar.length());
					if(anoInitial.equals(ano) || anoFinal.equals(ano)) {
						List<String> intervalMonth = c.getIntervalDate();
						month = dateCalendar.substring(dateCalendar.length() - 5, dateCalendar.length() - 3);
						if(intervalMonth.contains(month)) {
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
