package com.zombiesrus5.plugin.sose.editors;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreePathLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerLabel;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import sose.tools.DefaultHandler;
import sose.tools.EntityParseException;
import sose.tools.EntityParser;
import soseplugin.Activator;

import com.zombiesrus5.plugin.sose.builder.EntityBuilder;
import com.zombiesrus5.plugin.sose.domain.EntityObject;
import com.zombiesrus5.plugin.sose.domain.FieldObject;
import com.zombiesrus5.plugin.sose.domain.SoaseObject;
import com.zombiesrus5.plugin.sose.domain.StructureObject;
import com.zombiesrus5.plugin.sose.views.EntityDefinitionView;

public class EntityOutlinePage extends ContentOutlinePage implements IElementStateListener {
	public static final String ID = "com.zombiesrus5.plugin.sose.editors.EntityOutlinePage";
	
	private IDocumentProvider documentProvider;
	private EntityEditor entityEditor;
	private SoaseObject input;
	private FileEditorInput file;
	private Object[] EMPTY_ARRAY = {};
	private TreeViewer viewer;
	
	boolean hideTopLevel = false;
	
	class OutlineReporter extends DefaultHandler {
		
		private IFile file;
		private EntityObject root;
		private Stack<SoaseObject> current = new Stack<SoaseObject>();
		private EntityParser parser;
		
		public OutlineReporter(IFile file) {
			this.file = file;
		}

		public IFile getFile() {
			return file;
		}
		
		public void error(EntityParseException exception) throws EntityParseException {
		}

		public void fatal(EntityParseException exception) throws EntityParseException {
		}

		public void warn(EntityParseException exception) throws EntityParseException {
		}

		@Override
		public void endEntity() {
//			System.out.println("}");
			current.pop();
		}

		@Override
		public void endStructure() {
			current.pop();
		}

		@Override
		public void processField(String fieldName, String fieldValue,
				String fieldType, int lineNumber) {
//			System.out.println(fieldName + ": " + fieldValue + " (" + fieldType + ")");
			FieldObject field = new FieldObject();
			field.setFieldType(fieldType);
			field.setName(fieldName);
			field.setValue(fieldValue);
			field.setLineNumber(lineNumber);
			
			current.peek().add(field);
			
			if ("StringInfo".equals(fieldType)) {
				if (parser.getStringInfo() != null && parser.getStringInfo().getValue(fieldValue) != null) {
					String value = parser.getStringInfo().getValue(fieldValue);
					FieldObject strfield = new FieldObject();
					strfield.setFieldType("Any");
					strfield.setName("Value");
					strfield.setValue(value);
					strfield.setLineNumber(lineNumber);
					field.add(strfield);
				}
			}
			
		}

		@Override
		public void startEntity(String entityType, int lineNumber) {
//			System.out.println(entityType + " {");
			root = new EntityObject();
			root.setEntityType(entityType);
			root.setLineNumber(lineNumber);
			current.push(root);
		}

		@Override
		public void startStructure(String stringName, String structureType, int lineNumber) {
//			System.out.println(structureName + " {");
			StructureObject structure = new StructureObject();
			structure.setLabelName(stringName);
			structure.setStructureType(structureType);
			structure.setLineNumber(lineNumber);
			current.peek().add(structure);
			current.push(structure);
		}

		@Override
		public String getFileName() {
			// TODO Auto-generated method stub
			return getFile().getName().substring(0, getFile().getName().lastIndexOf("."));
		}

		public EntityObject getRoot() {
			return root;
		}

		public void setRoot(EntityObject root) {
			this.root = root;
		}

		public EntityParser getParser() {
			return parser;
		}

		public void setParser(EntityParser parser) {
			this.parser = parser;
		}
		
	}
	
	class ViewContentProvider implements ITreeContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			if (parent instanceof SoaseObject) {
				return ((SoaseObject)parent).getElements().toArray();
			} else {
				return EMPTY_ARRAY;
			}
		}
		@Override
		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof SoaseObject) {
				SoaseObject parent = (SoaseObject) parentElement;
				if (hideTopLevel && parent instanceof StructureObject) {
					List<SoaseObject> filter = new ArrayList<SoaseObject>();
					for (SoaseObject obj: ((SoaseObject)parentElement).getElements()) {
						if (obj instanceof FieldObject) {
							FieldObject field = (FieldObject) obj;
							if ("StringInfo".equalsIgnoreCase(field.getFieldType())) {
								filter.add(field);
							}
						}
					}
					return filter.toArray();
				} else {
					return ((SoaseObject)parentElement).getElements().toArray();
				}
			}
			return null;
		}
		@Override
		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public boolean hasChildren(Object element) {
			if (element instanceof SoaseObject) {
				return ((SoaseObject)element).getElements().isEmpty() == false;
			}
			return false;
		}
	}
	class ViewLabelProvider extends LabelProvider implements ITreePathLabelProvider {
		public String getColumnText(Object obj, int index) {
			if (obj instanceof EntityObject) {
				EntityObject entity = (EntityObject) obj;
				return entity.getLabelName() + ": " + entity.getEntityType();
			} else if (obj instanceof StructureObject) {
				StructureObject s = (StructureObject) obj;
				return s.getLabelName() + ": " + s.getStructureType();
			} else if (obj instanceof FieldObject) {
				FieldObject f = (FieldObject) obj;
				return f.getName() + ": " + f.getValue();
			}
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
		@Override
		public void updateLabel(ViewerLabel label, TreePath elementPath) {
			if (elementPath.getLastSegment() instanceof EntityObject) {
				EntityObject entity = (EntityObject) elementPath.getLastSegment();
				label.setText(entity.getLabelName() + ": " + entity.getEntityType());
				label.setImage(Activator.getDefault().getImageRegistry().get("IDI_IMAGE_ENTITY"));
			} else if (elementPath.getLastSegment() instanceof StructureObject) {
				StructureObject s = (StructureObject) elementPath.getLastSegment();
				label.setText( s.getLabelName() + ": " + s.getStructureType());
				label.setImage(Activator.getDefault().getImageRegistry().get("IDI_IMAGE_STRUCTURE"));
			} else if (elementPath.getLastSegment() instanceof FieldObject) {
				FieldObject f = (FieldObject) elementPath.getLastSegment();
				label.setText( f.getName() + ": " + f.getValue());
				label.setImage(Activator.getDefault().getImageRegistry().get("IDI_IMAGE_FIELD"));
			}
			
		}
	}
	
	public EntityOutlinePage(IDocumentProvider documentProvider,
			EntityEditor entityEditor) {
		this.documentProvider = documentProvider;
		this.entityEditor = entityEditor;
		documentProvider.addElementStateListener(this);
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		viewer = getTreeViewer();
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.addSelectionChangedListener(this);

		try {
			FileEditorInput input = getFile();
			EntityParser parser = EntityBuilder.getParser(input.getFile().getProject());
			String entityType;
			
			if (parser.isSupportsFileType(input.getFile().getFileExtension())) {
				entityType = input.getFile().getFileExtension();
				entityType = entityType.substring(0, 1).toUpperCase() + entityType.substring(1);
			} else {
				entityType = "";
			}
			
			OutlineReporter reporter = new OutlineReporter(input.getFile());
			reporter.setParser(parser);
			parser.validate(input.getFile().getContents(), input.getFile().getContents(), entityType, reporter);
			setInput(reporter.getRoot());
			viewer.setInput(getInput());
			viewer.expandAll();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public SoaseObject getInput() {
		return input;
	}

	public void setInput(SoaseObject input) {
		this.input = input;
	}

	public FileEditorInput getFile() {
		return file;
	}

	public void setFile(FileEditorInput file) {
		this.file = file;
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub
		if (event.getSelection() instanceof TreeSelection) {
			TreeSelection field = (TreeSelection)event.getSelection();
			if (field.getFirstElement() instanceof SoaseObject) {
				SoaseObject f = (SoaseObject)field.getFirstElement();
				
				IDocument d = entityEditor.getDocumentProvider().getDocument(entityEditor.getEditorInput());
				try {
					int lineStart = d.getLineOffset(f.getLineNumber()-1);
					entityEditor.selectAndReveal(lineStart, 0);
				
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(entityEditor);
				} catch (BadLocationException e) {
					// ignoring;
				}
			}
		}
		super.selectionChanged(event);
	}

	@Override
	public void elementDirtyStateChanged(Object element, boolean isDirty) {
		//System.out.println("elementDirtyStateChanged: " + isDirty);
		if (isDirty == false) {
			try {
				FileEditorInput input = getFile();
				EntityParser parser = EntityBuilder.getParser(input.getFile().getProject());
				String entityType;
				
				if (parser.isSupportsFileType(input.getFile().getFileExtension())) {
					entityType = input.getFile().getFileExtension();
					entityType = entityType.substring(0, 1).toUpperCase() + entityType.substring(1);
				} else {
					entityType = "";
				}
				
				OutlineReporter reporter = new OutlineReporter(input.getFile());
				reporter.setParser(parser);
				parser.validate(input.getFile().getContents(), input.getFile().getContents(), entityType, reporter);
				setInput(reporter.getRoot());
				getTreeViewer().setInput(getInput());
				getTreeViewer().expandAll();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void elementContentAboutToBeReplaced(Object element) {
		//System.out.println("elementContentAboutToBeReplaced");
	}

	@Override
	public void elementContentReplaced(Object element) {
		//System.out.println("elementContentReplaced");
	}

	@Override
	public void elementDeleted(Object element) {
		//System.out.println("elementDeleted");
	}

	@Override
	public void elementMoved(Object originalElement, Object movedElement) {
		//System.out.println("elementMoved");
	}

	@Override
	public void setActionBars(IActionBars actionBars) {
		Action action1 = new Action() {
			public void run() {
				
				//showMessage("Action 1 executed");

				if (hideTopLevel) {
					hideTopLevel = false;
					setToolTipText("Hides the top level components");
				} else {
					hideTopLevel = true;
					setToolTipText("Shows the top level components");
				}
				viewer.refresh();
				viewer.expandAll();
			}
		};
		actionBars.getToolBarManager().add(action1);
		action1.setText("Hide Top Level");
		action1.setToolTipText("Hides the top level components");
		action1.setId(ID + ".hideTopLevel");
		action1.setActionDefinitionId(ID + ".hideTopLevel");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_TOOL_COPY));

		super.setActionBars(actionBars);
	}
	private void showMessage(String message) {
		MessageDialog.openInformation(
			viewer.getControl().getShell(),
			"Entity Definition View",
			message);
	}

}
