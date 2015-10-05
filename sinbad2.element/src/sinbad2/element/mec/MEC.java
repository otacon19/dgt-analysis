package sinbad2.element.mec;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.graphics.Image;

import sinbad2.element.ProblemElement;
import sinbad2.element.criterion.Criterion;

public class MEC extends ProblemElement {

	private Map<Criterion, List<Object>> _criteria;
	private Image _formula;
	
	public MEC() {
		super();
		
		_criteria = new LinkedHashMap<Criterion, List<Object>>();
		_formula = null;
	}
	
	public MEC(String id) {
		super(id);

		_criteria = new LinkedHashMap<Criterion, List<Object>>();
		_formula = null;
	}
	
	public MEC(String id, Image formula, LinkedHashMap<Criterion, List<Object>> criteria) {
		super(id);
		
		_criteria = criteria;
		_formula = formula;
	}
	
	public void setCriteria(LinkedHashMap<Criterion, List<Object>> criteria) {
		_criteria = criteria;
	}
	
	public LinkedHashMap<Criterion, List<Object>> getCriteria() {
		return (LinkedHashMap<Criterion, List<Object>>) _criteria;
	}
	
	public List<Criterion> getAvailableCriteria() {
		List<Criterion> criteria = new LinkedList<Criterion>();
		for(Criterion c: _criteria.keySet()) {
			criteria.add(c);
		}
		
		return criteria;
	}
	
	public void setFormula(Image formula) {
		_formula = formula;
	}
	
	public Image getFormula() {
		return _formula;
	}
	
	public void addCriterion(Criterion c, int pos, double weight) {
		List<Object> data = new LinkedList<Object>();
		data.add(pos);
		data.add(weight);
		_criteria.put(c, data);
	}
	
	public void removeCriterion(Criterion c) {
		_criteria.remove(c);
	}

	@Override
	public String getCanonicalId() {
		return _id;
	}
}
