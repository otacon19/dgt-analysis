package sinbad2.element.alternative;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.core.validator.Validator;
import sinbad2.element.ProblemElement;

public class Alternative extends ProblemElement {
	
	private Alternative _parent;
	private List<Alternative> _childrens;
	
	public Alternative() {
		super();
		_childrens = new LinkedList<Alternative>();
		_parent = null;
	}
	
	public Alternative(String id) {
		super(id);
		
		_childrens = new LinkedList<Alternative>();
		_parent = null;
	}
	
	public void setParent(Alternative parent) {
		_parent = parent;
	}
	
	public Alternative getParent() {
		return _parent;
	}
	
	public void addChildren(Alternative children) {
		Validator.notNull(children);
		Validator.notSameElement(this, children);
		
		if(_childrens == null) {
			_childrens = new LinkedList<Alternative>();
		}
		_childrens.add(children);
		children.setParent(this);
	}
	
	public void removeChildren(Alternative children) {
		
		if(_childrens != null) {
			_childrens.remove(children);
			children.setParent(null);
		}
	}
	
	public List<Alternative> getChildrens() {
		return _childrens;
	}
	
	public void setChildrens(List<Alternative> values) {
		_childrens = values;
	}
	
	public boolean hasChildrens() {
		return !_childrens.isEmpty();
	}
	
	@Override
	public String getCanonicalId() {
		return _id;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(this == obj) {
			return true;
		}
		
		if(obj == null ) {
			return false;
		}
		
		if(obj.getClass() != this.getClass()) {
			return false;
		}
		
		final Alternative other = (Alternative) obj;
		return new EqualsBuilder().append(_id, other._id).isEquals();

	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder hcb = new HashCodeBuilder(17, 31);
		hcb.append(_id);
		return hcb.toHashCode();
	}
	
	@Override
	public Object clone() {
		Alternative result = null;
		result = (Alternative) super.clone();
		
		return result;
		
	}
}
