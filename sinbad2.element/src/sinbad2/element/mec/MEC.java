package sinbad2.element.mec;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.eclipse.swt.graphics.Image;

import sinbad2.element.ProblemElement;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;

public class MEC extends ProblemElement {
	
	private List<Alternative> _alternatives;
	private List<Criterion> _criteria;
	private Image _formula;
	private double _value;
	
	public MEC() {
		super();
		
		_alternatives = new LinkedList<Alternative>();
		_criteria = new LinkedList<Criterion>();
		_formula = null;
		
		Random random = new Random();
		_value = random.nextDouble();
	}
	
	public MEC(String id, Image formula, List<Alternative> alternatives, List<Criterion> criteria) {
		super(id);
		
		_alternatives = alternatives;
		_criteria = criteria;
		_formula = formula;
		
		Random random = new Random();
		_value = random.nextDouble();
	}
	
	public void setAlternatives(List<Alternative> alternatives) {
		_alternatives = alternatives;
	}
	
	public List<Alternative> getAlternatives() {
		return _alternatives;
	}
	
	public void setCriteria(List<Criterion> criteria) {
		_criteria = criteria;
	}
	
	public List<Criterion> getCriteria() {
		return _criteria;
	}
	
	public void setFormula(Image formula) {
		_formula = formula;
	}
	
	public Image getFormula() {
		return _formula;
	}
	
	public double getValue() {
		return _value;
	}
	
	public void addAlternative(Alternative a) {
		_alternatives.add(a);
	}
	
	public void removeAlternative(Alternative a) {
		_alternatives.remove(a);
	}
	
	public void addCriterion(Criterion c) {
		_criteria.add(c);
	}
	
	public void removeCriterion(Criterion c) {
		_criteria.remove(c);
	}

	@Override
	public String getCanonicalId() {
		return _id;
	}
}
