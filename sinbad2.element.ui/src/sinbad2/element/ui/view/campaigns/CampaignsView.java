package sinbad2.element.ui.view.campaigns;

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
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ECampaignsChange;
import sinbad2.element.ui.ComparatorCampaigns;
import sinbad2.element.ui.nls.Messages;
import sinbad2.element.ui.view.alternatives.AlternativesView;
import sinbad2.element.ui.view.campaigns.dialog.AddCampaignsDialog;
import sinbad2.element.ui.view.campaigns.provider.CampaignFinalDateLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignInitialDateLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignIdLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignProvinceLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignsSelectedContentProvider;
import sinbad2.element.ui.view.criteria.CriteriaView;

public class CampaignsView extends ViewPart {

	public static final String ID = "flintstones.element.ui.view.campaigns"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.element.ui.view.campaigns.campaigns_view"; //$NON-NLS-1$
	
	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);

	private TableViewer _tableViewer;
	private CampaignsSelectedContentProvider _provider;
	private ComparatorCampaigns _comparatorCampaigns;
	
	private static List<Campaign> _campaignsSelected;
	private static List<Campaign> _campaignsPreviouslyAdded;
	private static List<Button> _buttons;
	
	private Button _addCampaigns;
	private Button _removeCampaigns;
	
	private ProblemElementsSet _elementsSet; 
	
	public CampaignsView() {
		_campaignsSelected = new LinkedList<Campaign>();
		_campaignsPreviouslyAdded = new LinkedList<Campaign>();
		_buttons = new LinkedList<Button>();
		
		_comparatorCampaigns = new ComparatorCampaigns();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
	}
	
	public static List<Campaign> getCampaignsSelected() {
		if(_campaignsSelected == null) {
			_campaignsSelected = new LinkedList<Campaign>();
		}
		return _campaignsSelected;
	}
	
	public static List<Campaign> getCampaignsPreviouslyAdded() {
		return _campaignsPreviouslyAdded;
	}
	public static void setCampaignsSelected(List<Campaign> campaignsSelected) {
		_campaignsSelected = campaignsSelected;
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
		_tableViewer.setComparator(_comparatorCampaigns);
		_tableViewer.getTable().addListener(SWT.Selection, new Listener() {
	        @Override
	        public void handleEvent(Event event) {
	        	_tableViewer.getTable().deselectAll();
	        }
	    });
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_tableViewer.getTable().setLayoutData(gridData);
		
		_provider = new CampaignsSelectedContentProvider(_tableViewer);
		_tableViewer.setContentProvider(_provider);
		
		_tableViewer.getTable().setHeaderVisible(true);
		_tableViewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 23;
			}
		});
		
		_tableViewer.getTable().addListener(SWT.Paint, new Listener() {
			@Override
			public void handleEvent(Event event) {
				_tableViewer.getTable().layout();
			}
		});
		
		addColumns();
		hookContextMenu();
		hookFocusListener();
		
		_tableViewer.setInput(_provider.getInput());
		getSite().setSelectionProvider(_tableViewer);
		_provider.pack();
		
		Composite buttonsContainer = new Composite(campaigns, SWT.NONE);
		GridLayout g_layout = new GridLayout(2, true);
		buttonsContainer.setLayout(g_layout);
		buttonsContainer.setBackground(new Color(Display.getCurrent(), 255, 255, 255)); 
		gridData = new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1);
		buttonsContainer.setLayoutData(gridData);
		buttonsContainer.setLayout(g_layout);
		buttonsContainer.setBackground(new Color(Display.getCurrent(),255, 255, 255));
		
		_addCampaigns = new Button(buttonsContainer, SWT.PUSH);
		gridData = new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1);
		_addCampaigns.setLayoutData(gridData);
		_addCampaigns.setText(Messages.CampaignsView_Add);
		_addCampaigns.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		_addCampaigns.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_TOOL_NEW_WIZARD).createImage());
		_addCampaigns.setEnabled(true);
		
		_addCampaigns.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				if(!((List<Object>) _provider.getInput()).isEmpty()) {
					_campaignsPreviouslyAdded = (List<Campaign>) _provider.getInput();
				}
				
				AddCampaignsDialog dialog = new AddCampaignsDialog();
				dialog.open();
			}
		});
		
		_removeCampaigns = new Button(buttonsContainer, SWT.PUSH);
		gridData = new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1);
		_removeCampaigns.setLayoutData(gridData);
		_removeCampaigns.setText(Messages.CampaignsView_Remove);
		_removeCampaigns.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		_removeCampaigns.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE).createImage());
		_removeCampaigns.setEnabled(false);
		
		_removeCampaigns.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_elementsSet.notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.REMOVE_CAMPAIGNS_SELECTED, null , _campaignsSelected, false));
				disposeEditors();
				
				_removeCampaigns.setEnabled(false);
				_campaignsSelected.clear();
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
		tc.addSelectionListener(getSelectionAdapter(tvc.getColumn(), 0));
		tc.setText(Messages.CampaignsView_Campaign_column);
		tc.setResizable(false);
		
		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tvc.setLabelProvider(new CampaignProvinceLabelProvider());
		tc = tvc.getColumn();
		tc.addSelectionListener(getSelectionAdapter(tvc.getColumn(), 1));
		tc.setText(Messages.CampaignsView_Region_column);
		tc.setResizable(false);
		
		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tvc.setLabelProvider(new CampaignInitialDateLabelProvider());
		tc = tvc.getColumn();
		tc.addSelectionListener(getSelectionAdapter(tvc.getColumn(), 2));
		tc.setText(Messages.CampaignsView_Initial_date_column);
		tc.setResizable(false);
		
		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tvc.setLabelProvider(new CampaignFinalDateLabelProvider());
		tc = tvc.getColumn();
		tc.addSelectionListener(getSelectionAdapter(tvc.getColumn(), 3));
		tc.setText(Messages.CampaignsView_Final_date_column);
		tc.setResizable(false);
		
		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tc = tvc.getColumn();
		tc.setText(Messages.CampaignsView_Selection_column);
		tc.setResizable(false);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			Map<Object, Button> buttons = new HashMap<Object, Button>();

			@Override
			public void update(ViewerCell cell) {
				TableItem item = (TableItem) cell.getItem();
				
				final Button button;
				if (buttons.containsKey(cell.getElement()) && !((Button) buttons.get(cell.getElement())).isDisposed()) {
					button = buttons.get(cell.getElement());
				} else {
					button = new Button((Composite) cell.getViewerRow().getControl(), SWT.CHECK);
					button.setData("campaign", (Campaign) item.getData());  //$NON-NLS-1$
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							setEnabledButtonsCampaings(true);
							if(((Campaign) button.getData("campaign")).isACampaignData()) { //$NON-NLS-1$
								checkCompatibleCampaigns((Campaign) button.getData("campaign"), button.getSelection()); //$NON-NLS-1$
							} else {
								checkCompatibleDataCampaigns((Campaign) button.getData("campaign"), button.getSelection()); //$NON-NLS-1$
							}
							
							if(((Button) e.widget).getSelection()) {
								if(!_campaignsSelected.contains((Campaign) button.getData("campaign"))) { //$NON-NLS-1$
									_campaignsSelected.add((Campaign) button.getData("campaign")); //$NON-NLS-1$
								}
							} else {
								_campaignsSelected.remove((Campaign) button.getData("campaign")); //$NON-NLS-1$
							}
							
							if(_campaignsSelected.isEmpty()) {
								_elementsSet.notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.CAMPAIGNS_SELECTED_CHANGES, null, new LinkedList<Campaign>(), false));
								
								setVisibleButtons(false);
								_removeCampaigns.setEnabled(false);
							} else {
								_elementsSet.notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.CAMPAIGNS_SELECTED_CHANGES, null, _campaignsSelected, false));
								_removeCampaigns.setEnabled(true);
							}
						}

						private void checkCompatibleCampaigns(Campaign dataCampaign, boolean selection) {
							if(selection) {
								for(Button b: _buttons) {
									Campaign c = (Campaign) b.getData("campaign"); //$NON-NLS-1$
									if(!c.getProvince().equals(dataCampaign.getProvince())) {
										b.setEnabled(false);
									}
								}
							} else {
								setEnabledButtonsCampaings(!selection);
							}
						}
						
						private void checkCompatibleDataCampaigns(Campaign campaign, boolean selection) {
							if(selection) {
								for(Button b: _buttons) {
									Campaign c = (Campaign) b.getData("campaign"); //$NON-NLS-1$
									if(!c.getProvince().equals(campaign.getProvince()) && c.isACampaignData()) {
										b.setEnabled(false);
									}
								}
							} else {
								setEnabledButtonsCampaings(!selection);
							}
						}
					});
					buttons.put(cell.getElement(), button);				
					_buttons.add(button);
					
				}
				TableEditor editor = new TableEditor(item.getParent());
				button.pack();
				editor.minimumWidth = button.getSize().x;
				editor.horizontalAlignment = SWT.CENTER;
				editor.setEditor(button, item, cell.getColumnIndex());
				editor.layout();
				button.setData("editor", editor); //$NON-NLS-1$
			}

		});
	}
	
	private SelectionAdapter getSelectionAdapter(final TableColumn column, final int index) {
		SelectionAdapter selectionAdapter = new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_comparatorCampaigns.setColumn(index);
				int dir = _comparatorCampaigns.getDirection();
				_tableViewer.getTable().setSortDirection(dir);
				_tableViewer.getTable().setSortColumn(column);
				_tableViewer.refresh();
			}
		};
		return selectionAdapter;
	}
	
	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(_tableViewer.getTable());
		_tableViewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, _tableViewer);
	}
	
	private void setVisibleButtons(boolean state) {
		List<Button> buttonsCriteria = CriteriaView.getButtons();
		for(Button b: buttonsCriteria) {
			b.setVisible(state);
		}
		List<Button> buttonsAlternatives = AlternativesView.getButtons();
		for(Button b: buttonsAlternatives) {
			b.setVisible(state);
		}
	}
	
	private void setEnabledButtonsCampaings(boolean state) {
		for(Button b: _buttons) {
			b.setEnabled(state);
		}
	}
	
	private void disposeEditors() {
		List<Button> buttonsToRemove = new LinkedList<Button>();
		for(Campaign c: _campaignsSelected) {
			for(Button b: _buttons) {
				if(!b.isDisposed()) {
					Campaign campaign = (Campaign) b.getData("campaign"); //$NON-NLS-1$
					String id = campaign.getId();
					if(id.equals(c.getId())) {
						TableEditor editor = (TableEditor) b.getData("editor"); //$NON-NLS-1$
						editor.getEditor().dispose();
						buttonsToRemove.add(b);
					}
				}
			}
		}
		_buttons.removeAll(buttonsToRemove);
	}
	
	@Override
	public void setFocus() {
		_tableViewer.getControl().setFocus();
		
	}
}
