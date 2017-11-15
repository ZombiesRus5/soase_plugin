package com.zombiesrus5.plugin.sose.views;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.*;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.Image;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;
import org.w3c.dom.Document;

import com.zombiesrus5.plugin.sose.preferences.PreferenceConstants;

import sose.tools.DefinitionHandler;
import sose.tools.EntityParser;
import sose.tools.xml.XMLToolkit;
import soseplugin.Activator;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class EntityDefinitionView extends ViewPart implements ISelectionListener
 {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.zombiesrus5.plugin.sose.views.EntityDefinitionView";

	private Clipboard cb = null;
	
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action action1;
	private Action action2;
	private Action doubleClickAction;
	
	String entityType = "";
	EntityParser parser = new EntityParser();

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	class TreeObject implements IAdaptable {
		private String name;
		private String copyValue;
		
		private TreeParent parent;
		
		public TreeObject(String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		public void setParent(TreeParent parent) {
			this.parent = parent;
		}
		public TreeParent getParent() {
			return parent;
		}
		public String toString() {
			return getName();
		}
		public Object getAdapter(Class key) {
			return null;
		}
		public String getCopyValue() {
			return copyValue;
		}
		public void setCopyValue(String copyValue) {
			this.copyValue = copyValue;
		}
	}
	
	class TreeParent extends TreeObject {
		private ArrayList children;
		public TreeParent(String name) {
			super(name);
			children = new ArrayList();
		}
		public void addChild(TreeObject child) {
			children.add(child);
			child.setParent(this);
		}
		public void removeChild(TreeObject child) {
			children.remove(child);
			child.setParent(null);
		}
		public TreeObject [] getChildren() {
			return (TreeObject [])children.toArray(new TreeObject[children.size()]);
		}
		public boolean hasChildren() {
			return children.size()>0;
		}
	}
	
	class TreeBuilder implements DefinitionHandler {
		private TreeParent parent = null;
		boolean ignoreStructure = false;
		
		private TreeBuilder(TreeParent parent) {
			this.parent = parent;
		}
		
		public TreeParent getParent() {
			return parent;
		}
		
		@Override
		public void endEntity() {
			parent = parent.getParent();
		}

		@Override
		public void endStructure() {
			parent = parent.getParent();
		}

		@Override
		public void field(String fieldName, String fieldRule, boolean explicit, String[] fieldValues,
				String fieldType, String[] references, String helpText) {
			if (fieldValues == null) {
				TreeObject to1 = new TreeObject(fieldName + ": " + fieldType);
				to1.setCopyValue(fieldName);
				parent.addChild(to1);
			} else {
				TreeParent to1 = new TreeParent(fieldName + ": " + fieldType);
				to1.setCopyValue(fieldName);
				parent.addChild(to1);
				
				for (int i=0; i<fieldValues.length; i++) {
					TreeObject value = new TreeObject(fieldValues[i]);
					value.setCopyValue(fieldValues[i]);
					to1.addChild(value);
				}
			}
		}

		@Override
		public void startEntity(String entityType, String helpText) {
			TreeParent entity = new TreeParent(entityType + ": Entity");
			entity.setCopyValue(entityType);
			parent.addChild(entity);
			parent = entity;
		}

		@Override
		public void startStructure(String structureName, String structureType, boolean explicit, String[] reference, String helpText) {
			TreeParent structure = new TreeParent(structureName + ": Structure");
			structure.setCopyValue(structureName);
			parent.addChild(structure);
			parent = structure;
		}
		@Override
		public void endCondition() {
			parent = parent.getParent();
			ignoreStructure = false;
		}
		@Override
		public void endIteration() {
			parent = parent.getParent();
			ignoreStructure = false;
			
		}
		@Override
		public void startCondition(String fieldName, String conditionValue, String helpText) {
			TreeParent condition = new TreeParent(fieldName + ": Condition(" + conditionValue + ")");
			condition.setCopyValue(conditionValue);
			parent.addChild(condition);
			parent = condition;
			ignoreStructure = true;
		}
		@Override
		public void startIteration(String fieldName, String helpText) {
			TreeParent iteration = new TreeParent(fieldName + ": Iteration");
			iteration.setCopyValue(fieldName);
			parent.addChild(iteration);
			parent = iteration;
			ignoreStructure = true;
		}

		@Override
		public void endConditionBlock() throws Exception {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void startConditionBlock(String field, String fieldRule, String[] conditions,
				String helpText) throws Exception {
			// TODO Auto-generated method stub
			
		}
		
	}

	class ViewContentProvider implements IStructuredContentProvider, 
										   ITreeContentProvider {
		private TreeParent invisibleRoot;

		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot==null) initialize();
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}
		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject)child).getParent();
			}
			return null;
		}
		public Object [] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent)parent).getChildren();
			}
			return new Object[0];
		}
		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent)
				return ((TreeParent)parent).hasChildren();
			return false;
		}
/*
 * We will set up a dummy model to initialize tree heararchy.
 * In a real code, you will connect to a real model and
 * expose its hierarchy.
 */
		private void initialize() {
			TreeBuilder builder = new TreeBuilder(new TreeParent(""));
			
			try {
				if (entityType != null) {
					parser.processDefinition(entityType, builder);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			TreeObject to1 = new TreeObject("Leaf 1");
//			TreeObject to2 = new TreeObject("Leaf 2");
//			TreeObject to3 = new TreeObject("Leaf 3");
//			TreeParent p1 = new TreeParent("Parent 1");
//			p1.addChild(to1);
//			p1.addChild(to2);
//			p1.addChild(to3);
//			
//			TreeObject to4 = new TreeObject("Leaf 4");
//			TreeParent p2 = new TreeParent("Parent 2");
//			p2.addChild(to4);
//			
//			TreeParent root = new TreeParent("Root");
//			root.addChild(p1);
//			root.addChild(p2);
			
			invisibleRoot = builder.getParent();
		}
	}
	class ViewLabelProvider extends LabelProvider implements IColorProvider {

		public String getText(Object obj) {
			return obj.toString();
		}
		public Image getImage(Object obj) {
			return null;
//			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
//			if (obj instanceof TreeParent)
//			   imageKey = ISharedImages.IMG_OBJ_FOLDER;
//			return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
		}
		@Override
		
		public Color getBackground(Object element) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public Color getForeground(Object element) {
			// TODO Auto-generated method stub
			return Display.getDefault().getSystemColor(SWT.COLOR_BLUE);

		}
	}
	class NameSorter extends ViewerSorter {
	}

	/**
	 * The constructor.
	 */
	public EntityDefinitionView() {
		parser.setDebug(false);
		parser.setWarn(false);
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
						
		parser.setStrictValidation(store.getString(PreferenceConstants.STRICT_VERSION));

		parser.setIgnoreCaseOnFiles(store.getBoolean(PreferenceConstants.IGNORE_CASE_ON_REFERENCE_FILES));
		
		parser.setup();

	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER_SOLID);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
//		viewer.setSorter(new NameSorter());
		viewer.setInput(getViewSite());
		
		cb = new Clipboard(parent.getDisplay());
		
		// Create the help context id for the viewer's control
		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(), "com.zombiesrus5.plugin.sose.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();

		getViewSite().getPage().addSelectionListener(this);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				EntityDefinitionView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
		manager.add(action2);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(action1);
//		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
//		manager.add(action2);
		manager.add(new Separator());
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				ISelection selection = viewer.getSelection();
				if (selection instanceof TreeSelection) {
					// which it should be
					TreeSelection treeSel = (TreeSelection) selection;
					//System.out.println(treeSel.toList());
					//System.out.println(treeSel.getFirstElement());
					
					for (TreeObject a: (List<TreeObject>)treeSel.toList()) {
						pw.print(a.getCopyValue());
						pw.println();
					}
				} else {
					sw.write(selection.toString());
				}
				//System.out.println(selection);
				TextTransfer textTransfer = TextTransfer.getInstance();
				cb.setContents(new Object[] {sw.toString()}, new Transfer[] {textTransfer});
//				showMessage("Action 1 executed");
			}
		};
		action1.setText("Copy");
		action1.setToolTipText("Copies the Entity Definition to the Clipboard");
		action1.setId(ID + ".copy");
		action1.setActionDefinitionId(ID + ".copy");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		
		action2 = new Action() {
			public void run() {
				showMessage("Action 2 executed");
			}
		};
		action2.setText("Action 2");
		action2.setToolTipText("Action 2 tooltip");
		action2.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = viewer.getSelection();
				Object obj = ((IStructuredSelection)selection).getFirstElement();
//				showMessage("Double-click detected on "+obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Entity Definition View",
			message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof TextEditor) {
			TextEditor editor = (TextEditor) part;
			editor.getEditorInput();
			if (editor.getEditorInput() instanceof FileEditorInput) {
				if (((FileEditorInput)editor.getEditorInput()).getName().endsWith(".entity")) {
					String newEntityType = getEntityType((FileEditorInput)editor.getEditorInput());
					if (!newEntityType.equals(entityType)) {
						entityType = newEntityType;
						viewer.setContentProvider(new ViewContentProvider());
					}
				} else if (parser.isSupportsFileType(((FileEditorInput)editor.getEditorInput()).getFile().getFileExtension())) {
					String newEntityType = ((FileEditorInput)editor.getEditorInput()).getFile().getFileExtension();
					newEntityType = newEntityType.substring(0, 1).toUpperCase() + newEntityType.substring(1);

					if (!newEntityType.equals(entityType)) {
						entityType = newEntityType;
						viewer.setContentProvider(new ViewContentProvider());
					}
				} else {
				
					entityType = "";
				}
			}
		}
        if (selection instanceof IStructuredSelection) {
            Object first = ((IStructuredSelection)selection).getFirstElement();
            if (first instanceof String) {
            	;
            }
        }
	}

	public static String getEntityType(FileEditorInput fileEditorInput) {
		String entityType = "";
		try {
//			entityType = fileEditorInput.getFile().getPersistentProperty(new QualifiedName(null, "entityType"));
			InputStream is = fileEditorInput.getFile().getContents();
			LineNumberReader lnr = new LineNumberReader(new InputStreamReader(is));
			lnr.readLine(); // chew TXT
			String entityTypeLine = lnr.readLine();
//			System.out.println(entityTypeLine);
			entityType = parseValue(entityTypeLine);
//			System.out.println(entityType);
			is.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return entityType;
	}
	
	public static String parseValue(String currentLine) {
		currentLine = currentLine.trim();
		String[] parts = currentLine.split(" ");
		String value;
		if (parts.length == 1) {
			value = "";
		} else {
			value = parts[1];
			value = value.replace("\"", " ").trim();
		}
		return value;
	}
}