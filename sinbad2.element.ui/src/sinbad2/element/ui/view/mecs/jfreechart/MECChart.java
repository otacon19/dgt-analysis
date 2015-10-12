package sinbad2.element.ui.view.mecs.jfreechart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
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
import org.jfree.ui.TextAnchor;

import sinbad2.element.alternative.Alternative;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.mec.MEC;
import sinbad2.element.ui.nls.Messages;
import sinbad2.element.ui.view.alternatives.AlternativesView;

public class MECChart {

	private JFreeChart _barChart;
	private static JFreeChart _stackedChart;
	private static JFreeChart _lineChart;
	private ChartComposite _chartComposite;

	private List<Campaign> _campaignsSeries;
	private List<Alternative> _alternativesSelected;
	private MEC _mecSelected;
	private String _action;
	
	private static boolean _fontPDF;

	public MECChart() {
		_barChart = null;
		_lineChart = null;
		_stackedChart = null;
		_chartComposite = null;
		_campaignsSeries = new LinkedList<Campaign>();
		_alternativesSelected = new LinkedList<Alternative>();
		_action = ""; //$NON-NLS-1$
		_fontPDF = false;
	}
	
	public JFreeChart getBarChart() {
		return _barChart;
	}
	
	public JFreeChart getLineChart() {
		return _lineChart;
	}
	
	public JFreeChart getStackedChart() {
		return _stackedChart;
	}
	
	public void setFontPDF(boolean state) {
		_fontPDF = state;
	}

	public void refreshBarChart() {	
		if (_barChart == null) {
			if(_action.isEmpty()) {
				_barChart = createBarChart(createBarChartDatasetCombineCampaigns());
			} else if(_action.equals("combine")) { //$NON-NLS-1$
				_barChart = createBarChart(createBarChartDatasetCombineCampaigns());
			} else if(_action.equals("separate")) { //$NON-NLS-1$
				_barChart = createBarChart(createBarChartDatasetSeparateCampaigns());
			} else if(_action.equals("separate_provinces")) { //$NON-NLS-1$
				_barChart = createBarChart(createBarChartDatasetSeparateProvinces());
			}
		} else {
			if (_action.equals("separate")) { //$NON-NLS-1$
				_barChart.getCategoryPlot().setDataset(createBarChartDatasetSeparateCampaigns());
			} else if (_action.equals("combine")) { //$NON-NLS-1$
				_barChart.getCategoryPlot().setDataset(createBarChartDatasetCombineCampaigns());
			} else if (_action.equals("separate_provinces")) { //$NON-NLS-1$
				_barChart.getCategoryPlot().setDataset(createBarChartDatasetSeparateProvinces());
			}
		}
		activateLegend(_barChart);
	}

	private void activateLegend(JFreeChart chart) {
		if (chart != null) {
			if (chart.getLegend() == null) {
				LegendTitle legend = new LegendTitle(chart.getPlot());
				legend.setFrame(BlockBorder.NONE);
				legend.setMargin(new RectangleInsets(1.0, 1.0, 1.0, 1.0));
				legend.setBackgroundPaint(Color.white);
				legend.setPosition(RectangleEdge.BOTTOM);
				chart.addLegend(legend);
			}
		}
	}

	public void refreshStackedChart() {
		if (_stackedChart == null) {
			_stackedChart = createStacketChart(createBarChartDatasetSeparateContexts());
		} else {
			_stackedChart.getCategoryPlot().setDataset(createBarChartDatasetSeparateContexts());
		}
		
		activateLegend(_stackedChart);
	}

	public void refreshLineChart() {
		if (_lineChart == null) {
			if(_action.equals("combine")) { //$NON-NLS-1$
				_lineChart = createLineChart(createLineChartDatasetCombineCampaigns());
			} else if(_action.equals("separate_provinces")) { //$NON-NLS-1$
				_lineChart = createLineChart(createLineChartDatasetSeparateProvinces());
			} else if(_action.equals("contexts")) { //$NON-NLS-1$
				_lineChart = createLineChart(createLineChartDatasetSeparateContexts());
			}
		} else {
			if (_action.equals("combine")) { //$NON-NLS-1$
				_lineChart.getXYPlot().setDataset(createLineChartDatasetCombineCampaigns());
			} else if (_action.equals("separate_provinces")) { //$NON-NLS-1$
				_lineChart.getXYPlot().setDataset(createLineChartDatasetSeparateProvinces());
			} else if (_action.equals("contexts")) { //$NON-NLS-1$
				_lineChart.getXYPlot().setDataset(createLineChartDatasetSeparateContexts());
			}
		}
		
		activateLegend(_lineChart);
	}

	public void setMEC(List<Campaign> campaignsSeries, MEC mec, int typeChart, String action) {
		_alternativesSelected = AlternativesView.getAlternativesSelected();
		
		_campaignsSeries = campaignsSeries;
		_mecSelected = mec;
		_action = action;
		_fontPDF = false;

		if (typeChart == 0) {
			_chartComposite.setChart(_barChart);
			refreshBarChart();
		} else if (typeChart == 1) {
			_chartComposite.setChart(_lineChart);
			refreshLineChart();
		} else {
			_chartComposite.setChart(_stackedChart);
			refreshStackedChart();
		}
	}
	
	public void createChartByPDF(List<Campaign> campaignsSeries, MEC mec, int typeChart, String action) {
		_alternativesSelected = AlternativesView.getAlternativesSelected();
		
		_campaignsSeries = campaignsSeries;
		_mecSelected = mec;
		_action = action;
		_fontPDF = true;

		if (typeChart == 0) {
			refreshBarChart();
		} else if (typeChart == 1) {
			refreshLineChart();
		} else {
			refreshStackedChart();
		}
	}

	public void initializeBarChart(Composite container, int width, int height,
			int style) {
		refreshBarChart();

		if (_chartComposite == null) {
			_chartComposite = new ChartComposite(container, style, _barChart,
					true);
		}

		_chartComposite.setChart(_barChart);
		_chartComposite.redraw();
		_chartComposite.setSize(width, height);
	}

	public void initializeStackedChart(Composite container, int width, int height, int style) {
		refreshStackedChart();

		if (_chartComposite == null) {
			_chartComposite = new ChartComposite(container, style, _stackedChart, true);
		}

		_chartComposite.setChart(_stackedChart);
		_chartComposite.redraw();
		_chartComposite.setSize(width, height);
	}

	public void initializeLineChart(Composite container, int width, int height, int style) {
		refreshLineChart();

		if (_chartComposite == null) {
			_chartComposite = new ChartComposite(container, style, _lineChart, true);
		}

		_chartComposite.setChart(_lineChart);
		_chartComposite.redraw();
		_chartComposite.setSize(width, height);
	}

	public void clear() {
		if (_barChart != null) {
			_barChart.getCategoryPlot().setDataset(new DefaultCategoryDataset());
		}

		if (_lineChart != null) {
			_lineChart.getXYPlot().setDataset(new XYSeriesCollection());
		}

		if (_stackedChart != null) {
			_stackedChart.getCategoryPlot().setDataset(new DefaultCategoryDataset());
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
		// rangeAxis.setTickUnit(new NumberTickUnit(0.1));
		rangeAxis.setAutoRange(true);

		br.setDrawBarOutline(false);
		br.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));  //$NON-NLS-1$
		br.setSeriesItemLabelsVisible(0, true);
		br.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance())); //$NON-NLS-1$
		br.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.TOP_CENTER));
		plot.setRenderer(br);
		
		return chart;
	}

	private JFreeChart createStacketChart(CategoryDataset dataset) {

		final JFreeChart chart = ChartFactory.createStackedBarChart3D("", "", //$NON-NLS-1$ //$NON-NLS-2$
				"", dataset, PlotOrientation.HORIZONTAL, false, true, false); //$NON-NLS-1$

		chart.setBackgroundPaint(Color.white);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		
		BarRenderer br = (BarRenderer) plot.getRenderer();
		br.setMaximumBarWidth(.1);
		br.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{0} {2}", NumberFormat.getInstance(), NumberFormat.getNumberInstance())); //$NON-NLS-1$
		br.setBaseItemLabelsVisible(true);
		br.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 0));
		br.setSeriesPositiveItemLabelPosition(1, new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 0));
		br.setBaseItemLabelFont(new java.awt.Font("Cantarell", Font.PLAIN, 12), false); //$NON-NLS-1$
		br.setSeriesItemLabelFont(0, new java.awt.Font("Cantarell", Font.PLAIN, 12)); //$NON-NLS-1$
		
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setLabelFont(new java.awt.Font("Cantarell", Font.PLAIN, 4)); //$NON-NLS-1$

		return chart;

	}

	@SuppressWarnings("serial")
	private static JFreeChart createLineChart(XYDataset dataset) {

		JFreeChart chart = ChartFactory.createXYLineChart(null, null, null, dataset, PlotOrientation.VERTICAL, false, true, false);

		chart.setBackgroundPaint(Color.white);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.white);

		String[] months = new String[12];
		months[0] = Messages.MECChart_January;
		months[1] = Messages.MECChart_February;
		months[2] = Messages.MECChart_March;
		months[3] = Messages.MECChart_April;
		months[4] = Messages.MECChart_May;
		months[5] = Messages.MECChart_June;
		months[6] = Messages.MECChart_July;
		months[7] = Messages.MECChart_August;
		months[8] = Messages.MECChart_September;
		months[9] = Messages.MECChart_October;
		months[10] = Messages.MECChart_November;
		months[11] = Messages.MECChart_December;
	
		SymbolAxis rangeAxis = new SymbolAxis("", months) { //$NON-NLS-1$
			@Override
			public Font getTickLabelFont() {
				if(!_fontPDF) {
					return new java.awt.Font("Cantarell", Font.PLAIN, 10); //$NON-NLS-1$
				} else {
					return new java.awt.Font("Cantarell", Font.PLAIN, 6); //$NON-NLS-1$
				}
			}
		};
		rangeAxis.setTickLabelFont(new java.awt.Font("Cantarell", Font.PLAIN, 10)); //$NON-NLS-1$
	
		plot.setDomainAxis(rangeAxis);

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer() {
			@Override
			public Stroke getItemStroke(int row, int column) {
				return new BasicStroke(4);
			}
		};
		plot.setRenderer(renderer);
		renderer.setBaseStroke(new BasicStroke(4));
		renderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(true);

		return chart;
	}

	private CategoryDataset createBarChartDatasetCombineCampaigns() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		if (!_campaignsSeries.isEmpty()) {
			if(!_alternativesSelected.isEmpty()) {
				double campaignValueMEC = 0, campaignValueMECDirect = 0;
				String categoryName = ""; //$NON-NLS-1$
				for (Campaign campaign : _campaignsSeries) {
					for(Alternative parent: _alternativesSelected) {
						if(parent.hasChildrens()) {
							if(!campaign.isACampaignData()) {
								campaignValueMEC += getValueMECAlternative(_mecSelected, campaign, parent);
							} else {
								campaignValueMECDirect += getValueMECDataAlternative(_mecSelected, campaign, parent);
							}
						}
					}
					if (!categoryName.contains(campaign.getName())) {
						categoryName += campaign.getName() + "-"; //$NON-NLS-1$
					}	
				}
				
				campaignValueMEC = (campaignValueMEC == 0) ? 1 : campaignValueMEC;
				campaignValueMECDirect = (campaignValueMECDirect == 0) ? 1 : campaignValueMECDirect;
				
				int numCampaignsData = getNumCampaignsData();
				numCampaignsData = (numCampaignsData == 0) ? 1 : numCampaignsData;
				campaignValueMECDirect /= numCampaignsData;
				
				campaignValueMEC *= campaignValueMECDirect;
				
				categoryName = categoryName.substring(0, categoryName.length() - 1);
				dataset.addValue(campaignValueMEC, _mecSelected.getId(), categoryName);
			}
		}
		
		return dataset;
	}
	
	private double getValueMECAlternative(MEC mec, Campaign campaign, Alternative alternative) {
		double numerator = 1, denominator = 1, acumValueAlternatives = 0;
		List<Object> positionAndWeigth;
		
		for(Criterion criterion: mec.getCriteria().keySet()) {
			acumValueAlternatives = 0;
			if(!criterion.isDirect()) {
				positionAndWeigth = mec.getCriteria().get(criterion);
				if(alternative.hasChildrens()) {
					for(Alternative children: alternative.getChildrens()) {
						if(_alternativesSelected.contains(children)) {
							acumValueAlternatives += campaign.getValue(criterion, children);
						}
					}
				} else {
					acumValueAlternatives = campaign.getValue(criterion, alternative);
				}
				if((Integer) positionAndWeigth.get(0) == 0) {
					numerator *= acumValueAlternatives * (Double) positionAndWeigth.get(1);
				} else {
					denominator *= acumValueAlternatives * (Double) positionAndWeigth.get(1);
				}
			}
		}
		
		denominator = (denominator == 0) ? 1 : denominator;
		
		return numerator / denominator;
	}
	
	private double getValueMECDataAlternative(MEC mec, Campaign campaign, Alternative alternative) {
		double numerator = 1, denominator = 1, acumValueAlternatives = 0;
		List<Object> positionAndWeigth;
		
		for(Criterion criterion: mec.getCriteria().keySet()) {
			acumValueAlternatives = 0;
			if(criterion.isDirect()) {
				positionAndWeigth = mec.getCriteria().get(criterion);
				if(alternative.hasChildrens()) {
					for(Alternative children: alternative.getChildrens()) {
						if(_alternativesSelected.contains(children)) {
							acumValueAlternatives += campaign.getValue(criterion, children);
						}
					}
				} else {
					acumValueAlternatives = campaign.getValue(criterion, alternative);
				}
				if((Integer) positionAndWeigth.get(0) == 0) {
					numerator *= acumValueAlternatives * (Double) positionAndWeigth.get(1);
				} else {
					denominator *= acumValueAlternatives * (Double) positionAndWeigth.get(1);
				}
			}
		}
		
		denominator = (denominator == 0) ? 1 : denominator;
		
		return numerator / denominator;
	}
	
	private int getNumCampaignsData() {
		int numCampaignsData = 0;
		for(Campaign campaign: _campaignsSeries) {
			if(campaign.isACampaignData()) {
				numCampaignsData++;
			}
		}
		return numCampaignsData;
	}

	private CategoryDataset createBarChartDatasetSeparateCampaigns() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		double campaignValueMEC = 0, campaignValueMECDirect = 0;

		if(!_alternativesSelected.isEmpty()) {
			for (Campaign campaign : _campaignsSeries) {
				campaignValueMEC = 0;
				campaignValueMECDirect = 0;
				for(Alternative parent: _alternativesSelected) {
					if(parent.hasChildrens()) {
						if(!campaign.isACampaignData()) {
							campaignValueMEC += getValueMECAlternative(_mecSelected, campaign, parent);
						} else {
							campaignValueMECDirect += getValueMECDataAlternative(_mecSelected, campaign, parent);
						}
					}
				}
				
				campaignValueMEC = (campaignValueMEC == 0) ? 1 : campaignValueMEC;
				campaignValueMECDirect = (campaignValueMECDirect == 0) ? 1 : campaignValueMECDirect;
				
				int numCampaignsData = getNumCampaignsData();
				if(numCampaignsData == 0) {
					numCampaignsData = 1;
				}
				
				if(campaignValueMEC != 1) {
					campaignValueMECDirect /= numCampaignsData;
				}
				campaignValueMEC *= campaignValueMECDirect;
				
				dataset.addValue(campaignValueMEC, _mecSelected.getId(), campaign.getName());
			}
		}

		return dataset;
	}
	
	private CategoryDataset createBarChartDatasetSeparateProvinces() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		Map<String, List<Campaign>> campaignsForProvinces = campaignsSameProvince();
		List<String> provinces = getProvincesCampaigns();

		if(!_alternativesSelected.isEmpty()) {
			double campaignValueMEC = 0, campaignValueMECDirect = 0, campaignAcumValueProvince = 0;
			for (String province : provinces) {
				List<Campaign> campaignsProvinces = campaignsForProvinces.get(province);
				campaignAcumValueProvince = 0;
				for (Campaign campaign : campaignsProvinces) {
					campaignValueMEC = 0;
					campaignValueMECDirect = 0;
					for(Alternative parent: _alternativesSelected) {
						if(parent.hasChildrens()) {
							if(!campaign.isACampaignData()) {
								campaignValueMEC += getValueMECAlternative(_mecSelected, campaign, parent);
							} else {
								campaignValueMECDirect += getValueMECDataAlternative(_mecSelected, campaign, parent);
							}
						}
					}
					
					campaignValueMEC = (campaignValueMEC == 0) ? 1 : campaignValueMEC;
					campaignValueMECDirect = (campaignValueMECDirect == 0) ? 1 : campaignValueMECDirect;
					
					int numCampaignsData = getNumCampaignsData();
					if(numCampaignsData == 0) {
						numCampaignsData = 1;
					}
					
					campaignValueMECDirect /= numCampaignsData;
					campaignValueMEC *= campaignValueMECDirect;
					if(campaignValueMEC != 1) {
						campaignAcumValueProvince += campaignValueMEC;
					}
				}
				dataset.addValue(campaignAcumValueProvince, _mecSelected.getId(), province);
			}
		}
		
		return dataset;
	}

	private Map<String, List<Campaign>> campaignsSameProvince() {
		List<String> provinces = getProvincesCampaigns();

		Map<String, List<Campaign>> campaignsProvince = new LinkedHashMap<String, List<Campaign>>();
		for (String province : provinces) {
			List<Campaign> campaignsSameProvince = new LinkedList<Campaign>();
			for (Campaign c : _campaignsSeries) {
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
		for (Campaign c : _campaignsSeries) {
			String province = c.getProvince();
			if (!provinces.contains(province)) {
				provinces.add(province);
			}
		}
		return provinces;
	}

	private CategoryDataset createBarChartDatasetSeparateContexts() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		
		if(!_alternativesSelected.isEmpty()) {
			Map<String, Double> alternativesValues = new HashMap<String, Double>();
			Map<String, List<Campaign>> campaignsForProvinces = campaignsSameProvince();
			List<String> provinces = getProvincesCampaigns();
			double campaignValueMEC = 0, campaignValueMECDirect = 0;
			for (String province : provinces) {
				List<Campaign> campaignsProvinces = campaignsForProvinces.get(province);
				for (Campaign campaign : campaignsProvinces) {
					for(Alternative children: _alternativesSelected) {
						if(!children.hasChildrens()) {
							campaignValueMEC = 0;
							campaignValueMECDirect = 0;
							
							if(!campaign.isACampaignData()) {
								campaignValueMEC = getValueMECAlternative(_mecSelected, campaign, children);
							} else {
								campaignValueMECDirect = getValueMECDataAlternative(_mecSelected, campaign, children);
							}
							
							campaignValueMEC = (campaignValueMEC == 0) ? 1 : campaignValueMEC;
							campaignValueMECDirect = (campaignValueMECDirect == 0) ? 1 : campaignValueMECDirect;
							
							int numCampaignsData = getNumCampaignsData();
							if(numCampaignsData == 0) {
								numCampaignsData = 1;
							}

							if(campaignValueMEC != 1) {
								campaignValueMECDirect /= numCampaignsData;
							}
							campaignValueMEC *= campaignValueMECDirect;
							
							if(alternativesValues.containsKey(children.getId() + province)) {
								campaignValueMEC += alternativesValues.get(children.getId() + province);
							}

							alternativesValues.put(children.getId() + province, campaignValueMEC);
							
							dataset.addValue(campaignValueMEC, children.getId(), children.getParent().getId() + "(" + campaign.getProvince() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
						}
					}
				}
			}
		}
		return dataset;
	}
	
	private XYDataset createLineChartDatasetCombineCampaigns() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		Map<Integer, Double> monthValues = new LinkedHashMap<Integer, Double>();
		XYSeries campaignSerie = null;

		if(!_alternativesSelected.isEmpty()) {
			double campaignValueMEC = 0, campaignValueMECDirect = 0;
			for (Campaign campaign : _campaignsSeries) {
				campaignValueMEC = 0;
				campaignValueMECDirect = 0;
				for(Alternative parent: _alternativesSelected) {
					if(parent.hasChildrens()) {
						if(!campaign.isACampaignData()) {
							campaignValueMEC += getValueMECAlternative(_mecSelected, campaign, parent);
						} else {
							campaignValueMECDirect += getValueMECDataAlternative(_mecSelected, campaign, parent);
						}
					}
				}
				
				campaignValueMEC = (campaignValueMEC == 0) ? 1 : campaignValueMEC;
				campaignValueMECDirect = (campaignValueMECDirect == 0) ? 1 : campaignValueMECDirect;
				
				campaignValueMEC *= campaignValueMECDirect;
				
				double monthValue = 0, total = 0;
				for (String month : campaign.getIntervalDate()) {
					int monthNum = Integer.parseInt(month);
					if (monthValues.containsKey(monthNum - 1)) {
						monthValue = monthValues.get(monthNum - 1);
						if(campaign.isACampaignData()) {
							total = (campaignValueMEC + monthValue) / getNumCampaignsDataSameMonth(campaign, Integer.toString(monthNum));
						} else {
							total = campaignValueMEC + monthValue;
						}
						monthValues.put(monthNum - 1, total);
					} else {
						monthValues.put(monthNum - 1, campaignValueMEC);
					}
				}
			}
	
			campaignSerie = new XYSeries(_mecSelected.getId());
			for (Integer month : monthValues.keySet()) {
				campaignSerie.add((int) month, monthValues.get(month));
			}
			
			dataset.addSeries(campaignSerie);
		}
		
		return dataset;
	}

	private int getNumCampaignsDataSameMonth(Campaign campaign, String month) {
		int campaignsSameMonth = 1;
		for(Campaign c: _campaignsSeries) {
			if(c.isACampaignData() && !c.equals(campaign)) {
				List<String> interval = c.getIntervalDate();
				if(interval.contains(month)) {
					campaignsSameMonth++;
				}
			}
		}
		
		return campaignsSameMonth;
	}

	private XYDataset createLineChartDatasetSeparateProvinces() {
		XYSeriesCollection dataset = new XYSeriesCollection();

		Map<String, List<Campaign>> campaignsForProvinces = campaignsSameProvince();
		List<String> provinces = getProvincesCampaigns();

		if(!_alternativesSelected.isEmpty()) {
			Map<Integer, Double> monthValues = new LinkedHashMap<Integer, Double>();
			double campaignValueMEC = 0, campaignValueMECDirect = 0;
			XYSeries campaignSerie = null;
			for (String province : provinces) {
				campaignSerie = new XYSeries(_mecSelected.getId() + "(" + province + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				List<Campaign> campaignsProvinces = campaignsForProvinces.get(province);
				if (!campaignsProvinces.isEmpty()) {
					for (Campaign campaign : campaignsProvinces) {
						campaignValueMEC = 0;
						campaignValueMECDirect = 0;
						for(Alternative parent: _alternativesSelected) {
							if(parent.hasChildrens()) {
								if(!campaign.isACampaignData()) {
									campaignValueMEC += getValueMECAlternative(_mecSelected, campaign, parent);
								} else {
									campaignValueMECDirect += getValueMECDataAlternative(_mecSelected, campaign, parent);
								}
							}
						}
						
						campaignValueMEC = (campaignValueMEC == 0) ? 1 : campaignValueMEC;
						campaignValueMECDirect = (campaignValueMECDirect == 0) ? 1 : campaignValueMECDirect;
						campaignValueMEC *= campaignValueMECDirect;
						
						double monthValue = 0, total = 0;
						for (String month : campaign.getIntervalDate()) {
							int monthNum = Integer.parseInt(month);
							if (monthValues.containsKey(monthNum - 1)) {
								monthValue = monthValues.get(monthNum - 1);
								if(campaign.isACampaignData()) {
									total = (campaignValueMEC + monthValue) / getNumCampaignsDataSameMonth(campaign, Integer.toString(monthNum));
								} else {
									total = campaignValueMEC + monthValue;
								}
								monthValues.put(monthNum - 1, total);
							} else {
								monthValues.put(monthNum - 1, campaignValueMEC);
							}
						}
					}
				} 

				for (Integer month : monthValues.keySet()) {
					campaignSerie.add((int) month, monthValues.get(month));
				}
				dataset.addSeries(campaignSerie);
				monthValues.clear();
			}
		}

		return dataset;
	}

	private XYDataset createLineChartDatasetSeparateContexts() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		
		if(!_alternativesSelected.isEmpty()) {
			List<Alternative> seriesAlreadyAdded = new LinkedList<Alternative>();
			Map<String, List<Campaign>> campaignsForProvinces = campaignsSameProvince();
			List<String> provinces = getProvincesCampaigns();
			double campaignValueMEC = 0, campaignValueMECDirect = 0;
			XYSeries campaignSerie = null;
			for (String province : provinces) {
				List<Campaign> campaignsProvinces = campaignsForProvinces.get(province);
				campaignValueMEC = 0;
				campaignValueMECDirect = 0;
				for (Campaign campaign : campaignsProvinces) {
					for(Alternative children: _alternativesSelected) {
						if(!children.hasChildrens()) {
							campaignValueMEC = 0;
							campaignValueMECDirect = 0;
							
							if (!seriesAlreadyAdded.contains(children)) {
								campaignSerie = new XYSeries(children.getId() + "_" + campaign.getProvince()); //$NON-NLS-1$
								dataset.addSeries(campaignSerie);
								seriesAlreadyAdded.add(children);
							} else {
								try {
									campaignSerie = dataset.getSeries(children.getId() + "_" + campaign.getProvince()); //$NON-NLS-1$
								} catch (UnknownKeyException e) {
									campaignSerie = new XYSeries(children.getId() + "_" + campaign.getProvince()); //$NON-NLS-1$
									dataset.addSeries(campaignSerie);
									seriesAlreadyAdded.add(children);
								}
							}
							
							if(!campaign.isACampaignData()) {
								campaignValueMEC = getValueMECAlternative(_mecSelected, campaign, children);
							} else {
								campaignValueMECDirect = getValueMECDataAlternative(_mecSelected, campaign, children);
							}
							
							campaignValueMEC = (campaignValueMEC == 0) ? 1 : campaignValueMEC;
							campaignValueMECDirect = (campaignValueMECDirect == 0) ? 1 : campaignValueMECDirect;
							campaignValueMEC *= campaignValueMECDirect;
		
							double total = campaignValueMEC;
							for (String month : campaign.getIntervalDate()) {
								int monthNum = Integer.parseInt(month);
								if (!campaignSerie.isEmpty()) {
									if (campaignSerie.indexOf(monthNum - 1) >= 0) {
										if(campaign.isACampaignData()) {
											campaignValueMEC /= getNumCampaignsDataSameMonth(campaign, month);
										}
										total += campaignSerie.getDataItem(campaignSerie.indexOf(monthNum - 1)).getYValue();
										campaignSerie.remove(campaignSerie.indexOf(monthNum - 1));
									}
								}
								campaignSerie.add(monthNum - 1, total);
							}
						}
					}
				}
			}
		}
		return dataset;
	}
}
