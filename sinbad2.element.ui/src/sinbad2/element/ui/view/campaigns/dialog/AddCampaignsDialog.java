package sinbad2.element.ui.view.campaigns.dialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.EAlternativesChange;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ECampaignsChange;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ECriteriaChange;
import sinbad2.element.ui.view.alternatives.provider.AlternativeIdLabelProvider;
import sinbad2.element.ui.view.alternatives.provider.AlternativesContentProvider;
import sinbad2.element.ui.view.campaigns.CampaignsView;
import sinbad2.element.ui.view.campaigns.provider.CampaignDateLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignIdLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignProvinceLabelProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignsContentProvider;
import sinbad2.element.ui.view.campaigns.provider.CampaignsAddedContentProvider;
import sinbad2.element.ui.view.criteria.provider.CriteriaContentProvider;
import sinbad2.element.ui.view.criteria.provider.CriterionIdLabelProvider;


public class AddCampaignsDialog extends Dialog {
	
	private TableViewer _tableViewerCriteria;
	private CriteriaContentProvider _providerCriteria;
	private TableViewer _tableViewerAlternatives;
	private AlternativesContentProvider _providerAlternatives;
	private TableViewer _tableViewerCampaigns;
	private CampaignsContentProvider _providerCampaigns;
	private TableViewer _tableViewerCampaignsSelected;
	private CampaignsAddedContentProvider _providerCampaignsSelected;

	private static List<Campaign> _campaignsSelected;
	
	private Button _addCampaigns;
	private Button _removeCampaigns;
	private Button _okButton;
	private List<TableItem> _tableItemsCriteria;
	private List<TableItem> _tableItemsAlternatives;
	private Combo _provinces;
	private CDateTime _calendar;
	
	private List<Campaign> _campaignsToRemove;
	private List<Campaign> _campaignsAdded;
	
	private ProblemElementsSet _elementsSet;

	public AddCampaignsDialog() {
		super(Display.getCurrent().getActiveShell());
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
	
		_tableItemsCriteria = new LinkedList<TableItem>();
		_tableItemsAlternatives = new LinkedList<TableItem>();
		
		_campaignsAdded = new LinkedList<Campaign>();
		_campaignsToRemove = new LinkedList<Campaign>();
		_campaignsSelected = new LinkedList<Campaign>();
		
	}
	
	public static List<Campaign> getCampaignsSelected() {
		return _campaignsSelected;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		GridLayout gridLayoutCampaign = new GridLayout(2, false);
		gridLayoutCampaign.marginRight = 10;
		gridLayoutCampaign.marginTop = 10;
		gridLayoutCampaign.marginLeft = 10;
		gridLayoutCampaign.marginBottom = 10;
		container.setLayout(gridLayoutCampaign);
		
		Composite provCalendar = new Composite(container, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		provCalendar.setLayoutData(gridData);
		provCalendar.setLayout(layout);
		
		_provinces = new Combo(provCalendar, SWT.NONE);
		_provinces.add("Jaén");
		_provinces.add("Córdoba");
		_provinces.add("Sevilla");
		_provinces.add("Huelva");
		_provinces.add("Cádiz");
		_provinces.add("Granada");
		_provinces.add("Málaga");
		_provinces.add("Almería");
		
		_provinces.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> dataCampaigns = new LinkedList<String>();
				dataCampaigns.add(_provinces.getItem(_provinces.getSelectionIndex()));
				
				if(_calendar.getText().contains("/")) {
					dataCampaigns.add(_calendar.getText());
				}
				
				_elementsSet.notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.SEARCH_CAMPAIGNS, null, dataCampaigns, false));
			}
		});
		
		_calendar = new CDateTime(provCalendar, CDT.BORDER | CDT.DROP_DOWN);
		_calendar.setFormat(CDT.DATE_SHORT);
		_calendar.setNullText("Choose date");
		_calendar.setToolTipText("Date");
		gridData = new GridData(SWT.LEFT, SWT.FILL, true, false, 1, 1);
		gridData.widthHint = 120;
		_calendar.setLayoutData(gridData);
		
		_calendar.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				List<String> dataCampaigns = new LinkedList<String>();
				dataCampaigns.add(_provinces.getItem(_provinces.getSelectionIndex()));
				
				if(_calendar.getText().contains("/")) {
					dataCampaigns.add(_calendar.getText());
				}
				
				_elementsSet.notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.SEARCH_CAMPAIGNS, null, dataCampaigns, false));
			}
		});
		
		new Label(container, SWT.NONE);
		
		_tableViewerCampaigns = new TableViewer(container,  SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
		
		_tableViewerCampaigns.getTable().addListener(SWT.Selection, new Listener() {
			
			public void handleEvent(Event e) {	
		    	_campaignsSelected.clear();
		         
		    	TableItem[] selection = _tableViewerCampaigns.getTable().getSelection();
	        	for(int i = 0; i < selection.length; ++i) {
	        		TableItem ti = selection[i];
	        		if(ti.getForeground().equals(new Color(Display.getCurrent(), 211, 211, 211))) {
	        			_tableViewerCampaigns.getTable().deselect(_tableViewerCampaigns.getTable().indexOf(ti));
	        		} else {
	        			_campaignsSelected.add((Campaign) ti.getData());
	        		}
	        	}
	        	
	        	if(selection.length == 1) {
	        		Campaign campaign = (Campaign) selection[selection.length - 1].getData();
	        		if(campaign.isACampaignData()) {
	        			_elementsSet.notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.CRITERIA_CHANGES, null, true, false));
	        			_elementsSet.notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.ALTERNATIVES_CHANGES, null, true, false));
	        		} else {
	        			_elementsSet.notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.CRITERIA_CHANGES, null, false, false));
	        			_elementsSet.notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.ALTERNATIVES_CHANGES, null, false, false));
	        		}
	        		selectAvailableDataCampaign(selection[selection.length - 1]);
	        	} else {
	        		boolean data = false;
	        		for(TableItem ti: selection) {
	        			if(((Campaign) ti.getData()).isACampaignData()) {
	        				data = true;
	        				break;
	        			}
	        		}
	        		if(data) {
	        			_elementsSet.notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.CRITERIA_CHANGES, null, _elementsSet.getCriteria(), false));
			        	_elementsSet.notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.ALTERNATIVES_CHANGES, null, _elementsSet.getAlternatives(), false));
	        		} else {
	        			_elementsSet.notifyCriteriaChanges(new CriteriaChangeEvent(ECriteriaChange.CRITERIA_CHANGES, null, false, false));
	        			_elementsSet.notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.ALTERNATIVES_CHANGES, null, false, false));
	        		}
	        		selectMatchingInformationAvailable(selection);
	        	}
	        	
	        	if(!_campaignsSelected.isEmpty()) {
	        		_addCampaigns.setEnabled(true);
	        	}
		     }

			private void selectAvailableDataCampaign(TableItem tableItem) {
				clearAvailableDataCampaign();
				
				Campaign campaignSelected = (Campaign) tableItem.getData();
				List<Criterion> criteria = campaignSelected.getCriteria();
				List<Criterion> allCriteria = _elementsSet.getCriteria();
				for(Criterion c1: allCriteria) {
					for(Criterion c2: criteria) {
						if(c1.equals(c2)) {
							for(TableItem ti: _tableItemsCriteria) {
								if(ti.getData().equals(c1)) {
									ti.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
								}
							}
						}
					}
				}
				List<Alternative> alternatives = campaignSelected.getAlternatives();
				List<Alternative> allAlternatives = _elementsSet.getAlternatives();
				for(Alternative a1: allAlternatives) {
					for(Alternative a2: alternatives) {
						if(a1.equals(a2)) {
							for(TableItem ti: _tableItemsAlternatives) {
								if(ti.getData().equals(a1)) {
									ti.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
								}
							}
						}
					}
				}			
			}
			
			private void clearAvailableDataCampaign() {
				List<TableItem> tableItemsCriteriaToRemove = new LinkedList<TableItem>();
				for(TableItem ti: _tableItemsCriteria) {
					if(!ti.isDisposed()) {
						ti.setForeground(new Color(Display.getCurrent(), 211, 211, 211));
					} else {
						tableItemsCriteriaToRemove.add(ti);
					}
				}
				_tableItemsCriteria.removeAll(tableItemsCriteriaToRemove);
				
				List<TableItem> tableItemsAlternativesToRemove = new LinkedList<TableItem>();
				for(TableItem ti: _tableItemsAlternatives) {
					if(!ti.isDisposed()) {
						ti.setForeground(new Color(Display.getCurrent(), 211, 211, 211));
					} else {
						tableItemsAlternativesToRemove.add(ti);
					}
				}
				_tableItemsAlternatives.removeAll(tableItemsAlternativesToRemove);
			}

			private void selectMatchingInformationAvailable(TableItem[] selection) {	
				clearAvailableDataCampaign();
				
				List<Criterion> directCriteria = new LinkedList<Criterion>();
				List<Criterion> allCriteriaCampaigns = new LinkedList<Criterion>();
				int numCampaignsData = 0;
				for(TableItem ti: selection) {
					Campaign c = (Campaign) ti.getData();
					if(!c.isACampaignData()) {
						List<Criterion> criteria = c.getCriteria();
						for(Criterion cri: criteria) {
							allCriteriaCampaigns.add(cri);
						}
					} else {
						numCampaignsData++;
						List<Criterion> criteria = c.getCriteria();
						for(Criterion cri: criteria) {
							if(!directCriteria.contains(cri)) {
								directCriteria.add(cri);
							}
						}
					}
				}
				List<Criterion> matchingCriterion;
				matchingCriterion = checkMatchingCriteria(allCriteriaCampaigns, selection.length - numCampaignsData);
				matchingCriterion.addAll(directCriteria);
				for(Criterion c1: matchingCriterion) {
					for(TableItem ti: _tableItemsCriteria) {
						if(c1.equals(ti.getData())) {
							if(c1.isDirect()) {
								ti.setForeground(new Color(Display.getCurrent(), 255, 0, 0));
							} else {
								ti.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
							}
						}
					}
				}
				
				List<Alternative> directAlternatives = new LinkedList<Alternative>();
				List<Alternative> allAlternativesCampaigns = new LinkedList<Alternative>();
				for(TableItem ti: selection) {
					Campaign c = (Campaign) ti.getData();
					if(!c.isACampaignData()) {
						List<Alternative> alternatives = c.getAlternatives();
						for(Alternative alt: alternatives) {
							allAlternativesCampaigns.add(alt);
						}
					} else {
						List<Alternative> alternatives = c.getAlternatives();
						for(Alternative alt: alternatives) {
							if(!directAlternatives.contains(alt)) {
								directAlternatives.add(alt);
							}
						}
					}
				}
				List<Alternative> matchingAlternatives;
				matchingAlternatives = checkMatchingAlternatives(allAlternativesCampaigns, selection.length - numCampaignsData);
				matchingAlternatives.addAll(directAlternatives);
				for(Alternative a1: matchingAlternatives) {
					for(TableItem ti: _tableItemsAlternatives) {
						if(a1.equals(ti.getData())) {
							if(a1.isDirect()) {
								ti.setForeground(new Color(Display.getCurrent(), 255, 0, 0));
							} else {
								ti.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
							}
						} 
					}
				}	
			}
			
			private List<Criterion> checkMatchingCriteria(List<Criterion> criteria, int numCampaigns) {
				List<Criterion> result = new LinkedList<Criterion>();
				int numRep;
				for(int i = 0; i < criteria.size(); i++){
				    Criterion c1 = criteria.get(i);
				    numRep = 1;
				    if(numRep == numCampaigns) {
				    	result.add(c1);
				    } else {
					    for(int j = i+1; j < criteria.size(); j++){
					    	Criterion c2 = criteria.get(j);
					        if(c1.equals(c2)){
					        	numRep++;
					            if(numRep == numCampaigns) {
					            	result.add(c1);
					            }
					        }
					    }
				    }
				}
				return result;
			}
			
			private List<Alternative> checkMatchingAlternatives(List<Alternative> alternatives, int numCampaigns) {
				List<Alternative> result = new LinkedList<Alternative>();
				int numRep;
				for(int i = 0; i < alternatives.size(); i++){
				    Alternative a1 = alternatives.get(i);
				    numRep = 1;
				    if(numRep == numCampaigns) {
				    	result.add(a1);
				    } else {
					    for(int j = i+1; j < alternatives.size(); j++){
					    	Alternative a2 = alternatives.get(j);
					        if(a1.equals(a2)){
					            numRep++;
					            if(numRep == numCampaigns) {
					            	result.add(a1);
					            }
					        }
					    }
				    }
				}
				return result;
			}
			
	      });
		
		_providerCampaigns = new CampaignsContentProvider(_tableViewerCampaigns);
		_tableViewerCampaigns.setContentProvider(_providerCampaigns);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		_tableViewerCampaigns.getTable().setLayoutData(gd_table);
		_tableViewerCampaigns.getTable().setHeaderVisible(true);
		
		_tableViewerCampaigns.getTable().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 18;
				
			}
		});
		
		addColumnsCampaigns();
		
		_tableViewerCampaigns.setInput(_providerCampaigns.getInput());
		_providerCampaigns.pack();
		
		Composite critAndAlt = new Composite(container, SWT.FILL);
		GridLayout gridLayoutCriteriaAndAlternatives = new GridLayout(1, false);
		critAndAlt.setLayout(gridLayoutCriteriaAndAlternatives);

		_tableViewerCriteria = new TableViewer(critAndAlt, SWT.BORDER | SWT.FULL_SELECTION);
		
		_providerCriteria = new CriteriaContentProvider(_tableViewerCriteria);
		_tableViewerCriteria.setContentProvider(_providerCriteria);
		gd_table = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_table.widthHint = 200;
		gd_table.heightHint = 100;
		_tableViewerCriteria.getTable().setLayoutData(gd_table);
		_tableViewerCriteria.getTable().setHeaderVisible(true);
	
		_tableViewerCriteria.getTable().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 23;
				
			}
		});
		
		addColumnsCriteria();
		
		_tableViewerCriteria.setInput(_providerCriteria.getInput());
		_providerCriteria.pack();
		
		_tableViewerAlternatives = new TableViewer(critAndAlt, SWT.BORDER | SWT.FULL_SELECTION);
		
		_providerAlternatives = new AlternativesContentProvider(_tableViewerAlternatives);
		_tableViewerAlternatives.setContentProvider(_providerAlternatives);
		gd_table = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_table.widthHint = 200;
		gd_table.heightHint = 100;
		_tableViewerAlternatives.getTable().setLayoutData(gd_table);
		_tableViewerAlternatives.getTable().setHeaderVisible(true);
		_tableViewerAlternatives.getTable().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 23;
				
			}
		});
		
		addColumnsAlternatives();
		
		_tableViewerAlternatives.setInput(_providerAlternatives.getInput());
		_providerAlternatives.pack();
	
		Composite buttonsContainer = new Composite(container, SWT.NONE);
		GridLayout buttonsGridLayout = new GridLayout(2, true);
		buttonsContainer.setLayout(buttonsGridLayout);
		GridData buttonsGridData = new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 1);
		buttonsContainer.setLayoutData(buttonsGridData);
		
		_addCampaigns = new Button(buttonsContainer, SWT.PUSH);
		GridData addGridData = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		_addCampaigns.setLayoutData(addGridData);
		_addCampaigns.setText("Add");
		_addCampaigns.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		_addCampaigns.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD).createImage());
		_addCampaigns.setEnabled(false);
		
		_addCampaigns.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_elementsSet.notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.ADD_CAMPAIGNS_SELECTED, null, _campaignsSelected, false));
				
				_campaignsAdded.addAll(_campaignsSelected);
	
				_okButton.setEnabled(true);
			}
		});
		
		_tableViewerCampaignsSelected = new TableViewer(container, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
		_tableViewerCampaignsSelected.getTable().addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				_campaignsToRemove.clear();
				
				TableItem[] selection = _tableViewerCampaignsSelected.getTable().getSelection();
	        	for(TableItem ti: selection) {
	        		_campaignsToRemove.add((Campaign) ti.getData());
	        	}
	        	
	        	_removeCampaigns.setEnabled(true);
			}
		});
		
		_providerCampaignsSelected = new CampaignsAddedContentProvider(_tableViewerCampaignsSelected);
		_tableViewerCampaignsSelected.setContentProvider(_providerCampaignsSelected);
		gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
		_tableViewerCampaignsSelected.getTable().setLayoutData(gd_table);
		_tableViewerCampaignsSelected.getTable().setHeaderVisible(true);
		
		_tableViewerCampaignsSelected.getTable().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 23;
				
			}
		});
		
		addColumnsCampaignsSelected();
		
		_tableViewerCampaignsSelected.setInput(_providerCampaignsSelected.getInput());
		
		_removeCampaigns = new Button(container, SWT.PUSH);
		GridData removeGridData = new GridData(SWT.LEFT, SWT.LEFT, false, false, 1, 1);
		_removeCampaigns.setLayoutData(removeGridData);
		_removeCampaigns.setText("Remove");
		_removeCampaigns.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		_removeCampaigns.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE).createImage());
		_removeCampaigns.setEnabled(false);
		_removeCampaigns.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_elementsSet.notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.REMOVE_CAMPAIGNS_SELECTED, null , _campaignsToRemove, false));

				_campaignsAdded.removeAll(_campaignsToRemove);
				if(_campaignsAdded.isEmpty()) {
					_okButton.setEnabled(false);
				}
				
				_removeCampaigns.setEnabled(false);
				
				_tableViewerCampaignsSelected.getTable().setFocus();
			}
		});
		
		return container;
	}
	
	private void addColumnsCampaigns() {
		TableViewerColumn tvc = new TableViewerColumn(_tableViewerCampaigns, SWT.CENTER);
		tvc.setLabelProvider(new CampaignIdLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				List<Campaign> _campaignsPreviouslyAdded = CampaignsView.getCampaignsPreviouslyAdded();
				if(!_campaignsPreviouslyAdded.isEmpty()) {
					TableItem ti = (TableItem) cell.getItem();
					for(Campaign c: _campaignsPreviouslyAdded) {
						if(ti.getData().equals(c)) {
							ti.setForeground(new Color(Display.getCurrent(), 211, 211, 211));
						}
					}
				}
			}
		});
		TableColumn tc = tvc.getColumn();
		tc.setText("Campaign");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewerCampaigns, SWT.CENTER);
		tvc.setLabelProvider(new CampaignProvinceLabelProvider());
		tc = tvc.getColumn();
		tc.setText("Province");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewerCampaigns, SWT.CENTER);
		tvc.setLabelProvider(new CampaignDateLabelProvider());
		tc = tvc.getColumn();
		tc.setText("Date");
		tc.setResizable(false);
		tc.pack();
	}
	
	private void addColumnsCriteria() {
		TableViewerColumn tvc = new TableViewerColumn(_tableViewerCriteria, SWT.CENTER);
		tvc.setLabelProvider(new CriterionIdLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				TableItem item = (TableItem) cell.getItem();
				item.setForeground(new Color(Display.getCurrent(), 211, 211, 211));
				_tableItemsCriteria.add(item);
			}
		});
		TableColumn tc = tvc.getColumn();
		tc.setText("Index");
		tc.setResizable(false);
		tc.pack();
	}
	
	private void addColumnsAlternatives() {
		TableViewerColumn tvc = new TableViewerColumn(_tableViewerAlternatives, SWT.CENTER);
		tvc.setLabelProvider(new AlternativeIdLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				TableItem item = (TableItem) cell.getItem();
				_tableItemsAlternatives.add(item);
			}
		});
		TableColumn tc = tvc.getColumn();
		tc.setText("Context");
		tc.setResizable(false);
		tc.pack();
	}
	
	private void addColumnsCampaignsSelected() {
		TableViewerColumn tvc = new TableViewerColumn(_tableViewerCampaignsSelected, SWT.CENTER);
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
				Collections.sort((List<Campaign>) _providerCampaignsSelected.getInput(), comparatorByName);
				_tableViewerCampaignsSelected.refresh();
			}
		});
		tvc.setLabelProvider(new CampaignIdLabelProvider());
		TableColumn tc = tvc.getColumn();
		tc.setText("Campaing");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewerCampaignsSelected, SWT.CENTER);
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
				Collections.sort((List<Campaign>) _providerCampaignsSelected.getInput(), comparatorByProvinces);
				_tableViewerCampaignsSelected.refresh();
			}
		});
		tvc.setLabelProvider(new CampaignProvinceLabelProvider());
		tc = tvc.getColumn();
		tc.setText("Province");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewerCampaignsSelected, SWT.CENTER);
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
				    		date1 = formatter.parse(c1.getDate());
				    		date2 = formatter.parse(c2.getDate());
				    	} catch (ParseException e) {
				    		e.printStackTrace();
				    	}
				        return date1.compareTo(date2);
					}
				};
				Collections.sort((List<Campaign>) _providerCampaignsSelected.getInput(), comparatorByDate);
				_tableViewerCampaignsSelected.refresh();
			}
		});
		tvc.setLabelProvider(new CampaignDateLabelProvider());
		tc = tvc.getColumn();
		tc.setText("Date");
		tc.setResizable(false);
		tc.pack();
	}
		
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add campaigns");
		newShell.setSize(600, 650);
	}
	
	@Override
	protected void initializeBounds() {
		super.initializeBounds(); 
		Shell shell = this.getShell(); 
		Monitor primary = shell.getMonitor(); 
	    Rectangle bounds = primary.getBounds (); 
		Rectangle rect = shell.getBounds (); 
		int x = bounds.x + (bounds.width - rect.width) / 2; 
		int y = bounds.y + (bounds.height - rect.height) / 2; 
		shell.setLocation (x, y);
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		_okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		_okButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
		_okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_elementsSet.notifyCampaignsChanges(new CampaignsChangeEvent(ECampaignsChange.FINAL_CAMPAIGNS, null, _campaignsAdded, false));
			}
		});
	}
}
