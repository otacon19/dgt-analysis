package sinbad2.element.ui.view.alternatives;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.EAlternativesChange;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ICampaignsChangeListener;
import sinbad2.element.ui.view.alternatives.provider.AlternativeSelectedIdLabelProvider;
import sinbad2.element.ui.view.alternatives.provider.AlternativesSelectedContentProvider;
import sinbad2.element.ui.view.campaigns.CampaignsView;

public class AlternativesView extends ViewPart implements ICampaignsChangeListener{
	
	public static final String ID = "flintstones.element.ui.view.alternatives"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.element.ui.view.alternatives.alternatives_view"; //$NON-NLS-1$
	
	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);

	private TreeViewer _treeViewer;
	private AlternativesSelectedContentProvider _provider;
	
	private static List<Alternative> _alternativesSelected;
	private static List<Button> _buttons;
	
	private List<Alternative> _alternativesBeforeSelected;
	
	private ProblemElementsSet _elementsSet;
	
	public AlternativesView() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_alternativesSelected = new LinkedList<Alternative>();
		_buttons = new LinkedList<Button>();
		
		_alternativesBeforeSelected = new LinkedList<Alternative>();
		
		_elementsSet.registerCampaignsChangesListener(this);
	}
	
	public static List<Alternative> getAlternativesSelected() {
		return _alternativesSelected;
	}
	
	public static List<Button> getButtons() {
		return _buttons;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		_treeViewer = new TreeViewer(parent, SWT.CENTER | SWT.BORDER | SWT.FULL_SELECTION);
		
		_provider = new AlternativesSelectedContentProvider(_treeViewer);
		_treeViewer.setContentProvider(_provider);
		
		_treeViewer.getTree().setHeaderVisible(true);
		_treeViewer.getTree().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 25;
			}
		});
		
		_treeViewer.getTree().addListener(SWT.Paint, new Listener() {
			@Override
			public void handleEvent(Event event) {
				_treeViewer.getTree().layout();
			}
		});
		
		addColumns();
		hookContextMenu();
		hookFocusListener();
		
		_treeViewer.setInput(_provider.getInput());
		_provider.pack();
		getSite().setSelectionProvider(_treeViewer);
	}
	
	private void hookFocusListener() {
		_treeViewer.getControl().addFocusListener(new FocusListener() {
			
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
		TreeViewerColumn tvc = new TreeViewerColumn(_treeViewer, SWT.CENTER);
		tvc.setLabelProvider(new AlternativeSelectedIdLabelProvider());
		TreeColumn tc = tvc.getColumn();
		tc.setText("Context");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TreeViewerColumn(_treeViewer, SWT.CENTER);
		tc = tvc.getColumn();
		tc.setText("Selection");
		tc.setResizable(false);
		tc.pack();
		tvc.setLabelProvider(new ColumnLabelProvider() {
			Map<Object, Button> buttons = new HashMap<Object, Button>();

			@Override
			public void update(ViewerCell cell) {
				TreeItem item = (TreeItem) cell.getItem();
				final Button button;
				if (buttons.containsKey(cell.getElement()) && !((Button) buttons.get(cell.getElement())).isDisposed()) {
					button = buttons.get(cell.getElement());
				} else {
					button = new Button((Composite) cell.getViewerRow().getControl(), SWT.CHECK);
					button.setSelection(false);
					button.setData("alternative", (Alternative) item.getData());
					buttons.put(cell.getElement(), button);
					if(_alternativesBeforeSelected.contains(item.getData())) {
						button.setSelection(true);
					} else {
						button.setSelection(false);
					}
					_buttons.add(button);
					
					List<Alternative> alternativesSelected = AlternativesView.getAlternativesSelected();
					for(Alternative a: alternativesSelected) {
						if(a.hasChildrens()) {
							List<Alternative> childrens = a.getChildrens();
							if(childrens.contains(item.getData())) {
								button.setSelection(true);
							}
						}
					}
							
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if(((Button) e.widget).getSelection()) {
								_alternativesSelected.add((Alternative) button.getData("alternative"));
								button.setSelection(true);
								
								if(((Alternative) button.getData("alternative")).hasChildrens()) {
									List<Alternative> childrens = ((Alternative) button.getData("alternative")).getChildrens();
									for(Alternative children: childrens) {
										if(!_alternativesSelected.contains(children)) {
											_alternativesSelected.add(children);
										}
									}
									
									for(Button b: _buttons) {
										if(childrens.contains(b.getData("alternative"))) {;
											b.setSelection(true);
										}
									}
								}
							} else {
								if(!_alternativesSelected.isEmpty()) {
									_alternativesSelected.remove((Alternative) button.getData("alternative"));
									button.setSelection(false);
									
									if(((Alternative) button.getData("alternative")).hasChildrens()) {
										List<Alternative> childrens = ((Alternative) button.getData("alternative")).getChildrens();
										for(Alternative children: childrens) {
											if(_alternativesSelected.contains(children)) {
												_alternativesSelected.remove(children);
											}
										}
										
										for(Button b: _buttons) {
											if(childrens.contains(b.getData("alternative"))) {
												b.setSelection(false);
											}
										}
									}
								}
							}
							
							_elementsSet.notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.ALTERNATIVES_SELECTED_CHANGES, null, _alternativesSelected, false));
						}
						
					});
					
				}
				
				TreeEditor editor = new TreeEditor(item.getParent());
				button.pack();
				editor.minimumWidth = button.getSize().x;
				editor.horizontalAlignment = SWT.CENTER;
				editor.setEditor(button, item, cell.getColumnIndex());
				editor.layout();
				button.setData("editor", editor);
				
				checkMatchingAlternatives(item);
			}
			
			private void checkMatchingAlternatives(TreeItem item) {
				List<Alternative> allAlternatives = _elementsSet.getAlternatives();
				
				Map<Alternative, Button> buttonsAlternatives = new HashMap<Alternative, Button>();
				for(Button b: _buttons) {
					buttonsAlternatives.put((Alternative) b.getData("alternative"), b);
				}
				
				List<Campaign> campaignsSelected = CampaignsView.getCampaignsSelected();
				if(campaignsSelected.size() == 1) {
					Campaign campaignSelected = CampaignsView.getCampaignsSelected().get(0);
					for(Alternative a: allAlternatives) {
						if(!campaignSelected.getAlternatives().contains(a)) {
							if(a.equals(item.getData())) {
								item.setForeground(new Color(Display.getCurrent(), 211, 211, 211));
								buttonsAlternatives.get(item.getData()).setEnabled(false);
							}
						} else if(a.equals(item.getData())) {
							item.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
							buttonsAlternatives.get(item.getData()).setEnabled(true);
						}
					}
				} else if(campaignsSelected.size() > 1) {
					List<Alternative> allAlternativesCampaigns = new LinkedList<Alternative>();
					List<Alternative> dataAlternatives = new LinkedList<Alternative>();
					int numCampaignsData = 0;
					for(Campaign c: campaignsSelected) {
						if(!c.isACampaignData()) { 
							List<Alternative> alternatives = c.getAlternatives();
							for(Alternative alt: alternatives) {
								allAlternativesCampaigns.add(alt);
							}
						} else {
							numCampaignsData++;
							List<Alternative> alternatives = c.getAlternatives();
							for(Alternative a: alternatives) {
								if(!dataAlternatives.contains(a)) {
									dataAlternatives.add(a);
								}
							}
						}
					}
					Map<Alternative, Integer> alternativesRepeat;
					alternativesRepeat = checkMatchingData(allAlternativesCampaigns, campaignsSelected.size() - numCampaignsData);
					for(Alternative a: dataAlternatives) {
						alternativesRepeat.put(a, campaignsSelected.size() - numCampaignsData);
					}
					for(Alternative alt: alternativesRepeat.keySet()) {
						int rep = alternativesRepeat.get(alt);
						if(rep != campaignsSelected.size() - numCampaignsData) {
							if(alt.equals(item.getData())) {
								item.setForeground(new Color(Display.getCurrent(), 211, 211, 211));
								buttonsAlternatives.get(item.getData()).setEnabled(false);
							}
						} else if(alt.equals(item.getData())) {
							item.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
							buttonsAlternatives.get(item.getData()).setEnabled(true);
						}
					}
				}
			}
			
			private Map<Alternative, Integer> checkMatchingData(List<Alternative> allAlternativesCampaigns, int numCampaigns) {		
				Map<Alternative, Integer> alternativesRepeat = new LinkedHashMap<Alternative, Integer>();
				int numRep;
				for(int i = 0; i < allAlternativesCampaigns.size(); i++){
				    Alternative a1 = allAlternativesCampaigns.get(i);
				    numRep = 0;
				    for(int j = 0; j < allAlternativesCampaigns.size(); j++){
				    	Alternative a2 = allAlternativesCampaigns.get(j);
				        if(a1.equals(a2)) {
				            numRep++;
				            alternativesRepeat.put(a1, numRep);
				        }
				    }
				}
				return alternativesRepeat;
			}

		});

	}
	
	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(_treeViewer.getTree());
		_treeViewer.getTree().setMenu(menu);
		getSite().registerContextMenu(menuManager, _treeViewer);
	}
	
	@Override
	public void setFocus() {
		_treeViewer.getControl().setFocus();
		
	}

	@Override
	public void notifyCampaignsChange(CampaignsChangeEvent event) {
		switch(event.getChange()) {
			case CAMPAIGNS_SELECTED_CHANGES:
				disposeEditors();
				break;
			case REMOVE_CAMPAIGNS_SELECTED:
				disposeEditors();
				break;
			default:
				break;
		}	
	}
	
	private void disposeEditors() {
		_alternativesBeforeSelected.clear();
		
		List<Campaign> campaignsSelected = CampaignsView.getCampaignsSelected();
		for(Button b: _buttons) {
			if(!b.isDisposed()) {
				if(b.getSelection() && !campaignsSelected.isEmpty()) {
					_alternativesBeforeSelected.add((Alternative) b.getData("alternative"));
				}
				TreeEditor editor = (TreeEditor) b.getData("editor");
				editor.getEditor().dispose();
			}
		}
		_buttons.clear();
		if(campaignsSelected.isEmpty()) {
			_alternativesSelected.clear();
		}
	}
}
