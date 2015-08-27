package sinbad2.element.ui.view.alternatives;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
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
import sinbad2.element.alternative.Alternative;
import sinbad2.element.alternative.listener.AlternativesChangeEvent;
import sinbad2.element.alternative.listener.EAlternativesChange;
import sinbad2.element.ui.nls.Messages;
import sinbad2.element.ui.view.alternatives.provider.AlternativeIdLabelProvider;
import sinbad2.element.ui.view.alternatives.provider.AlternativesContentProvider;

public class AlternativesView extends ViewPart {
	
	public static final String ID = "flintstones.element.ui.view.alternatives"; //$NON-NLS-1$
	public static final String CONTEXT_ID = "flintstones.element.ui.view.alternatives.alternatives_view"; //$NON-NLS-1$
	
	private static final IContextService _contextService = (IContextService) PlatformUI.getWorkbench().getService(IContextService.class);

	private TableViewer _tableViewer;
	private List<Alternative> _alternativesSelected;
	private List<Alternative> _alternatives;
 	
	private AlternativesContentProvider _provider;
	
	private ProblemElementsSet _elementsSet;
	
	private static List<Button> _buttons;
	
	public AlternativesView() {
		_alternativesSelected = new LinkedList<Alternative>();
		_buttons = new LinkedList<Button>();
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		_elementsSet = elementsManager.getActiveElementSet();
		_alternatives = _elementsSet.getAlternatives();
		_alternativesSelected = new LinkedList<Alternative>();
	}
	
	public static List<Button> getButtons() {
		if(_buttons == null) {
			_buttons = new LinkedList<Button>();
		}
		return _buttons;
	}
	
	@Override
	public void createPartControl(Composite parent) {
		_tableViewer = new TableViewer(parent, SWT.CENTER | SWT.BORDER | SWT.FULL_SELECTION);
		
		_provider = new AlternativesContentProvider(_tableViewer);
		_tableViewer.setContentProvider(_provider);
		
		_tableViewer.getTable().setHeaderVisible(true);
		_tableViewer.getTable().addListener(SWT.MeasureItem, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				event.height = 23;
				
			}
		});
		
		addColumns();
		hookContextMenu();
		hookFocusListener();
		
		_tableViewer.setInput(_provider.getInput());
		_provider.pack();
		getSite().setSelectionProvider(_tableViewer);
		
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

	private void addColumns() {
		TableViewerColumn tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tvc.setLabelProvider(new AlternativeIdLabelProvider());
		TableColumn tc = tvc.getColumn();
		tc.setText(Messages.AlternativesView_Column_Context);
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(_tableViewer, SWT.CENTER);
		tc = tvc.getColumn();
		tc.setText(Messages.AlternativesView_Column_Selection);
		tc.setResizable(false);
		tc.pack();
		tvc.setLabelProvider(new ColumnLabelProvider() {
			Map<Object, Button> buttons = new HashMap<Object, Button>();

			@Override
			public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();
				Button button;
				if (buttons.containsKey(cell.getElement())) {
					button = buttons.get(cell.getElement());
				} else {
					button = new Button((Composite) cell.getViewerRow().getControl(), SWT.CHECK);
					buttons.put(cell.getElement(), button);
					_buttons.add(button);
					
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if(((Button) e.widget).getSelection()) {
								_alternativesSelected.add(_alternatives.get(_tableViewer.getTable().indexOf(item)));
							} else {
								if(!_alternativesSelected.isEmpty()) {
									_alternativesSelected.remove(_alternatives.get(_tableViewer.getTable().indexOf(item)));
								}
							}
							Collections.sort(_alternativesSelected);
							
							_elementsSet.notifyAlternativesChanges(new AlternativesChangeEvent(EAlternativesChange.ALTERNATIVES_SELECTED_CHANGES, null, _alternativesSelected, false));
						}
					});
					
				}
				button.setSelection(false);
				
				TableEditor editor = new TableEditor(item.getParent());
				button.pack();
				editor.minimumWidth = button.getSize().x;
				editor.horizontalAlignment = SWT.CENTER;
				editor.setEditor(button, item, cell.getColumnIndex());
				editor.layout();
			}

		});

	}
	
	private void hookContextMenu() {
		MenuManager menuManager = new MenuManager();
		Menu menu = menuManager.createContextMenu(_tableViewer.getTable());
		_tableViewer.getTable().setMenu(menu);
		getSite().registerContextMenu(menuManager, _tableViewer);
	}
	
	@Override
	public void setFocus() {
		_tableViewer.getControl().setFocus();
		
	}
}
