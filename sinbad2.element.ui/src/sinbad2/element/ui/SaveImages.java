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
import sinbad2.element.criterion.Criterion;

public class SaveImages {

	public static void saveImages() {	
		ProblemElementsManager elementsManager = ProblemElementsManager.getInstance();
		ProblemElementsSet elementsSet = elementsManager.getActiveElementSet();
		List<Criterion> criteria = elementsSet.getCriteria();
		TeXFormula formula = null;
		String equationCriteria, nameFile = null;
		List<Object> combinations;
		List<String> formulaCriteria;
		
		Set<Object> allElementsSet = new HashSet<Object>();
		allElementsSet.addAll(criteria);

		for (Set<Object> s : powerSet(allElementsSet)) {
			combinations = new ArrayList<Object>(s);
			formulaCriteria = new LinkedList<String>();
			equationCriteria = ""; //$NON-NLS-1$
			if(!combinations.isEmpty()) {
				for(int i = 0; i < combinations.size(); ++i) {
					formulaCriteria.add(combinations.get(i).toString());
					Collections.sort(formulaCriteria);
				}
				
				for(int c = 0; c < formulaCriteria.size(); ++c) {
					if(c < formulaCriteria.size() - 1) {
						equationCriteria += formulaCriteria.get(c) + "$\\times$"; //$NON-NLS-1$
					} else {
						equationCriteria += formulaCriteria.get(c);
					}
				}

				if(!equationCriteria.isEmpty()) {
						formula = new TeXFormula("\\mbox{ME=" + equationCriteria + "}"); //$NON-NLS-1$ //$NON-NLS-2$
						nameFile = formulaCriteria.toString();
				}
				
				TeXIcon icon = formula.createTeXIcon(TeXConstants.STYLE_DISPLAY, 12);
				icon.setInsets(new Insets(5, 5, 5, 5));
	
				BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D graphicsImage = image.createGraphics();
				graphicsImage.fillRect(0, 0, icon.getIconWidth(), icon.getIconHeight());
				icon.paintIcon(null, graphicsImage, 0, 0);
	
				ImageData formulaImageData = convertToSWT(image);
				formulaImageData.transparentPixel = formulaImageData.getPixel(0, 0);
				Image formulaImage = new Image(Display.getCurrent(), formulaImageData);
	
				ImageLoader loader = new ImageLoader();
				loader.data = new ImageData[] { formulaImage.getImageData() };
				loader.save("D:\\Álvaro\\Escritorio\\Flintstones-DGT\\formulas" + "/" + nameFile + ".png", SWT.IMAGE_PNG); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
