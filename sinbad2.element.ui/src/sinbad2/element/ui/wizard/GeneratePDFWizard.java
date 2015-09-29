package sinbad2.element.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

public class GeneratePDFWizard extends Wizard {
	private SelectChartWizardPage _page1;
	private SelectCampaignsWizardPage _page2;

	public GeneratePDFWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public String getWindowTitle() {
		return "Generate PDF";
	}

	@Override
	public void addPages() {
		_page1 = new SelectChartWizardPage();
		_page2 = new SelectCampaignsWizardPage();
		addPage(_page1);
		addPage(_page2);
	}

	@Override
	public boolean performFinish() {
		return false;
	}

}
