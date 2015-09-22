package sinbad2.element.criterion.operation;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Button;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ECriteriaChange;

public class SelectAllCriterionOperation extends UndoableOperation {
	
	private List<Button> _buttons;
	private ProblemElementsSet _elementsSet;

	public SelectAllCriterionOperation(String label, List<Button> buttons, ProblemElementsSet elementsSet) {
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
		
		_elementsSet.notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.CRITERIA_SELECTED_CHANGES, null, _elementsSet.getCriteria(), _inUndoRedo));
		
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
