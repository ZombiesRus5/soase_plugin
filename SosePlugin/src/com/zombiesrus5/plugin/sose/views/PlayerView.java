package com.zombiesrus5.plugin.sose.views;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.dialogs.ViewLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.omg.CORBA.INITIALIZE;

public class PlayerView extends ViewPart {
	private TreeViewer viewer;
	private TreeParent invisibleRoot;

	class TreeObject implements IAdaptable {
		private String name;
		private TreeParent parent;
		private IResource resource;

		public TreeObject(String name) {
			setName(name);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public TreeParent getParent() {
			return parent;
		}

		public void setParent(TreeParent parent) {
			this.parent = parent;
		}

		public IResource getResource() {
			return resource;
		}

		public void setResource(IResource resource) {
			this.resource = resource;
		}

		@Override
		public Object getAdapter(Class adapter) {
			// TODO Auto-generated method stub
			return null;
		}
		
		public String toString() {
			return getName();
		}
	}

	class TreeParent extends TreeObject {
		private ArrayList<TreeObject> children = new ArrayList<TreeObject>();

		public TreeParent(String name) {
			super(name);
		}

		public TreeObject[] getChildren() {
			return (TreeObject[]) children.toArray(new TreeObject[children
					.size()]);
		}

		public void setChildren(ArrayList<TreeObject> children) {
			this.children = children;
		}

		public boolean hasChildren() {
			return children.size() > 0;
		}

		public void addChild(TreeObject treeObject) {
			children.add(treeObject);

		}
	}

	class ViewContentProvider implements ITreeContentProvider {

		@Override
		public Object[] getChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent) parent).getChildren();
			} else {
				return new Object[0];
			}
		}

		@Override
		public Object getParent(Object child) {
			if (child instanceof TreeObject) {
				return ((TreeObject) child).getParent();
			} else {
				return null;
			}
		}

		@Override
		public boolean hasChildren(Object parent) {
			if (parent instanceof TreeParent) {
				return ((TreeParent)parent).hasChildren();
			}
			return false;
		}

		@Override
		public Object[] getElements(Object parent) {
			if (parent.equals(getViewSite())) {
				if (invisibleRoot == null) {
					initialize();
				}
				return getChildren(invisibleRoot);
			}
			return getChildren(parent);
		}

		@Override
		public void dispose() {
			// TODO Auto-generated method stub

		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// TODO Auto-generated method stub

		}

	}

	public PlayerView() {
		// TODO Auto-generated constructor stub
	}

	public void initialize() {
		final TreeParent root = new TreeParent("Player Entity Files");
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();

			IProject[] projects = workspace.getRoot().getProjects();

			for (int i = 0; i < projects.length; i++) {
				projects[i].accept(new IResourceVisitor() {

					@Override
					public boolean visit(IResource resource)
							throws CoreException {
						// TODO Auto-generated method stub
						TreeObject object = new TreeObject(resource.toString());
						object.setResource(resource);
						root.addChild(object);
//						System.out.println("" + this + resource);
						return true;
					}
				}, IResource.DEPTH_INFINITE, IResource.NONE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		invisibleRoot = new TreeParent("");
		invisibleRoot.addChild(root);
	}

	class ViewLabelProvider extends LabelProvider {
		public String getText(Object obj) {
			return obj.toString();
		}

		public Image getImage(Object obj) {
			String imageKey = ISharedImages.IMG_OBJ_ELEMENT;

			if (obj instanceof TreeParent)
				imageKey = ISharedImages.IMG_OBJ_FOLDER;
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					imageKey);
		}

	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(getViewSite());

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				ISelection selection = event.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				if (!(obj instanceof TreeObject)) {
					return;
				} else if (obj instanceof TreeParent) {
					if (viewer.getExpandedState(obj)) {
						viewer.collapseToLevel(obj, 1);
					} else {
						viewer.expandToLevel(obj, 1);
					}
					return;
				} else {
					TreeObject tempObj = (TreeObject) obj;
					IFile ifile = ResourcesPlugin.getWorkspace().getRoot()
							.getFile(tempObj.getResource().getFullPath());
					IWorkbenchPage dpage = getViewSite().getWorkbenchWindow()
							.getActivePage();
					if (dpage != null) {
						try {
							IDE.openEditor(dpage, ifile, true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
