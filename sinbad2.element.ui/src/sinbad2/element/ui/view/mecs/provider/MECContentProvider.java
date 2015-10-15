package sinbad2.element.ui.view.mecs.provider;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ICampaignsChangeListener;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.criterion.listener.CriteriaChangeEvent;
import sinbad2.element.criterion.listener.ICriteriaChangeListener;
import sinbad2.element.mec.MEC;
import sinbad2.element.mec.listener.IMECsChangeListener;
import sinbad2.element.mec.listener.MECsChangeEvent;
import sinbad2.element.ui.ImagesFormulas;
import sinbad2.element.ui.view.criteria.CriteriaView;

public class MECContentProvider implements IStructuredContentProvider, IMECsChangeListener, ICampaignsChangeListener, ICriteriaChangeListener {
	
	private List<MEC> _mecs;
	
	private TableViewer _tableViewer;
	
	ProblemElementsSet _elementsSet;
	
	public MECContentProvider(TableViewer tableViewer) {
		_tableViewer = tableViewer;
		_mecs = new LinkedList<MEC>();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_elementsSet.registerCampaignsChangesListener(this);
		_elementsSet.registerMECsChangesListener(this);
		_elementsSet.registerCriteriaChangesListener(this);
	}
	
	@Override
	public void dispose() {}
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return ((List<MEC>)inputElement).toArray();
	}
	public Object getInput() {
		return _mecs;
	}
	
	public void pack() {
		_tableViewer.getTable().getColumns()[0].pack();
		
		Table table = _tableViewer.getTable();
	    int columnsWidth = 0;
	    
	    for (int i = 0; i < table.getColumnCount() - 1; i++) {
	        columnsWidth += table.getColumn(i).getWidth();
	    }
	    TableColumn lastColumn = table.getColumn(table.getColumnCount() - 1);
	    lastColumn.pack();

	    Rectangle area = table.getClientArea();

	    int width = area.width - 2*table.getBorderWidth();

	    if(lastColumn.getWidth() < width - columnsWidth) {
	        lastColumn.setWidth(width - columnsWidth + 3);
	    }
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyCampaignsChange(CampaignsChangeEvent event) {
		switch(event.getChange()) {
			case FINAL_CAMPAIGNS:	
				for(MEC m: _elementsSet.getMECs()) {
					if(m.getFormula() == null) {
						if(m.getId().equals("Distancia")) { //$NON-NLS-1$
							m.setFormula(ImagesFormulas.Distancia);
						} else if(m.getId().equals("Tiempo")) { //$NON-NLS-1$
							m.setFormula(ImagesFormulas.Tiempo);
						} else if(m.getId().equals("Desplazamientos")) { //$NON-NLS-1$
							m.setFormula(ImagesFormulas.Desplazamiento);
						}
					}
				}
				break;
			case CAMPAIGNS_SELECTED_CHANGES:
				List<Campaign> campaignsSelected = (List<Campaign>) event.getNewValue();
				_mecs.clear();
				_mecs.addAll(_elementsSet.getMECs());
				if(!campaignsSelected.isEmpty()) {
					_tableViewer.refresh();
					pack();
					searchMatchingMECs(campaignsSelected);
					checkAvailableMECs(CriteriaView.getCriteriaSelected());
				} else {
					_mecs.clear();
					_tableViewer.refresh();
				}
				break;
			case REMOVE_CAMPAIGNS_SELECTED:
				if(!_tableViewer.getTable().isDisposed()) {
					_mecs.clear();
					_tableViewer.refresh();
					pack();
				}
				break;
			default:
				break;
		}
	}
	
	private void searchMatchingMECs(List<Campaign> campaignsSelected) {
		TableItem[] tableItems = _tableViewer.getTable().getItems();
		
		if(campaignsSelected.size() == 1) {
			Campaign campaignSelected = campaignsSelected.get(0);
			List<Criterion> criteria = campaignSelected.getCriteria();
			for(MEC m: _mecs) {
				List<Criterion> mecCriteria = m.getAvailableCriteria();
				if(criteria.containsAll(mecCriteria)) {
					for(TableItem ti: tableItems) {
						if(ti.getData().equals(m)) {
							ti.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
						}
					}
				} else {
					for(TableItem ti: tableItems) {
						if(ti.getData().equals(m)) {
							ti.setForeground(new Color(Display.getCurrent(), 211, 211, 211));
						}
					}
				}
			}
		} else {
			List<Criterion> allCriteriaCampaigns = new LinkedList<Criterion>();
			List<Criterion> criteriaData = new LinkedList<Criterion>();
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
						if(!criteriaData.contains(cri)) {
							criteriaData.add(cri);
						}
					}
				}
			}
			Map<Criterion, Integer> criteriaRepeat;
			criteriaRepeat = checkMatchingData(allCriteriaCampaigns, campaignsSelected.size());
			for(Criterion c: criteriaData) {
				criteriaRepeat.put(c, numCampaignsData);
			}
			
			for(MEC m: _mecs) {
				List<Criterion> mecCriteria = m.getAvailableCriteria();
				for(Criterion mc: mecCriteria) {
					if(criteriaRepeat.containsKey(mc)) {
						int rep = criteriaRepeat.get(mc);
						if(rep == campaignsSelected.size()) {
							for(TableItem ti: tableItems) {
								if(ti.getData().equals(m)) {
									ti.setForeground(new Color(Display.getCurrent(), 0, 0, 0));
								}
							}
						} else {
							for(TableItem ti: tableItems) {
								if(ti.getData().equals(m)) {
									ti.setForeground(new Color(Display.getCurrent(), 211, 211, 211));
								}
							}
						}
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
	
	@Override
	public void notifyMecsChange(MECsChangeEvent event) {
		switch(event.getChange()) {
			case ADD_MEC:
				MEC mec = (MEC) event.getNewValue();
				addMec((MEC) mec);
				_tableViewer.refresh();
				pack();
				checkAvailableMECs(CriteriaView.getCriteriaSelected());
				break;
			default:
				break;
		}
	}
	
	private void addMec(MEC mec) {	
		int pos = 0; 
		boolean find = false; 
	 	
		_mecs.add(mec);
		
		do { 
	 		if(_elementsSet.getMECs().get(pos) == mec) { 
	 			find = true; 
	 		} else { 
	 			pos++; 
	 		} 
	 	} while(!find); 
		
	 }

	@SuppressWarnings("unchecked")
	@Override
	public void notifyCriteriaChange(CriteriaChangeEvent event) {
		switch(event.getChange()) {
			case CRITERIA_SELECTED_CHANGES:
				List<Criterion> criteriaSelected = (List<Criterion>) event.getNewValue();
				checkAvailableMECs(criteriaSelected);
				break;
			default:
				break;
		}
	}

	private void checkAvailableMECs(List<Criterion> criteriaSelected) {
		TableItem[] tableItems = _tableViewer.getTable().getItems();
		Map<MEC, TableItem> mecsTable = new LinkedHashMap<MEC, TableItem>();
		for(TableItem ti: tableItems) {
			mecsTable.put((MEC) ti.getData(), ti);
			FontDescriptor boldDescriptor = FontDescriptor.createFrom(ti.getFont()).setStyle(SWT.NONE);
			Font boldFont = boldDescriptor.createFont(ti.getDisplay());
			ti.setFont(boldFont);
		}
		
		for(Criterion c: criteriaSelected) {
			for(MEC mec: _mecs) {
				TableItem ti = mecsTable.get(mec);
				if(mec.getAvailableCriteria().contains(c)  && !ti.getForeground().equals(new Color(Display.getCurrent(), 211, 211, 211))) {
					FontDescriptor boldDescriptor = FontDescriptor.createFrom(ti.getFont()).setStyle(SWT.BOLD);
					Font boldFont = boldDescriptor.createFont(ti.getDisplay());
					ti.setFont(boldFont);
				}
			}
		}
	}
}