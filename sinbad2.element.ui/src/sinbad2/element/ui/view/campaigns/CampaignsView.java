package sinbad2.element.ui.view.campaigns;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ECampaignsChange;
import sinbad2.element.campaigns.listener.ICampaignsChangeListener;
import sinbad2.element.ui.nls.Messages;
import sinbad2.element.ui.view.campaigns.dialog.NewMergeMecDialog;
import sinbad2.element.ui.view.campaigns.provider.CampaignDateLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignIdLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignsContentProvider;

public class CampaignsView extends ViewPart implements ICampaignsChangeListener {

	public static final String ID = "flintstones.element.ui.view.campaigns"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.element.ui.view.campaigns.campaigns_view"; //$NON-NLS-1$
	
	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);

	private static List<Campaign> _campaignsSelected;
	private static List<Button> _buttons;
	
	private Button _mergeButton;
	private Button _compareButton;
	private int _buttonRowPosition;
	private List<Campaign> _campaigns;
	
	private ProblemElementsSet _elementSet; 
	
	private TableViewer _tableViewer;
	
	private CampaignsContentProvider _provider;
	
	public CampaignsView() {
		_campaignsSelected = new LinkedList<Campaign>();
		_buttons = new LinkedList<Button>();
		_buttonRowPosition = 0;
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementSet = elementsManager.getActiveElementSet();
		_campaigns =_elementSet.getCampaigns();
		
		_elementSet.registerCampaignsChangesListener(this);
	}
	
	public static List<Campaign> getCampaignsSelected() {
		if(_campaignsSelected == null) {
			_campaignsSelected = new LinkedList<Campaign>();
		}
		return _campaignsSelected;
	}
	
	public static List<Button> getButtons() {
		if(_buttons == null) {
			_buttons = new LinkedList<Button>();
		}
		return _buttons;
	}
	
	@Override
	public void createPartControl(Composite parent) {		
		Composite campaigns = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		campaigns.setLayoutData(gridData);
		campaigns.setLayout(layout);
		campaigns.setBackground(new Color(Display.getCurrent(),255, 255, 255));

		_tableViewer = new TableViewer(campaigns, SWT.CENTER | SWT.BORDER | SWT.FULL_SELECTION);
		_tableViewer.getTable().setLayoutData(gridData);
		
		_provider = new CampaignsContentProvider(_tableViewer);
		_tableViewer.setContentProvider(_provider);
		
		_tableViewer.getTable().setHeaderVisible(true);
		_tableViewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 23;
				
			}
		});
		
		addColumns();
		hookContextMenu();
		hookFocusListener();
		
		_tableViewer.setInput(_provider.getInput());
		getSite().setSelectionProvider(_tableViewer);
		
		Composite buttonsContainer = new Composite(campaigns, SWT.NONE);
		GridLayout g_layout = new GridLayout(2, true);
		buttonsContainer.setLayout(g_layout);
		buttonsContainer.setBackground(new Color(Display.getCurrent(), 255, 255, 255)); 
		gridData = new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1);
		buttonsContainer.setLayoutData(gridData);
		buttonsContainer.setLayout(g_layout);
		buttonsContainer.setBackground(new Color(Display.getCurrent(),255, 255, 255));
		
		_mergeButton = new Button(buttonsContainer, SWT.PUSH);
		gridData = new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1);
		_mergeButton.setLayoutData(gridData);
		_mergeButton.setText(Messages.CampaignsView_Merge_Button);
		_mergeButton.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		_mergeButton.setEnabled(false);
		
		_mergeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewMergeMecDialog dialog = new NewMergeMecDialog();
				dialog.open();
			}
		});
		
		_compareButton = new Button(buttonsContainer, SWT.PUSH);
		gridData = new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1);
		_compareButton.setLayoutData(gridData);
		_compareButton.setText(Messages.CampaignsView_Compare_Button);
		_compareButton.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		_compareButton.setEnabled(false);
		
		_compareButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_elementSet.notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.COMPARE_CAMPAIGNS, null, _campaignsSelected, false));
			}
		});
			
	}
	
	private void hookFocusListener() {
		_tableViewer.getControl().addFocusListener(new FocusListener() {
			
			private IContextActivation activation = null;
			
			@Override
			public void focusLost(FocusEvent e) {
				_contextService.deactivateContext(activation);
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				activation = _contextService.activateContext(CONTEXT_ID);
			}
		});
	}

	private void addColumns() {
		TableViewerColumn tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tvc.setLabelProvider(new CampaignIdLabelProvider());
		TableColumn tc = tvc.getColumn();
		tc.setText(Messages.CampaignsView_Column_Campaign);
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tvc.setLabelProvider(new CampaignDateLabelProvider());
		tc = tvc.getColumn();
		tc.setText(Messages.CampaignsView_Date);
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tc = tvc.getColumn();
		tc.setText(Messages.CampaignsView_Column_Selection);
		tc.setResizable(false);
		tc.pack();
		tvc.setLabelProvider(new ColumnLabelProvider() {
			Map<Object, Button> buttons = new HashMap<Object, Button>();

			@Override
			public void update(ViewerCell cell) {
				TableItem item = (TableItem) cell.getItem();
				Button button;
				if (buttons.containsKey(cell.getElement())) {
					button = buttons.get(cell.getElement());
				} else {
					button = new Button((Composite) cell.getViewerRow().getControl(), SWT.CHECK);
					button.setData(Messages.CampaignsView_Row_Num, _buttonRowPosition);
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							int numRow = (Integer) e.widget.getData(Messages.CampaignsView_Row_Num);
							if(((Button) e.widget).getSelection()) {
								if(!_campaignsSelected.contains(_campaigns.get(numRow))) {
									_campaignsSelected.add(_campaigns.get(numRow));
								}
							} else {
								_campaignsSelected.remove(_campaigns.get(numRow));
							}
							
							Collections.sort(_campaignsSelected);
							
							if(_campaignsSelected.isEmpty()) {
								_elementSet.notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.CAMPAIGNS_SELECTED_CHANGES, null, new LinkedList<Campaign>(), false));
							} else {
								_elementSet.notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.CAMPAIGNS_SELECTED_CHANGES, null, _campaignsSelected, false));
							}
						}
					});
					buttons.put(cell.getElement(), button);
					
					_buttonRowPosition++;
					_buttons.add(button);
					
				}
				TableEditor editor = new TableEditor(item.getParent());
				button.pack();
				editor.minimumWidth = button.getSize().x;
				editor.horizontalAlignment = SWT.CENTER;
				editor.setEditor(button, item, cell.getColumnIndex());
				editor.layout();
			}

		});
	}
	
	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(_tableViewer.getTable());
		_tableViewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, _tableViewer);
	}
	
	@Override
	public void setFocus() {
		_tableViewer.getControl().setFocus();
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyCampaignsChange(CampaignsChangeEvent event) {
		switch(event.getChange()) {
			case CAMPAIGNS_SELECTED_CHANGES:
				List<Campaign> campaigns = (List<Campaign>) event.getNewValue();
				_campaignsSelected = campaigns;
				if(campaigns.size() > 1) {
					_mergeButton.setEnabled(true);
					_compareButton.setEnabled(true);
				} else {
					_mergeButton.setEnabled(false);
					_compareButton.setEnabled(false);
				}
			break;
			case MERGE_CAMPAIGNS:
				List<Campaign> mergeCampaigns = (List<Campaign>) event.getNewValue();
				_campaignsSelected = mergeCampaigns;
				_mergeButton.setEnabled(false);
				_compareButton.setEnabled(false);
				break;
			default:
				break;
		}
	}
}
