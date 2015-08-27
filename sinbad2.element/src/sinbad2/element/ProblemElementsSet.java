package sinbad2.element;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.core.workspace.Workspace;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.EAlternativesChange;
import sinbad2.element.alternative.listener.IAlternativesChangeListener;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ECampaignsChange;
import sinbad2.element.campaigns.listener.ICampaignsChangeListener;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ECriteriaChange;
import sinbad2.element.criterion.listener.ICriteriaChangeListener;
import sinbad2.element.mec.listener.IMECsChangeListener;
import sinbad2.element.mec.listener.MECsChangeEvent;

public class ProblemElementsSet implements Cloneable {
	
	private List<Alternative> _alternatives;
	private List<Criterion> _criteria;
	private List<Campaign> _campaigns;
	
	private List<IAlternativesChangeListener> _alternativesListener;
	private List<ICriteriaChangeListener> _criteriaListener;
	private List<IMECsChangeListener> _mecsListener;
	private List<ICampaignsChangeListener> _campaignsListener;

	
	public ProblemElementsSet(){
		_alternatives = new LinkedList<Alternative>();
		_criteria = new LinkedList<Criterion>();
		_campaigns = new LinkedList<Campaign>();
		
		_alternativesListener = new LinkedList<IAlternativesChangeListener>();
		_criteriaListener = new LinkedList<ICriteriaChangeListener>();
		_mecsListener = new LinkedList<IMECsChangeListener>();
		_campaignsListener = new LinkedList<ICampaignsChangeListener>();
	}
	
	public List<Alternative> getAlternatives() {
		return _alternatives;
	}
	
	public List<Criterion> getCriteria() {
		return _criteria;
	}
	
	public List<Campaign> getCampaigns() {
		return _campaigns;
	}

	public void setAlternatives(List<Alternative> alternatives) {
		Validator.notNull(alternatives);
		
		_alternatives = alternatives;
		
		notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.ALTERNATIVES_CHANGES, null, _alternatives, false));
	}
	
	public void setCriteria(List<Criterion> criteria) {
		Validator.notNull(criteria);
		
		_criteria = criteria;
		
		notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.CRITERIA_CHANGES, null, _criteria, false));
		
	}
	
	public void addAlternative(Alternative alternative, boolean inUndoRedo) {
		_alternatives.add(alternative);
		Collections.sort(_alternatives);
		
		notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.ADD_ALTERNATIVE, null, alternative, inUndoRedo));
		
	}
	
	public void addCriterion(Criterion criterion, boolean inUndoRedo) {
		_criteria.add(criterion);
		Collections.sort(_criteria);
		
		notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.ADD_CRITERION, null, criterion, inUndoRedo));
		
	}
	
	public void addCampaign(Campaign c) {
		_campaigns.add(c);
		
		notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.ADD_CAMPAIGN, null, c, false));
	}

	public void registerAlternativesChangesListener(IAlternativesChangeListener listener) {
		_alternativesListener.add(listener);
	}
	
	public void unregisterAlternativesChangeListener(IAlternativesChangeListener listener) {
		_alternativesListener.remove(listener);
	}
	
	public void registerCriteriaChangesListener(ICriteriaChangeListener listener) {
		_criteriaListener.add(listener);
	}
	
	public void unregisterCriteriaChangeListener(ICriteriaChangeListener listener) {
		_criteriaListener.remove(listener);
	}
	
	public void registerMECsChangesListener(IMECsChangeListener listener) {
		_mecsListener.add(listener);
	}
	
	public void unregisterMECsChangeListener(IMECsChangeListener listener) {
		_mecsListener.remove(listener);
	}
		
	public void registerCampaignsChangesListener(ICampaignsChangeListener listener) {
		_campaignsListener.add(listener);
	}
	
	public void unregisterCampaignsChangeListener(ICampaignsChangeListener listener) {
		_campaignsListener.remove(listener);
	}
	
	public void notifyAlternativesChanges(AlternativesChangeEvent event) {
		for(IAlternativesChangeListener listener: _alternativesListener) {
			listener.notifyAlternativesChange(event);
		}
		
		Workspace.getWorkspace().updateHashCode();
	}
	
	public void notifyCriteriaChanges(CriteriaChangeEvent event) {
		for(ICriteriaChangeListener listener: _criteriaListener) {
			listener.notifyCriteriaChange(event);
		}
		
		Workspace.getWorkspace().updateHashCode();
	}
	
	public void notifyMECsChanges(MECsChangeEvent event) {
		List<IMECsChangeListener> clone = new LinkedList<IMECsChangeListener>();
		clone.addAll(_mecsListener);
		for(IMECsChangeListener listener: _mecsListener) {
			listener.notifyMecsChange(event);
		}
		
		Workspace.getWorkspace().updateHashCode();
	}
	
	public void notifyCampaignsChanges(CampaignsChangeEvent event) {
		List<ICampaignsChangeListener> clone = new LinkedList<ICampaignsChangeListener>();
		clone.addAll(_campaignsListener);
		for(ICampaignsChangeListener listener: clone) {
			listener.notifyCampaignsChange(event);
		}
		
		Workspace.getWorkspace().updateHashCode();
	}
	
	public void clear() {
		if(!_alternatives.isEmpty()) {
			_alternatives.clear();
			notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.ALTERNATIVES_CHANGES, null, _alternatives, false));
		}
		
		if(!_criteria.isEmpty()) {
			_criteria.clear();
			notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.CRITERIA_CHANGES, null, _criteria, false));
		}
		
		if(!_campaigns.isEmpty()) {
			_campaigns.clear();
			notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.CAMPAIGNS_CHANGES, null, _campaigns, false));
		}
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		for (Alternative alternative : _alternatives) {
			hcb.append(alternative);
		}
		for (Criterion criterion : _criteria) {
			hcb.append(criterion);
		}
		for (Campaign campaign : _campaigns) {
			hcb.append(campaign);
		}
		
		return hcb.toHashCode();
	}

	
	@Override
	public Object clone() throws CloneNotSupportedException {
		
		ProblemElementsSet result = null;
		
		result = (ProblemElementsSet) super.clone();
		
		result._alternatives = new LinkedList<Alternative>();
		for(Alternative alternative: _alternatives){
			result._alternatives.add((Alternative) alternative.clone());
		}
		
		result._criteria = new LinkedList<Criterion>();
		for(Criterion criterion: _criteria){
			result._criteria.add((Criterion) criterion.clone());
		}
		
		result._campaigns = new LinkedList<Campaign>();
		for(Campaign campaign: _campaigns){
			result._campaigns.add((Campaign) campaign.clone());
		}
		
		result._alternativesListener = new LinkedList<IAlternativesChangeListener>();
		for(IAlternativesChangeListener listener: _alternativesListener) {
			result._alternativesListener.add(listener);
		}
		
		result._criteriaListener = new LinkedList<ICriteriaChangeListener>();
		for(ICriteriaChangeListener listener: _criteriaListener) {
			result._criteriaListener.add(listener);
		}
		
		result._campaignsListener = new LinkedList<ICampaignsChangeListener>();
		for(ICampaignsChangeListener listener: _campaignsListener) {
			result._campaignsListener.add(listener);
		}
		
		result._mecsListener = new LinkedList<IMECsChangeListener>();
		for(IMECsChangeListener listener: _mecsListener) {
			result._mecsListener.add(listener);
		}
		
		return result;
		
	}
}