package sinbad2.resolutionphase.analysis.ui;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import sinbad2.resolutionphase.analysis.MockModel;

public class AnalysisPerspective implements IPerspectiveFactory {
	
	public static final String ID = "flintstones.resolutionphase.analysis.perspective";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);
		layout.setFixed(true);
		new MockModel();
	}
	
}	
