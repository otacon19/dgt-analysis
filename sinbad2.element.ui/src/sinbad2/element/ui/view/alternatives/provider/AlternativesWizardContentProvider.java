package sinbad2.element.ui.view.alternatives.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.ui.view.alternatives.AlternativesView;

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
		List<Alternative> alternativesParent = new LinkedList<Alternative>();
		for(Alternative parent: AlternativesView.getAlternativesSelected()) {
			if(parent.hasChildrens()) {
				alternativesParent.add(parent);
			}
		}
		return alternativesParent;
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
		
		Tree tree = _treeViewer.getTree();
	    int columnsWidth = 0;
	    
	    for (int i = 0; i < tree.getColumnCount() - 1; i++) {
	        columnsWidth += tree.getColumn(i).getWidth();
	    }
	    TreeColumn lastColumn = tree.getColumn(tree.getColumnCount() - 1);
	    lastColumn.pack();

	    Rectangle area = tree.getClientArea();

	    int width = area.width - 2*tree.getBorderWidth();

	    if(lastColumn.getWidth() < width - columnsWidth) {
	        lastColumn.setWidth(width - columnsWidth + 3);
	    }
	}
	
}
