package sinbad2.element.ui.handler.campaign.select;

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
import sinbad2.element.campaigns.operation.DeselectAllCampaignOperation;
import sinbad2.element.ui.view.campaigns.CampaignsView;

public class DeselectAllCampaignHandler extends AbstractHandler {
	
	public final static String ID = "flintstones.element.campaign.deselectAll"; //$NON-NLS-1$
	
	public DeselectAllCampaignHandler() {}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		List<Button> buttons;
		buttons =  CampaignsView.getButtons();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
		
		IUndoableOperation operation = new DeselectAllCampaignOperation("Deselect all", buttons, elementsSet);
		IOperationHistory operationHistory = OperationHistoryFactory.getOperationHistory();
			
		operation.addContext(IOperationHistory.GLOBAL_UNDO_CONTEXT);
		operationHistory.execute(operation, null, null);
		
		return null;
	}

}
