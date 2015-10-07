package sinbad2.element.ui.wizard;

import org.eclipse.jface.wizard.Wizard;

import sinbad2.element.ui.nls.Messages;

public class GeneratePDFWizard extends Wizard {
	private SelectChartWizardPage _page1;
	private SelectCampaignsWizardPage _page2;
	private SelectAlternativesWizardPage _page3;
	private SelectMEsWizardPage _page4;

	public GeneratePDFWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public String getWindowTitle() {
		return Messages.GeneratePDFWizard_Generate_PDF;
	}

	@Override
	public void addPages() {
		_page1 = new SelectChartWizardPage();
		_page2 = new SelectCampaignsWizardPage();
		_page3 = new SelectAlternativesWizardPage();
		_page4 = new SelectMEsWizardPage();
		addPage(_page1);
		addPage(_page2);
		addPage(_page3);
		addPage(_page4);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

}
