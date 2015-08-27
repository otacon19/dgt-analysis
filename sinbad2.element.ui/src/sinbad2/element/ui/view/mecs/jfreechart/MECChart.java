package sinbad2.element.ui.view.mecs.jfreechart;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.experimental.chart.swt.ChartComposite;

import sinbad2.element.mec.MEC;
import sinbad2.element.ui.nls.Messages;


public class MECChart {
	
	public static final Color[] colors = {Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.PINK};
	
	private JFreeChart _chart;
	private ChartComposite _chartComposite;
	
	private Map<String, List<MEC>> _categoriesAndSeries;

	public MECChart() {
		_chart = null;
		_chartComposite = null;
	
		_categoriesAndSeries = new HashMap<String, List<MEC>>();
	}
	
	public void refreshChart() {
		if(_chart == null) {
			_chart = createChart(createDataset());
		} else {
			_chart.getCategoryPlot().setDataset(createDataset());
			paintSeries();
		}
	}
	
	private void paintSeries() {
        CategoryPlot plot = _chart.getCategoryPlot();

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        
        int numSeries = countNumSeries();
        if(numSeries != plot.getDataset().getRowCount()) {
        	renderer.setSeriesPaint(numSeries, null);
        }
	}

	private int countNumSeries() {
		int result = 0;
		
		List<MEC> mecs;
        for(String campaign: _categoriesAndSeries.keySet()) {
        	mecs = _categoriesAndSeries.get(campaign);
        	for(int i = 0; i < mecs.size(); ++i) {
        		result++;
        	}
        	
        }
		
		return result;
	}

	public void setMEC(Map<String, List<MEC>> categoriesAndSeries) {
		_categoriesAndSeries = categoriesAndSeries;
		
		refreshChart();
	}
	
	public void initialize(Composite container, int width, int height, int style) {
		refreshChart();
		
		if(_chartComposite == null) {
			_chartComposite = new ChartComposite(container, style, _chart, true);
		}
		_chartComposite.setSize(width, height);
		
	}
	
	public void clear() {
		_chart.getCategoryPlot().setDataset(new DefaultCategoryDataset());
	}
	
	private JFreeChart createChart(CategoryDataset dataset) {
		JFreeChart chart = ChartFactory.createBarChart3D(null, null, Messages.MECChart_Risk_index, dataset, PlotOrientation.VERTICAL, true, true, false);

        chart.setBackgroundPaint(Color.white);
        
        CategoryPlot categoryPlot = chart.getCategoryPlot();
		BarRenderer br = (BarRenderer) categoryPlot.getRenderer();
		br.setMaximumBarWidth(.05);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setRange(0, 1);
        rangeAxis.setAutoRange(false);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}",new DecimalFormat( "#.##"))); //$NON-NLS-1$ //$NON-NLS-2$
        renderer.setBaseItemLabelsVisible(true);
        
        return chart;
	}
	
	private CategoryDataset createDataset() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
       
        List<MEC> mecs;
        for(String campaign: _categoriesAndSeries.keySet()) {
        	mecs = _categoriesAndSeries.get(campaign);
        	for(int i = 0; i < mecs.size(); ++i) {
        		dataset.addValue(mecs.get(i).getValue(), mecs.get(i).getId(), campaign);
        	}
        	
        }

        return dataset;
	}
}
