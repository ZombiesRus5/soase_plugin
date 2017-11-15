package soseplugin;

import org.eclipse.swt.graphics.Image;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.zombiesrus5.plugin.sose.editors.EntityEditor;
import com.zombiesrus5.plugin.sose.editors.templates.SinsTemplateContextType;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {
	/** Key to store custom templates. */
	private static final String CUSTOM_TEMPLATES_KEY= "com.zombiesrus5.plugin.sose.customtemplates"; //$NON-NLS-1$

	// The plug-in ID
	public static final String PLUGIN_ID = "com.zombiesrus5.plugin.sose";

	// The shared instance
	private static Activator plugin;
	
	private TemplateStore templateStore;
	
	private ContributionContextTypeRegistry registry;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		ImageDescriptor d = imageDescriptorFromPlugin(PLUGIN_ID, "icons/entity.gif");
		getImageRegistry().put("IDI_IMAGE_ENTITY", d);
		d = imageDescriptorFromPlugin(PLUGIN_ID, "icons/field_private_obj.png");
		getImageRegistry().put("IDI_IMAGE_FIELD", d);
		d = imageDescriptorFromPlugin(PLUGIN_ID, "icons/field_default_obj.png");
		getImageRegistry().put("IDI_IMAGE_STRUCTURE", d);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	/**
	 * Returns this plug-in's template store.
	 * 
	 * @return the template store of this plug-in instance
	 */
	public TemplateStore getTemplateStore() {
		if (templateStore == null) {
			templateStore= new ContributionTemplateStore(getContextTypeRegistry(), Activator.getDefault().getPreferenceStore(), CUSTOM_TEMPLATES_KEY);
			try {
				templateStore.load();
			} catch (IOException e) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, "com.zombiesrus5.plugin.sose", IStatus.OK, "", e)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		return templateStore;
	}

	/**
	 * Returns this plug-in's context type registry.
	 * 
	 * @return the context type registry for this plug-in instance
	 */
	public ContextTypeRegistry getContextTypeRegistry() {
		if (registry == null) {
			// create an configure the contexts available in the template editor
			registry= new ContributionContextTypeRegistry();
			registry.addContextType(SinsTemplateContextType.SINS_CONTEXT_TYPE);
		}
		return registry;
	}
	public Image getEntityImage() {
		return getImageRegistry().get("IDI_IMAGE_ENTITY");
	}
}
