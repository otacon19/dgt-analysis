package sinbad2.element.alternative.operation;

import java.util.List;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Button;

import sinbad2.core.undoable.UndoableOperation;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.EAlternativesChange;

public class SelectAllAlternativeOperation extends UndoableOperation {
	
	private List<Button> _buttons;
	private ProblemElementsSet _elementsSet;

	public SelectAllAlternativeOperation(String label, List<Button> buttons, ProblemElementsSet elementsSet) {
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
		
		_elementsSet.notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.ALTERNATIVES_SELECTED_CHANGES, null, _elementsSet.getAlternatives(), _inUndoRedo));
		
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
