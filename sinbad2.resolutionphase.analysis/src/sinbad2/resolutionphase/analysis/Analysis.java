package sinbad2.resolutionphase.analysis;


import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.element.ProblemElementsSet;
import sinbad2.resolutionphase.IResolutionPhase;
import sinbad2.resolutionphase.state.EResolutionPhaseStateChange;
import sinbad2.resolutionphase.state.ResolutionPhaseStateChangeEvent;

public class Analysis implements IResolutionPhase {
	
	private ProblemElementsSet _elementSet;
	
	public Analysis() {
		_elementSet = new ProblemElementsSet();
	}
	
	public ProblemElementsSet getElementSet() {
		return _elementSet;
	}
	
	@Override
	public IResolutionPhase copyStructure() {
		return new Analysis();
	}
	
	@Override
	public void copyData(IResolutionPhase iResolutionPhase) {
		Analysis analysis = (Analysis) iResolutionPhase;

		clear();
		_elementSet.setAlternatives(analysis.getElementSet().getAlternatives());
		_elementSet.setCriteria(analysis.getElementSet().getCriteria());	
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_elementSet);
		return hcb.toHashCode();
	}
	
	@Override
	public IResolutionPhase clone() {
		Analysis result = null;

		try {
			result = (Analysis) super.clone();
			result._elementSet = (ProblemElementsSet) _elementSet.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		return result;
	}

	@Override
	public void notifyResolutionPhaseStateChange(
			ResolutionPhaseStateChangeEvent event) {
		if (event.getChange().equals(EResolutionPhaseStateChange.ACTIVATED)) {
			activate();
		}
	}
	
	@Override
	public void clear() {
		_elementSet.clear();
		
	}

	@Override
	public void activate() {}

	@Override
	public boolean validate() {
		if (_elementSet.getAlternatives().isEmpty()) {
			return false;
		}
		
		if (_elementSet.getCriteria().isEmpty()) {
			return false;
		}
		
		return true;
	}

}
