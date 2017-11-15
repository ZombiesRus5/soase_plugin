package com.zombiesrus5.plugin.sose.handlers;

import java.net.URL;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.util.OpenStrategy;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SinsHandler extends AbstractHandler {
	/**
	 * The constructor.
	 */
	public SinsHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        try {
            boolean activate = OpenStrategy.activateOnOpen();

            //            System.out.println(action.getText());
            URL url = new URL("http://www.moddb.com/games/sins-of-a-solar-empire-rebellion/mods");
            final IWebBrowser browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser(); 
            browser.openURL(url); 
            
        } catch (Exception e) {
        	e.printStackTrace();
    		MessageDialog.openInformation(
    				window.getShell(),
    				"SosePlugin",
    				"MODDB Site Not Found!");
        }

		return null;
	}
}
