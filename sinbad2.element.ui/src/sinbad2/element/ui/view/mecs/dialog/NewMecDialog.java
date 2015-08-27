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
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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
import sinbad2.element.alternative.Alternative;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.mec.MEC;
import sinbad2.element.mec.listener.EMECsChange;
import sinbad2.element.mec.listener.MECsChangeEvent;
import sinbad2.element.ui.nls.Messages;
import sinbad2.element.ui.view.alternatives.provider.AlternativeCampaignIdLabelProvider;
import sinbad2.element.ui.view.alternatives.provider.AlternativesCampaignsContentProvider;
import sinbad2.element.ui.view.campaigns.CampaignsView;
import sinbad2.element.ui.view.criteria.provider.CriteriaCampaignsContentProvider;
import sinbad2.element.ui.view.criteria.provider.CriterionCampaignIdLabelProvider;

public class NewMecDialog extends Dialog {

	private String _mecName;
	private String _numerator;
	private String _denominator;
	private Button _okButton;
	private ControlDecoration _nameControlDecoration;

	private List<Alternative> _alternativesSelected;
	private List<Criterion> _criteriaSelected;
	private Button _addAlternatives;
	private Button _addCriteria;
	
	private Image _formulaImage;
	
	private TableViewer _tableViewerAlternatives;
	private TableViewer _tableViewerCriteria;
	private AlternativesCampaignsContentProvider _providerAlternatives;
	private CriteriaCampaignsContentProvider _providerCriteria;
	
	private static Frame _frame;

	public NewMecDialog() {
		super(Display.getCurrent().getActiveShell());
		_mecName = ""; //$NON-NLS-1$
		_numerator = ""; //$NON-NLS-1$
		_denominator = ""; //$NON-NLS-1$
		_alternativesSelected = new LinkedList<Alternative>();
		_criteriaSelected = new LinkedList<Criterion>();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Control createDialogArea(Composite parent) {
		
		//SaveImages.saveImages();
		
		Composite container = (Composite) super.createDialogArea(parent);
		
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginRight = 10;
		gridLayout.marginTop = 10;
		gridLayout.marginLeft = 10;
		gridLayout.marginBottom = 10;
		container.setLayout(gridLayout);

		Label idLabel = new Label(container, SWT.NULL);
		GridData gridData = new GridData(SWT.CENTER, SWT.CENTER, true, false,
				4, 1);
		idLabel.setLayoutData(gridData);
		idLabel.setText(Messages.NewMecDialog_ME_Name);
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

		_providerCriteria = new CriteriaCampaignsContentProvider(
				_tableViewerCriteria);
		_tableViewerCriteria.setContentProvider(_providerCriteria);
		_tableViewerCriteria.getTable().setHeaderVisible(true);
		GridData gd_table = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		_tableViewerCriteria.getTable().setLayoutData(gd_table);
		_tableViewerCriteria.getTable().addListener(SWT.MeasureItem,
				new Listener() {

					@Override
					public void handleEvent(Event event) {
						//event.height = 25;

					}
				});

		addColumnsCriteria(_tableViewerCriteria);

		_tableViewerCriteria.setInput(_providerCriteria.getInput());
		_providerCriteria.pack();
		_criteriaSelected.addAll((List<Criterion>) _providerCriteria.getInput());
		
		_addCriteria = new Button(container, SWT.NULL);
		_addCriteria.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false,
				false, 1, 1));
		_addCriteria.setText(Messages.NewMecDialog_Add_Criteria_Button);
		_addCriteria.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_ADD).createImage());
		_addCriteria
				.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		_addCriteria.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(!_numerator.isEmpty()) {
					_numerator += ", "; //$NON-NLS-1$
				}
				
				if(!_denominator.isEmpty()) {
					_denominator += ", "; //$NON-NLS-1$
				}
				
				for (int i = 0; i < _criteriaSelected.size(); ++i) {
					Criterion c = _criteriaSelected.get(i);
					if(!c.getIsDirect()) {
						if(!_numerator.contains(c.getId())){
							_numerator += c.getId() + " ,"; //$NON-NLS-1$
						}
					} else {
						if(!_denominator.contains(c.getId())){
							_denominator += c.getId() + " ,"; //$NON-NLS-1$
						}
					}
				}
				
				if(_numerator.endsWith(",")) { //$NON-NLS-1$
					 _numerator = _numerator.substring(0, _numerator.length() - 1);
				}
				
				if(_denominator.endsWith(",")) { //$NON-NLS-1$
					_denominator = _denominator.substring(0, _denominator.length() - 1);
				}
				
				createFormula();
				validate();
			}
		});

		_tableViewerAlternatives = new TableViewer(container, SWT.CENTER | SWT.BORDER | SWT.FULL_SELECTION);

		_providerAlternatives = new AlternativesCampaignsContentProvider(
				_tableViewerAlternatives);
		_tableViewerAlternatives.setContentProvider(_providerAlternatives);
		_tableViewerAlternatives.getTable().setHeaderVisible(true);
		gd_table = new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1);
		_tableViewerAlternatives.getTable().setLayoutData(gd_table);
		_tableViewerAlternatives.getTable().addListener(SWT.MeasureItem,
				new Listener() {

					@Override
					public void handleEvent(Event event) {
						//event.height = 25;

					}
				});

		addColumnsAlternative(_tableViewerAlternatives);

		_tableViewerAlternatives.setInput(_providerAlternatives.getInput());
		_providerAlternatives.pack();
		_alternativesSelected.addAll((List<Alternative>) _providerAlternatives
				.getInput());

		_addAlternatives = new Button(container, SWT.NULL);
		_addAlternatives.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false,
				false, 1, 1));
		_addAlternatives.setText(Messages.NewMecDialog_Add_Alternatives_Button);
		_addAlternatives.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_ADD).createImage());
		_addAlternatives.setBackground(new Color(Display.getCurrent(), 255, 255,
				255));

		_addAlternatives.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				
				if(!_numerator.isEmpty()) {
					_numerator += ", "; //$NON-NLS-1$
				}
				
				for (int i = 0; i < _alternativesSelected.size(); ++i) {
					Alternative a = _alternativesSelected.get(i);
					if(!_numerator.contains(a.getId())) {
						_numerator += a.getId() + " ,"; //$NON-NLS-1$
					}
				}
				
				if(_numerator.endsWith(",")) { //$NON-NLS-1$
					 _numerator = _numerator.substring(0, _numerator.length() - 1);
				}
				
				createFormula();
				validate();
			}
		});

		Composite composite = new Composite(container, SWT.EMBEDDED | SWT.BORDER);
		gridData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		composite.setLayoutData(gridData);
		_frame = SWT_AWT.new_Frame(composite);
		_frame.setLayout(new BorderLayout());
		_frame.setBackground(java.awt.Color.WHITE);
		_frame.setVisible(true);

		Button clearFormula = new Button(container, SWT.NULL);
		clearFormula.setLayoutData(new GridData(SWT.RIGHT, SWT.RIGHT, false,
				false, 1, 1));
		clearFormula.setText(Messages.NewMecDialog_Clear_Button);
		clearFormula.setImage(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR).createImage());
		clearFormula
				.setBackground(new Color(Display.getCurrent(), 255, 255, 255));
		
		clearFormula.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				_numerator = ""; //$NON-NLS-1$
				_denominator = ""; //$NON-NLS-1$
				_formulaImage = null;
				_frame.repaint();
				_okButton.setEnabled(false);
			}
		});
		
		return container;
	}

	private void addColumnsAlternative(TableViewer tableViewer) {
		TableViewerColumn tvc = new TableViewerColumn(tableViewer, SWT.CENTER);
		tvc.setLabelProvider(new AlternativeCampaignIdLabelProvider());
		TableColumn tc = tvc.getColumn();
		tc.setText(Messages.NewMecDialog_Context_Column);
		tc.setResizable(false);
		tc.pack();

		tvc = new TableViewerColumn(tableViewer, SWT.CENTER);
		tc = tvc.getColumn();
		tc.setText(Messages.NewMecDialog_Selection_Column);
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
					button = new Button((Composite) cell.getViewerRow()
							.getControl(), SWT.CHECK);
					buttons.put(cell.getElement(), button);
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							@SuppressWarnings("unchecked")
							List<Alternative> alternatives = (List<Alternative>) _providerAlternatives.getInput();
							if (((Button) e.widget).getSelection()) {
								_alternativesSelected.add(alternatives.get(_tableViewerAlternatives.getTable().indexOf(item)));
							} else {
								if (!_alternativesSelected.isEmpty()) {
									_alternativesSelected.remove(alternatives.get(_tableViewerAlternatives.getTable().indexOf(item)));
								}
								Collections.sort(_alternativesSelected);
							}
							
							if(_alternativesSelected.isEmpty()) {
								_addAlternatives.setEnabled(false);
							} else {
								_addAlternatives.setEnabled(true);
							}
						};
					});
				}
				button.setSelection(true);
				TableEditor editor = new TableEditor(item.getParent());
				button.pack();
				editor.minimumWidth = button.getSize().x;
				editor.horizontalAlignment = SWT.CENTER;
				editor.setEditor(button, item, cell.getColumnIndex());
				editor.layout();
			}

		});

	}

	private void addColumnsCriteria(TableViewer tableViewer) {
		TableViewerColumn tvc = new TableViewerColumn(tableViewer, SWT.CENTER);
		tvc.setLabelProvider(new CriterionCampaignIdLabelProvider());
		TableColumn tc = tvc.getColumn();
		tc.setText(Messages.NewMecDialog_Index_Column);
		tc.setResizable(false);
		tc.pack();

		tvc = new TableViewerColumn(tableViewer, SWT.CENTER);
		tc = tvc.getColumn();
		tc.setText(Messages.NewMecDialog_Selection_Column);
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
					button = new Button((Composite) cell.getViewerRow()
							.getControl(), SWT.CHECK);
					buttons.put(cell.getElement(), button);
					
					button.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							@SuppressWarnings("unchecked")
							List<Criterion> criteria = (List<Criterion>) _providerCriteria.getInput();
							if (((Button) e.widget).getSelection()) {
								_criteriaSelected.add(criteria.get(_tableViewerCriteria.getTable().indexOf(item)));
							} else {
								if (!_criteriaSelected.isEmpty()) {
									_criteriaSelected.remove(criteria.get(_tableViewerCriteria.getTable().indexOf(item)));
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
				button.setSelection(true);
				
				TableEditor editor = new TableEditor(item.getParent());
				button.pack();
				editor.minimumWidth = button.getSize().x;
				editor.horizontalAlignment = SWT.CENTER;
				editor.setEditor(button, item, cell.getColumnIndex());
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
		boolean validId;

		String message = ""; //$NON-NLS-1$

		if (!_mecName.isEmpty()) {
			Campaign campaignSelected = CampaignsView.getCampaignsSelected().get(0);
			for(MEC m : campaignSelected.getMECs()) {
				if(m.getId().equals(_mecName)) {
					message = Messages.NewMecDialog_Duplicate_Id;
				}
			}
		} else {
			message = Messages.NewMecDialog_Empty_Name;
		}

		validId = validate(_nameControlDecoration, message);

		_okButton.setEnabled(validId && _formulaImage != null);
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
				MEC mec = new MEC(_mecName, _formulaImage, _alternativesSelected, _criteriaSelected);
				
				elementsSet.notifyMECsChanges(new MECsChangeEvent(EMECsChange.ADD_MEC, null, mec, false));
			}
		});
	}

	@Override
	protected Point getInitialSize() {
		return new Point(600, 630);
	}
	
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(Messages.NewMecDialog_New_ME);
	}

	public void createFormula() {
		TeXFormula formula;
		
		if(_numerator.isEmpty() && !_denominator.isEmpty()) {
			String formulaText = "ME=\\frac{1}{" + _denominator + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			formula = new TeXFormula(formulaText);
		} else if(!_numerator.isEmpty() && _denominator.isEmpty()) {
			formula = new TeXFormula("ME=" + _numerator); //$NON-NLS-1$
		} else {
			String formulaText = "ME=\\frac{" + _numerator + "}{" + _denominator + "}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			formula = new TeXFormula(formulaText);
		}

		TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 12);
		icon.setInsets(new Insets(_frame.getHeight() / 4, 5, 5, 5));

		BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphicsImage = image.createGraphics();
		graphicsImage.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
		icon.paintIcon(null, graphicsImage, 0, 0);
		
		Graphics g = _frame.getGraphics();
		g.drawImage(image, 0, 0, null);
		
		ImageData formulaImageData = convertToSWT(image);
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
}
