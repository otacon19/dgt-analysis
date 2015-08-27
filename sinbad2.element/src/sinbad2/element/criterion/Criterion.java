package sinbad2.element.criterion;

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.element.ProblemElement;

public class Criterion extends ProblemElement {
	private boolean _isDirect;
	
	public Criterion() {
		super();
	}
	
	public Criterion(String id) {
		super(id);
	}
	
	public void setIsDirect(boolean isDirect) {
		_isDirect = isDirect;
	}
	
	public boolean getIsDirect() {
		return _isDirect;
	}
	
	@Override
	public String getCanonicalId() {
		String result = _id;

		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null) {
			return false;
		}
		
		if(obj.getClass() != this.getClass()) {
			return false;
		}
		
		final Criterion other = (Criterion) obj;
		
		EqualsBuilder eb = new EqualsBuilder();
		eb.append(_id, other._id);
		
		return eb.isEquals();
	}
	
	public static Criterion getCriterionByCanonicalId(List<Criterion> criteria, String formatId) {
		
		Validator.notNull(criteria);
		Validator.notNull(formatId);
		
		for(Criterion criterion: criteria) {
			if(criterion.getId().equals(formatId)) {
				return criterion;
			}
		}
		
		return null;
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(getCanonicalId());

		return hcb.toHashCode();
	}
	
	@Override
	public Object clone() {
		Criterion result = null;
		
		result = (Criterion) super.clone();
		
		return result;
	}
}
