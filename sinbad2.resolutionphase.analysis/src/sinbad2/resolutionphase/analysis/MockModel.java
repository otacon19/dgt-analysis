package sinbad2.resolutionphase.analysis;

import java.util.List;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.mec.MEC;

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
		a1.setIsDirect(false);
		a1.addChildren(new Alternative("Ocio"));
		a1.addChildren(new Alternative("Trabajo"));
		elementSet.addAlternative(a1, true);
		Alternative a2 = new Alternative("Tipo vehículo");
		a2.setIsDirect(false);
		a2.addChildren(new Alternative("Coche"));
		a2.addChildren(new Alternative("Moto"));
		a2.addChildren(new Alternative("Camion"));
		elementSet.addAlternative(a2, true);
		Alternative a3 = new Alternative("Franja horaria");
		a3.setIsDirect(false);
		a3.addChildren(new Alternative("6:00-14:00"));
		a3.addChildren(new Alternative("14:00-22:00"));
		a3.addChildren(new Alternative("22:00-6:00"));
		elementSet.addAlternative(a3, true);
		Alternative a4 = new Alternative("Periodo semanal");
		a4.setIsDirect(false);
		a4.addChildren(new Alternative("Entresemana"));
		a4.addChildren(new Alternative("Fin de semana"));
		elementSet.addAlternative(a4, true);
		Alternative a5 = new Alternative("Permisos");
		a5.setIsDirect(true);
		a5.addChildren(new Alternative("A"));
		a5.addChildren(new Alternative("A1"));
		elementSet.addAlternative(a5, true);
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
		//Directos
		Criterion c4 = new Criterion("Habitantes");
		c4.setIsDirect(true);
		elementSet.addCriterion(c4, true);
		Criterion c5 = new Criterion("Censo");
		c5.setIsDirect(true);
		elementSet.addCriterion(c5, true);
		Criterion c6 = new Criterion("Licencias");
		c6.setIsDirect(true);
		elementSet.addCriterion(c6, true);
		
		//Campañas
		//Campañas Jaén 2015
		//Random random = new Random();
		int value = 0;
		for(int i = 0; i < 11; ++i) {
			Campaign campaign_1 = new Campaign(Integer.toString(i), "Campaña");
			for(Criterion cri: elementSet.getCriteria()) {
				if(!cri.isDirect()) {
					campaign_1.addCriterion(cri);
				}
			}
			for(Alternative alt: elementSet.getAlternatives()) {
				if(!alt.isDirect()) {
					campaign_1.addAlternative(alt);
				}
			}

			for(Criterion c: campaign_1.getCriteria()) {
				for(Alternative a: campaign_1.getAlternatives()) {
					if(a.hasChildrens()) {
						List<Alternative> childrens = a.getChildrens();
						for(Alternative children: childrens) {
							if(c.getId().equals("Distancia")) {
								value = 10000 / childrens.size();
							} else if(c.getId().equals("Tiempo")) {
								value = 20000 / childrens.size();
							} else if(c.getId().equals("Desplazamientos")) {
								value = 30000 / childrens.size();
							}
							campaign_1.addValue(c, children, value);
						}
					}
				}
			}
			//Provincia
			campaign_1.setProvince("Jaén");
			
			//Fecha
			if(i < 10) {
				campaign_1.setDate("0" + (i+1) + "/15");
			} else {
				campaign_1.setDate((i+1) + "/15");
			}
			elementSet.addCampaign(campaign_1);
		}
		
		Campaign campaign_extra = new Campaign("11", "Campaña");
		campaign_extra.addAlternative(a1);
		campaign_extra.addAlternative(a3);
		campaign_extra.addCriterion(c1);
		campaign_extra.addCriterion(c3);
		for(Criterion c: campaign_extra.getCriteria()) {
			for(Alternative a: campaign_extra.getAlternatives()) {
				if(a.hasChildrens()) {
					List<Alternative> childrens = a.getChildrens();
					for(Alternative children: childrens) {
						if(c.getId().equals("Distancia")) {
							value = 10000 / childrens.size();
						} else if(c.getId().equals("Tiempo")) {
							value = 20000 / childrens.size();
						} else if(c.getId().equals("Desplazamientos")) {
							value = 30000 / childrens.size();
						}
						campaign_extra.addValue(c, children, value);
					}
				}
			}
		}
		
		campaign_extra.setProvince("Jaén");
		campaign_extra.setDate("12" + "/15");
		elementSet.addCampaign(campaign_extra);
		
		Campaign campaign_data = new Campaign("12", "Campaña");
		campaign_data.addAlternative(a5);
		campaign_data.addCriterion(c4);
		campaign_data.addCriterion(c5);
		campaign_data.addCriterion(c6);
		for(Criterion c: campaign_data.getCriteria()) {
			for(Alternative a: campaign_data.getAlternatives()) {
				if(a.hasChildrens()) {
					List<Alternative> childrens = a.getChildrens();
					for(Alternative children: childrens) {
						if(c.getId().equals("Habitantes")) {
							value = 100000 / childrens.size();
						} else if(c.getId().equals("Censo")) {
							value = 200000 / childrens.size();
						} else if(c.getId().equals("Licencias")) {
							value = 300000 / childrens.size();
						}
						campaign_data.addValue(c, children, value);
					}
				}
			}
		}
		
		campaign_data.setProvince("Jaén");
		campaign_data.setDate("01" + "/15");
		elementSet.addCampaign(campaign_data);
			
		int id = 13;
		for(int i = 0; i < 12; ++i) {
			Campaign campaign_1 = new Campaign(Integer.toString(id), "Campaña");
			for(Criterion cri: elementSet.getCriteria()) {
				if(!cri.isDirect()) {
					campaign_1.addCriterion(cri);
				}
			}
			for(Alternative alt: elementSet.getAlternatives()) {
				if(!alt.isDirect()) {
					campaign_1.addAlternative(alt);
				}
			}
			
			for(Criterion c: campaign_1.getCriteria()) {
				for(Alternative a: campaign_1.getAlternatives()) {
					if(a.hasChildrens()) {
						List<Alternative> childrens = a.getChildrens();
						for(Alternative children: childrens) {
							if(c.getId().equals("Distancia")) {
								value = 15000 / childrens.size();
							} else if(c.getId().equals("Tiempo")) {
								value = 25000 / childrens.size();
							} else if(c.getId().equals("Desplazamientos")) {
								value = 35000 / childrens.size();
							}
							campaign_1.addValue(c, children, value);
						}
					}
				}
			}
			
			//Provincia
			campaign_1.setProvince("Granada");
			
			//Fecha
			if(i < 10) {
				campaign_1.setDate("0" + (i+1) + "/15");
			} else {
				campaign_1.setDate((i+1) + "/15");
			}
			elementSet.addCampaign(campaign_1);
			id++;
		}
		//MECs
		MEC distancia = new MEC("Distancia");
		distancia.addCriterion(c1, 0, 1.0);
		elementSet.addMEC(distancia);
		MEC tiempo = new MEC("Tiempo");
		tiempo.addCriterion(c2, 0, 1.0);
		elementSet.addMEC(tiempo);
		MEC desplazamientos = new MEC("Desplazamientos");
		desplazamientos.addCriterion(c3, 0, 1.0);
		elementSet.addMEC(desplazamientos);
	}
	
}
