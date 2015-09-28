package sinbad2.element.campaigns;

import java.util.Collections;
import java.util.LinkedHashMap;
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
	private String _initialDate;
	private String _finalDate;
	
	public Campaign() {
		super();
		_alternatives = new LinkedList<Alternative>();
		_criteria = new LinkedList<Criterion>();
		_values = new LinkedHashMap<Criterion, Map<Alternative, Integer>>();
	}
	
	public Campaign(String id, String name) {
		super(id);
		_name = name;
		_alternatives = new LinkedList<>();
		_criteria = new LinkedList<>();
		_values = new LinkedHashMap<Criterion, Map<Alternative, Integer>>();
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
	
	public void setInitialDate(String initialDate) {
		_initialDate = initialDate;
	}
	
	public String getInitialDate() {
		return _initialDate;
	}
	
	public void setFinalDate(String finalDate) {
		_finalDate = finalDate;
	}
	
	public String getFinalDate() {
		return _finalDate;
	}
	
	public List<String> getIntervalDate() {
		List<String> months = new LinkedList<String>();
		int monthInitialDate = Integer.parseInt(_initialDate.substring(_initialDate.length() - 5, _initialDate.length() - 3));
		int monthFinalDate = Integer.parseInt(_finalDate.substring(_finalDate.length() - 5, _finalDate.length() - 3));
		for(int i = monthInitialDate; i <= monthFinalDate; ++i) {
			if(i <= 9) {
				months.add("0" + Integer.toString(i));
			} else {
				months.add(Integer.toString(i));
			}
		}
		return months;
	}
	
	public void setDates(String initialDate, String finalDate) {
		_initialDate = initialDate;
		_finalDate = finalDate;
	}
	
	public String getProvince() {
		return _province;
	}
	
	public void setProvince(String province) {
		_province = province;
	}
	
	public void addAlternative(Alternative a) {
		_alternatives.add(a);
		Collections.sort(_alternatives);
	}
	
	public void addCriterion(Criterion c) {
		_criteria.add(c);
		Collections.sort(_criteria);
	}
	
	public void addValue(Criterion c, Alternative a, int value) {
		Map<Alternative, Integer> valueAlternative;
		if(_values.get(c) != null) {
			valueAlternative = _values.get(c);
		} else {
			valueAlternative = new LinkedHashMap<Alternative, Integer>();
		}
		valueAlternative.put(a, value);
		_values.put(c, valueAlternative);
	}
	
	public int getValue(Criterion c, Alternative a) {
		if(_values.get(c).get(a) != null) {
			return _values.get(c).get(a);
		} else {
			return 0;
		}
	}
	
	public Map<Alternative, Integer> getAlternativesWithValues(Criterion c) {
		return _values.get(c);
	}
	
	public int getAcumValue(Criterion c, Alternative aParent, List<Alternative> alternativesSelected) {
		Map<Alternative, Integer> valueAlternative = _values.get(c);
		int acum = 0;
		for(Alternative a: valueAlternative.keySet()) {
			if(aParent.getChildrens().contains(a) && alternativesSelected.contains(a)) {
				acum += valueAlternative.get(a);
			}
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
		result.setInitialDate(_initialDate) ;
		result.setFinalDate(_finalDate);
		result.setValues(_values);
		
		return result;
		
	}

}
