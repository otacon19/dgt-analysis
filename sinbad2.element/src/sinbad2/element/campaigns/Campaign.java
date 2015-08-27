package sinbad2.element.campaigns;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import sinbad2.element.ProblemElement;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.mec.MEC;

public class Campaign extends ProblemElement {

	private List<Alternative> _alternatives;
	private List<Criterion> _criteria;
	private List<MEC> _mecs;
	private String _date;
	
	public Campaign() {
		super();
		_alternatives = new LinkedList<Alternative>();
		_criteria = new LinkedList<Criterion>();
		_mecs = new LinkedList<MEC>();
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		_date = dateFormat.format(date);
		  
	}
	
	public Campaign(String id) {
		super(id);
		_alternatives = new LinkedList<>();
		_criteria = new LinkedList<>();
		_mecs = new LinkedList<MEC>();
		
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		Date date = new Date();
		_date = dateFormat.format(date);
	}
	
	public List<Alternative> getAlternatives() {
		return _alternatives;
	}
	
	public void setAlternatives(List<Alternative> alternatives) {
		_alternatives = alternatives;
	}
	
	public List<Criterion> getCriteria() {
		return _criteria;
	}
	
	public void setCriteria(List<Criterion> criteria) {
		_criteria = criteria;
	}
	
	public List<MEC> getMECs() {
		return _mecs;
	}
	
	public void setMECs(List<MEC> mecs) {
		_mecs = mecs;
	}
	
	public List<String> getMECsId() {
		List<String> result = new LinkedList<>();
		for(MEC m: _mecs) {
			result.add(m.getId());
		}
		
		return result;
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
	
	public void addMEC(MEC m) {
		_mecs.add(m);
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
		
		return result;
		
	}

}
