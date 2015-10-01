package sinbad2.element.ui.wizard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import sinbad2.element.campaigns.Campaign;
import sinbad2.element.ui.view.campaigns.provider.CampaignFinalDateLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignIdLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignInitialDateLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignProvinceLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignsWizardContentProvider;

public class SelectCampaignsWizardPage extends WizardPage {
	
	private TableViewer _tableViewerCampaigns;
	private CampaignsWizardContentProvider _provider;
	
	private static List<Campaign> _campaignsSelected;
	private static int _aggregationSelected;

	protected SelectCampaignsWizardPage() {
		super("Select campaigns");
		setDescription("Select the campaigns you want");
		
		_campaignsSelected = new LinkedList<Campaign>();
	}
	
	public static List<Campaign> getInformationCampaigns() {
		return _campaignsSelected;
	}
	
	public static int getInformationAggregation() {
		return _aggregationSelected;
	}

	@Override
	public void createControl(Composite parent) {
		Composite campaigns = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		campaigns.setLayoutData(gridData);
		campaigns.setLayout(layout);
		
		_tableViewerCampaigns = new TableViewer(campaigns, SWT.CENTER | SWT.BORDER | SWT.FULL_SELECTION);
		_tableViewerCampaigns.getTable().addListener(SWT.Selection, new Listener() {
	        @Override
	        public void handleEvent(Event event) {
	        	_tableViewerCampaigns.getTable().deselectAll();
	        }
	    });
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_tableViewerCampaigns.getTable().setLayoutData(gridData);
		
		_provider = new CampaignsWizardContentProvider(_tableViewerCampaigns);
		_tableViewerCampaigns.setContentProvider(_provider);
		_tableViewerCampaigns.getTable().setHeaderVisible(true);
		
		_tableViewerCampaigns.getTable().addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.height = 25;
			}
		});
		_tableViewerCampaigns.getTable().addListener(SWT.Paint, new Listener() {
			@Override
			public void handleEvent(Event event) {
				_tableViewerCampaigns.getTable().layout();
			}
		});
		
		addColumns();
		
		_tableViewerCampaigns.setInput(_provider.getInput());
		
		Composite containerAggregation = new Composite(campaigns, SWT.CENTER);
		layout = new GridLayout(4, false);
		containerAggregation.setLayout(layout);
		Label aggregationLabel = new Label(containerAggregation, SWT.LEFT);
		aggregationLabel.setText("Aggregate");
		Button aggregateButton = new Button(containerAggregation, SWT.RADIO);
		aggregateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_aggregationSelected = 0;
			}
		});
		aggregateButton.setSelection(false);
		
		Label desaggregationLabel = new Label(containerAggregation, SWT.LEFT);
		desaggregationLabel.setText("Desaggregate");
		Button desaggregateButton = new Button(containerAggregation, SWT.RADIO);
		desaggregateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_aggregationSelected = 1;
			}
		});
		desaggregateButton.setSelection(false);
		
		setControl(campaigns);
		setPageComplete(false);
		
		_provider.pack();
	}
	
	private void addColumns() {
		TableViewerColumn tvc = new TableViewerColumn(_tableViewerCampaigns, SWT.CENTER);
		tvc.getColumn().addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				Comparator<Campaign> comparatorByName = new Comparator<Campaign>() {
					@Override
					public int compare(Campaign c1, Campaign c2) {
				        return c1.getName().compareTo(c2.getName());
				    }
				};
				Collections.sort((List<Campaign>) _provider.getInput(), comparatorByName);
				_tableViewerCampaigns.refresh();
			}
		});
		tvc.setLabelProvider(new CampaignIdLabelProvider());
		TableColumn tc = tvc.getColumn();
		tc.setText("Campaign");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewerCampaigns, SWT.CENTER);
		tvc.getColumn().addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				Comparator<Campaign> comparatorByProvinces = new Comparator<Campaign>() {
					@Override
					public int compare(Campaign c1, Campaign c2) {
				        return c1.getProvince().compareTo(c2.getProvince());
				    }
				};
				Collections.sort((List<Campaign>) _provider.getInput(), comparatorByProvinces);
				_tableViewerCampaigns.refresh();
			}
		});
		tvc.setLabelProvider(new CampaignProvinceLabelProvider());
		tc = tvc.getColumn();
		tc.setText("Province");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewerCampaigns, SWT.CENTER);
		tvc.getColumn().addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				Comparator<Campaign> comparatorByDate = new Comparator<Campaign>() {	
					@Override
					public int compare(Campaign c1, Campaign c2) {
						SimpleDateFormat formatter = new SimpleDateFormat("MM/yy");
				    	Date date1 = null, date2 = null;
				    	try {
				    		date1 = formatter.parse(c1.getInitialDate());
				    		date2 = formatter.parse(c2.getInitialDate());
				    	} catch (ParseException e) {
				    		e.printStackTrace();
				    	}
				        return date1.compareTo(date2);
					}
				};
				Collections.sort((List<Campaign>) _provider.getInput(), comparatorByDate);
				_tableViewerCampaigns.refresh();
			}
		});
		tvc.setLabelProvider(new CampaignInitialDateLabelProvider());
		tc = tvc.getColumn();
		tc.setText("Initial date");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewerCampaigns, SWT.CENTER);
		tvc.getColumn().addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("unchecked")
			@Override
			public void widgetSelected(SelectionEvent e) {
				Comparator<Campaign> comparatorByDate = new Comparator<Campaign>() {	
					@Override
					public int compare(Campaign c1, Campaign c2) {
						SimpleDateFormat formatter = new SimpleDateFormat("MM/yy");
				    	Date date1 = null, date2 = null;
				    	try {
				    		date1 = formatter.parse(c1.getFinalDate());
				    		date2 = formatter.parse(c2.getFinalDate());
				    	} catch (ParseException e) {
				    		e.printStackTrace();
				    	}
				        return date1.compareTo(date2);
					}
				};
				Collections.sort((List<Campaign>) _provider.getInput(), comparatorByDate);
				_tableViewerCampaigns.refresh();
			}
		});
		tvc.setLabelProvider(new CampaignFinalDateLabelProvider());
		tc = tvc.getColumn();
		tc.setText("Final date");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewerCampaigns, SWT.CENTER);
		tc = tvc.getColumn();
		tc.setText("Selection");
		tc.pack();
		tvc.setLabelProvider(new ColumnLabelProvider() {
			Map<Object, Button> buttons = new HashMap<Object, Button>();

			@Override
			public void update(ViewerCell cell) {
				TableItem item = (TableItem) cell.getItem();

				final Button button;
				if (buttons.containsKey(cell.getElement())) {
					button = buttons.get(cell.getElement());
				} else {
					button = new Button((Composite) cell.getViewerRow().getControl(), SWT.CHECK);
					button.setData("campaign", (Campaign) item.getData()); 
					buttons.put(cell.getElement(), button);
					
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if(((Button) e.widget).getSelection()) {
								_campaignsSelected.add((Campaign) button.getData("campaign"));
							} else {
								if(!_campaignsSelected.isEmpty()) {
									_campaignsSelected.remove((Campaign) button.getData("campaign"));
								}
							}
							if(!_campaignsSelected.isEmpty()) {
								setPageComplete(true);
							} else {
								setPageComplete(false);
							}
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
			}
		});
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		_provider.pack();
	}
}

