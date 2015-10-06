package sinbad2.element.ui.handler.pdf.generate;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.window.Window;
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
import java.util.LinkedList;
import java.util.List;

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
	private static PdfWriter _writer; 

	private static int _tableSelected;
	private static java.util.List<String> _chartsSelected;
	private static java.util.List<Campaign> _campaignsSelected;
	private static int _aggregationSelected;
	private static java.util.List<String> _desaggregationOption;
	private static java.util.List<Alternative> _alternativesSelected;
	private static java.util.List<MEC> _mecsSelected;

	private static String NAME_FILE = "/prueba.png";

	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	private static Font normalFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL);
	private static Font whiteFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.WHITE);
	private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 15, Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);

	private static final BaseColor COLOR_HEADER_TABLE = new BaseColor(65, 50, 186);
	private static final BaseColor COLOR_PARENT_TABLE = new BaseColor(197, 214, 255);
	
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
	                table_pages.writeSelectedRows(0, -1, 34, 60, writer.getDirectContent());
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

		try {
			_document = new Document(PageSize.A4, 36, 36, 54, 70);
			
			FileDialog dialogSave = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.SAVE);
			dialogSave.setFilterNames(new String[] { "PDF Files", "All Files (*.*)" });
			dialogSave.setFilterExtensions(new String[] { "*.pdf", "*.*" });
			dialogSave.setFilterPath(System.getProperty("user.name"));
			dialogSave.setFileName("Analysis.pdf");
			
			if(dialog.open() == Window.OK) {
				
				_tableSelected = SelectChartWizardPage.getInformationTable();
				_chartsSelected = SelectChartWizardPage.getInformationCharts();
				_campaignsSelected = SelectCampaignsWizardPage.getInformationCampaigns();
				_aggregationSelected = SelectCampaignsWizardPage.getInformationAggregation();
				_desaggregationOption = SelectCampaignsWizardPage.getInformationDesaggregationOption();
				_alternativesSelected = SelectAlternativesWizardPage.getInformationAlternatives();
				_mecsSelected = SelectMEsWizardPage.getInformationMECs();
				
				String path = dialogSave.open();
				_writer = PdfWriter.getInstance(_document, new FileOutputStream(path));
				_writer.setPageEvent(new PageStamper());
				_writer.setPageEmpty(false);

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
		Anchor anchor = new Anchor("Campaigns Analysis", catFont);
		Paragraph empty = new Paragraph();
		addEmptyLine(empty, 1);
		anchor.add(empty);
		anchor.setName("Campaigns Analysis");
		
		Chapter catPart = new Chapter(new Paragraph(anchor), 1);
		
		for (MEC mec : _mecsSelected) {
			
			Paragraph subPara = new Paragraph(mec.getId(), subFont);
			addEmptyLine(subPara, 1);
			Section subCatPart = catPart.addSection(subPara);
			
			if (_tableSelected == 1) {
				createTableMEC(subCatPart, mec);
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
		}
		_document.add(catPart);
		//_document.newPage();
	    //_writer.setPageEmpty(false);
	}

	private static void createTableMEC(Section subCatPart, MEC mec) throws DocumentException {
		if (_aggregationSelected == 0) {
			PdfPTable table = new PdfPTable(3);
			table.setHeaderRows(20);
			table.setWidths(new int[]{12, 12, 12});
			table.setTotalWidth(510);
			table.setLockedWidth(true);
			table.getDefaultCell().setFixedHeight(12);

			PdfPCell c1 = new PdfPCell(new Phrase("Criterion", whiteFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBackgroundColor(COLOR_HEADER_TABLE);
			table.addCell(c1);
			
			c1 = new PdfPCell(new Phrase("Alternative", whiteFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBackgroundColor(COLOR_HEADER_TABLE);
			table.addCell(c1);

			c1 = new PdfPCell(new Phrase("Value", whiteFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBackgroundColor(COLOR_HEADER_TABLE);
			table.addCell(c1);
			table.setHeaderRows(1);

			aggregateCampaings(table, mec);
			subCatPart.add(table);
		} else {
			desaggregateCampaigns(subCatPart, mec);
		}
		
		Paragraph empty = new Paragraph();
		addEmptyLine(empty, 1);
		subCatPart.add(empty);
	}

	private static void aggregateCampaings(PdfPTable table, MEC mec) {
		PdfPCell cell;
		for(Campaign campaign: _campaignsSelected) {
			for (Criterion c : mec.getCriteria().keySet()) {
				cell = new PdfPCell(new Phrase(c.getId(), normalFont));
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell.setRowspan(_alternativesSelected.size());
				table.addCell(cell);
				for (Alternative a : _alternativesSelected) {
					if(a.hasChildrens()) {
						cell = new PdfPCell(new Phrase(a.getId(), normalFont));
						cell.setBackgroundColor(COLOR_PARENT_TABLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(Double.toString(getTotalValueMEC(mec, campaign, a))));
						cell.setBackgroundColor(COLOR_PARENT_TABLE);
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
					} else { 
						cell = new PdfPCell(new Phrase(a.getId(), normalFont));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(Double.toString(getValueMEC(mec, campaign, a)), normalFont));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
					}
				}
			}
		}
	}
	
	private static double getTotalValueMEC(MEC mec, Campaign campaign, Alternative parent) {
		double result = 0;
		for(Alternative children: parent.getChildrens()) {
			result += getValueMEC(mec, campaign, children);
		}
		
		return result;
	}

	private static double getValueMEC(MEC mec, Campaign campaign, Alternative alternative) {
		double numerator = 1, denominator = 1;
		
		List<Object> positionAndWeigth;
		for(Criterion criterion: mec.getCriteria().keySet()) {
			positionAndWeigth = mec.getCriteria().get(criterion);
			if((Integer) positionAndWeigth.get(0) == 0) {
				numerator *= campaign.getValue(criterion, alternative) * (Double) positionAndWeigth.get(1);
			} else {
				denominator *= campaign.getValue(criterion, alternative) * (Double) positionAndWeigth.get(1);
			}
		}
		
		return numerator / denominator;
	}

	private static void desaggregateCampaigns(Section subCatPart, MEC mec) throws DocumentException {
		for (Campaign campaign : _campaignsSelected) {
			PdfPTable table = new PdfPTable(3);
			table.setHeaderRows(20);
			table.setWidths(new int[]{12, 12, 12});
			table.setTotalWidth(510);
			table.setLockedWidth(true);
			table.getDefaultCell().setFixedHeight(12);

			PdfPCell c1 = new PdfPCell(new Phrase("Criterion", whiteFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBackgroundColor(COLOR_HEADER_TABLE);
			table.addCell(c1);
			
			c1 = new PdfPCell(new Phrase("Alternative", whiteFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBackgroundColor(COLOR_HEADER_TABLE);
			table.addCell(c1);

			c1 = new PdfPCell(new Phrase("Value", whiteFont));
			c1.setHorizontalAlignment(Element.ALIGN_CENTER);
			c1.setBackgroundColor(COLOR_HEADER_TABLE);
			table.addCell(c1);
			table.setHeaderRows(1);

			PdfPCell cell;
			java.util.List<Criterion> criteriaMEC = mec.getAvailableCriteria();
			for (Criterion criterion : criteriaMEC) {
				cell = new PdfPCell(new Phrase(criterion.getId(), normalFont));
				cell.setRowspan(_alternativesSelected.size());
				cell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell(cell);
				for (Alternative alternative : _alternativesSelected) {
					if (campaign.getValue(criterion, alternative) != 0) {
						cell = new PdfPCell(new Phrase(alternative.getId(), normalFont));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(Double.toString(getValueMEC(mec, campaign, alternative)), normalFont));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						table.addCell(cell);
					} else {
						cell = new PdfPCell(new Phrase(alternative.getId(), normalFont));
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(COLOR_PARENT_TABLE);
						table.addCell(cell);
						cell = new PdfPCell(new Phrase(Double.toString(getTotalValueMEC(mec, campaign, alternative)), normalFont));		
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
						cell.setBackgroundColor(COLOR_PARENT_TABLE);
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
			chart.createChartByPDF(campaigns, mec, 0, "combine", _alternativesSelected);
			JFreeChart barChart = chart.getBarChart();
			generatePNGChart(barChart, subCatPart);
		} else {
			java.util.List<Campaign> campaigns = new LinkedList<Campaign>();
			for (Campaign campaign : _campaignsSelected) {
				Campaign clone = (Campaign) campaign.clone();
				clone.setName(campaign.getId() + "_" + campaign.getName() + "(" + campaign.getInitialDate() + "-" + campaign.getFinalDate() + ")");
				campaigns.add(clone);
			}
			JFreeChart barChart = null;
			for (String action : _desaggregationOption) {
				if (!action.equals("contexts")) {
					chart.createChartByPDF(campaigns, mec, 0, action, _alternativesSelected);
					barChart = chart.getBarChart();
				} else {
					chart.createChartByPDF(campaigns, mec, 2, action, _alternativesSelected);
					barChart = chart.getStackedChart();
				}
				generatePNGChart(barChart, subCatPart);
			}
		}
	}

	private static void generatePNGChart(JFreeChart chart, Section subCatPart) {
		File file = new File(System.getProperty("user.home") + NAME_FILE);
		try {
			ChartUtilities.saveChartAsPNG(file, chart, 530, 230);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		Image pdfImage;
		try {
			pdfImage = com.itextpdf.text.Image.getInstance(System.getProperty("user.home") + NAME_FILE);
			subCatPart.add(pdfImage);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (BadElementException e) {
			e.printStackTrace();
		}
		
		Paragraph sectionPara = new Paragraph();
		subCatPart.add(sectionPara);
		addEmptyLine(sectionPara, 1);
	}

	private static void createLineCharts(Section subCatPart, MEC mec) throws BadElementException {
		MECChart chart = new MECChart();

		if (_aggregationSelected == 0) {
			java.util.List<Campaign> campaigns = new LinkedList<Campaign>();
			for (Campaign campaign : _campaignsSelected) {
				campaigns.add(campaign);
			}
			chart.createChartByPDF(campaigns, mec, 1, "combine", _alternativesSelected);
			JFreeChart lineChart = chart.getLineChart();
			generatePNGChart(lineChart, subCatPart);
		} else {
			java.util.List<Campaign> campaigns = new LinkedList<Campaign>();
			for (Campaign campaign : _campaignsSelected) {
				Campaign clone = (Campaign) campaign.clone();
				clone.setName(campaign.getId() + "_" + campaign.getName() + "(" + campaign.getInitialDate() + "-" + campaign.getFinalDate() + ")");
				campaigns.add(clone);
			}

			JFreeChart lineChart = null;
			for (String action : _desaggregationOption) {
				if (action.equals("separate")) {
					action = "combine";
				}
				chart.createChartByPDF(campaigns, mec, 1, action, _alternativesSelected);
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
