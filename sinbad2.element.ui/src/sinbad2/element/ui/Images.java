package sinbad2.element.ui;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Images {

	private static final String PLUGIN_ID = "sinbad2.element.ui"; //$NON-NLS-1$

	public static final Image Journey = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/journey.png").createImage(); //$NON-NLS-1$
	
	public static final Image Campaign = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/campaign.png").createImage(); //$NON-NLS-1$

	public static final Image Kind_of_cars = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/kind_of_vehicles_22x22.png").createImage(); //$NON-NLS-1$

	public static final Image Reason = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/reason_22x22.png").createImage(); //$NON-NLS-1$

	public static final Image Time_zone = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/time_zone_22x22.png").createImage(); //$NON-NLS-1$
	
	public static final Image Week = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/week.png").createImage(); //$NON-NLS-1$
	
	public static final Image Distance = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/distance_22x22.png").createImage(); //$NON-NLS-1$
	
	public static final Image Time = AbstractUIPlugin
			.imageDescriptorFromPlugin(PLUGIN_ID, "icons/time_22x22.png").createImage(); //$NON-NLS-1$
	
	private Images() {
	}
}


