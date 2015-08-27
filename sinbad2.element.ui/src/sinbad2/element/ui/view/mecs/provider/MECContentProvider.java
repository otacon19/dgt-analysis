package sinbad2.element.ui.view.mecs.provider;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ICampaignsChangeListener;
import sinbad2.element.mec.MEC;
import sinbad2.element.ui.view.campaigns.CampaignsView;

public class MECContentProvider implements IStructuredContentProvider, ICampaignsChangeListener {
	
	private List<MEC> _mecs;
	
	private TableViewer _tableViewer;
	
	public MECContentProvider(TableViewer tableViewer) {
		_tableViewer = tableViewer;
		_mecs = new LinkedList<MEC>();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
		
		elementsSet.registerCampaignsChangesListener(this);
	}
	
	@Override
	public void dispose() {}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<MEC>)inputElement).toArray();
	}
	
	public Object getInput() {
		_mecs.clear();
		if(CampaignsView.getCampaignsSelected().size() == 1) {
			_mecs.addAll(CampaignsView.getCampaignsSelected().get(0).getMECs());
		}
		
		return _mecs;
	}
	
	public void pack() {
		_tableViewer.getTable().getColumns()[0].pack();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyCampaignsChange(CampaignsChangeEvent event) {
		switch(event.getChange()) {
			case ADD_CAMPAIGN:
				addCampaign((Campaign) event.getNewValue());
				break;
			case COMPARE_CAMPAIGNS:
				compareCampaigns((List<Campaign>) event.getNewValue());
				break;
			case MERGE_CAMPAIGNS:
				mergeCampaigns(((List<Campaign>) event.getNewValue()).get(0));
				break;
			default:
				_tableViewer.setInput(getInput());
				break;
		}
		_tableViewer.refresh();
	}
	
	private void addCampaign(Campaign newCampaign) {
		_tableViewer.setInput(newCampaign.getMECs());
	}
	
	private void compareCampaigns(List<Campaign> compareCampaigns) {
		List<MEC> compareCampaignsMECs = new LinkedList<MEC>();
		for(Campaign c: compareCampaigns) {
			compareCampaignsMECs.addAll(c.getMECs());
		}
		_tableViewer.setInput(compareCampaignsMECs);
	}
	
	private void mergeCampaigns(Campaign mergeCampaign) {
		_tableViewer.setInput(mergeCampaign.getMECs()); 	
	}
}