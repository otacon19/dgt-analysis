package sinbad2.element.ui.view.criteria;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
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
import sinbad2.element.campaigns.listener.ICampaignsChangeListener;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ECriteriaChange;
import sinbad2.element.ui.Images;
import sinbad2.element.ui.view.campaigns.CampaignsView;
import sinbad2.element.ui.view.criteria.provider.CriteriaSelectedContentProvider;
import sinbad2.element.ui.view.criteria.provider.CriterionOperationLabelProvider;
import sinbad2.element.ui.view.criteria.provider.CriterionSelectedIdLabelProvider;

public class CriteriaView extends ViewPart implements ICampaignsChangeListener {
	
	public static final String ID = "flintstones.element.ui.view.criteria"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.element.ui.view.criteria.criteria_view"; //$NON-NLS-1$
	
	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
	
	private TableViewer _tableViewer;
	private CriteriaSelectedContentProvider _provider;

	private static List<Criterion> _criteriaSelected;
	private static List<Button> _buttons;
	
	private List<Criterion> _criteriaBeforeSelected;
	
	private ProblemElementsSet _elementsSet;
	
	public CriteriaView() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_criteriaSelected = new LinkedList<Criterion>();
		_buttons = new LinkedList<Button>();
		
		_criteriaBeforeSelected = new LinkedList<Criterion>();
		
		_elementsSet.registerCampaignsChangesListener(this);
	}
	
	public static List<Button> getButtons() {
		return _buttons;
	}
	
	public static List<Criterion> getCriteriaSelected() {
		return _criteriaSelected;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		_tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		_tableViewer.getTable().setHeaderVisible(true);
		_tableViewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 23;	
			}
		});
		
		_tableViewer.getTable().addListener(SWT.Selection, new Listener() {
	        @Override
	        public void handleEvent(Event event) {
	        	_tableViewer.getTable().deselectAll();
	        }
	    });
		
		_tableViewer.getTable().addListener(SWT.Paint, new Listener() {
			@Override
			public void handleEvent(Event event) {
				_tableViewer.getTable().layout();
			}
		});
		
		_provider = new CriteriaSelectedContentProvider(_tableViewer);
		_tableViewer.setContentProvider(_provider);
		
		addColumns();
		hookContextMenu();
		hookFocusListener();
		
		_tableViewer.setInput(_provider.getInput());
		_provider.pack();
		getSite().setSelectionProvider(_tableViewer);
	}

	private void addColumns() {
		TableViewerColumn tvc = new TableViewerColumn(_tableViewer, SWT.NONE);
		tvc.setLabelProvider(new CriterionSelectedIdLabelProvider());
		TableColumn tc = tvc.getColumn();
		tc.setText("Index");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tc = tvc.getColumn();
		tc.setText("Selection");
		tc.pack();
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
					button.setData("criterion", (Criterion) item.getData()); 
					buttons.put(cell.getElement(), button);
					if(_criteriaBeforeSelected.contains(item.getData())) {
						button.setSelection(true);
					} else {
						button.setSelection(false);
					}
					_buttons.add(button);
					
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if(((Button) e.widget).getSelection()) {
								_criteriaSelected.add((Criterion) button.getData("criterion"));
							} else {
								if(!_criteriaSelected.isEmpty()) {
									_criteriaSelected.remove((Criterion) button.getData("criterion"));
								}
							}
							Collections.sort(_criteriaSelected);
							
							_elementsSet.notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.CRITERIA_SELECTED_CHANGES, null, _criteriaSelected, false));
						}
					});
				}

				TableEditor editor = new TableEditor(item.getParent());
				button.pack();
				editor.minimumWidth = button.getSize().x;
				editor.horizontalAlignment = SWT.CENTER;
				editor.setEditor(button, item, cell.getColumnIndex());
				editor.layout();
				button.setData("editor", editor);
				
				checkMatchingCriteria(item);
			}

			private void checkMatchingCriteria(TableItem item) {
				List<Criterion> allCriteria = _elementsSet.getCriteria();
				
				Map<Criterion, Button> criteriaButtons = new HashMap<Criterion, Button>();
				for(Button b: _buttons) {
					criteriaButtons.put((Criterion) b.getData("criterion"), b);
				}
				
				List<Campaign> campaignsSelected = CampaignsView.getCampaignsSelected();
				if(campaignsSelected.size() == 1) {
					Campaign campaignSelected = CampaignsView.getCampaignsSelected().get(0);
					for(Criterion c: allCriteria) {
						if(!campaignSelected.getCriteria().contains(c)) {
							if(c.equals(item.getData())) {
								item.setForeground(new Color(Display.getCurrent(), 211, 211, 211));
								criteriaButtons.get(c).setEnabled(false);
							}
						} else if(c.equals(item.getData())) {
							item.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
							criteriaButtons.get(c).setEnabled(true);
						}
					}
				} else if(campaignsSelected.size() > 1) {
					List<Criterion> allCriteriaCampaigns = new LinkedList<Criterion>();
					List<Criterion> dataCriteria = new LinkedList<Criterion>();
					int numCampaignsData = 0;
					for(Campaign c: campaignsSelected) {
						if(!c.isACampaignData()) {
							List<Criterion> criteria = c.getCriteria();
							for(Criterion cri: criteria) {
								allCriteriaCampaigns.add(cri);
							}
						} else {
							numCampaignsData++;
							List<Criterion> criteria = c.getCriteria();
							for(Criterion cri: criteria) {
								if(!dataCriteria.contains(cri)) {
									dataCriteria.add(cri);
								}
							}
						}
					}
					Map<Criterion, Integer> criteriaRepeat;
					criteriaRepeat = checkMatchingData(allCriteriaCampaigns, campaignsSelected.size() - numCampaignsData);
					for(Criterion dc: dataCriteria) {
						criteriaRepeat.put(dc, campaignsSelected.size() - numCampaignsData);
					}

					for(Criterion cri: _elementsSet.getCriteria()) {
						if(!criteriaRepeat.containsKey(cri)) {
							if(cri.equals(item.getData())) {
								item.setForeground(new Color(Display.getCurrent(), 211, 211, 211));
								criteriaButtons.get(cri).setEnabled(false);
							}
						} else {
							int rep = criteriaRepeat.get(cri);
							if((rep != campaignsSelected.size() - numCampaignsData)) {
								if(cri.equals(item.getData())) {
									item.setForeground(new Color(Display.getCurrent(), 211, 211, 211));
									criteriaButtons.get(cri).setEnabled(false);
								}
							} else if(cri.equals(item.getData())) {
								item.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
								criteriaButtons.get(cri).setEnabled(true);
							}
						}
					}
				}
			}
			
			private Map<Criterion, Integer> checkMatchingData(List<Criterion> allCriteriaCampaigns, int numCampaigns) {		
				Map<Criterion, Integer> criteriaRepeat = new LinkedHashMap<Criterion, Integer>();
				int numRep;
				for(int i = 0; i < allCriteriaCampaigns.size(); i++){
				    Criterion c1 = allCriteriaCampaigns.get(i);
				    numRep = 0;
				    for(int j = 0; j < allCriteriaCampaigns.size(); j++){
				    	Criterion c2 = allCriteriaCampaigns.get(j);
				        if(c1.equals(c2)) {
				            numRep++;
				            criteriaRepeat.put(c1, numRep);
				        }
				    }
				}
				return criteriaRepeat;
			}
		});
		
		class TypeLabelProvider extends OwnerDrawLabelProvider {

			@Override
			protected void measure(Event event, Object element) {}

			@Override
			protected void paint(Event event, Object element) {
				TableItem item = (TableItem) event.item;
				Criterion c = (Criterion) item.getData();
				Image type;
				
				if(c.isDirect()) {
					type = Images.Direct;
				} else {
					type = Images.User;
				}

				if (type != null) {
					Rectangle bounds = ((TableItem) event.item).getBounds(event.index);
					Rectangle imageBounds = type.getBounds();
					bounds.width /= 2;
					bounds.width -= imageBounds.width / 2;
					bounds.height /= 2;
					bounds.height -= imageBounds.height / 2;
					
					int x = bounds.width > 0 ? bounds.x + bounds.width: bounds.x;
					int y = bounds.height > 0 ? bounds.y + bounds.height: bounds.y;
			 		
					event.gc.drawImage(type, x, y);
				}
			}
		}
		
		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tvc.setLabelProvider(new TypeLabelProvider());
		tc = tvc.getColumn();
		tc.setText("Type");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tvc.setLabelProvider(new CriterionOperationLabelProvider());
		tc = tvc.getColumn();
		tc.setText("Operation");
		tc.setResizable(false);
		tc.pack();

	}
	
	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(_tableViewer.getTable());
		_tableViewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, _tableViewer);
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

	@Override
	public void setFocus() {
		_tableViewer.getControl().setFocus();
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
		_criteriaBeforeSelected.clear();
		
		List<Campaign> campaignsSelected = CampaignsView.getCampaignsSelected();
		for(Button b: _buttons) {
			if(!b.isDisposed()) {
				if(b.getSelection() && !campaignsSelected.isEmpty()) {
					_criteriaBeforeSelected.add((Criterion) b.getData("criterion"));
				}
				TableEditor editor = (TableEditor) b.getData("editor");
				editor.getEditor().dispose();
			}
		}
		_buttons.clear();
		
		if(campaignsSelected.isEmpty()) {
			_criteriaSelected.clear();
		}
	}
}
