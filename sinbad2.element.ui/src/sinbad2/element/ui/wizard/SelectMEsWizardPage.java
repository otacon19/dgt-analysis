package sinbad2.element.ui.wizard;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.OwnerDrawLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
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
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import sinbad2.element.mec.MEC;
import sinbad2.element.ui.view.mecs.provider.MECIdLabelProvider;
import sinbad2.element.ui.view.mecs.provider.MECsWizardContentProvider;

public class SelectMEsWizardPage extends WizardPage {
	
	private TableViewer _tableViewerMEs;
	private MECsWizardContentProvider _provider;

	private static List<MEC> _mecsSelected;
	
	protected SelectMEsWizardPage() {
		super("Select MEs");
		setDescription("Select the MEs you want");
		
		_mecsSelected = new LinkedList<MEC>();
	}
	
	public static List<MEC> getInformationMECs() {
		return _mecsSelected;
	}

	@Override
	public void createControl(Composite parent) {
		Composite campaigns = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		campaigns.setLayoutData(gridData);
		campaigns.setLayout(layout);
		campaigns.setBackground(new Color(Display.getCurrent(), 255, 255, 255));	
		
		_tableViewerMEs = new TableViewer(campaigns, SWT.CENTER | SWT.BORDER | SWT.FULL_SELECTION);
		_tableViewerMEs.getTable().addListener(SWT.Selection, new Listener() {
	        @Override
	        public void handleEvent(Event event) {
	        	_tableViewerMEs.getTable().deselectAll();
	        }
	    });
		
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_tableViewerMEs.getTable().setLayoutData(gridData);
		
		_provider = new MECsWizardContentProvider(_tableViewerMEs);
		_tableViewerMEs.setContentProvider(_provider);
		_tableViewerMEs.getTable().setHeaderVisible(true);
		
		_tableViewerMEs.getTable().addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.height = 25;
			}
		});
		
		_tableViewerMEs.getTable().addListener(SWT.Paint, new Listener() {
			@Override
			public void handleEvent(Event event) {
				_tableViewerMEs.getTable().layout();
			}
		});
		
		addColumns();
		
		_tableViewerMEs.setInput(_provider.getInput());
		
		setControl(campaigns);
		setPageComplete(false);
		
		_provider.pack();
	}
		
	private void addColumns() {
		TableViewerColumn tvc = new TableViewerColumn(_tableViewerMEs, SWT.NONE);
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
			 		int itemHeight = _tableViewerMEs.getTable().getItemHeight();
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
					if (_tableViewerMEs.getTable().getColumn(1).getWidth() < formula.getImageData().width + 15) {
						_tableViewerMEs.getTable().getColumn(1).setWidth(formula.getImageData().width + 15);
					}
				}
			}
		}

		tvc = new TableViewerColumn(_tableViewerMEs, SWT.NONE);
		tvc.setLabelProvider(new FormulaLabelProvider());
		tc = tvc.getColumn();
		tc.setText("Formula");
		tc.setResizable(false);
		tc.pack();

		tvc = new TableViewerColumn(_tableViewerMEs, SWT.CENTER);
		tc = tvc.getColumn();
		tc.setText("Selection");
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
					button.setData("mec", (MEC) item.getData()); 
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if(((Button) e.widget).getSelection()) {
								if(!_mecsSelected.contains((MEC) button.getData("mec"))) {
									_mecsSelected.add((MEC) button.getData("mec"));
								}
							} else {
								_mecsSelected.remove((MEC) button.getData("mec"));
							}
							
							if(!_mecsSelected.isEmpty()) {
								setPageComplete(true);
							} else {
								setPageComplete(false);
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
