package sinbad2.element.ui.handler.pdf.generate;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.osgi.framework.Bundle;

import sinbad2.element.alternative.Alternative;
import sinbad2.element.campaigns.Campaign;
import sinbad2.element.criterion.Criterion;
import sinbad2.element.mec.MEC;
import sinbad2.element.ui.view.mecs.jfreechart.MECChart;
import sinbad2.element.ui.wizard.GeneratePDFWizard;
import sinbad2.element.ui.wizard.SelectAlternativesWizardPage;
import sinbad2.element.ui.wizard.SelectCampaignsWizardPage;
import sinbad2.element.ui.wizard.SelectChartWizardPage;
import sinbad2.element.ui.wizard.SelectMEsWizardPage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.itextpdf.text.Anchor;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Section;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.PageSize;

public class GeneratePDFHandler extends AbstractHandler {

	private static Document _document;

	private static int _tableSelected;
	private static java.util.List<String> _chartsSelected;
	private static java.util.List<Campaign> _campaignsSelected;
	private static int _aggregationSelected;
	private static java.util.List<String> _desaggregationOption;
	private static java.util.List<Alternative> _alternativesSelected;
	private static java.util.List<MEC> _mecsSelected;

	private static String NAME_FILE = "/prueba.png";
	public static final String RESOURCE = "icons/%s.png";

	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
			Font.BOLD);
	private static Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
			Font.NORMAL);
	private static Font boldFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
			Font.BOLD);
	private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
			Font.NORMAL, BaseColor.RED);
	private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 13,
			Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
			Font.BOLD);

	public class PageStamper extends PdfPageEventHelper {
		PdfTemplate total;

		@Override
		public void onEndPage(PdfWriter writer, Document document) {
			
			if(writer.getPageNumber() == 1) {
				try {
					Bundle bundle = Platform.getBundle("sinbad2.element.ui");
					Path path = new Path("/icons/ministerio_del_interior.png");
					Path path_logo = new Path("/icons/logo_esquina.png");
					URL fileURL = FileLocator.find(bundle, path, null);
					URL fileLogoURL = FileLocator.find(bundle, path_logo, null);
					URL resolved = null, resolvedLogo = null;
					try {
					    resolved = FileLocator.resolve(fileURL);
					    resolvedLogo = FileLocator.resolve(fileLogoURL);
					} catch (IOException e) {
					    throw new RuntimeException(e);
					}
					
					Image[] img = {Image.getInstance(String.format(resolved.getPath(), "")), Image.getInstance(String.format(resolvedLogo.getPath(), "")) };
					PdfPTable table_header = new PdfPTable(2);
					table_header.setHeaderRows(12);
					table_header.setWidths(new int[]{12, 12});
	            	table_header.setTotalWidth(803);
	            	table_header.setLockedWidth(true);
	            	table_header.getDefaultCell().setFixedHeight(10);
	                table_header.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	                PdfPCell cell = new PdfPCell(Image.getInstance(img[0]));
	                cell.setBorder(Rectangle.NO_BORDER);
	                table_header.addCell(cell);
	                cell = new PdfPCell(Image.getInstance(img[1]));
	                cell.setBorder(Rectangle.NO_BORDER);
	                table_header.addCell(cell);
	                table_header.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
				} catch (IOException | DocumentException e) {
					e.printStackTrace();
				}
			} else {
				PdfPTable table_pages = new PdfPTable(3);
	            try {           	
	                table_pages.setWidths(new int[]{24, 24, 2});
	                table_pages.setTotalWidth(527);
	                table_pages.setLockedWidth(true);
	                table_pages.getDefaultCell().setFixedHeight(20);
	                table_pages.getDefaultCell().setBorder(Rectangle.NO_BORDER);
	                table_pages.addCell("");
	                table_pages.getDefaultCell().setHorizontalAlignment(Element.ALIGN_RIGHT);
	                table_pages.addCell("");
	                table_pages.addCell(String.format("%d", writer.getPageNumber()));
	                table_pages.writeSelectedRows(0, -1, 34, 78, writer.getDirectContent());
	            }
	            catch(DocumentException de) {
	                de.printStackTrace();
	            }
			}
		}

		@Override
		public void onOpenDocument(PdfWriter writer, Document document) {
			total = writer.getDirectContent().createTemplate(30, 16);
		}

		@Override
		public void onCloseDocument(PdfWriter writer, Document document) {
			ColumnText.showTextAligned(total, Element.ALIGN_RIGHT, new Phrase(String.valueOf(writer.getPageNumber() - 1)), 9, 2, 0);
		}
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), new GeneratePDFWizard());
		dialog.open();

		_tableSelected = SelectChartWizardPage.getInformationTable();
		_chartsSelected = SelectChartWizardPage.getInformationCharts();
		_campaignsSelected = SelectCampaignsWizardPage.getInformationCampaigns();
		_aggregationSelected = SelectCampaignsWizardPage.getInformationAggregation();
		_desaggregationOption = SelectCampaignsWizardPage.getInformationDesaggregationOption();
		_alternativesSelected = SelectAlternativesWizardPage.getInformationAlternatives();
		_mecsSelected = SelectMEsWizardPage.getInformationMECs();

		try {
			_document = new Document(PageSize.A4, 36, 36, 54, 36);
			
			FileDialog dialogSave = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
			dialogSave.setFilterNames(new String[] { "PDF Files", "All Files (*.*)" });
			dialogSave.setFilterExtensions(new String[] { "*.pdf", "*.*" });
			dialogSave.setFilterPath(System.getProperty("user.name"));
			dialogSave.setFileName("Analysis.pdf");
			if(!_alternativesSelected.isEmpty()) {
				String path = dialogSave.open();
				PdfWriter writer = PdfWriter.getInstance(_document, new FileOutputStream(path));
				writer.setPageEvent(new PageStamper());

				_document.open();
				addMetaData();
				addTitlePage();
				addContent();
				_document.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static void addMetaData() {
		_document.addTitle("Decision-MEC Analysis");
		_document.addSubject("DGT Analysis Campaigns");
		_document.addKeywords("chart, campaign, context, me");
		_document.addAuthor("DGT");
		_document.addCreator("DGT");
	}

	private static void addTitlePage() throws DocumentException {
		Paragraph preface = new Paragraph();

		addEmptyLine(preface, 13);
		
		Bundle bundle = Platform.getBundle("sinbad2.element.ui");
		Path path = new Path("/icons/logo.png");
		URL fileURL = FileLocator.find(bundle, path, null);
		URL resolved = null;
		try {
		    resolved = FileLocator.resolve(fileURL);
		    Image logo = Image.getInstance(String.format(resolved.getPath(), ""));
		    preface.add(logo);
		} catch (IOException e) {
		    throw new RuntimeException(e);
		}
		
		addEmptyLine(preface, 13);
		
		preface.add(new Paragraph("Report generated by: " + System.getProperty("user.name") + ", " + new Date(), smallBold));
		

		_document.add(preface);
		_document.newPage();
	}

	private static void addContent() throws DocumentException {
		Anchor anchor = new Anchor("Data campaigns", catFont);
		anchor.setName("Data campaigns");

		Chapter catPart = new Chapter(new Paragraph(anchor), 1);

		for (MEC mec : _mecsSelected) {
			Paragraph subPara = new Paragraph(mec.getId(), subFont);
			addEmptyLine(subPara, 1);
			Section subCatPart = catPart.addSection(subPara);
			addEmptyLine(subPara, 1);
			if (_tableSelected == 1) {
				createTableMEC(subCatPart, mec);
				if (!_chartsSelected.isEmpty()) {
					if (_chartsSelected.size() == 1) {
						if (_chartsSelected.get(0).equals("0")) {
							createBarCharts(subCatPart, mec);
						} else {
							createLineCharts(subCatPart, mec);
						}
					} else {
						createBarCharts(subCatPart, mec);
						createLineCharts(subCatPart, mec);
					}
				}
			} else {
				if (!_chartsSelected.isEmpty()) {
					if (_chartsSelected.size() == 1) {
						if (_chartsSelected.get(0).equals("0")) {
							createBarCharts(subCatPart, mec);
						} else {
							createLineCharts(subCatPart, mec);
						}
					} else {
						createBarCharts(subCatPart, mec);
						createLineCharts(subCatPart, mec);
					}
				}
			}
			addEmptyLine(subPara, 1);
		}

		_document.add(catPart);

		anchor = new Anchor("Second Chapter", catFont);
		anchor.setName("Second Chapter");

		catPart = new Chapter(new Paragraph(anchor), 1);

		_document.add(catPart);

	}

	private static void createTableMEC(Section subCatPart, MEC mec)
			throws BadElementException {
		if (_aggregationSelected == 0) {
			PdfPTable table = new PdfPTable(3);

			PdfPCell c1 = new PdfPCell(new Phrase("Criterion", boldFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBackgroundColor(new BaseColor(197, 214, 255));
			table.addCell(c1);

			c1 = new PdfPCell(new Phrase("Alternative", boldFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBackgroundColor(new BaseColor(197, 214, 255));
			table.addCell(c1);

			c1 = new PdfPCell(new Phrase("Value", boldFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBackgroundColor(new BaseColor(197, 214, 255));
			table.addCell(c1);
			table.setHeaderRows(1);

			aggregateCampaings(table, mec);
			subCatPart.add(table);
		} else {
			desaggregateCampaigns(subCatPart, mec);
		}
	}

	private static void aggregateCampaings(PdfPTable table, MEC mec) {
		Map<Criterion, Map<Alternative, Double>> criteriaWithValueAcumAlternatives = new LinkedHashMap<Criterion, Map<Alternative, Double>>();
		for (Campaign campaign : _campaignsSelected) {
			java.util.List<Criterion> criteriaMEC = mec.getAvailableCriteria();
			for (Criterion criterion : criteriaMEC) {
				for (Alternative alternative : _alternativesSelected) {
					if (campaign.getValue(criterion, alternative) != 0) {
						if (criteriaWithValueAcumAlternatives.get(criterion) != null) {
							Map<Alternative, Double> alternativesValuesAcum = criteriaWithValueAcumAlternatives
									.get(criterion);
							if (alternativesValuesAcum.get(alternative) != null) {
								double valueAcum = alternativesValuesAcum
										.get(alternative)
										+ campaign.getValue(criterion,
												alternative);
								alternativesValuesAcum.put(alternative,
										valueAcum);
								criteriaWithValueAcumAlternatives.put(
										criterion, alternativesValuesAcum);
							} else {
								alternativesValuesAcum.put(alternative,
										campaign.getValue(criterion,
												alternative));
								criteriaWithValueAcumAlternatives.put(
										criterion, alternativesValuesAcum);
							}
						} else {
							Map<Alternative, Double> alternativesValuesAcum = new LinkedHashMap<Alternative, Double>();
							alternativesValuesAcum.put(alternative,
									campaign.getValue(criterion, alternative));
							criteriaWithValueAcumAlternatives.put(criterion,
									alternativesValuesAcum);
						}
					}
				}
			}
		}

		PdfPCell cell;
		for (Criterion c : criteriaWithValueAcumAlternatives.keySet()) {
			Map<Alternative, Double> aValues = criteriaWithValueAcumAlternatives
					.get(c);
			cell = new PdfPCell(new Phrase(c.getId(), normalFont));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setRowspan(aValues.size());
			table.addCell(cell);
			for (Alternative a : aValues.keySet()) {
				cell = new PdfPCell(new Phrase(a.getId(), normalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
				cell = new PdfPCell(new Phrase(Double.toString(aValues.get(a)),
						normalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
			}
		}
	}

	private static void desaggregateCampaigns(Section subCatPart, MEC mec) {
		for (Campaign campaign : _campaignsSelected) {
			PdfPTable table = new PdfPTable(3);

			PdfPCell c1 = new PdfPCell(new Phrase("Criterion", normalFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);

			c1 = new PdfPCell(new Phrase("Alternative", normalFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);

			c1 = new PdfPCell(new Phrase("Value", normalFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(c1);
			table.setHeaderRows(1);

			PdfPCell cell;
			java.util.List<Criterion> criteriaMEC = mec.getAvailableCriteria();
			for (Criterion criterion : criteriaMEC) {
				for (Alternative alternative : _alternativesSelected) {
					cell = new PdfPCell(new Phrase(criterion.getId(),
							normalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
					cell = new PdfPCell(new Phrase(alternative.getId(),
							normalFont));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					table.addCell(cell);
					if (campaign.getValue(criterion, alternative) != 0) {
						cell = new PdfPCell(new Phrase(Double.toString(campaign
								.getValue(criterion, alternative)), normalFont));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
					} else {
						cell = new PdfPCell(new Phrase(Double.toString(campaign
								.getAcumValue(criterion, alternative,
										_alternativesSelected)), normalFont));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
					}
				}
			}
			subCatPart.add(table);
			Paragraph subPara = new Paragraph("", subFont);
			subCatPart.add(subPara);
			addEmptyLine(subPara, 1);
		}
	}

	private static void createBarCharts(Section subCatPart, MEC mec)
			throws BadElementException {
		MECChart chart = new MECChart();

		if (_aggregationSelected == 0) {
			java.util.List<Campaign> campaigns = new LinkedList<Campaign>();
			for (Campaign campaign : _campaignsSelected) {
				campaigns.add(campaign);
			}
			chart.createChartByPDF(campaigns, mec, 0, "combine",
					_alternativesSelected);
			JFreeChart barChart = chart.getBarChart();
			generatePNGChart(barChart, subCatPart);
		} else {
			java.util.List<Campaign> campaigns = new LinkedList<Campaign>();
			for (Campaign campaign : _campaignsSelected) {
				Campaign clone = (Campaign) campaign.clone();
				clone.setName(campaign.getId() + "_" + campaign.getName() + "("
						+ campaign.getInitialDate() + "-"
						+ campaign.getFinalDate() + ")");
				campaigns.add(clone);
			}
			JFreeChart barChart = null;
			for (String action : _desaggregationOption) {
				if (!action.equals("contexts")) {
					chart.createChartByPDF(campaigns, mec, 0, action,
							_alternativesSelected);
					barChart = chart.getBarChart();
				} else {
					chart.createChartByPDF(campaigns, mec, 2, action,
							_alternativesSelected);
					barChart = chart.getStackedChart();
				}
				generatePNGChart(barChart, subCatPart);
			}
		}
	}

	private static void generatePNGChart(JFreeChart chart, Section subCatPart) {
		File file = new File(System.getProperty("user.home") + NAME_FILE);
		try {
			ChartUtilities.saveChartAsPNG(file, chart, 530, 200);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Image pdfImage;
		try {
			pdfImage = com.itextpdf.text.Image.getInstance(System
					.getProperty("user.home") + NAME_FILE);
			subCatPart.add(pdfImage);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BadElementException e) {
			e.printStackTrace();
		}
	}

	private static void createLineCharts(Section subCatPart, MEC mec)
			throws BadElementException {
		MECChart chart = new MECChart();

		if (_aggregationSelected == 0) {
			java.util.List<Campaign> campaigns = new LinkedList<Campaign>();
			for (Campaign campaign : _campaignsSelected) {
				campaigns.add(campaign);
			}
			chart.createChartByPDF(campaigns, mec, 1, "combine",
					_alternativesSelected);
			JFreeChart lineChart = chart.getLineChart();
			generatePNGChart(lineChart, subCatPart);
		} else {
			java.util.List<Campaign> campaigns = new LinkedList<Campaign>();
			for (Campaign campaign : _campaignsSelected) {
				Campaign clone = (Campaign) campaign.clone();
				clone.setName(campaign.getId() + "_" + campaign.getName() + "("
						+ campaign.getInitialDate() + "-"
						+ campaign.getFinalDate() + ")");
				campaigns.add(clone);
			}

			JFreeChart lineChart = null;
			for (String action : _desaggregationOption) {
				if (action.equals("separate")) {
					action = "combine";
				}
				chart.createChartByPDF(campaigns, mec, 1, action,
						_alternativesSelected);
				lineChart = chart.getLineChart();
				generatePNGChart(lineChart, subCatPart);
			}
		}
	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

}
