package sinbad2.element.ui.view.mecs.jfreechart;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.UnknownKeyException;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.experimental.chart.swt.ChartComposite;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.RectangleInsets;

import sinbad2.element.alternative.Alternative;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.mec.MEC;
import sinbad2.element.ui.view.alternatives.AlternativesView;

public class MECChart {
	
	private JFreeChart _barChart;
	private JFreeChart _lineChart;
	private ChartComposite _chartComposite;
	
	private Map<Campaign, MEC> _categoriesAndSeries;
	String _action;

	public MECChart() {
		_barChart = null;
		_lineChart = null;
		_chartComposite = null;
	
		_categoriesAndSeries = new LinkedHashMap<Campaign, MEC>();
	}
	
	public void refreshBarChart() {
		if(_barChart == null) {
			_barChart = createBarChart(createBarChartDatasetCombineCampaigns());
		} else {
			if(_action.equals("contexts")) {
				_barChart.getCategoryPlot().setDataset(createBarChartDatasetSeparateContexts());
			} else if(_action.equals("separate")) {
				_barChart.getCategoryPlot().setDataset(createBarChartDatasetSeparateCampaigns());	
			} else if(_action.equals("combine")){
				_barChart.getCategoryPlot().setDataset(createBarChartDatasetCombineCampaigns());
			} else if(_action.equals("separate_provinces")) {
				_barChart.getCategoryPlot().setDataset(createBarChartDatasetSeparateProvinces());
			}
		}
	}
	
	public void refreshLineChart() {
		if(_lineChart == null) {
			_lineChart = createLineChart(createLineChartDatasetCombineCampaigns());
		} else {
			if(_action.equals("contexts")) {
				_lineChart.getXYPlot().setDataset(createLineChartDatasetSeparateContexts());
			} else if(_action.equals("separate")) {
				_lineChart.getXYPlot().setDataset(createLineChartDatasetSeparateCampaigns());
			} else if(_action.equals("combine")) {
				_lineChart.getXYPlot().setDataset(createLineChartDatasetCombineCampaigns());
			} else if(_action.equals("separate_provinces")) {
				_lineChart.getXYPlot().setDataset(createLineChartDatasetSeparateProvinces());
			}
		}
	}

	public void setMEC(Map<Campaign, MEC> categoriesAndSeries, int typeChart, String action) {
		_categoriesAndSeries = categoriesAndSeries;
		_action = action;
		
		if(typeChart == 0) {
			_chartComposite.setChart(_barChart);
			refreshBarChart();
		} else {
			_chartComposite.setChart(_lineChart);
			refreshLineChart();
		}
	}
	
	public void initializeBarChart(Composite container, int width, int height, int style) {
		refreshBarChart();
		
		if(_chartComposite == null) {
			_chartComposite = new ChartComposite(container, style, _barChart, true);
		}
		
		_chartComposite.setChart(_barChart);
		_chartComposite.redraw();
		_chartComposite.setSize(width, height);
	}
	
	public void initializeLineChart(Composite container, int width, int height, int style) {
		refreshLineChart();
		
		if(_chartComposite == null) {
			_chartComposite = new ChartComposite(container, style, _lineChart, true);
		}
		
		_chartComposite.setChart(_lineChart);
		_chartComposite.redraw();
		_chartComposite.setSize(width, height);
	}
	
	public void clear() {
		if(_barChart != null) {
			_barChart.getCategoryPlot().setDataset(new DefaultCategoryDataset());
		}
		
		if(_lineChart != null) {
			_lineChart.getXYPlot().setDataset(new XYSeriesCollection());
		}
	}
	
	public void setSize(int width, int height) {
		_chartComposite.setSize(width, height);
		_chartComposite.redraw();
	}
	
	private JFreeChart createBarChart(CategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createBarChart3D(null, null, null, dataset, PlotOrientation.VERTICAL, false, true, false);

        chart.setBackgroundPaint(Color.white);
    
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);
        BarRenderer br = (BarRenderer) plot.getRenderer();
		br.setMaximumBarWidth(.05);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        //rangeAxis.setTickUnit(new NumberTickUnit(0.1));
        rangeAxis.setAutoRange(true);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        DecimalFormat decimalformat = new DecimalFormat("######,###.000");
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", decimalformat)); //$NON-NLS-1$ //$NON-NLS-2$
        renderer.setBaseItemLabelsVisible(false);
        
        return chart;
	}
	
	private static JFreeChart createLineChart(XYDataset dataset) {

		JFreeChart chart = ChartFactory.createXYLineChart(null, null, null, dataset, PlotOrientation.VERTICAL, false, true, false);

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setRangeGridlinePaint(Color.white);
        
        String[] months =  new String[12];
        months[0] = "January";
        months[1] = "February";
        months[2] = "March";
        months[3] = "April";
        months[4] = "May";
        months[5] = "June";
        months[6] = "July";
        months[7] = "August";
        months[8] = "September";
        months[9] = "October";
        months[10] = "November";
        months[11] = "December";
        SymbolAxis rangeAxis = new SymbolAxis("", months);
        plot.setDomainAxis(rangeAxis);
        
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);
        
        return chart;
    }
	
	private CategoryDataset createBarChartDatasetCombineCampaigns() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        if(!_categoriesAndSeries.isEmpty()) {
	        List<Double> dataValues = loadCampaignsDataDirect();
	        List<Campaign> noDataCampaigns = getNoDataCampaigns();
	        List<Alternative> noDataAlternativesCampaigns = getNoDataAlternativesCampaigns();
	        
	        double acumValue, value, numerator, denominator, weight;
	        int pos = -1;
	        String category = "";
	       
	        MEC mec = null;
	        List<Object> data;
	        Map<Campaign, List<Double>> campaignsTotalValue = new LinkedHashMap<Campaign, List<Double>>();
	        List<Double> numeratorAndDenominator;
	        for(Campaign campaign: noDataCampaigns) {
	        	mec = _categoriesAndSeries.get(campaign);
	        	Map<Criterion, List<Object>> criteriaData = mec.getCriteria();
	        	numerator = 1;
	        	denominator = 1;
	        	for(Criterion c: criteriaData.keySet()) {
	        		if(campaign.getCriteria().contains(c)) {
	        			value = 0;
		        		acumValue = 0;
		        		data = criteriaData.get(c);
		        		for(Alternative a: noDataAlternativesCampaigns) {
		        			if(a.hasChildrens()) {
		        				List<Alternative> childrens = a.getChildrens();
		        				for(Alternative children: childrens) {
		        					if(noDataAlternativesCampaigns.contains(children)) {
		        						acumValue += campaign.getValue(c, children);
		        					}
		        				}
		        				if(value == 0) {
	        						value = acumValue;
	        					} else if(acumValue < value) {
		        					value = acumValue;
		        				}
		        			}
		        		}
		        		weight = (double) data.get(1);
		        		value *= weight;
		        		if(!c.isDirect()) {
			        		pos = (int) data.get(0);
		    				if(pos == 0) {
		    					numerator *= value;
		    				} else {
		    					denominator *= value;
		    				}
		        		}
	        		}
	        	}
		        numeratorAndDenominator = new LinkedList<Double>();
		        numeratorAndDenominator.add(numerator);
		        numeratorAndDenominator.add(denominator);
	        	campaignsTotalValue.put(campaign, numeratorAndDenominator);
	        	category += campaign.getName() + "-";
	        }
	        
	        double valueAcum = 0;
	        double numeratorAcum = 0, denominatorAcum = 0;
	        for(Campaign c: campaignsTotalValue.keySet()) {
	        	List<Double> nAd = campaignsTotalValue.get(c);
	        	if(nAd.get(0) != 1) {
	        		numeratorAcum += nAd.get(0);
	        	}
	        	if(nAd.get(1) != 1) {
	        		denominatorAcum += nAd.get(1);
	        	}
	        }
	        if(numeratorAcum == 0) {
	        	numeratorAcum = 1;
	        }
	        if(denominatorAcum == 0) {
	        	denominatorAcum = 1;
	        }
	        
	        if(dataValues.isEmpty()) {
	        	valueAcum = numeratorAcum / denominatorAcum;
	        	if(valueAcum == 1) {
	        		valueAcum = 0;
	        	}
	        } else {
	        	valueAcum = (numeratorAcum * dataValues.get(0)) / (denominatorAcum * dataValues.get(1));
	        }
	        if(Double.isInfinite(valueAcum)) {
	        	valueAcum = 0;
	        }
	        
	        category = category.substring(0, category.length() - 1);
	        dataset.addValue(valueAcum, mec.getId(), category);
	        
	        if(_barChart != null) {
	        	if(_barChart.getLegend() == null) {
		        	LegendTitle legend = new LegendTitle(_barChart.getPlot());
		            legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
		            legend.setFrame(new LineBorder());
		            legend.setBackgroundPaint(Color.white);
		            legend.setPosition(RectangleEdge.BOTTOM);
		            _barChart.addLegend(legend);
	        	}
	    	}
        }        
        return dataset;
	}
	
	private List<Double> loadCampaignsDataDirect() {
		List<Alternative> alternativesSelected = AlternativesView.getAlternativesSelected();
		List<Double> numeratorAndDenominator;
		double numerator = 1, denominator = 1;
		
		List<Campaign> dataCampaigns = new LinkedList<Campaign>();
		for(Campaign c: _categoriesAndSeries.keySet()) {
			if(c.isACampaignData()) {
				dataCampaigns.add(c);
			}
		}
		
		if(!dataCampaigns.isEmpty()) {
			MEC mecSelected = _categoriesAndSeries.get(dataCampaigns.get(0));
			Map<Criterion, List<Object>> mecCriteria = mecSelected.getCriteria();
			Map<Criterion, Double> dataValues = new LinkedHashMap<Criterion, Double>();
			Map<Criterion, Integer> dataValuesRepeat = new LinkedHashMap<Criterion, Integer>();
			List<Object> dataOfCriterion;
			double acumValue = 0, weight;
			for(Campaign dataCampaign: dataCampaigns) {
				List<Criterion> dataCampaignCriteria = dataCampaign.getCriteria();
				Map<Criterion, List<Object>> criteriaData = mecSelected.getCriteria();
				for(Criterion c: dataCampaignCriteria) {
					dataOfCriterion = criteriaData.get(c);
					acumValue = 0;
					if(mecCriteria.containsKey(c)) {
						for(Alternative alternativeSelected: alternativesSelected) {
							if(!alternativeSelected.hasChildrens() && alternativeSelected.isDirect()) {
								acumValue += dataCampaign.getValue(c, alternativeSelected);
							}
						}
						weight = (double) dataOfCriterion.get(1);
						acumValue *= weight;
						if(dataValues.containsKey(c)) {
							acumValue += dataValues.get(c);
							int rep = dataValuesRepeat.get(c);
							rep++;
							dataValuesRepeat.put(c, rep);
							if(rep == dataCampaigns.size()) {
								acumValue /= dataCampaigns.size();
							}
						} else {
							dataValuesRepeat.put(c, 1);
						}
						dataValues.put(c, acumValue);
					}
				}
			}
			
			int pos;
			for(Criterion c: dataValues.keySet()) {
				pos = (int) mecSelected.getCriteria().get(c).get(0);
				if(pos == 0) {
					numerator *= dataValues.get(c);
				} else {
					denominator *= dataValues.get(c);
				}
			}
			
			numeratorAndDenominator = new LinkedList<Double>();
			numeratorAndDenominator.add(numerator);
			numeratorAndDenominator.add(denominator);
			
			return numeratorAndDenominator;
			
		} else {
			return new LinkedList<Double>();
		}
	}
	
	private List<Campaign> getNoDataCampaigns() {
		List<Campaign> result = new LinkedList<Campaign>();
		for(Campaign c: _categoriesAndSeries.keySet()) {
			if(!c.isACampaignData()) {
				result.add(c);
			}
		}
		return result;
	}

	private List<Alternative> getNoDataAlternativesCampaigns() {
		List<Alternative> result = new LinkedList<Alternative>();
		List<Alternative> alternativesSelected = AlternativesView.getAlternativesSelected();
		for(Alternative a: alternativesSelected) {
			if(!a.isDirect()) {
				result.add(a);
			}
		}
		
		return result;
	}
	
	private CategoryDataset createBarChartDatasetSeparateCampaigns() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        MEC mec;
        double acumValue, value, numerator, denominator, weight, total;
        int pos = -1;
        List<Double> dataValues = loadCampaignsDataDirect();
        List<Campaign> noDataCampaigns = getNoDataCampaigns();
        List<Alternative> noDataAlternativesCampaigns = getNoDataAlternativesCampaigns();
        for(Campaign campaign: noDataCampaigns) {
        	mec = _categoriesAndSeries.get(campaign);
        	Map<Criterion, List<Object>> criteriaData = mec.getCriteria();
        	numerator = 1;
        	denominator = 1;
        	for(Criterion c: criteriaData.keySet()) {
        		if(campaign.getCriteria().contains(c)) {
	        		acumValue = 0;
	        		value = 0;
	        		List<Object> data = criteriaData.get(c);
	        		for(Alternative a: noDataAlternativesCampaigns) {
	        			if(a.hasChildrens() && !a.isDirect()) {
	        				List<Alternative> childrens = a.getChildrens();
	        				for(Alternative children: childrens) {
	        					if(noDataAlternativesCampaigns.contains(children)) {
	        						acumValue += campaign.getValue(c, children);
	        					}
	        				}
	        				if(value == 0) {
        						value = acumValue;
        					} else if(acumValue < value) {
	        					value = acumValue;
	        				}
	        			}
	        		}
	        		weight = (double) data.get(1);
	        		value *= weight;
	        		if(!c.isDirect()) {
		        		pos = (int) data.get(0);
	    				if(pos == 0) {
	    					numerator *= value;
	    					if(numerator == 0) {
	    						numerator = 1;
	    					}
	    				} else {
	    					denominator *= value;
	    					if(denominator == 0) {
	    						denominator = 1;
	    					}
	    				}
	        		}
	        	}
	        	
        		if(!dataValues.isEmpty()) {
		        	total = (numerator * dataValues.get(0)) / (denominator * dataValues.get(1));
		        	if(Double.isInfinite(total)) {
		        		total = 0;
		        	}
	        	} else {
		        	total = numerator / denominator;
		        	if(total == 1) {
		        		total = 0;
		        	}
	        	}
	        	
	        	String category = campaign.getName();
		    	dataset.addValue(total, mec.getId(), category);
        	}
        }
        
        if(_barChart != null) {
        	if(_barChart.getLegend() == null) {
	        	LegendTitle legend = new LegendTitle(_barChart.getPlot());
	            legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
	            legend.setFrame(new LineBorder());
	            legend.setBackgroundPaint(Color.white);
	            legend.setPosition(RectangleEdge.BOTTOM);
	            _barChart.addLegend(legend);
        	}
    	}

        return dataset;
	}
	
	private CategoryDataset createBarChartDatasetSeparateProvinces() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        Map<String, List<Campaign>> campaignsForProvinces = campaignsSameProvince();
        Map<Campaign, List<Double>> campaignsTotalValue = new LinkedHashMap<Campaign, List<Double>>();
		List<String> provinces = getProvincesCampaigns();
        List<Double> dataValues = loadCampaignsDataDirect();
        List<Alternative> noDataAlternativesCampaigns = getNoDataAlternativesCampaigns();
        List<Double> numeratorAndDenominator;
		
		MEC mec = null;
		double acumValue, value, numerator = 1, denominator = 1, weight;
        int pos = -1;
        for(String province: provinces) {
        	List<Campaign> campaignsProvinces = campaignsForProvinces.get(province);
        	campaignsTotalValue.clear();
        	for(Campaign campaign: campaignsProvinces) {
	        	mec = _categoriesAndSeries.get(campaign);
	        	Map<Criterion, List<Object>> criteriaData = mec.getCriteria();
	        	numerator = 1;
	        	denominator = 1;
	        	for(Criterion c: criteriaData.keySet()) {
	        		if(campaign.getCriteria().contains(c)) {
		        		acumValue = 0;
		        		value = 0;
		        		List<Object> data = criteriaData.get(c);
		        		for(Alternative a: noDataAlternativesCampaigns) {
		        			if(a.hasChildrens() && !a.isDirect()) {
		        				List<Alternative> childrens = a.getChildrens();
		        				for(Alternative children: childrens) {
		        					if(noDataAlternativesCampaigns.contains(children)) {
		        						acumValue += campaign.getValue(c, children);
		        					}
		        				}
		        				if(value == 0) {
	        						value = acumValue;
	        					} else if(acumValue < value) {
		        					value = acumValue;
		        				}
		        			}
		        		}
		        		weight = (double) data.get(1);
		        		value *= weight;
		        		if(!c.isDirect()) {
			        		pos = (int) data.get(0);
		    				if(pos == 0) {
		    					numerator *= value;
		    				} else {
		    					denominator *= value;
		    				}
		        		}
		        	}
		        	
	        		numeratorAndDenominator = new LinkedList<Double>();
			        numeratorAndDenominator.add(numerator);
			        numeratorAndDenominator.add(denominator);
		        	campaignsTotalValue.put(campaign, numeratorAndDenominator);
	        	}
        	}
        	
        	double valueAcum = 0;
	        double numeratorAcum = 0, denominatorAcum = 0;
	        for(Campaign c: campaignsTotalValue.keySet()) {
	        	List<Double> nAd = campaignsTotalValue.get(c);
	        	numeratorAcum += nAd.get(0);
	        	denominatorAcum += nAd.get(1);
	        }
	        
	        if(dataValues.isEmpty()) {
	        	valueAcum += numeratorAcum / denominatorAcum;
	        } else {
	        	valueAcum += (numeratorAcum * dataValues.get(0)) / (denominatorAcum * dataValues.get(1));
	        }
	        if(Double.isInfinite(valueAcum)) {
	        	valueAcum = 0;
	        }
	        
        	dataset.addValue(valueAcum, mec.getId(), province);
        }
        
        if(_barChart != null) {
        	if(_barChart.getLegend() == null) {
	        	LegendTitle legend = new LegendTitle(_barChart.getPlot());
	            legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
	            legend.setFrame(new LineBorder());
	            legend.setBackgroundPaint(Color.white);
	            legend.setPosition(RectangleEdge.BOTTOM);
	            _barChart.addLegend(legend);
        	}
    	}

        return dataset;
	}
	
	private Map<String, List<Campaign>> campaignsSameProvince() {
		List<String> provinces = getProvincesCampaigns();

		Map<String, List<Campaign>> campaignsProvince = new LinkedHashMap<String, List<Campaign>>();
		for (String province : provinces) {
			List<Campaign> campaignsSameProvince = new LinkedList<Campaign>();
			for (Campaign c : _categoriesAndSeries.keySet()) {
				if (province.equals(c.getProvince())) {
					campaignsSameProvince.add(c);
				}
			}
			campaignsProvince.put(province, campaignsSameProvince);
		}
		return campaignsProvince;
	}
	
	private List<String> getProvincesCampaigns() {
		List<String> provinces = new LinkedList<String>();
		for (Campaign c : _categoriesAndSeries.keySet()) {
			String province = c.getProvince();
			if(!provinces.contains(province)) {
				provinces.add(province);
			}
		}
		return provinces;
	}

	
	private CategoryDataset createBarChartDatasetSeparateContexts() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        MEC mec;
        double acumValue = 0, weight;
        List<Alternative> alternativesSelected = AlternativesView.getAlternativesSelected();
        Map<Alternative, Double> childrenValue;
        Map<Criterion, Integer> criteriaPos = new LinkedHashMap<Criterion, Integer>();
        Map<Criterion, Map<Alternative, Double>> alternativesWithValues = new LinkedHashMap<Criterion, Map<Alternative, Double>>();
        Map<Campaign, Map<Criterion, Map<Alternative, Double>>> campaignsAlternativesWithValues = new LinkedHashMap<Campaign, Map<Criterion, Map<Alternative, Double>>>();
        for(Campaign campaign: _categoriesAndSeries.keySet()) {
        	alternativesWithValues = new LinkedHashMap<Criterion, Map<Alternative,Double>>();
        	mec = _categoriesAndSeries.get(campaign);
        	Map<Criterion, List<Object>> criteriaData = mec.getCriteria();
        	for(Criterion c: criteriaData.keySet()) {
        		if(campaign.getCriteria().contains(c)) {
	        		childrenValue = new LinkedHashMap<Alternative, Double>();
	        		List<Object> data = criteriaData.get(c);
	        		criteriaPos.put(c, (Integer) data.get(0));
	        		for(Alternative a: alternativesSelected) {
	        			if(a.hasChildrens()) {
	        				List<Alternative> childrens = a.getChildrens();
	        				for(Alternative children: childrens) {
	        					if(alternativesSelected.contains(children)) {
	        						acumValue = campaign.getValue(c, children);
	        						weight = (double) data.get(1);
	        	            		acumValue *= weight;
	        	            		childrenValue.put(children, acumValue);
	        					}
	        				}
	        			}
	        		}
	        		if(!childrenValue.isEmpty()) {
	        			alternativesWithValues.put(c, childrenValue);
	        		}
	        	}
	        	campaignsAlternativesWithValues.put(campaign, alternativesWithValues);
        	}
        }

        double numerator, denominator, total;
        Map<String, List<Campaign>> campaignsForProvinces = campaignsSameProvince();
        Map<String, Double> alternativesValuesAcum = new LinkedHashMap<String, Double>();
		List<String> provinces = getProvincesCampaigns();
		for(String province: provinces) {
			total = 0;
	        List<Campaign> campaignsProvinces = campaignsForProvinces.get(province);
	        for(Campaign campaign: campaignsProvinces) {
	        	Map<Criterion, Map<Alternative, Double>> criteriaWithAlternativesAndValues = campaignsAlternativesWithValues.get(campaign);
	        	for(Alternative a: alternativesSelected) {
	        		if(!a.hasChildrens()) {
		        		numerator = 1;
		        		denominator = 1;
		        		for(Criterion c: criteriaWithAlternativesAndValues.keySet()) {
		        			if(campaign.getCriteria().contains(c)) {
			        			int pos = criteriaPos.get(c);
			        			Map<Alternative, Double> alternativesValues = criteriaWithAlternativesAndValues.get(c);
			        			if(alternativesValues.get(a) != null) {
			        				if(pos == 0) {
			        					numerator *= alternativesValues.get(a);
			        				} else {
			            				denominator *= alternativesValues.get(a);
			        				}
			        			}
		        			}
		        		}

				        total = numerator / denominator;
				        if(Double.isInfinite(total)) {
				        	total = 0;
				        }
		    	        
		    	        if(alternativesValuesAcum.get(a.getId() + campaign.getProvince()) == null) {
		    	        	alternativesValuesAcum.put(a.getId() + campaign.getProvince(), total);
		    	        } else {
		    	        	total += alternativesValuesAcum.get(a.getId() + campaign.getProvince());
		    	        	alternativesValuesAcum.put(a.getId() + campaign.getProvince(), total);
		    	        }
		    	        
		    	    	dataset.addValue(total, _categoriesAndSeries.get(campaign).getId() + "_" + campaign.getProvince(), a.getId());
	        		}
	        	}
	        }
        }
		
        if(_barChart != null) {
        	if(_barChart.getLegend() == null) {
	        	LegendTitle legend = new LegendTitle(_barChart.getPlot());
	            legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
	            legend.setFrame(new LineBorder());
	            legend.setBackgroundPaint(Color.white);
	            legend.setPosition(RectangleEdge.BOTTOM);
	            _barChart.addLegend(legend);
        	}
    	}

        return dataset;
	}
	
	private XYDataset createLineChartDatasetCombineCampaigns() {
		XYSeriesCollection dataset = new XYSeriesCollection();
	
        String serieName = "";
        for(Campaign campaign: _categoriesAndSeries.keySet()) {
        	if(!campaign.isACampaignData()) {
        		serieName += campaign.getName() + "-"; 
        	}
        }
        
        Map<Integer, List<Double>> monthValues = new LinkedHashMap<Integer, List<Double>>();
        List<Double> dataValues = loadCampaignsDataDirect();
        List<Campaign> noDataCampaigns = getNoDataCampaigns();
        List<Alternative> noDataAlternativesCampaigns = getNoDataAlternativesCampaigns();
        List<Double> numeratorAndDenominator;
        
        MEC mec;
        double acumValue, value, numerator, denominator, weight;
        int pos = -1;
        for(Campaign campaign: noDataCampaigns) {
        	numerator = 1;
        	denominator = 1;
        	mec = _categoriesAndSeries.get(campaign);
        	Map<Criterion, List<Object>> criteriaData = mec.getCriteria();
        	for(Criterion c: criteriaData.keySet()) {
        		acumValue = 0;
        		value = 0;
        		List<Object> data = criteriaData.get(c);
        		for(Alternative a: noDataAlternativesCampaigns) {
        			if(a.hasChildrens() && !a.isDirect()) {
        				List<Alternative> childrens = a.getChildrens();
        				for(Alternative children: childrens) {
        					if(noDataAlternativesCampaigns.contains(children)) {
        						acumValue += campaign.getValue(c, children);
        					}
        				}
        				if(value == 0) {
    						value = acumValue;
    					} else if(acumValue < value) {
        					value = acumValue;
        				}
        			}
        		}
        		weight = (double) data.get(1);
        		value *= weight;
        		if(!c.isDirect()) {
	        		pos = (int) data.get(0);
    				if(pos == 0) {
    					numerator *= value;
    				} else {
    					denominator *= value;
    				}
        		}
        	}
        	
	        numeratorAndDenominator = new LinkedList<Double>();
	        numeratorAndDenominator.add(numerator);
	        numeratorAndDenominator.add(denominator);

        	String date = campaign.getDate();
	    	String month = date.substring(date.length() - 5, date.length() - 3);
			int category = Integer.parseInt(month);
			double numeratorAcum = 0, denominatorAcum = 0;
			if(monthValues.get(category - 1) != null) {
				List<Double> nAd = monthValues.get(category - 1); 
				List<Double> total = new LinkedList<Double>();
				numeratorAcum = nAd.get(0) + numeratorAndDenominator.get(0); 
				denominatorAcum = nAd.get(1) + numeratorAndDenominator.get(1); 
				total.add(numeratorAcum);
				total.add(denominatorAcum);
				monthValues.put(category - 1, total);
			} else {
				monthValues.put(category - 1, numeratorAndDenominator);
			}
        }
        
        XYSeries campaignSerie = new XYSeries(serieName);
        double valueAcum = 0;
    	for(Integer month: monthValues.keySet()) {
    		if(dataValues.isEmpty()) {
    			valueAcum = monthValues.get(month).get(0) / monthValues.get(month).get(1);
    		} else {
    			valueAcum = (monthValues.get(month).get(0) * dataValues.get(0)) / (monthValues.get(month).get(1) * dataValues.get(1));
    		}
	        if(Double.isInfinite(valueAcum)) {
	        	valueAcum = 0;
	        }
	        
    		campaignSerie.add((int) month, valueAcum);
    	}
    	dataset.addSeries(campaignSerie);
        
        if(_lineChart != null) {
        	if(_lineChart.getLegend() == null) {
	        	LegendTitle legend = new LegendTitle(_lineChart.getPlot());
	            legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
	            legend.setFrame(new LineBorder());
	            legend.setBackgroundPaint(Color.white);
	            legend.setPosition(RectangleEdge.BOTTOM);
	            _lineChart.addLegend(legend);
        	}
    	}
        
        return dataset;
	}
	
	
	private XYDataset createLineChartDatasetSeparateCampaigns() {
		XYSeriesCollection dataset = new XYSeriesCollection();

		List<Double> dataValues = loadCampaignsDataDirect();
        List<Campaign> noDataCampaigns = getNoDataCampaigns();
        List<Alternative> noDataAlternativesCampaigns = getNoDataAlternativesCampaigns();
		
		MEC mec;
        double acumValue, value, numerator, denominator, weight, total;
        int pos = -1;
        for(Campaign campaign: noDataCampaigns) {
        	XYSeries campaignSerie = new XYSeries(campaign.getName());
        	mec = _categoriesAndSeries.get(campaign);
        	Map<Criterion, List<Object>> criteriaData = mec.getCriteria();
        	numerator = 1;
        	denominator = 1;
        	for(Criterion c: criteriaData.keySet()) {
        		acumValue = 0;
        		value = 0;
        		List<Object> data = criteriaData.get(c);
        		for(Alternative a: noDataAlternativesCampaigns) {
        			if(a.hasChildrens() && !a.isDirect()) {
        				List<Alternative> childrens = a.getChildrens();
        				for(Alternative children: childrens) {
        					if(noDataAlternativesCampaigns.contains(children)) {
        						acumValue += campaign.getValue(c, children);
        					}
        				}
        				if(value == 0) {
    						value = acumValue;
    					} else if(acumValue < value) {
        					value = acumValue;
        				}
        			}
        			weight = (double) data.get(1);
	        		value *= weight;
	        		if(!c.isDirect()) {
		        		pos = (int) data.get(0);
	    				if(pos == 0) {
	    					numerator *= value;
	    					if(numerator == 0) {
	    						numerator = 1;
	    					}
	    				} else {
	    					denominator *= value;
	    					if(denominator == 0) {
	    						denominator = 1;
	    					}
	    				}
	        		}
        		}
        	}
        	
        	if(!dataValues.isEmpty()) {
	        	total = (numerator * dataValues.get(0)) / (denominator * dataValues.get(1));
	        	if(Double.isInfinite(total)) {
	        		total = 0;
	        	}
        	} else {
	        	total = numerator / denominator;
	        	if(total == 1) {
	        		total = 0;
	        	}
        	}
    		
    		String date = campaign.getDate();
	    	String month = date.substring(date.length() - 5, date.length() - 3);
			int category = Integer.parseInt(month);
			campaignSerie.add(category - 1, total);
        	
			dataset.addSeries(campaignSerie);
        }
        
        if(_lineChart != null) {
        	if(_lineChart.getLegend() == null) {
	        	LegendTitle legend = new LegendTitle(_lineChart.getPlot());
	            legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
	            legend.setFrame(new LineBorder());
	            legend.setBackgroundPaint(Color.white);
	            legend.setPosition(RectangleEdge.BOTTOM);
	            _lineChart.addLegend(legend);
        	}
    	}
        
        return dataset;
	}
	
	private XYDataset createLineChartDatasetSeparateProvinces() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		Map<String, List<Campaign>> campaignsForProvinces = campaignsSameProvince();
		List<String> provinces = getProvincesCampaigns();
		List<Double> dataValues = loadCampaignsDataDirect();
        List<Alternative> noDataAlternativesCampaigns = getNoDataAlternativesCampaigns();
		Map<Campaign, Double> campaignsTotalValue = new LinkedHashMap<Campaign, Double>();
		
		MEC mec;
        double acumValue, value, numerator, denominator, weight, total;
        int pos = -1;
  
        XYSeries campaignSerie = null;
        for(String province: provinces) {
        	campaignSerie = new XYSeries(province);
        	List<Campaign> campaignsProvinces = campaignsForProvinces.get(province);
        	campaignsTotalValue.clear();
        	for(Campaign campaign: campaignsProvinces) {
	        	mec = _categoriesAndSeries.get(campaign);
	        	Map<Criterion, List<Object>> criteriaData = mec.getCriteria();
	        	numerator = 1;
	        	denominator = 1;
	        	for(Criterion c: criteriaData.keySet()) {
	        		acumValue = 0;
	        		value = 0;
	        		List<Object> data = criteriaData.get(c);
	        		for(Alternative a: noDataAlternativesCampaigns) {
	        			if(a.hasChildrens() && !a.isDirect()) {
	        				List<Alternative> childrens = a.getChildrens();
	        				for(Alternative children: childrens) {
	        					if(noDataAlternativesCampaigns.contains(children)) {
	        						acumValue += campaign.getValue(c, children);
	        					}
	        				}
	        				if(value == 0) {
	    						value = acumValue;
	    					} else if(acumValue < value) {
	        					value = acumValue;
	        				}
	        			}
	        		}
	        		weight = (double) data.get(1);
	        		value *= weight;
	        		if(!c.isDirect()) {
		        		pos = (int) data.get(0);
	    				if(pos == 0) {
	    					numerator *= value;
	    					if(numerator == 0) {
	    						numerator = 1;
	    					}
	    				} else {
	    					denominator *= value;
	    					if(denominator == 0) {
	    						denominator = 1;
	    					}
	    				}
	        		}
	        	}
	        	if(!dataValues.isEmpty()) {
		        	total = (numerator * dataValues.get(0)) / (denominator * dataValues.get(1));
		        	if(Double.isInfinite(total)) {
		        		total = 0;
		        	}
	        	} else {
		        	total = numerator / denominator;
		        	if(total == 1) {
		        		total = 0;
		        	}
	        	}
	        	
	        	String date = campaign.getDate();
		    	String month = date.substring(date.length() - 5, date.length() - 3);
				int category = Integer.parseInt(month);
				campaignSerie.add(category - 1, total);
        	}
        	
        	dataset.addSeries(campaignSerie);
        }
        
        if(_lineChart != null) {
        	if(_lineChart.getLegend() == null) {
	        	LegendTitle legend = new LegendTitle(_lineChart.getPlot());
	            legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
	            legend.setFrame(new LineBorder());
	            legend.setBackgroundPaint(Color.white);
	            legend.setPosition(RectangleEdge.BOTTOM);
	            _lineChart.addLegend(legend);
        	}
    	}
        
        return dataset;
	}
	
	private XYDataset createLineChartDatasetSeparateContexts() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		MEC mec;
        double acumValue = 0, weight;
        List<Alternative> alternativesSelected = AlternativesView.getAlternativesSelected();
        Map<Alternative, Double> childrenValue;
        Map<Criterion, Integer> criteriaPos = new LinkedHashMap<Criterion, Integer>();
        Map<Criterion, Map<Alternative, Double>> alternativesWithValues = new LinkedHashMap<Criterion, Map<Alternative, Double>>();
        Map<Campaign, Map<Criterion, Map<Alternative, Double>>> campaignsAlternativesWithValues = new LinkedHashMap<Campaign, Map<Criterion, Map<Alternative, Double>>>();
        for(Campaign campaign: _categoriesAndSeries.keySet()) {
        	mec = _categoriesAndSeries.get(campaign);
        	Map<Criterion, List<Object>> criteriaData = mec.getCriteria();
        	alternativesWithValues = new LinkedHashMap<Criterion, Map<Alternative,Double>>();
        	for(Criterion c: criteriaData.keySet()) {
        		childrenValue = new LinkedHashMap<Alternative, Double>();
        		List<Object> data = criteriaData.get(c);
        		criteriaPos.put(c, (Integer) data.get(0));
        		for(Alternative a: alternativesSelected) {
        			if(a.hasChildrens()) {
        				List<Alternative> childrens = a.getChildrens();
        				for(Alternative children: childrens) {
        					if(alternativesSelected.contains(children)) {
        						acumValue = campaign.getValue(c, children);
        						weight = (double) data.get(1);
        	            		acumValue *= weight;
        	            		childrenValue.put(children, acumValue);
        					}
        				}
        			}
        		}
        		alternativesWithValues.put(c, childrenValue);
        	}
        	campaignsAlternativesWithValues.put(campaign, alternativesWithValues);
        }

        double numerator, denominator;
        XYSeries serie = null;
        List<Alternative> seriesAlreadyAdded = new LinkedList<Alternative>();
        Map<String, List<Campaign>> campaignsForProvinces = campaignsSameProvince();
		List<String> provinces = getProvincesCampaigns();
		for(String province: provinces) {
	        List<Campaign> campaignsProvinces = campaignsForProvinces.get(province);
	        for(Campaign campaign: campaignsProvinces) {
	        	Map<Criterion, Map<Alternative, Double>> criteriaWithAlternativesAndValues = campaignsAlternativesWithValues.get(campaign);
	        	for(Alternative a: alternativesSelected) {
	        		if(!a.hasChildrens()) {
	        			if(!seriesAlreadyAdded.contains(a)) {
	        				serie = new XYSeries(a.getId() + "_" + campaign.getProvince());
	            			dataset.addSeries(serie);
	            			seriesAlreadyAdded.add(a);
	        			} else {
	        				try {
	        					serie = dataset.getSeries(a.getId() + "_" + campaign.getProvince());
	        				} catch(UnknownKeyException e) {
	        					serie = new XYSeries(a.getId() + "_" + campaign.getProvince());
	                			dataset.addSeries(serie);
	                			seriesAlreadyAdded.add(a);
	        				}
	        			}
		        		numerator = 1;
		        		denominator = 1;
		        		for(Criterion c: criteriaWithAlternativesAndValues.keySet()) {
		        			int pos = criteriaPos.get(c);
		        			Map<Alternative, Double> alternativesValues = criteriaWithAlternativesAndValues.get(c);
		        			if(alternativesValues.get(a) != null) {
		        				if(pos == 0) {
		        					numerator *= alternativesValues.get(a);
		        				} else {
		            				denominator *= alternativesValues.get(a);
		        				}
		        			}
		        		}
		        		double total = 0;
		        		total = numerator / denominator;
	
		    	        String date = campaign.getDate();
		    	    	String month = date.substring(date.length() - 5, date.length() - 3);
		    			int category = Integer.parseInt(month);
		    	        
		    	        serie.add(category - 1, total);
	        		}
	        	}
	        }
		}
        
        if(_lineChart != null) {
        	if(_lineChart.getLegend() == null) {
	        	LegendTitle legend = new LegendTitle(_lineChart.getPlot());
	            legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
	            legend.setFrame(new LineBorder());
	            legend.setBackgroundPaint(Color.white);
	            legend.setPosition(RectangleEdge.BOTTOM);
	            _lineChart.addLegend(legend);
        	}
    	}
        return dataset;
	}

}
