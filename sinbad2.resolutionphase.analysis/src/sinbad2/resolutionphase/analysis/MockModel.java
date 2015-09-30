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
		a1.addChildren(new Alternative("Ocio"));
		a1.addChildren(new Alternative("Trabajo"));
		elementSet.addAlternative(a1, true);
		Alternative a2 = new Alternative("Tipo vehículo");
		a2.addChildren(new Alternative("Coche"));
		a2.addChildren(new Alternative("Moto"));
		a2.addChildren(new Alternative("Camion"));
		elementSet.addAlternative(a2, true);
		Alternative a3 = new Alternative("Franja horaria");
		a3.addChildren(new Alternative("6:00-14:00"));
		a3.addChildren(new Alternative("14:00-22:00"));
		a3.addChildren(new Alternative("22:00-6:00"));
		elementSet.addAlternative(a3, true);
		Alternative a4 = new Alternative("Periodo semanal");
		a4.addChildren(new Alternative("Entresemana"));
		a4.addChildren(new Alternative("Fin de semana"));
		elementSet.addAlternative(a4, true);
		Alternative a5 = new Alternative("Permisos");
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
		Campaign campaign = new Campaign("0", "Campaña Navidad");
		for(Criterion cri: elementSet.getCriteria()) {
			if(!cri.isDirect()) {
				campaign.addCriterion(cri);
			}
		}
		campaign.setAlternatives(elementSet.getAlternatives());
		int value = 0;
		for(Criterion c: campaign.getCriteria()) {
			for(Alternative a: campaign.getAlternatives()) {
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
						campaign.addValue(c, children, value);
					}
				}
			}
		}
		//Provincia
		campaign.setProvince("Jaén");
		//Fecha
		campaign.setDates("11/15", "12/15");
		elementSet.addCampaign(campaign);
		
		Campaign campaignWithoutAllData = new Campaign("1", "Campaña_Diciembre");
		campaignWithoutAllData.addAlternative(a1);
		campaignWithoutAllData.addAlternative(a3);
		campaignWithoutAllData.addCriterion(c1);
		campaignWithoutAllData.addCriterion(c3);
		for(Criterion c: campaignWithoutAllData.getCriteria()) {
			for(Alternative a: campaignWithoutAllData.getAlternatives()) {
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
						campaignWithoutAllData.addValue(c, children, value);
					}
				}
			}
		}
		
		campaignWithoutAllData.setProvince("Jaén");
		campaignWithoutAllData.setDates("12/15", "12/15");
		elementSet.addCampaign(campaignWithoutAllData);
		
		Campaign campaignSummer = new Campaign("2", "Campaña_Verano");
		campaignSummer.addAlternative(a1);
		campaignSummer.addCriterion(c1);
		campaignSummer.addCriterion(c2);
		campaignSummer.addCriterion(c3);
		for(Criterion c: campaignSummer.getCriteria()) {
			for(Alternative a: campaignSummer.getAlternatives()) {
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
						campaignSummer.addValue(c, children, value);
					}
				}
			}
		}
		campaignSummer.setProvince("Jaén");
		campaignSummer.setDates("06/15", "09/15");
		elementSet.addCampaign(campaignSummer);
		
		Campaign campaign_data = new Campaign("3", "Campaña_Datos_1");
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
		campaign_data.setDates("01/15", "01/15");
		elementSet.addCampaign(campaign_data);
		
		campaign_data = new Campaign("4", "Campaña_Datos_2");
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
							value = 110000 / childrens.size();
						} else if(c.getId().equals("Censo")) {
							value = 210000 / childrens.size();
						} else if(c.getId().equals("Licencias")) {
							value = 310000 / childrens.size();
						}
						campaign_data.addValue(c, children, value);
					}
				}
			}
		}
		campaign_data.setProvince("Jaén");
		campaign_data.setDates("02/15", "02/15");
		elementSet.addCampaign(campaign_data);
		
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
