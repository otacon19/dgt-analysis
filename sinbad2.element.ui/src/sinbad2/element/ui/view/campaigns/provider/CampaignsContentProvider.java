package sinbad2.element.ui.view.campaigns.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TableColumn;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.IAlternativesChangeListener;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ECampaignsChange;
import sinbad2.element.campaigns.listener.ICampaignsChangeListener;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ICriteriaChangeListener;
import sinbad2.element.ui.view.campaigns.CampaignsView;

public class CampaignsContentProvider implements IStructuredContentProvider,
		IAlternativesChangeListener, ICriteriaChangeListener, ICampaignsChangeListener {
	
	private List<Campaign> _campaigns;
	private List<Alternative> _alternativesSelected;
	private List<Criterion> _criteriaSelected;
	private List<Campaign> _campaignsSelected;
	
	private ProblemElementsSet _elementSet;

	private TableViewer _tableViewer;

	public CampaignsContentProvider(TableViewer tableViewer) {
		_tableViewer = tableViewer;

		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementSet = elementsManager.getActiveElementSet();
		_campaigns = _elementSet.getCampaigns();
		
		_campaignsSelected = new LinkedList<Campaign>();
		_alternativesSelected = new LinkedList<Alternative>();
		_criteriaSelected = new LinkedList<Criterion>();
		
		_campaignsSelected.addAll(_campaigns);

		_elementSet.registerAlternativesChangesListener(this);
		_elementSet.registerCriteriaChangesListener(this);
		_elementSet.registerCampaignsChangesListener(this);

		checkCampaigns(_alternativesSelected, _criteriaSelected);
	}

	@Override
	public void dispose() {
		_elementSet.unregisterAlternativesChangeListener(this);
		_elementSet.unregisterCriteriaChangeListener(this);
		_elementSet.unregisterCampaignsChangeListener(this);
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
		for(TableColumn c: _tableViewer.getTable().getColumns()) {
			c.pack();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notifyCriteriaChange(CriteriaChangeEvent event) {
		switch (event.getChange()) {
			case CRITERIA_SELECTED_CHANGES:
				List<Criterion> criteria = (List<Criterion>) event.getNewValue();
				//FIXME hecho así porque se añaden los índices de obtención directa a la campaña en el MockModel pero esos nunca se seleccionan
				if(criteria.size() + 3 == _elementSet.getCriteria().size()) {
					_criteriaSelected = _elementSet.getCriteria();
				} else {
				_criteriaSelected = (List<Criterion>) event.getNewValue();
				}
				checkCampaigns(_alternativesSelected, _criteriaSelected);
				_tableViewer.setInput(_campaignsSelected);
				break;
			default:
				break;
		}
		
		pack();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyAlternativesChange(AlternativesChangeEvent event) {
		switch (event.getChange()) {
		case ALTERNATIVES_SELECTED_CHANGES:
			_alternativesSelected = (List<Alternative>) event.getNewValue();
			checkCampaigns(_alternativesSelected, _criteriaSelected);
			_tableViewer.setInput(_campaignsSelected);
			break;
		default:
			break;
		}
		
		pack();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyCampaignsChange(CampaignsChangeEvent event) {
		switch(event.getChange()) {
			case ADD_CAMPAIGN:
				addCampaign((Campaign) event.getNewValue());
				break;
			case CAMPAIGNS_CHANGES:
				_campaigns = (List<Campaign>) event.getNewValue();
				_tableViewer.setInput(_campaigns);
				break;
			default:
				break;
		}
		pack();
	}
	
	private void checkCampaigns(List<Alternative> alternativesSelected, List<Criterion> criteriaSelected) {
		_campaignsSelected.clear();
		for (Campaign c : _campaigns) {
			if(((c.getAlternatives().size() == alternativesSelected.size()) && (c.getAlternatives().containsAll(alternativesSelected))) && 
					(c.getCriteria().size() == criteriaSelected.size()) && (c.getCriteria().containsAll(criteriaSelected))) {
				_campaignsSelected.add(c);
			}
		}
		
		if(_campaignsSelected.isEmpty()) {
			checkButtons();
		}
	}	
	
	private void checkButtons() {
		List<Button> buttons = CampaignsView.getButtons();
		for(int i = 0; i < buttons.size(); ++i) {
			buttons.get(i).setVisible(false);
		}
	}

	private void addCampaign(Campaign c) {	
		int pos = 0; 
		boolean find = false; 
		
	 	do { 
	 		if(_elementSet.getCampaigns().get(pos) == c) { 
	 			find = true; 
	 		} else { 
	 			pos++; 
	 		} 
	 	} while(!find); 
	 	
	 	_campaignsSelected.add(c);
	 	_tableViewer.refresh();
	 	
	 	checkSelection(c);
	 	
	 }

	private void checkSelection(Campaign c) {
		List<Button> buttons = CampaignsView.getButtons();
		for(int i = 0; i < buttons.size(); ++i) {
			if(i != buttons.size() - 1) {
				buttons.get(i).setSelection(false);
			} else {
				buttons.get(i).setSelection(true);
			}
		}
		
		List<Campaign> mergeCampaign = new LinkedList<Campaign>();
		mergeCampaign.add(c);
		_elementSet.notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.MERGE_CAMPAIGNS, null, mergeCampaign, false));
	} 

}
