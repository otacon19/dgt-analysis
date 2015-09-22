package sinbad2.element.campaigns.operation;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Button;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ECampaignsChange;

public class SelectAllCampaignOperation extends UndoableOperation {
	
	List<Button> _buttons;
	ProblemElementsSet _elementsSet;

	public SelectAllCampaignOperation(String label, List<Button> buttons, ProblemElementsSet elementsSet) {
		super(label);
		
		_elementsSet = elementsSet;
		_buttons = buttons;
	}

	@Override
	public IStatus executeOperation(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		
		for(Button b: _buttons) {
			b.setSelection(true);
		}
		
		_elementsSet.notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.CAMPAIGNS_SELECTED_CHANGES, null, _elementsSet.getCampaigns(), _inUndoRedo));
		
		return Status.OK_STATUS;
	}

	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		
		return Status.OK_STATUS;
	}

	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info)
			throws ExecutionException {
		
		return Status.OK_STATUS;
	}
	
}
