package sinbad2.element.ui.view.mecs.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
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
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wb.swt.SWTResourceManager;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.mec.MEC;
import sinbad2.element.ui.view.criteria.CriteriaView;
import sinbad2.element.ui.view.criteria.provider.CriteriaMEsContentProvider;
import sinbad2.element.ui.view.criteria.provider.CriterionSelectedIdLabelProvider;

public class NewMeDialog extends Dialog {

	private TableViewer _tableViewerCriteria;
	private CriteriaMEsContentProvider _providerCriteria;
	
	private String _mecName;
	private String _numerator;
	private String _denominator;
	
	private Button _okButton;
	private Button _addCriteria;
	private List<Combo> _combos;
	private List<Text> _texts;
	private ControlDecoration _nameControlDecoration;
	private Image _formulaImage;

	private List<Criterion> _criteriaSelected;
	private List<Criterion> _criteriaNumerator;
	private List<Criterion> _criteriaDenominator;
	private Map<Criterion, Double> _criteriaWeight;
	
	private static Frame _frame;

	public NewMeDialog() {
		super(Display.getCurrent().getActiveShell());
		
		_mecName = ""; //$NON-NLS-1$
		_numerator = ""; //$NON-NLS-1$
		_denominator = ""; //$NON-NLS-1$
		_criteriaSelected = new LinkedList<Criterion>();
		_criteriaNumerator = new LinkedList<Criterion>();
		_criteriaDenominator = new LinkedList<Criterion>();
		_criteriaWeight = new LinkedHashMap<Criterion, Double>();
		_combos = new LinkedList<Combo>();
		_texts = new LinkedList<Text>();
	}

	@Override
	protected Control createDialogArea(Composite parent) {	
		Composite container = (Composite) super.createDialogArea(parent);
		
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginRight = 10;
		gridLayout.marginTop = 10;
		gridLayout.marginLeft = 10;
		gridLayout.marginBottom = 10;
		container.setLayout(gridLayout);

		Label idLabel = new Label(container, SWT.NULL);
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false, 4, 1);
		idLabel.setLayoutData(gridData);
		idLabel.setText("ME name");
		idLabel.setFont(SWTResourceManager.getFont("Cantarell", 10, SWT.BOLD)); //$NON-NLS-1$

		Text text = new Text(container, SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.CENTER, true, false, 4, 1);
		text.setLayoutData(gridData);
		text.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				_mecName = ((Text) e.getSource()).getText().trim();
				validate();
			}
		});

		_nameControlDecoration = createNotificationDecorator(text);
		
		_tableViewerCriteria = new TableViewer(container, SWT.CENTER | SWT.BORDER | SWT.FULL_SELECTION);
		_tableViewerCriteria.getTable().setLinesVisible(true);
		_providerCriteria = new CriteriaMEsContentProvider(_tableViewerCriteria);
		_tableViewerCriteria.setContentProvider(_providerCriteria);
		_tableViewerCriteria.getTable().setHeaderVisible(true);
		GridData gd_table = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1);
		gd_table.heightHint = 300;
		_tableViewerCriteria.getTable().setLayoutData(gd_table);
		_tableViewerCriteria.getTable().addListener(SWT.MeasureItem, new Listener() {

				@Override
				public void handleEvent(Event event) {
					event.height = 20;
				}
			});

		addColumnsCriteria(_tableViewerCriteria);

		_tableViewerCriteria.setInput(_providerCriteria.getInput());
		_providerCriteria.pack();
		
		_addCriteria = new Button(container, SWT.NULL);
		_addCriteria.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1));
		_addCriteria.setText("Add");
		_addCriteria.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD).createImage());
		_addCriteria.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		_addCriteria.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_criteriaNumerator.clear();
				_criteriaDenominator.clear();
				_numerator = "";
				_denominator = "";
				_formulaImage = null;
				_frame.repaint();
				
				if(!_numerator.isEmpty()) {
					_numerator += "$\\times$"; //$NON-NLS-1$
				}
				
				if(!_denominator.isEmpty()) {
					_denominator += "$\\times$"; //$NON-NLS-1$
				}
				
				int position = 0;
				for (int i = 0; i < _criteriaSelected.size(); ++i) {
					Criterion c = _criteriaSelected.get(i);
					for(Combo comb: _combos) {
						if(comb.getData("criterion").equals(c)) {
							position = comb.getSelectionIndex();
							if(position == 0) {
								if(!_numerator.contains(c.getId())){
									_numerator += c.getId() + "$\\times$"; //$NON-NLS-1$
									_criteriaNumerator.add(c);
								}
							} else {
								if(!_denominator.contains(c.getId())){
									_denominator += c.getId() + "$\\times$"; //$NON-NLS-1$
									_criteriaDenominator.add(c);
								}
							}
						}
					}
				}
				
				if(_numerator.endsWith("$\\times$")) { //$NON-NLS-1$
					 _numerator = _numerator.substring(0, _numerator.length() - 8);
				}
				
				if(_denominator.endsWith("$\\times$")) { //$NON-NLS-1$
					_denominator = _denominator.substring(0, _denominator.length() - 8);
				}
				
				createFormula();
				validate();
			}
		});
	
		Composite composite = new Composite(container, SWT.EMBEDDED | SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		composite.setLayoutData(gridData);
		_frame = SWT_AWT.new_Frame(composite);
		_frame.setLayout(new BorderLayout());
		_frame.setBackground(java.awt.Color.WHITE);
		_frame.setVisible(true);

		Button clearFormula = new Button(container, SWT.NULL);
		clearFormula.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false, false, 1, 1));
		clearFormula.setText("Clear");
		clearFormula.setImage(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR).createImage());
		clearFormula.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		clearFormula.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_criteriaNumerator.clear();
				_criteriaDenominator.clear();
				_numerator = ""; //$NON-NLS-1$
				_denominator = ""; //$NON-NLS-1$
				_formulaImage = null;
				_frame.repaint();
				_okButton.setEnabled(false);
			}
		});
		
		checkAvailableData();
		
		return container;
	}

	private void addColumnsCriteria(TableViewer tableViewer) {
		TableViewerColumn tvc = new TableViewerColumn(tableViewer, SWT.CENTER);
		tvc.setLabelProvider(new CriterionSelectedIdLabelProvider());
		TableColumn tc = tvc.getColumn();
		tc.setText("Index");
		tc.setResizable(false);
		tc.pack();
		
		tvc = new TableViewerColumn(tableViewer, SWT.CENTER);
		tc = tvc.getColumn();
		tc.setText("Position");
		tc.setResizable(false);
		tc.setWidth(100);
		tvc.setLabelProvider(new ColumnLabelProvider() {
			Map<Object, Combo> combos = new HashMap<Object, Combo>();

			@Override
			public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();
				Combo combo;
				if (combos.containsKey(cell.getElement())) {
					combo = combos.get(cell.getElement());
				} else {
					combo = new Combo((Composite) cell.getViewerRow().getControl(), SWT.CENTER);
					_combos.add(combo);
					combo.add("Numerator");
					combo.add("Denominator");
					combo.add("Not selected");
					combo.select(2);
					combo.setData("criterion", item.getData());
					combos.put(cell.getElement(), combo);		
					
					combo.addSelectionListener(new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {
							if (((Combo) e.widget).getSelectionIndex() == 0 || ((Combo) e.widget).getSelectionIndex() == 1) {
								_criteriaSelected.add((Criterion) item.getData());
							} else {
								if (!_criteriaSelected.isEmpty()) {
									_criteriaSelected.remove((Criterion) item.getData());
								}
								Collections.sort(_criteriaSelected);
							}
							
							if(_criteriaSelected.isEmpty()) {
								_addCriteria.setEnabled(false);
							} else {
								_addCriteria.setEnabled(true);
							}
						};
					});
				}
				
				TableEditor editor = new TableEditor(item.getParent());
				editor.grabHorizontal = true;
				editor.setEditor(combo, item, cell.getColumnIndex());
			}
		});
		
		tvc = new TableViewerColumn(tableViewer, SWT.CENTER);
		tc = tvc.getColumn();
		tc.setText("Weighing");
		tc.setResizable(false);
		tc.pack();
		tvc.setLabelProvider(new ColumnLabelProvider() {
			Map<Object, Text> texts = new HashMap<Object, Text>();

			@Override
			public void update(ViewerCell cell) {
				final TableItem item = (TableItem) cell.getItem();
				Text text;
				if (texts.containsKey(cell.getElement())) {
					text = texts.get(cell.getElement());
				} else {
					text = new Text((Composite) cell.getViewerRow().getControl(), SWT.CENTER);
					text.setData("criterion", item.getData());
					_texts.add(text);
					texts.put(cell.getElement(), text);		
				}
				
				text.addVerifyListener(new VerifyListener() {
					
					@Override
					public void verifyText(VerifyEvent e) {
						Text text = (Text) e.getSource();

			            final String oldS = text.getText();
			            String newS = oldS.substring(0, e.start) + e.text + oldS.substring(e.end);

			            if(!newS.isEmpty()) {
			            	if(newS.equals("00") || newS.equals("01")) {
			            		e.doit = false;
			            	} else {
					            boolean isFloat = true;
					            float num = 0;
					            try {
					                num = Float.parseFloat(newS);
					                text.setData("value", newS);
					            } catch(NumberFormatException ex) {
					                isFloat = false;
					            }
					            
					            if(!isFloat || num < 0 || num > 1  || newS.length() > 4) {
					                e.doit = false;
					            } 
			            	}
			            }
			            
			            validate();
					}
				});
				
				TableEditor editor = new TableEditor(item.getParent());
				editor.grabHorizontal = true;
				editor.setEditor(text, item, cell.getColumnIndex());
				editor.layout();
			}
		});
	}

	private ControlDecoration createNotificationDecorator(Text text) {
		ControlDecoration controlDecoration = new ControlDecoration(text,
				SWT.LEFT | SWT.TOP);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		controlDecoration.setImage(fieldDecoration.getImage());
		validate(controlDecoration, ""); //$NON-NLS-1$

		return controlDecoration;
	}
	
	private boolean validate(ControlDecoration controlDecoration, String text) {
		controlDecoration.setDescriptionText(text);
		if (text.isEmpty()) {
			controlDecoration.hide();
			return true;
		} else {
			controlDecoration.show();
			return false;
		}
	}

	private void validate() {
		boolean validId, validValue, validImage = false;

		String message = ""; //$NON-NLS-1$
		
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
		if (!_mecName.isEmpty()) {
			List<MEC> allMECs = elementsSet.getMECs();
			for(MEC m : allMECs) {
				if(m.getId().equals(_mecName)) {
					message = "Duplicated id";
				}
			}
		} else {
			message = "Empty name";
		}
		
		validId = validate(_nameControlDecoration, message);
		
		float acum = 0;
		for(Text t: _texts) {
			String value = (String) t.getData("value");
			try {
				if(t.getData("value") == null || ((String) t.getData("value")).isEmpty()) {
					value = "0";
				}
				_criteriaWeight.put((Criterion) t.getData("criterion"), Double.parseDouble(value));
				acum += Double.parseDouble(value);
			} catch(NumberFormatException ex) {
	        	acum = -1;
	        }
		}
		if(!(acum == 1.0)) {
			message = "Values must add 1";
			weightsNoCorrect();
		} else {
			weightsCorrect();
		}
		
		validValue = validate(_nameControlDecoration, message);
		
		if(_formulaImage == null) {
			message = "Empty ME";
		} else {
			validImage = true;
		}
		
		validImage = validate(_nameControlDecoration, message);
		
		_okButton.setEnabled(validId && validValue && validImage);
	}

	@SuppressWarnings("unchecked")
	private void weightsCorrect() {
		Map<Criterion, Combo> combos = new HashMap<Criterion, Combo>();
		for(Combo c: _combos) {
			combos.put((Criterion) c.getData("criterion"), c);
		}
		
		Map<Criterion, Text> texts = new HashMap<Criterion, Text>();
		for(Text t: _texts) {
			texts.put((Criterion) t.getData("criterion"), t);
		}
		
		for(Criterion c: (List<Criterion>) _providerCriteria.getInput()) {
			Combo combo = combos.get(c);
			if(combo.getSelectionIndex() != 2) {
				texts.get(c).setBackground(new Color(Display.getCurrent(), 203, 255, 203));
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	private void weightsNoCorrect() {
		Map<Criterion, Combo> combos = new HashMap<Criterion, Combo>();
		for(Combo c: _combos) {
			combos.put((Criterion) c.getData("criterion"), c);
		}
		
		Map<Criterion, Text> texts = new HashMap<Criterion, Text>();
		for(Text t: _texts) {
			texts.put((Criterion) t.getData("criterion"), t);
		}
		
		for(Criterion c: (List<Criterion>) _providerCriteria.getInput()) {
			Combo combo = combos.get(c);
			if(combo.getSelectionIndex() != 2) {
				texts.get(c).setBackground(new Color(Display.getCurrent(), 255, 203, 203));
			}
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		_okButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		_okButton.setEnabled(false);
		_nameControlDecoration.show();
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		
		_okButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
				ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
				
				Map<Criterion, List<Object>> data = new LinkedHashMap<>();
				for(Criterion numerator: _criteriaNumerator) {
					List<Object> posAndWeight = new LinkedList<Object>();
					posAndWeight.add(0);
					posAndWeight.add(_criteriaWeight.get(numerator));
					data.put(numerator, posAndWeight);
				}
				
				for(Criterion denominator: _criteriaDenominator) {
					List<Object> posAndWeight = new LinkedList<Object>();
					posAndWeight.add(1);
					posAndWeight.add(_criteriaWeight.get(denominator));
					data.put(denominator, posAndWeight);
				}
				MEC mec = new MEC(_mecName, _formulaImage, (LinkedHashMap<Criterion, List<Object>>) data);
				
				elementsSet.addMEC(mec);
			}
		});
	}

	@Override
	protected Point getInitialSize() {
		return new Point(500, 600);
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
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("New ME");
	}

	public void createFormula() {
		TeXFormula formula;
		
		if(_numerator.isEmpty() && !_denominator.isEmpty()) {
			String formulaText = "\\mbox{MEC=\\dfrac{1}{" + _denominator + "}}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			formula = new TeXFormula(formulaText);
		} else if(!_numerator.isEmpty() && _denominator.isEmpty()) {
			formula = new TeXFormula("\\mbox{MEC=" + _numerator + "}"); //$NON-NLS-1$
		} else {
			String formulaText = "\\mbox{MEC=\\dfrac{" + _numerator + "}{" + _denominator + "}}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			formula = new TeXFormula(formulaText);
		}

		TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 12);
		icon.setInsets(new Insets(5, 5, 5, 5));

		BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphicsImage = image.createGraphics();
		graphicsImage.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
		icon.paintIcon(null, graphicsImage, 0, 0);
		
		Graphics g = _frame.getGraphics();
		int width = image.getWidth();
		int height = image.getHeight();
		g.drawImage(image, (_frame.getWidth() - width) / 2, (_frame.getHeight() - height) / 2 , null);
		
		ImageData formulaImageData = convertToSWT(image);
		formulaImageData.transparentPixel = formulaImageData.getPixel(0, 0);
		_formulaImage = new Image(Display.getCurrent(), formulaImageData);
	}

	private ImageData convertToSWT(BufferedImage bufferedImage) {
		DirectColorModel colorModel = (DirectColorModel) bufferedImage
				.getColorModel();
		PaletteData palette = new PaletteData(colorModel.getRedMask(),
				colorModel.getGreenMask(), colorModel.getBlueMask());
		ImageData data = new ImageData(bufferedImage.getWidth(),
				bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
		for (int y = 0; y < data.height; y++) {
			for (int x = 0; x < data.width; x++) {
				int rgb = bufferedImage.getRGB(x, y);
				int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF,
						(rgb >> 8) & 0xFF, rgb & 0xFF));
				data.setPixel(x, y, pixel);
				if (colorModel.hasAlpha()) {
					data.setAlpha(x, y, (rgb >> 24) & 0xFF);
				}
			}
		}
		return data;
	}

	private void checkAvailableData() {
		List<Button> buttonsCriteria = CriteriaView.getButtons();
		
		TableItem[] tableItems = _tableViewerCriteria.getTable().getItems();
		Map<Criterion, TableItem> tableItemsCriteria = new HashMap<Criterion, TableItem>();
		for(TableItem ti: tableItems) {
			tableItemsCriteria.put((Criterion) ti.getData(), ti);
		}
		Map<Criterion, Combo> combos = new HashMap<Criterion, Combo>();
		for(Combo c: _combos) {
			combos.put((Criterion) c.getData("criterion"), c);
		}
		
		Map<Criterion, Text> texts = new HashMap<Criterion, Text>();
		for(Text t: _texts) {
			texts.put((Criterion) t.getData("criterion"), t);
		}
		
		for(Button b: buttonsCriteria) {
			if(!b.isEnabled()) {
				TableItem ti = tableItemsCriteria.get(b.getData("criterion"));
				ti.setForeground(new Color(Display.getCurrent(), 211, 211, 211));
				Combo c = combos.get(b.getData("criterion"));
				c.setEnabled(false);
				Text t = texts.get(b.getData("criterion"));
				t.setEnabled(false);
			}
		}

	}
}
