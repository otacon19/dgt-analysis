package sinbad2.element.ui.view.campaigns.provider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
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
		for(TableColumn tc: _tableViewer.getTable().getColumns()) {
			tc.pack();
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
			
			Collections.sort(_campaignsSelected, new Comparator<Campaign>() {
			    public int compare(Campaign c1, Campaign c2) {
			    	SimpleDateFormat formatter = new SimpleDateFormat("MM/yy");
			    	Date date1 = null, date2 = null;
			    	try {
			    		date1 = formatter.parse(c1.getDate());
			    		date2 = formatter.parse(c2.getDate());
			    	} catch (ParseException e) {
			    		e.printStackTrace();
			    	}
			        return date1.compareTo(date2);
			    }
			});
			
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
