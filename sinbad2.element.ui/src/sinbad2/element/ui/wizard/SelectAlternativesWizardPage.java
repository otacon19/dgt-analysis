package sinbad2.element.ui.wizard;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import sinbad2.element.alternative.Alternative;
import sinbad2.element.ui.view.alternatives.provider.AlternativeSelectedIdLabelProvider;
import sinbad2.element.ui.view.alternatives.provider.AlternativesWizardContentProvider;

public class SelectAlternativesWizardPage extends WizardPage {

	private TreeViewer _treeViewerAlternatives;
	private AlternativesWizardContentProvider _provider;
	
	private Map<Alternative, Button> _buttons;
	
	private static List<Alternative> _alternativesSelected;
	
	protected SelectAlternativesWizardPage() {
		super("Select contexts");
		setDescription("Select the contexts you want");
		
		_alternativesSelected = new LinkedList<Alternative>();
		
		_buttons = new HashMap<Alternative, Button>();
		
	}
	
	public static List<Alternative> getInformationAlternatives() {
		return _alternativesSelected;
	}

	@Override
	public void createControl(Composite parent) {
		Composite alternatives = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		alternatives.setLayoutData(gridData);
		alternatives.setLayout(layout);
		_treeViewerAlternatives = new TreeViewer(alternatives, SWT.CENTER | SWT.BORDER | SWT.FULL_SELECTION);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		_treeViewerAlternatives.getTree().setLayoutData(gridData);
		
		_provider = new AlternativesWizardContentProvider(_treeViewerAlternatives);
		_treeViewerAlternatives.setContentProvider(_provider);
		
		_treeViewerAlternatives.getTree().setHeaderVisible(true);
		_treeViewerAlternatives.getTree().addListener(SWT.MeasureItem, new Listener() {
			@Override
			public void handleEvent(Event event) {
				event.height = 25;
			}
		});
		
		_treeViewerAlternatives.getTree().addListener(SWT.Paint, new Listener() {
			@Override
			public void handleEvent(Event event) {
				_treeViewerAlternatives.getTree().layout();
			}
		});
		
		addColumns();
		
		_treeViewerAlternatives.setInput(_provider.getInput());
	
		setControl(alternatives);
		setPageComplete(false);
		
		_provider.pack();
	}
	
	private void addColumns() {
		TreeViewerColumn tvc = new TreeViewerColumn(_treeViewerAlternatives, SWT.CENTER);
		tvc.setLabelProvider(new AlternativeSelectedIdLabelProvider());
		TreeColumn tc = tvc.getColumn();
		tc.setText("Context");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TreeViewerColumn(_treeViewerAlternatives, SWT.CENTER);
		tc = tvc.getColumn();
		tc.setText("Selection");
		tc.setResizable(false);
		tc.pack();
		tvc.setLabelProvider(new ColumnLabelProvider() {
			Map<Object, Button> buttons = new HashMap<Object, Button>();

			@Override
			public void update(ViewerCell cell) {
				TreeItem item = (TreeItem) cell.getItem();
				final Button button;
				if (buttons.containsKey(cell.getElement())) {
					button = buttons.get(cell.getElement());
				} else {
					button = new Button((Composite) cell.getViewerRow().getControl(), SWT.CHECK);
					button.setSelection(false);
					button.setData("alternative", (Alternative) item.getData());
					buttons.put(cell.getElement(), button);
					_buttons.put((Alternative) item.getData(), button);
					
					for(Alternative a: _alternativesSelected) {
						if(a.hasChildrens()) {
							List<Alternative> childrens = a.getChildrens();
							if(childrens.contains(item.getData())) {
								button.setSelection(true);
							}
						}
					}
					
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if(((Button) e.widget).getSelection()) {
								_alternativesSelected.add((Alternative) button.getData("alternative"));
								button.setSelection(true);
								
								if(((Alternative) button.getData("alternative")).hasChildrens()) {
									List<Alternative> childrens = ((Alternative) button.getData("alternative")).getChildrens();
									for(Alternative children: childrens) {
										if(!_alternativesSelected.contains(children)) {
											_alternativesSelected.add(children);
										}
									}
									for(Alternative children: childrens) {
										if(_buttons.get(children) != null) {
											_buttons.get(children).setSelection(true);
										}
									}
								}
							} else {
								if(!_alternativesSelected.isEmpty()) {
									_alternativesSelected.remove((Alternative) button.getData("alternative"));
									button.setSelection(false);
									
									if(((Alternative) button.getData("alternative")).hasChildrens()) {
										List<Alternative> childrens = ((Alternative) button.getData("alternative")).getChildrens();
										for(Alternative children: childrens) {
											if(_alternativesSelected.contains(children)) {
												_alternativesSelected.remove(children);
											}
										}
										for(Alternative children: childrens) {
											_buttons.get(children).setSelection(false);
										}
									}
								}
							}
							System.out.println(_alternativesSelected);
							if(!_alternativesSelected.isEmpty()) {
								setPageComplete(true);
							} else {
								setPageComplete(false);
							}
						}
						
					});
					
				}
				
				TreeEditor editor = new TreeEditor(item.getParent());
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
