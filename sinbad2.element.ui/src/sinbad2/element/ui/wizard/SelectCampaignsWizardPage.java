package sinbad2.element.ui.wizard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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

import sinbad2.element.campaigns.Campaign;
import sinbad2.element.ui.view.campaigns.CampaignsView;
import sinbad2.element.ui.view.campaigns.provider.CampaignFinalDateLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignIdLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignInitialDateLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignProvinceLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignsWizardContentProvider;

public class SelectCampaignsWizardPage extends WizardPage {
	
	private TableViewer _tableViewerCampaigns;
	private CampaignsWizardContentProvider _provider;
	
	private Button _aggregateButton;
	private Button _desaggregateButton;
	private Button _campaignsButton;
	private Button _provincesButton;
	private Button _contextsButton;
	
	private static List<Campaign> _campaignsSelected;
	private static int _aggregationSelected;
	private static List<String> _desaggregationOption;

	protected SelectCampaignsWizardPage() {
		super("Select campaigns");
		setDescription("Select the campaigns you want");
		
		_campaignsSelected = CampaignsView.getCampaignsSelected();
		_desaggregationOption = new LinkedList<String>();
	}
	
	public static List<Campaign> getInformationCampaigns() {
		return _campaignsSelected;
	}
	
	public static int getInformationAggregation() {
		return _aggregationSelected;
	}
	
	public static List<String> getInformationDesaggregationOption() {
		return _desaggregationOption;
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
		_aggregateButton = new Button(containerAggregation, SWT.RADIO);
		_aggregateButton.setSelection(false);
		
		Label desaggregationLabel = new Label(containerAggregation, SWT.LEFT);
		desaggregationLabel.setText("Desaggregate");
		_desaggregateButton = new Button(containerAggregation, SWT.RADIO);
		_desaggregateButton.setSelection(false);
		
		Composite containerDesaggregationOptions = new Composite(campaigns, SWT.CENTER);
		layout = new GridLayout(2, false);
		layout.marginLeft = 100;
		containerDesaggregationOptions.setLayout(layout);
		
		final Label labelCampaigns = new Label(containerDesaggregationOptions, SWT.LEFT);
		labelCampaigns.setText("- By Campaigns");
		labelCampaigns.setVisible(false);
		_campaignsButton = new Button(containerDesaggregationOptions, SWT.CHECK);
		_campaignsButton.setVisible(false);
		_campaignsButton.setSelection(false);
		_campaignsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});
		final Label labelProvinces = new Label(containerDesaggregationOptions, SWT.LEFT);
		labelProvinces.setVisible(false);
		labelProvinces.setText("- By Provinces");
		_provincesButton = new Button(containerDesaggregationOptions, SWT.CHECK);
		_provincesButton.setVisible(false);
		_provincesButton.setSelection(false);
		_provincesButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});
		final Label labelContexts = new Label(containerDesaggregationOptions, SWT.LEFT);
		labelContexts.setVisible(false);
		labelContexts.setText("- By Contexts");
		_contextsButton = new Button(containerDesaggregationOptions, SWT.CHECK);
		_contextsButton.setSelection(false);
		_contextsButton.setVisible(false);
		_contextsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				validate();
			}
		});
		
		_aggregateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_aggregationSelected = 0;
				
				labelCampaigns.setVisible(false);
				_campaignsButton.setVisible(false);
				labelProvinces.setVisible(false);
				_provincesButton.setVisible(false);
				labelContexts.setVisible(false);
				_contextsButton.setVisible(false);
				
				validate();
			}
		});
		
		_desaggregateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_aggregationSelected = 1;
				
				labelCampaigns.setVisible(true);
				_campaignsButton.setVisible(true);
				labelProvinces.setVisible(true);
				_provincesButton.setVisible(true);
				labelContexts.setVisible(true);
				_contextsButton.setVisible(true);
			}
		});
		
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
		
		tvc = new TableViewerColumn(_tableViewerCampaigns, SWT.LEFT);
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
	}
	
	private void validate() {
		if(_aggregateButton.getSelection()) {
			setPageComplete(!_campaignsSelected.isEmpty());
		} else {
			if(_campaignsButton.getSelection()) {
				if(!_desaggregationOption.contains("separate")) {
					_desaggregationOption.add("separate");
				}
			} else {
				_desaggregationOption.remove("separate");
			}
			
			if(_provincesButton.getSelection()) {
				if(!_desaggregationOption.contains("separate_provinces")) {
					_desaggregationOption.add("separate_provinces");
				}
			} else {
				_desaggregationOption.remove("separate_provinces");
			}
			
			if(_contextsButton.getSelection()) {
				if(!_desaggregationOption.contains("contexts")) {
					_desaggregationOption.add("contexts");
				}
			} else {
				_desaggregationOption.remove("contexts");
			}
			setPageComplete((_campaignsButton.getSelection() || _provincesButton.getSelection() || _contextsButton.getSelection()) && !_campaignsSelected.isEmpty());
		}
	}
	
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		_provider.pack();
	}
}

