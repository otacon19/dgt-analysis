package sinbad2.element.ui.view.mecs.jfreechart;

import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
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
import org.jfree.chart.block.LineBorder;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
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
import org.jfree.data.xy.XYDataItem;
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
import sinbad2.element.ui.view.alternatives.AlternativesView;

public class MECChart {

	private JFreeChart _barChart;
	private static JFreeChart _stackedChart;
	private static JFreeChart _lineChart;
	private ChartComposite _chartComposite;

	private List<Campaign> _campaignsSeries;
	private MEC _mecSelected;
	private String _action;
	
	private List<Alternative> _alternativesSelectedPDF;

	public MECChart() {
		_barChart = null;
		_lineChart = null;
		_stackedChart = null;
		_chartComposite = null;
		_campaignsSeries = new LinkedList<Campaign>();
		_alternativesSelectedPDF = new LinkedList<Alternative>();
		_action = "";
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

	public void refreshBarChart() {
		if (_barChart == null) {
			if(_action.isEmpty()) {
				_barChart = createBarChart(createBarChartDatasetCombineCampaigns());
			} else if(_action.equals("combine")) {
				_barChart = createBarChart(createBarChartDatasetCombineCampaigns());
			} else if(_action.equals("separate")) {
				_barChart = createBarChart(createBarChartDatasetSeparateCampaigns());
			} else if(_action.equals("separate_provinces")) {
				_barChart = createBarChart(createBarChartDatasetSeparateProvinces());
			}
		} else {
			if (_action.equals("separate")) {
				_barChart.getCategoryPlot().setDataset(
						createBarChartDatasetSeparateCampaigns());
			} else if (_action.equals("combine")) {
				_barChart.getCategoryPlot().setDataset(
						createBarChartDatasetCombineCampaigns());
			} else if (_action.equals("separate_provinces")) {
				_barChart.getCategoryPlot().setDataset(
						createBarChartDatasetSeparateProvinces());
			}
		}
	}

	public void refreshStackedChart() {
		if (_stackedChart == null) {
			_stackedChart = createStacketChart(createBarChartDatasetSeparateContexts());
		} else {
			_stackedChart.getCategoryPlot().setDataset(createBarChartDatasetSeparateContexts());
		}
	}

	public void refreshLineChart() {
		if (_lineChart == null) {
			if(_action.equals("combine")) {
				_lineChart = createLineChart(createLineChartDatasetCombineCampaigns());
			} else if(_action.equals("separate_provinces")) {
				_lineChart = createLineChart(createLineChartDatasetSeparateProvinces());
			} else if(_action.equals("contexts")) {
				_lineChart = createLineChart(createLineChartDatasetSeparateContexts());
			}
		} else {
			if (_action.equals("contexts")) {
				_lineChart.getXYPlot().setDataset(
						createLineChartDatasetSeparateContexts());
			} else if (_action.equals("combine")) {
				_lineChart.getXYPlot().setDataset(
						createLineChartDatasetCombineCampaigns());
			} else if (_action.equals("separate_provinces")) {
				_lineChart.getXYPlot().setDataset(
						createLineChartDatasetSeparateProvinces());
			}
		}
	}

	public void setMEC(List<Campaign> campaignsSeries, MEC mec,
			int typeChart, String action) {
		_campaignsSeries = campaignsSeries;
		_mecSelected = mec;
		_action = action;

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
	
	public void createChartByPDF(List<Campaign> campaignsSeries, MEC mec, int typeChart, String action, List<Alternative> alternativesSelectedPDF) {
		_campaignsSeries = campaignsSeries;
		_mecSelected = mec;
		_action = action;
		_alternativesSelectedPDF = alternativesSelectedPDF;

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

	public void initializeStackedChart(Composite container, int width,
			int height, int style) {
		refreshStackedChart();

		if (_chartComposite == null) {
			_chartComposite = new ChartComposite(container, style,
					_stackedChart, true);
		}

		_chartComposite.setChart(_stackedChart);
		_chartComposite.redraw();
		_chartComposite.setSize(width, height);
	}

	public void initializeLineChart(Composite container, int width, int height,
			int style) {
		refreshLineChart();

		if (_chartComposite == null) {
			_chartComposite = new ChartComposite(container, style, _lineChart,
					true);
		}

		_chartComposite.setChart(_lineChart);
		_chartComposite.redraw();
		_chartComposite.setSize(width, height);
	}

	public void clear() {
		if (_barChart != null) {
			_barChart.getCategoryPlot()
					.setDataset(new DefaultCategoryDataset());
		}

		if (_lineChart != null) {
			_lineChart.getXYPlot().setDataset(new XYSeriesCollection());
		}

		if (_stackedChart != null) {
			_stackedChart.getCategoryPlot().setDataset(
					new DefaultCategoryDataset());
		}
	}

	public void setSize(int width, int height) {
		_chartComposite.setSize(width, height);
		_chartComposite.redraw();
	}

	private JFreeChart createBarChart(CategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createBarChart3D(null, null, null,
				dataset, PlotOrientation.VERTICAL, false, true, false);

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
		br.setSeriesItemLabelGenerator(0, new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance())); 
		br.setSeriesItemLabelsVisible(0, true);
		br.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", NumberFormat.getNumberInstance()));
		br.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.TOP_CENTER));
		plot.setRenderer(br);
		
		return chart;
	}

	private JFreeChart createStacketChart(CategoryDataset dataset) {

		final JFreeChart chart = ChartFactory.createStackedBarChart3D("", "",
				"", dataset, PlotOrientation.HORIZONTAL, false, true, false);

		chart.setBackgroundPaint(Color.white);

		CategoryPlot plot = chart.getCategoryPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);
		plot.setDomainCrosshairVisible(true);
		plot.setRangeCrosshairVisible(true);
		
		BarRenderer br = (BarRenderer) plot.getRenderer();
		br.setMaximumBarWidth(.1);
		br.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{0} {2}", NumberFormat.getInstance(), NumberFormat.getNumberInstance()));
		br.setBaseItemLabelsVisible(true);
		br.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 0));
		br.setSeriesPositiveItemLabelPosition(1, new ItemLabelPosition(ItemLabelAnchor.CENTER, TextAnchor.CENTER, TextAnchor.CENTER, 0));
		br.setBaseItemLabelFont(new java.awt.Font("Cantarell", Font.PLAIN, 12), false);
		br.setSeriesItemLabelFont(0, new java.awt.Font("Cantarell", Font.PLAIN, 12));
		
		CategoryAxis domainAxis = plot.getDomainAxis();
		domainAxis.setLabelFont(new java.awt.Font("Cantarell", Font.PLAIN, 5));

		return chart;

	}

	private static JFreeChart createLineChart(XYDataset dataset) {

		JFreeChart chart = ChartFactory.createXYLineChart(null, null, null,
				dataset, PlotOrientation.VERTICAL, false, true, false);

		chart.setBackgroundPaint(Color.white);

		XYPlot plot = (XYPlot) chart.getPlot();
		plot.setBackgroundPaint(Color.lightGray);
		plot.setRangeGridlinePaint(Color.white);

		String[] months = new String[12];
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
		rangeAxis.setLabelFont(new java.awt.Font("Cantarell", Font.PLAIN, 5));
		plot.setDomainAxis(rangeAxis);
		
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesLinesVisible(1, true);
		renderer.setSeriesShapesVisible(0, true);
		
		
		renderer.setBaseItemLabelFont(new java.awt.Font("Cantarell", Font.PLAIN, 6), false);
		renderer.setSeriesItemLabelFont(0, new java.awt.Font("Cantarell", Font.PLAIN, 6));
		plot.setRenderer(renderer);

		return chart;
	}

	private CategoryDataset createBarChartDatasetCombineCampaigns() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		if (!_campaignsSeries.isEmpty()) {
			List<Double> dataValues = loadCampaignsDataDirectAggregation();
			List<Campaign> noDataCampaigns = getNoDataCampaigns();
			List<Alternative> alternativesSelected;
			if(_alternativesSelectedPDF.isEmpty()) {
				alternativesSelected = AlternativesView.getAlternativesSelected();
			} else {
				alternativesSelected = _alternativesSelectedPDF;
			}

			double acumValue, value, numerator, denominator, weight;
			int pos = -1;
			String category = "";
			
			List<Object> data;
			Map<Campaign, List<Double>> campaignsTotalValue = new LinkedHashMap<Campaign, List<Double>>();
			List<Double> numeratorAndDenominator;
			for (Campaign campaign : noDataCampaigns) {
				Map<Criterion, List<Object>> criteriaData = _mecSelected
						.getCriteria();
				numerator = 1;
				denominator = 1;
				for (Criterion c : criteriaData.keySet()) {
					if (campaign.getCriteria().contains(c)) {
						value = 0;
						acumValue = 0;
						data = criteriaData.get(c);
						for (Alternative a : alternativesSelected) {
							if (a.hasChildrens()) {
								List<Alternative> childrens = a.getChildrens();
								for (Alternative children : childrens) {
									if (alternativesSelected.contains(children)) {
										acumValue += campaign.getValue(c,
												children);
									}
								}
								if (value == 0) {
									value = acumValue;
								} else if (acumValue < value) {
									value = acumValue;
								}
							}
						}
						weight = (double) data.get(1);
						value *= weight;
						if (!c.isDirect()) {
							pos = (int) data.get(0);
							if (pos == 0) {
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

				if (!category.contains(campaign.getName())) {
					category += campaign.getName() + "-";
				}
			}

			double valueAcum = 0;
			if (!campaignsTotalValue.isEmpty()) {
				for (Campaign c : campaignsTotalValue.keySet()) {
					List<Double> nAd = campaignsTotalValue.get(c);
					if (dataValues.isEmpty()) {
						valueAcum += nAd.get(0) / nAd.get(1);
					} else {
						valueAcum += (nAd.get(0) * dataValues.get(0))
								/ (nAd.get(1) * dataValues.get(1));
					}
				}
			} else {
				valueAcum = dataValues.get(0) / dataValues.get(1);
			}

			if (valueAcum == 1 || Double.isInfinite(valueAcum)) {
				valueAcum = 0;
			}

			if (!category.isEmpty()) {
				category = category.substring(0, category.length() - 1);
			} else {
				List<Campaign> dataCampaigns = getDataCampaigns();
				for (Campaign c : dataCampaigns) {
					if (!category.contains(c.getName())) {
						category += c.getName() + "-";
					}
				}
				category = category.substring(0, category.length() - 1);
			}

			dataset.addValue(valueAcum, _mecSelected.getId(), category);

			if (_barChart != null) {
				if (_barChart.getLegend() == null) {
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

	private List<Double> loadCampaignsDataDirectAggregation() {
		List<Alternative> alternativesSelected = AlternativesView
				.getAlternativesSelected();
		List<Double> numeratorAndDenominator;
		double numerator = 1, denominator = 1;

		List<Campaign> dataCampaigns = new LinkedList<Campaign>();
		for (Campaign c : _campaignsSeries) {
			if (c.isACampaignData()) {
				dataCampaigns.add(c);
			}
		}

		if (!dataCampaigns.isEmpty()) {
			Map<Criterion, List<Object>> mecCriteria = _mecSelected
					.getCriteria();
			Map<Criterion, Double> dataValues = new LinkedHashMap<Criterion, Double>();
			Map<Criterion, Integer> dataValuesRepeat = new LinkedHashMap<Criterion, Integer>();
			List<Object> dataOfCriterion;
			double acumValue = 0, weight;
			for (Campaign dataCampaign : dataCampaigns) {
				List<Criterion> dataCampaignCriteria = dataCampaign
						.getCriteria();
				for (Criterion c : dataCampaignCriteria) {
					dataOfCriterion = mecCriteria.get(c);
					acumValue = 0;
					if (mecCriteria.containsKey(c)) {
						for (Alternative alternativeSelected : alternativesSelected) {
							if (!alternativeSelected.hasChildrens()) {
								acumValue += dataCampaign.getValue(c,
										alternativeSelected);
							}
						}
						weight = (double) dataOfCriterion.get(1);
						acumValue *= weight;
						if (dataValues.containsKey(c)) {
							acumValue += dataValues.get(c);
							int rep = dataValuesRepeat.get(c);
							rep++;
							dataValuesRepeat.put(c, rep);
							if (rep == dataCampaigns.size()) {
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
			for (Criterion c : dataValues.keySet()) {
				pos = (int) _mecSelected.getCriteria().get(c).get(0);
				if (pos == 0) {
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

	private CategoryDataset createBarChartDatasetSeparateCampaigns() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		double acumValue, value, numerator, denominator, weight, total;
		int pos = -1;

		List<Campaign> noDataCampaigns = getNoDataCampaigns();
		if (!noDataCampaigns.isEmpty()) {
			List<Double> dataValues = loadCampaignsDataDirectAggregation();
			List<Alternative> alternativesSelected = AlternativesView
					.getAlternativesSelected();
			for (Campaign campaign : noDataCampaigns) {
				Map<Criterion, List<Object>> criteriaData = _mecSelected
						.getCriteria();
				numerator = 1;
				denominator = 1;
				for (Criterion c : criteriaData.keySet()) {
					if (campaign.getCriteria().contains(c)) {
						acumValue = 0;
						value = 0;
						List<Object> data = criteriaData.get(c);
						for (Alternative a : alternativesSelected) {
							if (a.hasChildrens()) {
								List<Alternative> childrens = a.getChildrens();
								for (Alternative children : childrens) {
									if (alternativesSelected.contains(children)) {
										acumValue += campaign.getValue(c,
												children);
									}
								}
								if (value == 0) {
									value = acumValue;
								} else if (acumValue < value) {
									value = acumValue;
								}
							}
						}
						weight = (double) data.get(1);
						value *= weight;
						if (!c.isDirect()) {
							pos = (int) data.get(0);
							if (pos == 0) {
								numerator *= value;
								if (numerator == 0) {
									numerator = 1;
								}
							} else {
								denominator *= value;
								if (denominator == 0) {
									denominator = 1;
								}
							}
						}
					}

					if (!dataValues.isEmpty()) {
						total = (numerator * dataValues.get(0))
								/ (denominator * dataValues.get(1));
					} else {
						total = numerator / denominator;
					}

					if (Double.isInfinite(total) || total == 1) {
						total = 0;
					}

					String category = campaign.getName();
					dataset.addValue(total, _mecSelected.getId(), category);
				}
			}
		} else {
			Map<Campaign, Double> mecCampaignsDataValue = loadCampaignsDataDirectNoAggregation();
			for (Campaign c : mecCampaignsDataValue.keySet()) {
				dataset.addValue(mecCampaignsDataValue.get(c), _mecSelected, c.getName());
			}
		}

		if (_barChart != null) {
			if (_barChart.getLegend() == null) {
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

	private List<Campaign> getNoDataCampaigns() {
		List<Campaign> result = new LinkedList<Campaign>();
		for (Campaign c : _campaignsSeries) {
			if (!c.isACampaignData()) {
				result.add(c);
			}
		}
		return result;
	}

	private List<Campaign> getDataCampaigns() {
		List<Campaign> result = new LinkedList<Campaign>();
		for (Campaign c : _campaignsSeries) {
			if (c.isACampaignData()) {
				result.add(c);
			}
		}
		return result;
	}

	private Map<Campaign, Double> loadCampaignsDataDirectNoAggregation() {
		List<Alternative> alternativesSelected = AlternativesView
				.getAlternativesSelected();
		double numerator = 1, denominator = 1;
		int pos = -1;

		List<Campaign> dataCampaigns = new LinkedList<Campaign>();
		for (Campaign c : _campaignsSeries) {
			if (c.isACampaignData()) {
				dataCampaigns.add(c);
			}
		}

		Map<Campaign, Double> mecCampaignsValue = new LinkedHashMap<Campaign, Double>();
		if (!dataCampaigns.isEmpty()) {
			Map<Criterion, List<Object>> mecCriteria = _mecSelected
					.getCriteria();
			List<Object> dataOfCriterion;
			double acumValue = 0, weight;
			for (Campaign dataCampaign : dataCampaigns) {
				List<Criterion> dataCampaignCriteria = dataCampaign
						.getCriteria();
				numerator = 1;
				denominator = 1;
				for (Criterion c : dataCampaignCriteria) {
					dataOfCriterion = mecCriteria.get(c);
					acumValue = 0;
					if (mecCriteria.containsKey(c)) {
						for (Alternative alternativeSelected : alternativesSelected) {
							if (!alternativeSelected.hasChildrens()) {
								acumValue += dataCampaign.getValue(c,
										alternativeSelected);
							}
						}
						weight = (double) dataOfCriterion.get(1);
						acumValue *= weight;
						pos = (int) dataOfCriterion.get(0);
						if (pos == 0) {
							numerator *= acumValue;
						} else {
							denominator *= acumValue;
						}
					}
				}
				mecCampaignsValue.put(dataCampaign, numerator / denominator);
			}
		}
		return mecCampaignsValue;
	}

	private CategoryDataset createBarChartDatasetSeparateProvinces() {
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		Map<String, List<Campaign>> campaignsForProvinces = campaignsSameProvince(false);
		Map<Campaign, List<Double>> campaignsTotalValue = new LinkedHashMap<Campaign, List<Double>>();
		List<String> provinces = getProvincesCampaigns();
		List<Double> dataValues = loadCampaignsDataDirectAggregation();
		List<Alternative> alternativesSelected = AlternativesView
				.getAlternativesSelected();
		List<Double> numeratorAndDenominator;

		double acumValue, value, numerator = 1, denominator = 1, weight;
		int pos = -1;
		for (String province : provinces) {
			List<Campaign> campaignsProvinces = campaignsForProvinces
					.get(province);
			campaignsTotalValue.clear();
			for (Campaign campaign : campaignsProvinces) {
				Map<Criterion, List<Object>> criteriaData = _mecSelected
						.getCriteria();
				numerator = 1;
				denominator = 1;
				for (Criterion c : criteriaData.keySet()) {
					if (campaign.getCriteria().contains(c)) {
						acumValue = 0;
						value = 0;
						List<Object> data = criteriaData.get(c);
						for (Alternative a : alternativesSelected) {
							if (a.hasChildrens()) {
								List<Alternative> childrens = a.getChildrens();
								for (Alternative children : childrens) {
									if (alternativesSelected.contains(children)) {
										acumValue += campaign.getValue(c,
												children);
									}
								}
								if (value == 0) {
									value = acumValue;
								} else if (acumValue < value) {
									value = acumValue;
								}
							}
						}
						weight = (double) data.get(1);
						value *= weight;
						if (!c.isDirect()) {
							pos = (int) data.get(0);
							if (pos == 0) {
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
			if (!campaignsTotalValue.isEmpty()) {
				for (Campaign c : campaignsTotalValue.keySet()) {
					List<Double> nAd = campaignsTotalValue.get(c);
					if (dataValues.isEmpty()) {
						valueAcum += nAd.get(0) / nAd.get(1);
					} else {
						valueAcum += (nAd.get(0) * dataValues.get(0))
								/ (nAd.get(1) * dataValues.get(1));
					}
				}
			} else {
				valueAcum = dataValues.get(0) / dataValues.get(1);
			}

			if (valueAcum == 1 || Double.isInfinite(valueAcum)) {
				valueAcum = 0;
			}

			dataset.addValue(valueAcum, _mecSelected.getId(), province);
		}

		if (_barChart != null) {
			if (_barChart.getLegend() == null) {
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

	private Map<String, List<Campaign>> campaignsSameProvince(
			boolean withCampaignsData) {
		List<String> provinces = getProvincesCampaigns();

		Map<String, List<Campaign>> campaignsProvince = new LinkedHashMap<String, List<Campaign>>();
		for (String province : provinces) {
			List<Campaign> campaignsSameProvince = new LinkedList<Campaign>();
			for (Campaign c : _campaignsSeries) {
				if (!withCampaignsData) {
					if (province.equals(c.getProvince())
							&& !c.isACampaignData()) {
						campaignsSameProvince.add(c);
					}
				} else {
					if (province.equals(c.getProvince())) {
						campaignsSameProvince.add(c);
					}
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

		double acumValue = 0, weight;
		List<Alternative> alternativesSelected = AlternativesView
				.getAlternativesSelected();
		Map<Alternative, Double> childrenValue;
		Map<Criterion, Integer> criteriaPos = new LinkedHashMap<Criterion, Integer>();
		Map<Criterion, Map<Alternative, Double>> alternativesWithValues = new LinkedHashMap<Criterion, Map<Alternative, Double>>();
		Map<Campaign, Map<Criterion, Map<Alternative, Double>>> campaignsAlternativesWithValues = new LinkedHashMap<Campaign, Map<Criterion, Map<Alternative, Double>>>();
		for (Campaign campaign : _campaignsSeries) {
			alternativesWithValues = new LinkedHashMap<Criterion, Map<Alternative, Double>>();
			Map<Criterion, List<Object>> criteriaData = _mecSelected
					.getCriteria();
			for (Criterion c : criteriaData.keySet()) {
				if (campaign.getCriteria().contains(c)) {
					childrenValue = new LinkedHashMap<Alternative, Double>();
					List<Object> data = criteriaData.get(c);
					criteriaPos.put(c, (Integer) data.get(0));
					for (Alternative a : alternativesSelected) {
						if (a.hasChildrens()) {
							List<Alternative> childrens = a.getChildrens();
							for (Alternative children : childrens) {
								if (alternativesSelected.contains(children)) {
									if (campaign.getValue(c, children) != -1) {
										acumValue = campaign.getValue(c,
												children);
										weight = (double) data.get(1);
										acumValue *= weight;
										childrenValue.put(children, acumValue);
									}
								}
							}
						}
					}
					if (!childrenValue.isEmpty()) {
						alternativesWithValues.put(c, childrenValue);
					}
				}
				campaignsAlternativesWithValues.put(campaign,
						alternativesWithValues);
			}
		}

		double numerator, denominator, total;
		Map<String, List<Campaign>> campaignsForProvinces = campaignsSameProvince(true);
		Map<String, Double> alternativesValuesAcum = new LinkedHashMap<String, Double>();
		List<String> provinces = getProvincesCampaigns();
		for (String province : provinces) {
			total = 0;
			List<Campaign> campaignsProvinces = campaignsForProvinces
					.get(province);
			for (Campaign campaign : campaignsProvinces) {
				Map<Criterion, Map<Alternative, Double>> criteriaWithAlternativesAndValues = campaignsAlternativesWithValues
						.get(campaign);
				for (Alternative a : alternativesSelected) {
					if (!a.hasChildrens()) {
						numerator = 1;
						denominator = 1;
						for (Criterion c : criteriaWithAlternativesAndValues
								.keySet()) {
							if (campaign.getCriteria().contains(c)) {
								int pos = criteriaPos.get(c);
								Map<Alternative, Double> alternativesValues = criteriaWithAlternativesAndValues
										.get(c);
								if (alternativesValues.get(a) != null) {
									if (pos == 0) {
										numerator *= alternativesValues.get(a);
									} else {
										denominator *= alternativesValues
												.get(a);
									}
								}
							}
						}

						total = numerator / denominator;
						if (Double.isInfinite(total) || total == 1) {
							total = 0;
						}

						if (alternativesValuesAcum.get(a.getId()
								+ campaign.getProvince()) == null) {
							alternativesValuesAcum.put(
									a.getId() + campaign.getProvince(), total);
						} else {
							total += alternativesValuesAcum.get(a.getId()
									+ campaign.getProvince());
							alternativesValuesAcum.put(
									a.getId() + campaign.getProvince(), total);
						}

						dataset.addValue(total, a.getId(), a.getParent() + "(" + campaign.getProvince() + ")");
					}
				}
			}
		}

		if (_barChart != null) {
			if (_barChart.getLegend() == null) {
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

		String serieNameNoData = "", serieNameData = "";
		for (Campaign campaign : _campaignsSeries) {
			if (!campaign.isACampaignData()) {
				serieNameNoData += campaign.getName() + "-";
			} else {
				serieNameData += campaign.getName() + "-";
			}
		}

		Map<Integer, List<Double>> monthValues = new LinkedHashMap<Integer, List<Double>>();
		List<Double> dataValues = loadCampaignsDataDirectAggregation();
		List<Campaign> noDataCampaigns = getNoDataCampaigns();
		List<Alternative> alternativesSelected = AlternativesView
				.getAlternativesSelected();
		List<Double> numeratorAndDenominator;
		XYSeries campaignSerie;

		double acumValue, value, numerator, denominator, weight;
		int pos = -1;
		for (Campaign campaign : noDataCampaigns) {
			numerator = 1;
			denominator = 1;
			Map<Criterion, List<Object>> criteriaData = _mecSelected
					.getCriteria();
			for (Criterion c : criteriaData.keySet()) {
				acumValue = 0;
				value = 0;
				List<Object> data = criteriaData.get(c);
				for (Alternative a : alternativesSelected) {
					if (a.hasChildrens()) {
						List<Alternative> childrens = a.getChildrens();
						for (Alternative children : childrens) {
							if (alternativesSelected.contains(children)) {
								acumValue += campaign.getValue(c, children);
							}
						}
						if (value == 0) {
							value = acumValue;
						} else if (acumValue < value) {
							value = acumValue;
						}
					}
				}
				weight = (double) data.get(1);
				value *= weight;
				if (!c.isDirect()) {
					pos = (int) data.get(0);
					if (pos == 0) {
						numerator *= value;
					} else {
						denominator *= value;
					}
				}
			}

			numeratorAndDenominator = new LinkedList<Double>();
			numeratorAndDenominator.add(numerator);
			numeratorAndDenominator.add(denominator);

			double numeratorAcum = 0, denominatorAcum = 0;
			for (String month : campaign.getIntervalDate()) {
				int monthNum = Integer.parseInt(month);
				if (monthValues.get(monthNum - 1) != null) {
					List<Double> nAd = monthValues.get(monthNum - 1);
					List<Double> total = new LinkedList<Double>();
					if (numeratorAndDenominator.get(0) != 1) {
						numeratorAcum = nAd.get(0)
								+ numeratorAndDenominator.get(0);
					} else {
						numeratorAcum = nAd.get(0);
					}
					if (numeratorAndDenominator.get(1) != 1) {
						denominatorAcum = nAd.get(1)
								+ numeratorAndDenominator.get(1);
					} else {
						denominatorAcum = nAd.get(1);
					}
					total.add(numeratorAcum);
					total.add(denominatorAcum);
					monthValues.put(monthNum - 1, total);
				} else {
					monthValues.put(monthNum - 1, numeratorAndDenominator);
				}
			}
		}

		double valueAcum = 0;
		if (!monthValues.isEmpty()) {
			serieNameNoData = serieNameNoData.substring(0,
					serieNameNoData.length() - 1);
			campaignSerie = new XYSeries(serieNameNoData);
			for (Integer month : monthValues.keySet()) {
				if (dataValues.isEmpty()) {
					valueAcum = monthValues.get(month).get(0)
							/ monthValues.get(month).get(1);
				} else {
					valueAcum = (monthValues.get(month).get(0) * dataValues
							.get(0))
							/ (monthValues.get(month).get(1) * dataValues
									.get(1));
				}
				if (Double.isInfinite(valueAcum)) {
					valueAcum = 0;
				}
				campaignSerie.add((int) month, valueAcum);
			}
		} else {
			if (!serieNameData.isEmpty()) {
				serieNameData = serieNameData.substring(0,
						serieNameData.length() - 1);
			}
			campaignSerie = new XYSeries(serieNameData);

			Map<Campaign, Double> mecCampaignsDataValue = loadCampaignsDataDirectNoAggregation();
			Map<Integer, Double> monthDataValues = new LinkedHashMap<Integer, Double>();
			Map<Integer, Integer> campaignsDataForEachMonth = new LinkedHashMap<Integer, Integer>();
			for (Campaign campaignData : mecCampaignsDataValue.keySet()) {
				String date = campaignData.getInitialDate();
				String month = date.substring(date.length() - 5,
						date.length() - 3);
				int category = Integer.parseInt(month);
				if (monthDataValues.get(category - 1) != null) {
					double acumValueData = mecCampaignsDataValue
							.get(campaignData);
					acumValueData += monthDataValues.get(category - 1);
					monthDataValues.put(category - 1, acumValueData);
					int numRep = campaignsDataForEachMonth.get(category - 1) + 1;
					campaignsDataForEachMonth.put(category - 1, numRep);
				} else {
					monthDataValues.put(category - 1,
							mecCampaignsDataValue.get(campaignData));
					campaignsDataForEachMonth.put(category - 1, 1);
				}
			}
			for (int month : monthDataValues.keySet()) {
				campaignSerie.add(month, monthDataValues.get(month)
						/ campaignsDataForEachMonth.get(month));
			}
		}
		dataset.addSeries(campaignSerie);

		if (_lineChart != null) {
			if (_lineChart.getLegend() == null) {
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

		Map<String, List<Campaign>> campaignsForProvinces = campaignsSameProvince(false);
		List<String> provinces = getProvincesCampaigns();
		List<Double> dataValues = loadCampaignsDataDirectAggregation();
		List<Alternative> alternativesSelected = AlternativesView
				.getAlternativesSelected();
		Map<Campaign, Double> campaignsTotalValue = new LinkedHashMap<Campaign, Double>();

		double acumValue, value, numerator, denominator, weight, total;
		int pos = -1;

		XYSeries campaignSerie = null;
		for (String province : provinces) {
			campaignSerie = new XYSeries(province);
			List<Campaign> campaignsProvinces = campaignsForProvinces
					.get(province);
			campaignsTotalValue.clear();
			if (!campaignsProvinces.isEmpty()) {
				for (Campaign campaign : campaignsProvinces) {
					Map<Criterion, List<Object>> criteriaData = _mecSelected
							.getCriteria();
					numerator = 1;
					denominator = 1;
					for (Criterion c : criteriaData.keySet()) {
						acumValue = 0;
						value = 0;
						List<Object> data = criteriaData.get(c);
						for (Alternative a : alternativesSelected) {
							if (a.hasChildrens()) {
								List<Alternative> childrens = a.getChildrens();
								for (Alternative children : childrens) {
									if (alternativesSelected.contains(children)) {
										acumValue += campaign.getValue(c,
												children);
									}
								}
								if (value == 0) {
									value = acumValue;
								} else if (acumValue < value) {
									value = acumValue;
								}
							}
						}
						weight = (double) data.get(1);
						value *= weight;
						if (!c.isDirect()) {
							pos = (int) data.get(0);
							if (pos == 0) {
								numerator *= value;
								if (numerator == 0) {
									numerator = 1;
								}
							} else {
								denominator *= value;
								if (denominator == 0) {
									denominator = 1;
								}
							}
						}
					}
					if (!dataValues.isEmpty()) {
						total = (numerator * dataValues.get(0))
								/ (denominator * dataValues.get(1));
						if (Double.isInfinite(total)) {
							total = 0;
						}
					} else {
						total = numerator / denominator;
						if (total == 1) {
							total = 0;
						}
					}

					for (String month : campaign.getIntervalDate()) {
						int monthNum = Integer.parseInt(month);
						if (!campaignSerie.isEmpty()) {
							if (campaignSerie.indexOf(monthNum - 1) >= 0) {
								XYDataItem item = campaignSerie.getDataItem(campaignSerie.indexOf(monthNum - 1));
								total += item.getYValue();
								campaignSerie.getDataItem(campaignSerie.indexOf(monthNum - 1)).setY(total);
							} else {
								campaignSerie.addOrUpdate(monthNum - 1, total);
							}
						} else {
							campaignSerie.addOrUpdate(monthNum - 1, total);
						}
					}
				}
			} else {
				Map<Campaign, Double> mecCampaignsDataValue = loadCampaignsDataDirectNoAggregation();
				Map<Integer, Double> monthDataValues = new LinkedHashMap<Integer, Double>();
				Map<Integer, Integer> campaignsDataForEachMonth = new LinkedHashMap<Integer, Integer>();
				for (Campaign campaignData : mecCampaignsDataValue.keySet()) {
					String date = campaignData.getInitialDate();
					String month = date.substring(date.length() - 5,
							date.length() - 3);
					int category = Integer.parseInt(month);
					if (monthDataValues.get(category - 1) != null) {
						double acumValueData = mecCampaignsDataValue
								.get(campaignData);
						acumValueData += monthDataValues.get(category - 1);
						monthDataValues.put(category - 1, acumValueData);
						int numRep = campaignsDataForEachMonth
								.get(category - 1) + 1;
						campaignsDataForEachMonth.put(category - 1, numRep);
					} else {
						monthDataValues.put(category - 1,
								mecCampaignsDataValue.get(campaignData));
						campaignsDataForEachMonth.put(category - 1, 1);
					}
				}
				for (int month : monthDataValues.keySet()) {
					campaignSerie.add(month, monthDataValues.get(month)
							/ campaignsDataForEachMonth.get(month));
				}
			}
			dataset.addSeries(campaignSerie);
		}

		if (_lineChart != null) {
			if (_lineChart.getLegend() == null) {
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

		double acumValue = 0, weight;
		List<Alternative> alternativesSelected = AlternativesView
				.getAlternativesSelected();
		Map<Alternative, Double> childrenValue;
		Map<Criterion, Integer> criteriaPos = new LinkedHashMap<Criterion, Integer>();
		Map<Criterion, Map<Alternative, Double>> alternativesWithValues = new LinkedHashMap<Criterion, Map<Alternative, Double>>();
		Map<Campaign, Map<Criterion, Map<Alternative, Double>>> campaignsAlternativesWithValues = new LinkedHashMap<Campaign, Map<Criterion, Map<Alternative, Double>>>();
		for (Campaign campaign : _campaignsSeries) {
			Map<Criterion, List<Object>> criteriaData = _mecSelected
					.getCriteria();
			alternativesWithValues = new LinkedHashMap<Criterion, Map<Alternative, Double>>();
			for (Criterion c : criteriaData.keySet()) {
				childrenValue = new LinkedHashMap<Alternative, Double>();
				List<Object> data = criteriaData.get(c);
				criteriaPos.put(c, (Integer) data.get(0));
				for (Alternative a : alternativesSelected) {
					if (a.hasChildrens()) {
						List<Alternative> childrens = a.getChildrens();
						for (Alternative children : childrens) {
							if (alternativesSelected.contains(children)) {
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
			campaignsAlternativesWithValues.put(campaign,
					alternativesWithValues);
		}

		double numerator, denominator;
		XYSeries serie = null;
		List<Alternative> seriesAlreadyAdded = new LinkedList<Alternative>();
		Map<String, List<Campaign>> campaignsForProvinces = campaignsSameProvince(true);
		List<String> provinces = getProvincesCampaigns();
		for (String province : provinces) {
			List<Campaign> campaignsProvinces = campaignsForProvinces
					.get(province);
			for (Campaign campaign : campaignsProvinces) {
				Map<Criterion, Map<Alternative, Double>> criteriaWithAlternativesAndValues = campaignsAlternativesWithValues
						.get(campaign);
				for (Alternative a : alternativesSelected) {
					if (!a.hasChildrens()) {
						if (!seriesAlreadyAdded.contains(a)) {
							serie = new XYSeries(a.getId() + "_"
									+ campaign.getProvince());
							dataset.addSeries(serie);
							seriesAlreadyAdded.add(a);
						} else {
							try {
								serie = dataset.getSeries(a.getId() + "_"
										+ campaign.getProvince());
							} catch (UnknownKeyException e) {
								serie = new XYSeries(a.getId() + "_"
										+ campaign.getProvince());
								dataset.addSeries(serie);
								seriesAlreadyAdded.add(a);
							}
						}
						numerator = 1;
						denominator = 1;
						for (Criterion c : criteriaWithAlternativesAndValues
								.keySet()) {
							int pos = criteriaPos.get(c);
							Map<Alternative, Double> alternativesValues = criteriaWithAlternativesAndValues
									.get(c);
							if (alternativesValues.get(a) != null) {
								if (pos == 0) {
									numerator *= alternativesValues.get(a);
								} else {
									denominator *= alternativesValues.get(a);
								}
							}
						}
						double total = 0;
						total = numerator / denominator;

						for (String month : campaign.getIntervalDate()) {
							int monthNum = Integer.parseInt(month);
							if (!serie.isEmpty()) {
								if (serie.indexOf(monthNum - 1) >= 0) {
									XYDataItem item = serie.getDataItem(serie.indexOf(monthNum - 1));
									total += item.getYValue();
									serie.getDataItem(serie.indexOf(monthNum - 1)).setY(total);
								} else {
									serie.addOrUpdate(monthNum - 1, total);
								}
							} else {
								serie.addOrUpdate(monthNum - 1, total);
							}
						}
					}
				}
			}
		}

		if (_lineChart != null) {
			if (_lineChart.getLegend() == null) {
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
