package sinbad2.element.ui.view.campaigns.dialog;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.mec.MEC;
import sinbad2.element.ui.nls.Messages;
import sinbad2.element.ui.view.campaigns.CampaignsView;

public class NewMergeMecDialog extends Dialog {
	
	private String _mecName;
	private Button _okButton;
	private ControlDecoration _mecNameControlDecoration;
	
	private ProblemElementsSet _elementSet;

	public NewMergeMecDialog() {
		super(Display.getCurrent().getActiveShell());
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementSet = elementsManager.getActiveElementSet();
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginRight = 10;
		gridLayout.marginTop = 10;
		gridLayout.marginLeft = 10;
		gridLayout.marginBottom = 10;
		container.setLayout(gridLayout);

		Label idLabel = new Label(container, SWT.NULL);
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1);
		idLabel.setLayoutData(gridData);
		idLabel.setText(Messages.NewMergeMecDialog_Label_Campaign_Name);
		idLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$
		
		Text text = new Text(container, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		text.setLayoutData(gridData);
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				_mecName = ((Text) e.getSource()).getText().trim();
				validate();
			}
		});

		_mecNameControlDecoration = createNotificationDecorator(text);
		
		return container;
	}
	
	private ControlDecoration createNotificationDecorator(Text text) {
		_mecNameControlDecoration = new ControlDecoration(text,
				SWT.LEFT | SWT.TOP);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		_mecNameControlDecoration.setImage(fieldDecoration.getImage());
		validate(""); //$NON-NLS-1$

		return _mecNameControlDecoration;
	}

	private boolean validate(String text) {
		_mecNameControlDecoration.setDescriptionText(text);
		if (text.isEmpty()) {
			_mecNameControlDecoration.hide();
			return true;
		} else {
			_mecNameControlDecoration.show();
			return false;

		}
	}

	private void validate() {
		boolean validId;

		String message = ""; //$NON-NLS-1$

		if (!_mecName.isEmpty()) {
			List<Campaign> campaigns = _elementSet.getCampaigns();
			for(Campaign c: campaigns) {
				if(c.getId().equals(_mecName)) {
					message = Messages.NewMergeMecDialog_Duplicate_Id;
				}
			}
		} else {
			message = Messages.NewMergeMecDialog_Empty_Name;
		}

		validId = validate(message);

		_okButton.setEnabled(validId);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		_okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		_okButton.setEnabled(false);
		_mecNameControlDecoration.show();
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
		_okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Campaign c = new Campaign(_mecName);
				Campaign campaignSelected = CampaignsView.getCampaignsSelected().get(0);
				c.setAlternatives(campaignSelected.getAlternatives());
				c.setCriteria(campaignSelected.getCriteria());
				
				List<MEC> allMECs = new LinkedList<MEC>();
				for(Campaign ca: CampaignsView.getCampaignsSelected()) {
					allMECs.addAll(ca.getMECs());
				}
				c.setMECs(allMECs);
				_elementSet.addCampaign(c);
			}
		});
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.NewMergeMecDialog_New_ME);
	}

	
}
