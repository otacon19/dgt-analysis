package sinbad2.element.ui.handler.criterion.select;

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
import sinbad2.element.criterion.operation.DeselectAllCriterionOperation;
import sinbad2.element.ui.view.criteria.CriteriaView;

public class DeselectAllCriterionHandler extends AbstractHandler {
	
	public final static String ID = "flintstones.element.criterion.deselectAll"; //$NON-NLS-1$
	
	public DeselectAllCriterionHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<Button> buttons;
		buttons =  CriteriaView.getButtons();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
		
		IUndoableOperation operation = new DeselectAllCriterionOperation("Deselect all", buttons, elementsSet);
		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
			
		operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
		operationHistory.execute(operation, null, null);
		
		return null;
	}
}
