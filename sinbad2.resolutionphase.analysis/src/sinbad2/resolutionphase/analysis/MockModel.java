package sinbad2.resolutionphase.analysis;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.criterion.Criterion;

public class MockModel {
	
	ProblemElementsManager _elementsManager;
	
	public MockModel() {
		_elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementSet = new ProblemElementsSet();
		_elementsManager.setActiveElementSet(elementSet);
		initialize();
	}
	
	private void initialize() {	
		ProblemElementsSet elementSet = _elementsManager.getActiveElementSet();
		//Alternativas
		Alternative a1 = new Alternative("Motivo");
		elementSet.addAlternative(a1, true);
		Alternative a2 = new Alternative("Tipo vehículo");
		elementSet.addAlternative(a2, true);
		Alternative a3 = new Alternative("Franja horaria");
		elementSet.addAlternative(a3, true);
		Alternative a4 = new Alternative("Periodo semanal");
		elementSet.addAlternative(a4, true);
		//Criterios
		Criterion c1 = new Criterion("Distancia");
		c1.setIsDirect(false);
		elementSet.addCriterion(c1, true);
		Criterion c2 = new Criterion("Tiempo");
		c2.setIsDirect(false);
		elementSet.addCriterion(c2, true);
		Criterion c3 = new Criterion("Desplazamientos");
		c3.setIsDirect(false);
		elementSet.addCriterion(c3, true);
		Criterion c4 = new Criterion("NºHabitantes");
		c4.setIsDirect(true);
		elementSet.addCriterion(c4, true);
		Criterion c5 = new Criterion("Parque vehículos");
		c5.setIsDirect(true);
		elementSet.addCriterion(c5, true);
		Criterion c6 = new Criterion("Licencias");
		c6.setIsDirect(true);
		elementSet.addCriterion(c6, true);
		//Campañas
		Campaign campaign_1 = new Campaign("Campaña Agosto/15");
		campaign_1.setAlternatives(elementSet.getAlternatives());
		campaign_1.setCriteria(elementSet.getCriteria());
		elementSet.addCampaign(campaign_1);
		Campaign campaign_2 = new Campaign("Campaña Septiembre/15");
		campaign_2.setAlternatives(elementSet.getAlternatives());
		campaign_2.setCriteria(elementSet.getCriteria());
		elementSet.addCampaign(campaign_2);
	}
	
}
