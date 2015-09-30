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

public class SelectChartWizardPage extends WizardPage {

	private Button _barChartSelectedButton;
	private Button _lineChartSelectedButton;
	
	private static List<String> _chartsSelected;
	
	protected SelectChartWizardPage() {
		super("Select chart");
		setDescription("Select the chart you want");
		
		_chartsSelected = new LinkedList<String>();
	}

	public static List<String> getInformationCharts() {
		return _chartsSelected;
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.CENTER);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		
		Label barChartLabel = new Label(container, SWT.CENTER);
		barChartLabel.setText("BarChart");
		_barChartSelectedButton = new Button(container, SWT.CHECK);
		_barChartSelectedButton.setSelection(false);
		_barChartSelectedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});
		
		
		Label lineChartLabel = new Label(container, SWT.CENTER);
		lineChartLabel.setText("LineChart");
		_lineChartSelectedButton = new Button(container, SWT.CHECK);
		_lineChartSelectedButton.setSelection(false);
		_lineChartSelectedButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});
		
		setControl(container);
		setPageComplete(false);
	}
	
	private void validate() {
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
		
		setPageComplete(_barChartSelectedButton.getSelection() || _lineChartSelectedButton.getSelection());	
	}
}
