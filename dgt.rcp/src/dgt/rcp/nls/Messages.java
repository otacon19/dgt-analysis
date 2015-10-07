package dgt.rcp.nls;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "dgt.rcp.nls.messages"; //$NON-NLS-1$
	public static String ApplicationWorkbenchWindowAdvisor_Perspective_analysis;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
