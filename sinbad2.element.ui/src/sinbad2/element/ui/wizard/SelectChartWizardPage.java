package sinbad2.element.ui.wizard;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import sinbad2.element.ui.Images;

public class SelectChartWizardPage extends WizardPage {

	private Button _tableSelectedButton;
	private Button _barChartSelectedButton;
	private Button _lineChartSelectedButton;
	
	private static List<String> _chartsSelected;
	private static int _tableSelected;
	
	protected SelectChartWizardPage() {
		super("Select chart");
		setDescription("Select the chart you want");
		
		_chartsSelected = new LinkedList<String>();
	}

	public static List<String> getInformationCharts() {
		return _chartsSelected;
	}
	
	public static int getInformationTable() {
		return _tableSelected;
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.CENTER);
		GridLayout layout = new GridLayout(3, false);
		container.setLayout(layout);
		
		Composite containerTable = new Composite(container, SWT.CENTER);
		layout = new GridLayout(2, false);
		layout.marginLeft = 60;
		containerTable.setLayout(layout);
		Label tableLabel = new Label(containerTable, SWT.RIGHT);
		tableLabel.setText("Table");
		_tableSelectedButton = new Button(containerTable, SWT.CHECK);
		_tableSelectedButton.setSelection(false);
		_tableSelectedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});
		
		Composite containerBarChart = new Composite(container, SWT.CENTER);
		layout = new GridLayout(2, false);
		layout.marginLeft = 45;
		containerBarChart.setLayout(layout);
		Label barChartLabel = new Label(containerBarChart, SWT.CENTER);
		barChartLabel.setText("Bar chart");
		_barChartSelectedButton = new Button(containerBarChart, SWT.CHECK);
		_barChartSelectedButton.setSelection(false);
		_barChartSelectedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});
		
		Composite containerLineChart = new Composite(container, SWT.CENTER);
		layout = new GridLayout(2, false);
		layout.marginLeft = 45;
		containerLineChart.setLayout(layout);
		Label lineChartLabel = new Label(containerLineChart, SWT.CENTER);
		lineChartLabel.setText("Line chart");
		_lineChartSelectedButton = new Button(containerLineChart, SWT.CHECK);
		_lineChartSelectedButton.setSelection(false);
		_lineChartSelectedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});
		
		Composite containerImageTable = new Composite(container, SWT.CENTER);
		layout = new GridLayout(1, false);
		layout.marginLeft = 21;
		containerImageTable.setLayout(layout);
		Label imageTableLabel = new Label(containerImageTable, SWT.CENTER);
		imageTableLabel.setImage(Images.Table);
		
		Composite containerImageBarChart = new Composite(container, SWT.CENTER);
		layout = new GridLayout(1, false);
		layout.marginLeft = 21;
		containerImageBarChart.setLayout(layout);
		Label imageBarChartLabel = new Label(containerImageBarChart, SWT.CENTER);
		imageBarChartLabel.setImage(Images.BarChart2);
		
		Composite containerImageLineChart = new Composite(container, SWT.CENTER);
		layout = new GridLayout(1, false);
		layout.marginLeft = 21;
		containerImageLineChart.setLayout(layout);
		Label imageLineChartLabel = new Label(containerImageLineChart, SWT.CENTER);
		imageLineChartLabel.setImage(Images.LineChart2);
		
		setControl(container);
		setPageComplete(false);
	}
	
	private void validate() {
		if(_tableSelectedButton.getSelection()) {
			_tableSelected = 1;
		} else {
			_tableSelected = 0;
		}
		
		if(_barChartSelectedButton.getSelection() && !_chartsSelected.contains("0")) {
			_chartsSelected.add("0");
		} else if(!_barChartSelectedButton.getSelection() && _chartsSelected.contains("0")) {
			_chartsSelected.remove("0");
		}
		
		if(_lineChartSelectedButton.getSelection() && !_chartsSelected.contains("1")) {
			_chartsSelected.add("1");
		} else if(!_lineChartSelectedButton.getSelection() && _chartsSelected.contains("1")) {
			_chartsSelected.remove("1");
		}
		
		setPageComplete(_barChartSelectedButton.getSelection() || _lineChartSelectedButton.getSelection() || _tableSelected == 1);	
	}
}
