package sinbad2.element.ui.view.alternatives.provider;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ICampaignsChangeListener;

public class AlternativesSelectedContentProvider implements ITreeContentProvider, ICampaignsChangeListener {

	TreeViewer _treeViewer;
	
	private List<Alternative> _alternatives;
	
	ProblemElementsSet _elementsSet;
	
	public AlternativesSelectedContentProvider(TreeViewer treeViewer) {
		_treeViewer = treeViewer;
		hookTreeListener();
		
		_alternatives = new LinkedList<Alternative>();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_elementsSet.registerCampaignsChangesListener(this);
	}
	
	private void hookTreeListener() {
		_treeViewer.addTreeListener(new ITreeViewerListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				Display.getCurrent().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						pack();
					}
				});
			}
			
			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				Display.getCurrent().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						pack();	
					}
				});
			}
		});
	}
	
	@Override
	public void dispose() {
		_elementsSet.unregisterCampaignsChangeListener(this);
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	public Object getInput() {
		return _alternatives;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Alternative>) inputElement).toArray();
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		return ((Alternative) parentElement).getChildrens().toArray();
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return ((Alternative) element).hasChildrens();
	}

	public void pack() {
		for(TreeColumn tc: _treeViewer.getTree().getColumns()) {
			tc.pack();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyCampaignsChange(CampaignsChangeEvent event) {
		switch(event.getChange()) {
			case CAMPAIGNS_SELECTED_CHANGES:
				_alternatives.clear();
				List<Campaign> campaignsSelected = (List<Campaign>) event.getNewValue();
				if(_alternatives.isEmpty()) {
					_alternatives.addAll(orderAvailableAlternatives(campaignsSelected));
					_treeViewer.refresh();
					pack();
				} else {
					_treeViewer.refresh();
				}
				
				if(campaignsSelected.isEmpty()) {
					_alternatives.clear();
					_treeViewer.refresh();
				}
				break;
			case REMOVE_CAMPAIGNS_SELECTED:
				if(!_alternatives.isEmpty()) {
					_alternatives.clear();
					_treeViewer.refresh();
					pack();
				}
				break;
			default:
				break;
		}	
	}

	private List<Alternative> orderAvailableAlternatives(List<Campaign> campaignsSelected) {
		Campaign campaignSelected = null;
		if(campaignsSelected.size() == 1) {
			campaignSelected = campaignsSelected.get(0);
			if(campaignSelected.isACampaignData()) {
				return campaignSelected.getAlternatives();
			} else {
				LinkedList<Alternative> alternativesWithoutDirect = new LinkedList<Alternative>();
				for(Alternative a: _elementsSet.getAlternatives()) {
					if(!a.isDirect()) {
						alternativesWithoutDirect.add(a);
					}
				}
				return alternativesWithoutDirect;
			}
		} else {
			boolean data = false;
			int numCampaignsData = 0;
			List<Alternative> allAlternativesCampaigns = new LinkedList<Alternative>();
			for(Campaign c: campaignsSelected) {
				if(c.isACampaignData()) {
					data = true;
					numCampaignsData++;
				}
				List<Alternative> alternatives = c.getAlternatives();
				for(Alternative alt: alternatives) {
					allAlternativesCampaigns.add(alt);
				}
			}
			Map<Alternative, Integer> alternativesRepeat;
			alternativesRepeat = checkMatchingData(allAlternativesCampaigns);
			List<Alternative> availableAlternatives = new LinkedList<Alternative>();
			List<Alternative> availableAlternativesDirect = new LinkedList<Alternative>();
			List<Alternative> noAvailableAlternatives = new LinkedList<Alternative>();
			for(Alternative a: _elementsSet.getAlternatives()) {
				if(data) {
					if((alternativesRepeat.get(a) != null && (alternativesRepeat.get(a) == campaignsSelected.size() - numCampaignsData)) || a.isDirect()) {
						if(a.isDirect()) {
							availableAlternativesDirect.add(a);
						} else {
							availableAlternatives.add(a);
						}
					} else {
						noAvailableAlternatives.add(a);
					}
				} else {
					if(!a.isDirect()) {
						if(alternativesRepeat.get(a) != null) {
							availableAlternatives.add(a);
						} else {
							noAvailableAlternatives.add(a);
						}
					}
				}
			}
			if(availableAlternatives.isEmpty() && !availableAlternativesDirect.isEmpty()) {
				return availableAlternativesDirect;
			} else if(!noAvailableAlternatives.isEmpty() && !availableAlternatives.isEmpty() && !availableAlternativesDirect.isEmpty()) {
				availableAlternatives.addAll(availableAlternativesDirect);
				availableAlternatives.addAll(noAvailableAlternatives);
				
				return availableAlternatives;
			} else if(noAvailableAlternatives.isEmpty() && !availableAlternatives.isEmpty() && !availableAlternativesDirect.isEmpty()) {
				availableAlternatives.addAll(availableAlternativesDirect);
				
				return availableAlternatives;
			} else if(!noAvailableAlternatives.isEmpty() && !availableAlternatives.isEmpty()) {
				availableAlternatives.addAll(noAvailableAlternatives);
				
				return availableAlternatives;	
			} else {
				List<Alternative> orderByKindOfAlternatives = new LinkedList<Alternative>();
				List<Alternative> lastAlternatives = new LinkedList<Alternative>();
				if(!availableAlternatives.isEmpty()) {
					for(Alternative a: availableAlternatives) {
						if(!a.isDirect()) {
							orderByKindOfAlternatives.add(a);
						} else {
							lastAlternatives.add(a);
						}
					}
					orderByKindOfAlternatives.addAll(lastAlternatives);
				} else {
					for(Alternative a: noAvailableAlternatives) {
						if(!a.isDirect()) {
							orderByKindOfAlternatives.add(a);
						} else {
							lastAlternatives.add(a);
						}
					}
					orderByKindOfAlternatives.addAll(lastAlternatives);
				}
				
				return orderByKindOfAlternatives;
			}
		}
	}

	private Map<Alternative, Integer> checkMatchingData(List<Alternative> allAlternativesCampaigns) {		
		Map<Alternative, Integer> alternativesRepeat = new LinkedHashMap<Alternative, Integer>();
		int numRep;
		for(int i = 0; i < allAlternativesCampaigns.size(); i++){
		    Alternative a1 = allAlternativesCampaigns.get(i);
		    numRep = 0;
		    for(int j = 0; j < allAlternativesCampaigns.size(); j++){
		    	Alternative a2 = allAlternativesCampaigns.get(j);
		        if(a1.equals(a2)) {
		            numRep++;
		            alternativesRepeat.put(a1, numRep);
		        }
		    }
		}
		return alternativesRepeat;
	}

}
