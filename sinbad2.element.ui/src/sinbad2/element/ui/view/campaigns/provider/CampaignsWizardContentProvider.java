package sinbad2.element.ui.view.campaigns.provider;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.ui.view.campaigns.CampaignsView;

public class CampaignsWizardContentProvider implements IStructuredContentProvider {
	
	TableViewer _tableViewer;
	
	ProblemElementsSet _elementsSet;
	
	public CampaignsWizardContentProvider(TableViewer tableViewer) {
		_tableViewer = tableViewer;
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
	}

	@Override
	public void dispose() {}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
	
	public Object getInput() {
		return CampaignsView.getCampaignsSelected();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Campaign>) inputElement).toArray();
	}
	
	public void pack() {
		for(int i = 0; i < _tableViewer.getTable().getColumnCount() - 1; ++i) {
			_tableViewer.getTable().getColumn(i).pack();
		}
		
		Table table = _tableViewer.getTable();
	    int columnsWidth = 0;
	    
	    for (int i = 0; i < table.getColumnCount() - 1; i++) {
	        columnsWidth += table.getColumn(i).getWidth();
	    }
	    TableColumn lastColumn = table.getColumn(table.getColumnCount() - 1);
	    lastColumn.pack();

	    Rectangle area = table.getClientArea();

	    int width = area.width - 2*table.getBorderWidth();

	    if(lastColumn.getWidth() < width - columnsWidth) {
	        lastColumn.setWidth(width - columnsWidth + 3);
	    }
	}
}
