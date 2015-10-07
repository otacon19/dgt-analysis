package sinbad2.element.ui.wizard;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeColumn;

import sinbad2.element.alternative.Alternative;
import sinbad2.element.ui.nls.Messages;
import sinbad2.element.ui.view.alternatives.AlternativesView;
import sinbad2.element.ui.view.alternatives.provider.AlternativeSelectedIdLabelProvider;
import sinbad2.element.ui.view.alternatives.provider.AlternativesWizardContentProvider;

public class SelectAlternativesWizardPage extends WizardPage {

	private TreeViewer _treeViewerAlternatives;
	private AlternativesWizardContentProvider _provider;
	
	private static List<Alternative> _alternativesSelected;
	
	protected SelectAlternativesWizardPage() {
		super(Messages.SelectAlternativesWizardPage_Select_contexts);
		setDescription(Messages.SelectAlternativesWizardPage_Select_the_contexts_you_want);
		
		_alternativesSelected = AlternativesView.getAlternativesSelected();		
	}
	
	public static List<Alternative> getInformationAlternatives() {
		return _alternativesSelected;
	}

	@Override
	public void createControl(Composite parent) {
		Composite alternatives = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		alternatives.setLayoutData(gridData);
		alternatives.setLayout(layout);
		_treeViewerAlternatives = new TreeViewer(alternatives, SWT.CENTER | SWT.BORDER | SWT.FULL_SELECTION);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_treeViewerAlternatives.getTree().setLayoutData(gridData);
		
		_provider = new AlternativesWizardContentProvider(_treeViewerAlternatives);
		_treeViewerAlternatives.setContentProvider(_provider);
		
		_treeViewerAlternatives.getTree().setHeaderVisible(true);
		_treeViewerAlternatives.getTree().addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.height = 25;
			}
		});
		
		_treeViewerAlternatives.getTree().addListener(SWT.Paint, new Listener() {
			@Override
			public void handleEvent(Event event) {
				_treeViewerAlternatives.getTree().layout();
			}
		});
		
		addColumns();
		
		_treeViewerAlternatives.setInput(_provider.getInput());
	
		setControl(alternatives);
		setPageComplete(true);
		
		_provider.pack();
	}
	
	private void addColumns() {
		TreeViewerColumn tvc = new TreeViewerColumn(_treeViewerAlternatives, SWT.CENTER);
		tvc.setLabelProvider(new AlternativeSelectedIdLabelProvider());
		TreeColumn tc = tvc.getColumn();
		tc.setText(Messages.SelectAlternativesWizardPage_Context_column);
		tc.setResizable(false);
		tc.pack();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		_provider.pack();
	}
}
