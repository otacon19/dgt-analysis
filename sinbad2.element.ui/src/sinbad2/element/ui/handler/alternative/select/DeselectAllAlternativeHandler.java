package sinbad2.element.ui.handler.alternative.select;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.swt.widgets.Button;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.operation.DeselectAllAlternativeOperation;
import sinbad2.element.ui.view.alternatives.AlternativesView;

public class DeselectAllAlternativeHandler extends AbstractHandler {

	public final static String ID = "flintstones.element.alternative.deselectAll"; //$NON-NLS-1$
	
	public DeselectAllAlternativeHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<Button> buttons;
		buttons =  AlternativesView.getButtons();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
		
		IUndoableOperation operation = new DeselectAllAlternativeOperation("Deselect all", buttons, elementsSet);
		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
			
		operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
		operationHistory.execute(operation, null, null);
		
		return null;
	}

}
