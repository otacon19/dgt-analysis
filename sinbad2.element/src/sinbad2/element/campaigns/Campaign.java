package sinbad2.element.campaigns;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.element.ProblemElement;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;

public class Campaign extends ProblemElement {
	
	private String _name;
	private List<Alternative> _alternatives;
	private List<Criterion> _criteria;
	private Map<Criterion, Map<Alternative, Integer>> _values;
	private String _province;
	private String _date;
	
	public Campaign() {
		super();
		_alternatives = new LinkedList<Alternative>();
		_criteria = new LinkedList<Criterion>();
		_values = new HashMap<Criterion, Map<Alternative, Integer>>();
	}
	
	public Campaign(String id, String name) {
		super(id);
		_name = name;
		_alternatives = new LinkedList<>();
		_criteria = new LinkedList<>();
		_values = new HashMap<Criterion, Map<Alternative, Integer>>();
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public String getName() {
		return _name;
	}
	
	public List<Alternative> getAlternatives() {
		return _alternatives;
	}
	
	public void setAlternatives(List<Alternative> alternatives) {
		_alternatives = alternatives;
	}
	
	public List<String> getAlternativesIds() {
		List<String> result = new LinkedList<String>();
		for(Alternative a: _alternatives) {
			result.add(a.getId());
		}
		return result;
	}
	
	public List<Criterion> getCriteria() {
		return _criteria;
	}
	
	public void setCriteria(List<Criterion> criteria) {
		_criteria = criteria;
	}
	
	public List<String> getCriteriaIds() {
		List<String> result = new LinkedList<String>();
		for(Criterion c: _criteria) {
			result.add(c.getId());
		}
		return result;
	}
	
	public Map<Criterion, Map<Alternative, Integer>> getValues() {
		return _values;
	}
	
	public void setValues(Map<Criterion, Map<Alternative, Integer>> values) {
		_values = values;
	}
	
	public void setDate(String date) {
		_date = date;
	}
	
	public String getProvince() {
		return _province;
	}
	
	public void setProvince(String province) {
		_province = province;
	}
	
	public String getDate() {
		return _date;
	}
	
	public void addAlternative(Alternative a) {
		_alternatives.add(a);
	}
	
	public void addCriterion(Criterion c) {
		_criteria.add(c);
	}
	
	public void addValue(Criterion c, Alternative a, int value) {
		Map<Alternative, Integer> valueAlternative;
		if(_values.get(c) != null) {
			valueAlternative = _values.get(c);
		} else {
			valueAlternative = new HashMap<Alternative, Integer>();
		}
		valueAlternative.put(a, value);
		_values.put(c, valueAlternative);
	}
	
	public int getValue(Criterion c, Alternative a) {
		return _values.get(c).get(a);
	}
	
	public Map<Alternative, Integer> getAlternativesWithValues(Criterion c) {
		return _values.get(c);
	}
	
	public int getAcumValue(Criterion c) {
		Map<Alternative, Integer> valueAlternative = _values.get(c);
		int acum = 0;
		for(Alternative a: valueAlternative.keySet()) {
			acum += valueAlternative.get(a);
		}
		
		return acum;
	}
	
	public boolean isACampaignData() {
		for(Criterion c: _criteria) {
			if(c.isDirect()) {
				return true;
			}
		}
		return false;
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
		
		final Campaign other = (Campaign) obj;
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
		Campaign result = null;
		result = (Campaign) super.clone();
		
		result.setName(_name);
		result.setCriteria(_criteria);
		result.setAlternatives(_alternatives);
		result.setProvince(_province);
		result.setDate(_date) ;
		result.setValues(_values);
		
		return result;
		
	}

}
