package sinbad2.element.ui;

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.image.DirectColorModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

import sinbad2.element.ProblemElementsManager;
import sinbad2.element.ProblemElementsSet;
import sinbad2.element.alternative.Alternative;
import sinbad2.element.criterion.Criterion;

public class SaveImages {

	public static void saveImages() {	
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
		List<Alternative> alternatives = elementsSet.getAlternatives();
		List<Criterion> criteria = elementsSet.getCriteria();
		TeXFormula formula;
		String equationAlternatives, equationCriteria, equationDenominator, nameFile;
		List<Object> combinations;
		List<String> formulaAlternatives, formulaCriteria, formulaDenominator;
		
		Set<Object> allElementsSet = new HashSet<Object>();
		allElementsSet.addAll(alternatives);
		allElementsSet.addAll(criteria);

		for (Set<Object> s : powerSet(allElementsSet)) {
			combinations = new ArrayList<Object>(s);
			formulaAlternatives = new LinkedList<String>();
			formulaCriteria = new LinkedList<String>();
			formulaDenominator = new LinkedList<String>();
			equationAlternatives = "";
			equationCriteria = "";
			equationDenominator = "";
			if(!combinations.isEmpty()) {
				for(int i = 0; i < combinations.size(); ++i) {
					if(combinations.get(i) instanceof Alternative) {
						formulaAlternatives.add(combinations.get(i).toString());
						Collections.sort(formulaAlternatives);
					} else {
						if(((Criterion) combinations.get(i)).getIsDirect()) {
							formulaDenominator.add(combinations.get(i).toString());
							Collections.sort(formulaDenominator);
						} else {
							formulaCriteria.add(combinations.get(i).toString());
							Collections.sort(formulaCriteria);
						}
					}
				}
				for(int a = 0; a < formulaAlternatives.size(); ++a) {
					if(a < formulaAlternatives.size() - 1) {
						equationAlternatives += formulaAlternatives.get(a) + " ,";
					} else {
						equationAlternatives += formulaAlternatives.get(a);
					}
				}
				
				for(int c = 0; c < formulaCriteria.size(); ++c) {
					if(c < formulaCriteria.size() - 1) {
						equationCriteria += formulaCriteria.get(c) + " ,";
					} else {
						equationCriteria += formulaCriteria.get(c);
					}
				}
				for(int d = 0; d < formulaDenominator.size(); ++d) {
					if(d < formulaDenominator.size() - 1) {
						equationDenominator += formulaDenominator.get(d) + " ,";
					} else {
						equationDenominator += formulaDenominator.get(d);
					}
				}
				if(!equationAlternatives.isEmpty() && equationCriteria.isEmpty()) {
					if(equationDenominator.isEmpty()) {
						formula = new TeXFormula("ME=" + equationAlternatives); //$NON-NLS-1$
						nameFile = formulaAlternatives.toString();
					} else {
						formula = new TeXFormula("ME=\\frac{" + equationAlternatives + "}{" + equationDenominator + "}"); //$NON-NLS-1$
						nameFile = formulaAlternatives.toString() + formulaDenominator.toString();
					}
				} else if(equationAlternatives.isEmpty() && !equationCriteria.isEmpty()) {
					if(equationDenominator.isEmpty()) {
						formula = new TeXFormula("ME=" + equationCriteria); //$NON-NLS-1$
						nameFile = formulaCriteria.toString();
					} else {
						formula = new TeXFormula("ME=\\frac{" + equationCriteria + "}{" + equationDenominator + "}"); //$NON-NLS-1$
						nameFile = formulaCriteria.toString() + formulaDenominator.toString();
					}
				} else if(equationAlternatives.isEmpty() && equationCriteria.isEmpty()) {
					if(equationDenominator.isEmpty()) {
						formula = new TeXFormula(""); //$NON-NLS-1$
						nameFile = "vacío";
					} else {
						formula = new TeXFormula("ME=\\frac{1}{" + equationDenominator + "}"); //$NON-NLS-1$
						nameFile = formulaDenominator.toString();
					} 
				} else {
					if(equationDenominator.isEmpty()) {
						formula = new TeXFormula("ME=" + equationAlternatives + " ," + equationCriteria); //$NON-NLS-1$
						nameFile = formulaAlternatives.toString() + formulaCriteria.toString();
					} else {
						formula = new TeXFormula("ME=\\frac{" + equationAlternatives + " ," + equationCriteria + "}{" + equationDenominator + "}"); //$NON-NLS-1$
						nameFile = formulaAlternatives.toString() + formulaCriteria.toString() + formulaDenominator.toString();
					} 
				}
				
				TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 12);
				icon.setInsets(new Insets(5, 5, 5, 5));
	
				BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D graphicsImage = image.createGraphics();
				graphicsImage.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
				icon.paintIcon(null, graphicsImage, 0, 0);
	
				ImageData formulaImageData = convertToSWT(image);
				Image formulaImage = new Image(Display.getCurrent(), formulaImageData);
	
				ImageLoader loader = new ImageLoader();
				loader.data = new ImageData[] { formulaImage.getImageData() };
				loader.save("D:/Álvaro/Escritorio/Fórmulas" + "/" + nameFile + ".png", SWT.IMAGE_PNG);
			}
		}
	}

	private static ImageData convertToSWT(BufferedImage bufferedImage) {
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

	public static <T> Set<Set<T>> powerSet(Set<T> originalSet) {
		Set<Set<T>> sets = new HashSet<Set<T>>();
		if (originalSet.isEmpty()) {
			sets.add(new HashSet<T>());
			return sets;
		}
		List<T> list = new ArrayList<T>(originalSet);
		T head = list.get(0);
		Set<T> rest = new HashSet<T>(list.subList(1, list.size()));
		for (Set<T> set : powerSet(rest)) {
			Set<T> newSet = new HashSet<T>();
			newSet.add(head);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set);
		}
		return sets;
	}
}
