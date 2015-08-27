package sinbad2.element.ui.view.criteria.provider;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TableColumn;

import sinbad2.element.campaigns.Campaign;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.ui.view.campaigns.CampaignsView;

public class CriteriaCampaignsContentProvider implements
		IStructuredContentProvider {
	
	private TableViewer _tableViewer;

	public CriteriaCampaignsContentProvider(TableViewer tableViewer) {
		_tableViewer = tableViewer;
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<Criterion>) inputElement).toArray();
	}

	public Object getInput() {
		 List<Campaign> campaigns = CampaignsView.getCampaignsSelected();
		 return campaigns.get(0).getCriteria();
	}

	public void pack() {
		for (TableColumn tc : _tableViewer.getTable().getColumns()) {
			tc.pack();
		}
	}

}
