package sinbad2.element.ui.view.alternatives.provider;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;

public class AlternativesWizardContentProvider implements ITreeContentProvider {

	TreeViewer _treeViewer;
	
	List<Alternative> _alternatives;
	
	ProblemElementsSet _elementsSet;
	
	public AlternativesWizardContentProvider(TreeViewer treeViewer) {
		_treeViewer = treeViewer;
		hookTreeListener();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_alternatives = _elementsSet.getAlternatives();
	}

	private void hookTreeListener() {
		_treeViewer.addTreeListener(new ITreeViewerListener() {
			
			@Override
			public void treeExpanded(TreeExpansionEvent event) {
				Display.getCurrent().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						pack();
					}
				});
			}
			
			@Override
			public void treeCollapsed(TreeExpansionEvent event) {
				Display.getCurrent().asyncExec(new Runnable() {
					
					@Override
					public void run() {
						pack();	
					}
				});
			}
		});
	}
	
	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	public Object getInput() {
		return _alternatives;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Alternative>) inputElement).toArray();
	}
	
	@Override
	public Object[] getChildren(Object parentElement) {
		return ((Alternative) parentElement).getChildrens().toArray();
	}

	@Override
	public Object getParent(Object element) {
		return ((Alternative) element).getParent();
	}

	@Override
	public boolean hasChildren(Object element) {
		return ((Alternative) element).hasChildrens();
	}

	public void pack() {
		for(TreeColumn tc: _treeViewer.getTree().getColumns()) {
			tc.pack();
		}
	}
	
}
