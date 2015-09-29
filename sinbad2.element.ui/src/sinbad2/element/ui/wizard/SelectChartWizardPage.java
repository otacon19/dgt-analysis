package sinbad2.element.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class SelectChartWizardPage extends WizardPage {

	protected SelectChartWizardPage() {
		super("Select chart");
		setDescription("Select the chart you want");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.CENTER);
		GridLayout layout = new GridLayout(2, false);
		container.setLayout(layout);
		
		Label barChartLabel = new Label(container, SWT.CENTER);
		barChartLabel.setText("BarChart");
		Button barChartSelectedButton = new Button(container, SWT.CHECK);
		barChartSelectedButton.setSelection(false);
		Label lineChartLabel = new Label(container, SWT.CENTER);
		lineChartLabel.setText("LineChart");
		Button lineChartSelectedButton = new Button(container, SWT.CHECK);
		lineChartSelectedButton.setSelection(false);
		
		setControl(container);
		setPageComplete(true);
	}
}
