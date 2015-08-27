package sinbad2.element.ui.view.mecs;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
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
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.campaigns.listener.CampaignsChangeEvent;
import sinbad2.element.campaigns.listener.ICampaignsChangeListener;
import sinbad2.element.mec.MEC;
import sinbad2.element.mec.listener.IMECsChangeListener;
import sinbad2.element.mec.listener.MECsChangeEvent;
import sinbad2.element.ui.nls.Messages;
import sinbad2.element.ui.view.campaigns.CampaignsView;
import sinbad2.element.ui.view.mecs.dialog.NewMecDialog;
import sinbad2.element.ui.view.mecs.jfreechart.MECChart;
import sinbad2.element.ui.view.mecs.provider.MECContentProvider;
import sinbad2.element.ui.view.mecs.provider.MECFormulaLabelProvider;
import sinbad2.element.ui.view.mecs.provider.MECIdLabelProvider;

import org.eclipse.swt.widgets.Table;

public class MECView extends ViewPart implements IMECsChangeListener, ICampaignsChangeListener {

	public static final String ID = "flintstones.element.ui.view.mecs"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.element.ui.view.mecs.mecs_view"; //$NON-NLS-1$
	
	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);
	
	private Button _addFormulaButton;
	MECChart _chart;
	
	private TableViewer _tableViewer;

	private MECContentProvider _provider;
	
	private ProblemElementsSet _elementsSet;

	public MECView() {
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		
		_elementsSet.registerMECsChangesListener(this);;
		_elementsSet.registerCampaignsChangesListener(this);
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
		tableLabel.setText(Messages.MECView_Exposure_Measurements);
		tableLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$
		tableLabel.setBackground(new Color(Display.getCurrent(), 255, 255, 255));

		_tableViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		Table table = _tableViewer.getTable();
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd_table.heightHint = 222;
		table.setLayoutData(gd_table);
		
		_tableViewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.height = 55;
			}
		});
		
		_tableViewer.getTable().addListener(SWT.PaintItem, new Listener() {
	 		@Override	
	 		public void handleEvent(Event event) {
	 			TableItem item = (TableItem) event.item;
	 			MEC mec = (MEC) item.getData();
	 			Image formula = mec.getFormula();
	 			if(formula != null) {
		 			int x = event.x + event.width + 2;
			 		int itemHeight = _tableViewer.getTable().getItemHeight();
			 		int imageHeight = formula.getBounds().height;
			 		int y = event.y + (itemHeight - imageHeight) / 2;
			 		event.gc.drawImage(formula, x, y);
	 			}
	 		}
		});
		
		_addFormulaButton = new Button(container, SWT.NULL);
		_addFormulaButton.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1));
		_addFormulaButton.setText(Messages.MECView_Add_Formula_Button);
		_addFormulaButton.setEnabled(false);
		_addFormulaButton.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD).createImage());
		_addFormulaButton.setBackground(new Color(Display.getCurrent(), 255,255, 255));
		
		_addFormulaButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewMecDialog dialog = new NewMecDialog();
				dialog.open();
			}
		});
		
		final Composite chartComposite = new Composite(container, SWT.BORDER);
		GridData gd_chartComposite = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_chartComposite.heightHint = 300;
		chartComposite.setLayoutData(gd_chartComposite);
		chartComposite.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		_chart = new MECChart();
		
		chartComposite.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				_chart.initialize(chartComposite, chartComposite.getSize().x, chartComposite.getSize().y, SWT.CENTER);
			}
		});
		
		_tableViewer.getTable().setHeaderVisible(true);
		_provider = new MECContentProvider(_tableViewer);
		_tableViewer.setContentProvider(_provider);

		_tableViewer.setInput(_provider.getInput());
		getSite().setSelectionProvider(_tableViewer);
		
		addColumns();
		hookFocusListener();
	}

	private void addColumns() {
		TableViewerColumn tvc = new TableViewerColumn(_tableViewer, SWT.NONE);
		tvc.setLabelProvider(new MECIdLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				super.update(cell);
				TableItem item = (TableItem) cell.getItem();
	 			MEC mec = (MEC) item.getData();
	 			Image formula = mec.getFormula();
	 			if(formula != null) {
			 		if(_tableViewer.getTable().getColumn(1).getWidth() < formula.getImageData().width + 15) {
			 			_tableViewer.getTable().getColumn(1).setWidth(formula.getImageData().width + 15);
			 		}
	 			}
			}
		});
		TableColumn tc = tvc.getColumn();
		tc.setText(Messages.MECView_Id_Column);
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewer, SWT.NONE);
		tvc.setLabelProvider(new MECFormulaLabelProvider());
		tc = tvc.getColumn();
		tc.setText(Messages.MECView_Formula_Column);
		tc.setResizable(false);
		tc.pack();
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
	}
	
	@Override
	public void setFocus() {
		_tableViewer.getControl().setFocus();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void notifyCampaignsChange(CampaignsChangeEvent event) {
		Map<String, List<MEC>> campaignsAndMECs = new HashMap<String, List<MEC>>();
		switch(event.getChange()) {
			case CAMPAIGNS_SELECTED_CHANGES:
				List<Campaign> campaigns = (List<Campaign>) event.getNewValue();
				if(campaigns.isEmpty() || campaigns.size() > 1) {
					_addFormulaButton.setEnabled(false);
					_chart.clear();
				} else {
					_addFormulaButton.setEnabled(true);
					campaignsAndMECs.put(campaigns.get(0).getId(), campaigns.get(0).getMECs());
					_chart.setMEC(campaignsAndMECs);
				}
				break;
			case ADD_CAMPAIGN:
				Campaign campaignAdded = (Campaign) event.getNewValue();
				campaignsAndMECs.put(campaignAdded.getId(), campaignAdded.getMECs());
				_chart.setMEC(campaignsAndMECs);
				break;
			case COMPARE_CAMPAIGNS:
				List<Campaign> campaignsCompared = (List<Campaign>) event.getNewValue();
				for(Campaign c: campaignsCompared) {
					campaignsAndMECs.put(c.getId(), c.getMECs());
				}
				_chart.setMEC(campaignsAndMECs);
				break;
			default: 
				break;
		}
	} 
	
	@Override
	public void notifyMecsChange(MECsChangeEvent event) {
		switch(event.getChange()) {
			case ADD_MEC:
				addMec((MEC) event.getNewValue());
				break;
		}
		_tableViewer.setInput(_provider.getInput());
		_tableViewer.refresh();
		
		_provider.pack();
		
	}

	private void addMec(MEC mec) {	
		int pos = 0; 
		boolean find = false; 
		
		Campaign campaignSelected = CampaignsView.getCampaignsSelected().get(0);
		campaignSelected.addMEC(mec);
	 	
		do { 
	 		if(campaignSelected.getMECs().get(pos) == mec) { 
	 			find = true; 
	 		} else { 
	 			pos++; 
	 		} 
	 	} while(!find); 
		
		Map<String, List<MEC>> campaignsAndMecs = new HashMap<String, List<MEC>>();
		campaignsAndMecs.put(campaignSelected.getId(), campaignSelected.getMECs());
		_chart.setMEC(campaignsAndMecs);
		
	 	_tableViewer.insert(mec, pos); 

	 }
}
