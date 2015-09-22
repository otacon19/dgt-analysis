package sinbad2.element.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class ImagesFormulas {
	
	private static final String PLUGIN_ID = "sinbad2.element.ui"; //$NON-NLS-1$

	public static final Image Desplazamiento = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/[Desplazamientos].png").createImage(); //$NON-NLS-1$
	
	public static final Image Tiempo = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/[Tiempo].png").createImage(); //$NON-NLS-1$

	public static final Image Distancia = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/[Distancia].png").createImage(); //$NON-NLS-1$

	
}
