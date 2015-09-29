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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import sinbad2.element.campaigns.Campaign;
import sinbad2.element.ui.view.campaigns.provider.CampaignFinalDateLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignIdLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignInitialDateLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignProvinceLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignsSelectedContentProvider;

public class SelectCampaignsWizardPage extends WizardPage {
	
	private TableViewer _tableViewer;
	private CampaignsSelectedContentProvider _provider;
	
	private List<Campaign> _campaignsSelected;

	protected SelectCampaignsWizardPage() {
		super("Select campaigns");
		setDescription("Select the campaigns you want");
		
		_campaignsSelected = new LinkedList<Campaign>();
	}

	@Override
	public void createControl(Composite parent) {
		Composite campaigns = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		campaigns.setLayoutData(gridData);
		campaigns.setLayout(layout);
		campaigns.setBackground(new Color(Display.getCurrent(), 255, 255, 255));	
		
		_tableViewer = new TableViewer(campaigns, SWT.CENTER | SWT.BORDER | SWT.FULL_SELECTION);
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
		
		_tableViewer.setInput(_provider.getInput());
		
		setControl(campaigns);
		setPageComplete(false);
	}
	
	private void addColumns() {
		TableViewerColumn tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
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
				_tableViewer.refresh();
			}
		});
		tvc.setLabelProvider(new CampaignIdLabelProvider());
		TableColumn tc = tvc.getColumn();
		tc.setText("Campaign");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
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
				_tableViewer.refresh();
			}
		});
		tvc.setLabelProvider(new CampaignProvinceLabelProvider());
		tc = tvc.getColumn();
		tc.setText("Province");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
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
				_tableViewer.refresh();
			}
		});
		tvc.setLabelProvider(new CampaignInitialDateLabelProvider());
		tc = tvc.getColumn();
		tc.setText("Initial date");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
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
				_tableViewer.refresh();
			}
		});
		tvc.setLabelProvider(new CampaignFinalDateLabelProvider());
		tc = tvc.getColumn();
		tc.setText("Final date");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tc = tvc.getColumn();
		tc.setText("Selection");
		tc.setResizable(false);
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
					button.setData("campaign", (Campaign) item.getData()); 
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if(((Button) e.widget).getSelection()) {
								if(!_campaignsSelected.contains((Campaign) button.getData("campaign"))) {
									_campaignsSelected.add((Campaign) button.getData("campaign"));
								}
							} else {
								_campaignsSelected.remove((Campaign) button.getData("campaign"));
							}
						}
					});
					buttons.put(cell.getElement(), button);				
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
}
