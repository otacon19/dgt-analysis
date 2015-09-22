package sinbad2.element.ui.view.criteria.provider;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TableColumn;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ICampaignsChangeListener;
import sinbad2.element.criterion.Criterion;

public class CriteriaSelectedContentProvider implements IStructuredContentProvider, ICampaignsChangeListener {
	
	TableViewer _tableViewer;
	
	private List<Criterion> _criteria;
	
	ProblemElementsSet _elementsSet;
	
	public CriteriaSelectedContentProvider(TableViewer tableViewer) {
		_tableViewer = tableViewer;
		
		_criteria = new LinkedList<Criterion>();
		
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
		return _criteria;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Criterion>) inputElement).toArray();
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
			case CAMPAIGNS_SELECTED_CHANGES:
				List<Campaign> campaignsSelected = (List<Campaign>) event.getNewValue();
				List<Criterion> oldCriteria = new LinkedList<Criterion>();
				oldCriteria.addAll(_criteria);
				_criteria.clear();
				if(campaignsSelected.isEmpty()) {
					_tableViewer.refresh();
				} else {
					_criteria.addAll(orderAvailableCriteria(campaignsSelected));
					
					_tableViewer.refresh();
					if(!oldCriteria.containsAll(_criteria)) {
						pack();
					}
				}
				break;
			case REMOVE_CAMPAIGNS_SELECTED:
				if(!_criteria.isEmpty()) {
					_criteria.clear();
					_tableViewer.refresh();
					pack();
				}
				break;
			default:
				break;
		}	
	}

	private List<Criterion> orderAvailableCriteria(List<Campaign> campaignsSelected) {
		Campaign campaignSelected = null;
		if(campaignsSelected.size() == 1) {
			campaignSelected = campaignsSelected.get(0);
			if(campaignSelected.isACampaignData()) {
				return campaignSelected.getCriteria();
			} else {
				LinkedList<Criterion> criteriaWithoutDirect = new LinkedList<Criterion>();
				for(Criterion c: _elementsSet.getCriteria()) {
					if(!c.isDirect()) {
						criteriaWithoutDirect.add(c);
					}
				}
				return criteriaWithoutDirect;
			}
		} else {
			boolean data = false;
			int numCampaignsData = 0;
			List<Criterion> allCriteriaCampaigns = new LinkedList<Criterion>();
			for(Campaign c: campaignsSelected) {
				if(c.isACampaignData()) {
					data = true;
					numCampaignsData++;
				}
				List<Criterion> criteria = c.getCriteria();
				for(Criterion cri: criteria) {
					allCriteriaCampaigns.add(cri);
				}
			}
			Map<Criterion, Integer> criteriaRepeat;
			criteriaRepeat = checkMatchingData(allCriteriaCampaigns);
			List<Criterion> availableCriterion = new LinkedList<Criterion>();
			List<Criterion> availableCriterionDirect = new LinkedList<Criterion>();
			List<Criterion> noAvailableCriterion = new LinkedList<Criterion>();
			for(Criterion c: _elementsSet.getCriteria()) {
				if(data) {
					if((criteriaRepeat.get(c) != null && (criteriaRepeat.get(c) == campaignsSelected.size() - numCampaignsData)) || c.isDirect()) {
						if(c.isDirect()) {
							availableCriterionDirect.add(c);
						} else {
							availableCriterion.add(c);
						}
					} else {
						noAvailableCriterion.add(c);
					}
				} else {
					if(!c.isDirect()) {
						if(criteriaRepeat.get(c) != null) {
							availableCriterion.add(c);
						} else {
							noAvailableCriterion.add(c);
						}
					}
				}
			}
			
			if(availableCriterion.isEmpty() && !availableCriterionDirect.isEmpty()) {
				return availableCriterionDirect;
				
			} else if(!noAvailableCriterion.isEmpty() && !availableCriterion.isEmpty() && !availableCriterionDirect.isEmpty()) {
				availableCriterion.addAll(availableCriterionDirect);
				availableCriterion.addAll(noAvailableCriterion);
				
				return availableCriterion;
			} else if(noAvailableCriterion.isEmpty() && !availableCriterion.isEmpty() && !availableCriterionDirect.isEmpty()) {
				availableCriterion.addAll(availableCriterionDirect);
				
				return availableCriterion;
			} else if(!noAvailableCriterion.isEmpty() && !availableCriterion.isEmpty()) {
				availableCriterion.addAll(noAvailableCriterion);
				
				return availableCriterion;
			} else {
				List<Criterion> orderByKindOfCriterion = new LinkedList<Criterion>();
				List<Criterion> lastCriteria = new LinkedList<Criterion>();
				if(!availableCriterion.isEmpty()) {
					for(Criterion c: availableCriterion) {
						if(!c.isDirect()) {
							orderByKindOfCriterion.add(c);
						} else {
							lastCriteria.add(c);
						}
					}
					orderByKindOfCriterion.addAll(lastCriteria);
				} else {
					for(Criterion c: noAvailableCriterion) {
						if(!c.isDirect()) {
							orderByKindOfCriterion.add(c);
						} else {
							lastCriteria.add(c);
						}
					}
					orderByKindOfCriterion.addAll(lastCriteria);
				}
				
				return orderByKindOfCriterion;
			}
		}
	}

	private Map<Criterion, Integer> checkMatchingData(List<Criterion> allCriteriaCampaigns) {		
		Map<Criterion, Integer> criteriaRepeat = new LinkedHashMap<Criterion, Integer>();
		int numRep;
		for(int i = 0; i < allCriteriaCampaigns.size(); i++){
		    Criterion c1 = allCriteriaCampaigns.get(i);
		    numRep = 0;
		    for(int j = 0; j < allCriteriaCampaigns.size(); j++){
		    	Criterion c2 = allCriteriaCampaigns.get(j);
		        if(c1.equals(c2)) {
		            numRep++;
		            criteriaRepeat.put(c1, numRep);
		        }
		    }
		}
		return criteriaRepeat;
	}
}
