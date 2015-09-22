package sinbad2.element.ui.view.mecs;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.wb.swt.SWTResourceManager;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.IAlternativesChangeListener;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ICampaignsChangeListener;
import sinbad2.element.mec.MEC;
import sinbad2.element.ui.Images;
import sinbad2.element.ui.view.campaigns.CampaignsView;
import sinbad2.element.ui.view.mecs.dialog.NewMeDialog;
import sinbad2.element.ui.view.mecs.jfreechart.MECChart;
import sinbad2.element.ui.view.mecs.provider.MECContentProvider;
import sinbad2.element.ui.view.mecs.provider.MECIdLabelProvider;

import org.eclipse.swt.widgets.Table;

public class MECView extends ViewPart implements ICampaignsChangeListener, IAlternativesChangeListener {

	public static final String ID = "flintstones.element.ui.view.mecs"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.element.ui.view.mecs.mecs_view"; //$NON-NLS-1$

	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);

	private TableViewer _tableViewer;
	private MECContentProvider _provider;

	private static List<Button> _buttons;
	private static MEC _mecSelected;
	private static int _chartType;

	private int _aggregationOption;

	private Composite _chartComposite;
	private Button _addFormulaButton;
	private Button _changeChartButton;
	private Button _changeAggregationButton;
	private Combo _changeAxisCombo;
	private List<TableItem> _tableItems;
	private MECChart _chart;

	private ProblemElementsSet _elementsSet;

	public MECView() {
		_buttons = new LinkedList<Button>();
		_tableItems = new LinkedList<TableItem>();
		_mecSelected = null;
		_chartType = 0;
		_aggregationOption = 0;

		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();

		_elementsSet.registerCampaignsChangesListener(this);
		_elementsSet.registerAlternativesChangesListener(this);
	}
	
	public static List<Button> getButtons() {
		return _buttons;
	}

	public static MEC getMECSelected() {
		return _mecSelected;
	}

	public static int getChartType() {
		return _chartType;
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout(1, false);
		layout.marginBottom = 5;
		layout.marginLeft = 5;
		layout.marginRight = 5;
		layout.marginTop = 5;
		container.setLayout(layout);
		container.setBackground(new Color(Display.getCurrent(), 255, 255, 255));

		Label tableLabel = new Label(container, SWT.NULL);
		tableLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		tableLabel.setText("Exposure measurements");
		tableLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$
		tableLabel.setBackground(new Color(Display.getCurrent(), 255, 255, 255));

		_tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = _tableViewer.getTable();
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.heightHint = 222;
		table.setLayoutData(gd_table);
		
		_tableViewer.getTable().addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				_tableViewer.getTable().deselectAll();
			}
		});

		_tableViewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.height = 55;
			}
		});

		_tableViewer.getTable().addListener(SWT.MouseDown, new Listener() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleEvent(Event event) {
				
				_tableViewer.getTable().redraw();
				
				Point pt = new Point(event.x, event.y);
				TableItem item = _tableViewer.getTable().getItem(pt);
				if (item == null) {
					return;
				}
				
				_tableItems.add(item);
				
				List<Integer> coordinates = (List<Integer>) item.getData("coordinates");
				Point pCoordinates = new Point(coordinates.get(0), coordinates.get(1));
				Rectangle boundsImage = new Rectangle(pCoordinates.x, pCoordinates.y, 12, 12);
				
				if(!item.getForeground().equals(new Color(Display.getCurrent(), 211, 211, 211))) {
					if(boundsImage.contains(pt)) {
						if(item.getData("check") == null || !(boolean) item.getData("check")) {
							item.setData("check", true);
							_addFormulaButton.setEnabled(true);
							List<TableItem> tableItemsToRemove = new LinkedList<TableItem>();
							for(TableItem ti: _tableItems) {
								if(!ti.equals(item)) {
									if(!ti.isDisposed()) {
										ti.setData("check", false);
									} else {
										tableItemsToRemove.add(ti);
									}
								}
							}
							_tableItems.removeAll(tableItemsToRemove);
						} else if((boolean) item.getData("check")) {
							item.setData("check", false);
						}
					}
					
					for (int i = 0; i < _tableViewer.getTable().getColumnCount(); i++) {
						Rectangle rect = item.getBounds(i);
						if (rect.contains(pt)) {
							if (i == 2) {
								if(boundsImage.contains(pt)) {
									if ((boolean) item.getData("check")) {
										_mecSelected = (MEC) item.getData();
										List<Campaign> campaignsSelected = CampaignsView.getCampaignsSelected();
										if(!campaignsSelected.isEmpty()) {
											checkOptions(campaignsSelected);
										}
									} else {
										_mecSelected = null;
										_chart.clear();
									}
								}
							}
						}
					}
					_changeChartButton.setEnabled(true);
				}
			}
		});

		_addFormulaButton = new Button(container, SWT.NULL);
		_addFormulaButton.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1));
		_addFormulaButton.setText("Add");
		_addFormulaButton.setEnabled(false);
		_addFormulaButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD).createImage());
		_addFormulaButton.setBackground(new Color(Display.getCurrent(), 255, 255, 255));

		_addFormulaButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewMeDialog dialog = new NewMeDialog();
				dialog.open();
			}
		});

		_chartComposite = new Composite(container, SWT.BORDER);
		GridData gd_chartComposite = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_chartComposite.heightHint = 300;
		_chartComposite.setLayoutData(gd_chartComposite);
		_chartComposite.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		_chart = new MECChart();
		_chart.initializeLineChart(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.CENTER);
		_chart.initializeBarChart(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.CENTER);

		_chartComposite.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				_chart.setSize(_chartComposite.getSize().x, _chartComposite.getSize().y);
			}
		});

		_tableViewer.getTable().setHeaderVisible(true);
		_provider = new MECContentProvider(_tableViewer);
		_tableViewer.setContentProvider(_provider);

		_tableViewer.setInput(_provider.getInput());
		getSite().setSelectionProvider(_tableViewer);

		addColumns();
		hookFocusListener();

		Composite buttonsChartComposite = new Composite(container, SWT.NONE);
		layout = new GridLayout(3, false);
		buttonsChartComposite.setLayout(layout);
		GridData gd_buttonsChartComposite = new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1);
		buttonsChartComposite.setLayoutData(gd_buttonsChartComposite);
		buttonsChartComposite.setBackground(new Color(Display.getCurrent(), 255, 255, 255));

		_changeChartButton = new Button(buttonsChartComposite, SWT.NULL);
		_changeChartButton.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1));
		_changeChartButton.setEnabled(false);
		_changeChartButton.setImage(Images.LineChart);
		_changeChartButton.setBackground(new Color(Display.getCurrent(), 255, 255, 255));

		_changeChartButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<Campaign> campaigns = CampaignsView.getCampaignsSelected();
				if (_chartType == 0) {
					_changeChartButton.setImage(Images.BarChart);
					_chart.initializeLineChart(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.CENTER);
					_chartType = 1;
					_changeAggregationButton.setEnabled(false);
					if(campaigns.size() > 1) {
						_changeAxisCombo.setEnabled(true);
					}
				} else {
					_changeChartButton.setImage(Images.LineChart);
					_chart.initializeBarChart(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.CENTER);
					_chartType = 0;
					if(campaigns.size() > 1) {
						_changeAggregationButton.setEnabled(true);
						_changeAxisCombo.setEnabled(false);
					}
				}
				
				checkOptions(campaigns);
			}
		});

		_changeAggregationButton = new Button(buttonsChartComposite, SWT.NULL);
		_changeAggregationButton.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1));
		_changeAggregationButton.setEnabled(false);
		_changeAggregationButton.setImage(Images.No_aggregation);
		_changeAggregationButton.setBackground(new Color(Display.getCurrent(), 255, 255, 255));

		_changeAggregationButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {			
				List<Campaign> campaigns = CampaignsView.getCampaignsSelected();
				if (_aggregationOption == 0) {
					_changeAggregationButton.setImage(Images.Aggregation);
					_aggregationOption = 1;
					_changeAxisCombo.setEnabled(true);
					
					if(_chartType == 0) {
						_changeChartButton.setEnabled(false);
					}
					
				} else {
					_changeAggregationButton.setImage(Images.No_aggregation);
					_aggregationOption = 0;
					_changeAxisCombo.setEnabled(false);
					
					if(_chartType == 0) {
						_changeChartButton.setEnabled(true);
					}
					
				}
				checkOptions(campaigns);
			}

		});

		_changeAxisCombo = new Combo(buttonsChartComposite, SWT.NULL);
		_changeAxisCombo.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1));
		_changeAxisCombo.setEnabled(false);
		_changeAxisCombo.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		_changeAxisCombo.add("Campaigns");
		_changeAxisCombo.add("Provinces");
		_changeAxisCombo.add("Contexts");
		_changeAxisCombo.select(0);

		_changeAxisCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				checkOptions(CampaignsView.getCampaignsSelected());
			}
		});
	}

	private void addColumns() {
		TableViewerColumn tvc = new TableViewerColumn(_tableViewer, SWT.NONE);
		tvc.setLabelProvider(new MECIdLabelProvider());
		TableColumn tc = tvc.getColumn();
		tc.setText("ME");
		tc.setResizable(true);
		tc.pack();

		class FormulaLabelProvider extends OwnerDrawLabelProvider {

			@Override
			protected void measure(Event event, Object element) {}

			@Override
			protected void paint(Event event, Object element) {
				TableItem item = (TableItem) event.item;
				MEC mec = (MEC) item.getData();
				Image formula = mec.getFormula();

				if (formula != null) {
					int x = event.x + event.width + 2;
			 		int itemHeight = _tableViewer.getTable().getItemHeight();
			 		int imageHeight = formula.getBounds().height;
			 		int y = event.y + (itemHeight - imageHeight) / 2;
			 		
					event.gc.drawImage(formula, x, y);
				}
			}

			@Override
			public void update(ViewerCell cell) {
				super.update(cell);

				TableItem item = (TableItem) cell.getItem();
				MEC mec = (MEC) item.getData();
				Image formula = mec.getFormula();

				if (formula != null) {
					if (_tableViewer.getTable().getColumn(1).getWidth() < formula.getImageData().width + 15) {
						_tableViewer.getTable().getColumn(1).setWidth(formula.getImageData().width + 15);
					}
				}
			}
		}

		tvc = new TableViewerColumn(_tableViewer, SWT.NONE);
		tvc.setLabelProvider(new FormulaLabelProvider());
		tc = tvc.getColumn();
		tc.setText("Formula");
		tc.setResizable(false);
		tc.pack();
		
		abstract class SelectionLabelProvider extends OwnerDrawLabelProvider {

			@Override
			protected void measure(Event event, Object element) {}

			@Override
			protected void paint(Event event, Object element) {
				Image radioButton = getImage(element);
				List<Integer> coordinates = new LinkedList<Integer>();

				if (radioButton != null) {
					Rectangle bounds = ((TableItem) event.item).getBounds(event.index);
					Rectangle imageBounds = radioButton.getBounds();
					bounds.width /= 2;
					bounds.width -= imageBounds.width / 2;
					bounds.height /= 2;
					bounds.height -= imageBounds.height / 2;
					
					int x = bounds.width > 0 ? bounds.x + bounds.width: bounds.x;
					int y = bounds.height > 0 ? bounds.y + bounds.height: bounds.y;
					
					coordinates.add(x);
			 		coordinates.add(y);
			 		event.item.setData("coordinates", coordinates);
					
					event.gc.drawImage(radioButton, x, y);
				}
			}
			
			protected abstract Image getImage(Object element);
		}

		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tc = tvc.getColumn();
		tc.setText("Selection");
		tc.setResizable(false);
		tc.pack();
		tvc.setLabelProvider(new SelectionLabelProvider() {
			@Override
			public Image getImage(Object element) {
				if (_mecSelected == null) {
					return Images.RadioButtonUnselected;
				} else if (_mecSelected.equals((MEC) element)) {
					return Images.RadioButtonSelected;
				} else {
					return Images.RadioButtonUnselected;
				}
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

	@Override
	public void dispose() {
		_elementsSet.unregisterCampaignsChangeListener(this);
		_elementsSet.unregisterAlternativesChangeListener(this);
	}

	@Override
	public void setFocus() {
		_tableViewer.getControl().setFocus();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void notifyCampaignsChange(CampaignsChangeEvent event) {
		List<Campaign> campaignsSelected = (List<Campaign>) event.getNewValue();
		switch (event.getChange()) {
		case CAMPAIGNS_SELECTED_CHANGES:
			if(!campaignsSelected.isEmpty() && _mecSelected != null) {
				 checkOptions(campaignsSelected);
			} else if(!campaignsSelected.isEmpty() && _mecSelected == null){
				_addFormulaButton.setEnabled(true);
			} else {
				clearView(campaignsSelected);
			}
			break;
		case REMOVE_CAMPAIGNS_SELECTED:
			clearView(campaignsSelected);
			break;
		default:
			break;
		}
	}

	private void clearView(List<Campaign> campaignsSelected) {
		_tableViewer.getTable().removeAll();
		_mecSelected = null;
		_chart.clear();	
		_addFormulaButton.setEnabled(false);
		_changeChartButton.setEnabled(false);
		_changeChartButton.setImage(Images.LineChart);
		_changeAggregationButton.setEnabled(false);
		_changeAxisCombo.setEnabled(false);
		_changeAxisCombo.select(0);
		if(campaignsSelected.isEmpty() && _chartType == 1) {
			_chartType = 0;
			_changeAggregationButton.setImage(Images.No_aggregation);
			_chart.initializeBarChart(_chartComposite, _chartComposite.getSize().x, _chartComposite.getSize().y, SWT.CENTER);
		}
	}
	
	@Override
	public void notifyAlternativesChange(AlternativesChangeEvent event) {
		switch(event.getChange()) {
			case ALTERNATIVES_SELECTED_CHANGES:			
				List<Campaign> campaignsSelected = CampaignsView.getCampaignsSelected();
				if(_mecSelected != null) {
					checkOptions(campaignsSelected);
				}
				break;
			default:
				break;
		}
	}
	
	private void checkOptions(List<Campaign> campaignsSelected) {
		_addFormulaButton.setEnabled(true);
		if(campaignsSelected.size() == 1 || (campaignsSelected.size() == 2 && (campaignsSelected.get(0).isACampaignData() || campaignsSelected.get(1).isACampaignData()))) {
			if(_chartType == 0) {
				_changeAxisCombo.setEnabled(true);
				_changeChartButton.setEnabled(false);
				if(_changeAggregationButton.isEnabled()) {
					_changeAggregationButton.setEnabled(false);
					if(_mecSelected != null) {
						_chartType = 0;
						_changeAggregationButton.setImage(Images.No_aggregation);
						Map<Campaign, MEC> campaign = new LinkedHashMap<Campaign, MEC>();
						campaign.put(campaignsSelected.get(0), _mecSelected);
						_chart.setMEC(campaign, _chartType, "combine");
					}
				}
			} else {
				_changeAxisCombo.setEnabled(false);
				_changeAxisCombo.select(0);
				if(_mecSelected != null) {
					_chartType = 0;
					_changeAggregationButton.setImage(Images.No_aggregation);
					Map<Campaign, MEC> campaign = new LinkedHashMap<Campaign, MEC>();
					campaign.put(campaignsSelected.get(0), _mecSelected);
					_chart.setMEC(campaign, _chartType, "combine");
				}
			}
			
			if(_chartType == 0 || _chartType == 1) { //Gráfico de barras o temporal
				if(_aggregationOption == 0) { //Agregada
					if(_changeAxisCombo.getSelectionIndex() == 0) { //Por campañas o por contextos
						combineCampaigns(campaignsSelected);
					} else if(_changeAxisCombo.getSelectionIndex() == 1) {
						combineCampaignsProvinces(campaignsSelected);
					} else if(_changeAxisCombo.getSelectionIndex() == 2) {
						separateCampaigns(campaignsSelected);
					}
				}
			}
		} else {
			if(_chartType == 0) { //Gráfico de barras
				if(_aggregationOption == 0) { //Agregada
					if(_changeAxisCombo.getSelectionIndex() == 0 || _changeAxisCombo.getSelectionIndex() == 2) { //Por campañas o por contextos
						combineCampaigns(campaignsSelected);
					} else if(_changeAxisCombo.getSelectionIndex() == 1) {
						combineCampaignsProvinces(campaignsSelected);
					}
				} else { //Desagregada
					separateCampaigns(campaignsSelected);
				}
				_changeAggregationButton.setEnabled(true);
			} else { //Gráfico de líneas
				if((_aggregationOption == 0)) {
					if(_changeAxisCombo.getSelectionIndex() == 0) { //Por campañas o por contextos
						combineCampaigns(campaignsSelected);
					} else if(_changeAxisCombo.getSelectionIndex() == 1 || _changeAxisCombo.getSelectionIndex() == 2) {
						separateCampaigns(campaignsSelected);
					}
				} 
			}
		}
	}

	private void combineCampaigns(List<Campaign> campaigns) {
		Map<Campaign, MEC> campaignsAndMecs = new LinkedHashMap<Campaign, MEC>();
		for(Campaign c: campaigns) {
			Campaign clone = (Campaign) c.clone();
			clone.setName(c.getId() + "_" + c.getName() + "(" + c.getDate() + ")");
			campaignsAndMecs.put(clone, _mecSelected);
		}
		_chart.setMEC(campaignsAndMecs, _chartType, "combine");
	}
	
	private void combineCampaignsProvinces(List<Campaign> campaigns) {
		Map<Campaign, MEC> campaignsAndMecs = new LinkedHashMap<Campaign, MEC>();
		for(Campaign c: campaigns) {
			Campaign clone = (Campaign) c.clone();
			clone.setName(clone.getProvince());
			campaignsAndMecs.put(clone, _mecSelected);
		}
		_chart.setMEC(campaignsAndMecs, _chartType, "combine");
	}

	private void separateCampaigns(List<Campaign> campaigns) {
		Map<Campaign, MEC> campaignsAndMecs = new LinkedHashMap<Campaign, MEC>();
		for (Campaign c : campaigns) {
			Campaign clone = (Campaign) c.clone();
			clone.setName(c.getId() + "_" + c.getName() + "(" + c.getDate() + ")");
			campaignsAndMecs.put(clone, _mecSelected);
		}
		
		if(_changeAxisCombo.getSelectionIndex() != 2) {
			if(_changeAxisCombo.getSelectionIndex() == 1) {
				_chart.setMEC(campaignsAndMecs, _chartType, "separate_provinces");
			} else {
			_chart.setMEC(campaignsAndMecs, _chartType, "separate");
			}
		} else {
			_chart.setMEC(campaignsAndMecs, _chartType, "contexts");
		}
	}
}
