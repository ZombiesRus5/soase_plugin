package com.zombiesrus5.plugin.sose.builder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;

import sose.tools.DefaultHandler;
import sose.tools.EntityParseException;
import sose.tools.EntityParser;
import sose.tools.SetupMonitor;
import sose.tools.StringInfo;
import soseplugin.Activator;

import com.zombiesrus5.plugin.sose.domain.EntityObject;
import com.zombiesrus5.plugin.sose.domain.FieldObject;
import com.zombiesrus5.plugin.sose.domain.SoaseObject;
import com.zombiesrus5.plugin.sose.domain.StructureObject;
import com.zombiesrus5.plugin.sose.preferences.PreferenceConstants;

public class EntityBuilder extends IncrementalProjectBuilder {
	public static QualifiedName KEY_PARSER = new QualifiedName("soase", "entityParser");
	public static QualifiedName KEY_MOD_ROOT = new QualifiedName("soase", "modRoot");
	public static QualifiedName KEY_ENTITY_TYPE = new QualifiedName("soase", "entityType");

	private EntityParser parser = null;
	private boolean fullBuild = true;
	private boolean fullBuildInProcess = false;
	
	static class ResourceChangedListener implements IResourceChangeListener {
		EntityBuilder builder = null;
		
		public ResourceChangedListener(EntityBuilder builder) {
			this.builder = builder;
		}
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			try {
			if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
				event.getDelta().accept(new IResourceDeltaVisitor() {
					
					@Override
					public boolean visit(IResourceDelta delta) throws CoreException {
						if (delta.getResource().getProject() == builder.getProject()) {
//							System.out.println("my resource: " + delta.getResource());
//							System.out.println(builder.getProject());
							if ((delta.getKind() == IResourceDelta.ADDED || 
									delta.getKind() == IResourceDelta.REMOVED ||
									delta.getKind() == IResourceDelta.REPLACED) && 
									!(delta.getResource().getFullPath().toOSString().contains(".svn") ||
									  delta.getResource().getFullPath().toOSString().contains("Template"))) {
//								System.out.println("file added removed or replaced");
								if (builder.fullBuildInProcess == false) {
									builder.fullBuild = true;
								}
							} else if ((delta.getResource().getName().endsWith(".sounddata") ||
									   delta.getResource().getName().endsWith(".brushes") ||
									   delta.getResource().getName().endsWith(".str") ||
									   delta.getResource().getName().endsWith(".explosiondata") ||
									   delta.getResource().getName().endsWith(".galaxyScenarioDef")) &&
									   (!delta.getResource().getFullPath().toOSString().contains("Templates"))) {
//								System.out.println("important reference file modified");
								if (builder.fullBuildInProcess == false) {
									builder.fullBuild = true;
								}
							}
						}
						return true;
					}
				});
			}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	static class FindGameInfoDirectory implements IResourceVisitor {
		IProject project = null;
		EntityParser parser = null;
		public FindGameInfoDirectory(IProject p, EntityParser parser) {
			project = p;
			this.parser = parser;
		}
		@Override
		public boolean visit(IResource resource) throws CoreException {
			if (resource.getName().equals("GameInfo")) {
				 parser.setModDirectory(resource.getParent().getLocation().toOSString());
				 project.setPersistentProperty(KEY_MOD_ROOT, resource.getParent().getProjectRelativePath().toString());
				 return false;
			} else {
				return true;
			}
		}
	}
	
	static class AddGameInfoDirectory implements IResourceVisitor {
		IProject project = null;
		EntityParser parser = null;
		public AddGameInfoDirectory(IProject p, EntityParser parser) throws CoreException {
			project = p;
			this.parser = parser;
		}
		@Override
		public boolean visit(IResource resource) throws CoreException {
//			System.out.println(resource.getName());
			if (resource.getName().equalsIgnoreCase("GameInfo")) {
//				System.out.println(resource.getName());
				 parser.getBaseModDirectory().add(resource.getParent().getLocation().toOSString());
				 return false;
			} else {
				return true;
			}
		}
	}
	static class SetStringInfoDirectory implements IResourceVisitor {
		IProject project = null;
		EntityParser parser = null;
		public SetStringInfoDirectory(IProject p, EntityParser parser) throws CoreException {
			project = p;
			this.parser = parser;
		}
		@Override
		public boolean visit(IResource resource) throws CoreException {
			//System.out.println(resource.getName());
			if (resource.getName().equalsIgnoreCase("English.str")) {
				//System.out.println(resource.getName());
				 parser.setStringDirectory(resource.getParent().getParent().getLocation().toOSString());
				 return false;
			} else {
				return true;
			}
		}
	}
	class CheckEntityDeltaVisitor implements IResourceDeltaVisitor {
		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core.resources.IResourceDelta)
		 */
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
				// handle added resource
				checkEntity(resource);
				validateReferenced(resource);
				break;
			case IResourceDelta.REMOVED:
				// handle removed resource
				break;
			case IResourceDelta.CHANGED:
				// handle changed resource
				checkEntity(resource);
				validateReferenced(resource);
				break;
			}
			//return true to continue visiting children.
			return true;
		}
	}

	class CheckEntityResourceVisitor implements IResourceVisitor {
		IProgressMonitor myMonitor = null;
		public CheckEntityResourceVisitor(IProgressMonitor monitor) {
			// TODO Auto-generated constructor stub
			myMonitor = monitor;
		}

		public boolean visit(IResource resource) {
			myMonitor.subTask(resource.getName());
			checkEntity(resource);
			myMonitor.worked(1);
			//return true to continue visiting children.
			return true;
		}
	}

	class CheckEntityResourcePass2Visitor implements IResourceVisitor {
		IProgressMonitor myMonitor = null;
		public CheckEntityResourcePass2Visitor(IProgressMonitor monitor) {
			// TODO Auto-generated constructor stub
			myMonitor = monitor;
		}

		public boolean visit(IResource resource) {
			if (resource.getFileExtension() != null && resource.getFileExtension().equals("entity")) {
				myMonitor.subTask(resource.getName());
				checkEntity(resource);
				myMonitor.worked(1);
			}
			//return true to continue visiting children.
			return true;
		}
	}
	
	class CheckReferencesResourceVisitor implements IResourceVisitor {
		IProgressMonitor myMonitor = null;
		public CheckReferencesResourceVisitor(IProgressMonitor monitor) {
			// TODO Auto-generated constructor stub
			myMonitor = monitor;
		}

		public boolean visit(IResource resource) {
			myMonitor.subTask(resource.getName());
			validateReferenced(resource);
			myMonitor.worked(1);
			//return true to continue visiting children.
			return true;
		}
	}

	class EntityErrorHandler extends DefaultHandler {
		
		private IFile file;
		private EntityObject root;
//		private Stack<StructureObject> current = new Stack<StructureObject>();

		public EntityErrorHandler(IFile file) {
			this.file = file;
		}

		public IFile getFile() {
			return file;
		}
		
		private void addMarker(EntityParseException e, int severity) {
			EntityBuilder.this.addMarker(file, e.getMessage() + ": " + e.getLineContents().trim(), e
					.getLineNumber(), severity);
		}

		public void error(EntityParseException exception) throws EntityParseException {
//			exception.printStackTrace();
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void fatal(EntityParseException exception) throws EntityParseException {
//			exception.printStackTrace();
			addMarker(exception, IMarker.SEVERITY_ERROR);
		}

		public void warn(EntityParseException exception) throws EntityParseException {
//			exception.printStackTrace();
			addMarker(exception, IMarker.SEVERITY_WARNING);
		}

		@Override
		public void endEntity() {
//			System.out.println("}");
//			current.pop();
		}

		@Override
		public void endStructure() {
//			current.pop();
		}

		@Override
		public void processField(String fieldName, String fieldValue,
				String fieldType, int lineNumber) {
//			System.out.println(fieldName + ": " + fieldValue + " (" + fieldType + ")");
//			FieldObject field = new FieldObject();
//			field.setFieldType(fieldType);
//			field.setName(fieldName);
//			field.setValue(fieldValue);
//			field.setLineNumber(lineNumber);
//			current.peek().add(field);
		}

		@Override
		public void startEntity(String entityType, int lineNumber) {
//			System.out.println(entityType + " {");
//			try {
//				file.setPersistentProperty(KEY_ENTITY_TYPE, entityType);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//			root = new EntityObject();
//			root.setEntityType(entityType);
//			root.setLineNumber(lineNumber);
//			current.push(root);
		}

		@Override
		public void startStructure(String stringName, String structureType, int lineNumber) {
//			System.out.println(structureName + " {");
//			StructureObject structure = new StructureObject();
//			structure.setLabelName(stringName);
//			structure.setStructureType(structureType);
//			structure.setLineNumber(lineNumber);
//			current.peek().add(structure);
//			current.push(structure);
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
		
	}

	public static final String BUILDER_ID = "com.zombiesrus5.plugin.sose.entityBuilder";

	public static final String MARKER_TYPE = "com.zombiesrus5.plugin.sose.entityProblem";

	//private SAXParserFactory parserFactory;

	private void addMarker(IFile file, String message, int lineNumber,
			int severity) {
		try {
			IMarker marker = file.createMarker(MARKER_TYPE);
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
			if (lineNumber == -1) {
				lineNumber = 1;
			}
			marker.setAttribute(IMarker.LINE_NUMBER, lineNumber);
		} catch (CoreException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		if (kind == FULL_BUILD) {
			if (parser != null) {
				parser.reset();
			}
			parser = null;
			fullBuild(monitor);
		} else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null || fullBuild == true) {
//				System.out.println("fullBuild");
				fullBuild(monitor);
			} else {
//				System.out.println("incrementalBuild");
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	void validateReferenced(IResource resource) {
		try {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			IPreferenceStore store = Activator.getDefault().getPreferenceStore();
			boolean warnEntityNotReferenced = store.getBoolean(PreferenceConstants.WARN_ENTITY_NOT_REFERENCED);
			
			if ("entity".equalsIgnoreCase(resource.getFileExtension()) && warnEntityNotReferenced) {
				String referenceType = "Entity";
				// check parser and referenced parsers to see if this entity is referenced
				boolean referenced = false;
				String referenceName = resource.getName().replaceFirst("." + resource.getFileExtension(), "");

				referenced = isReferenced(referenceName, referenceType, referenced);
				if (!referenced) {
					addMarker(file, "Entity file does not appear to be referenced from another entity. This may or may not be an issue.", 1, IMarker.SEVERITY_WARNING);
				}
			}
			if (("ogg".equalsIgnoreCase(resource.getFileExtension()) || "mp3".equalsIgnoreCase(resource.getFileExtension())
					|| "wav".equalsIgnoreCase(resource.getFileExtension())) && warnEntityNotReferenced) {
				String referenceType = "SoundFile";
				// check parser and referenced parsers to see if this entity is referenced
				boolean referenced = false;
				referenced = isReferenced(resource.getName(), referenceType, referenced);
				String referenceName = resource.getName().replaceFirst("." + resource.getFileExtension(), "");
				referenced = referenced || isReferenced(referenceName, referenceType, referenced);
				if (!referenced) {
					addMarker(file, "SoundFile file does not appear to be referenced from another entity. This may or may not be an issue.", 1, IMarker.SEVERITY_WARNING);
				}
			}		
			if (("mesh".equalsIgnoreCase(resource.getFileExtension())) && warnEntityNotReferenced) {
				String referenceType = "Mesh";
				// check parser and referenced parsers to see if this entity is referenced
				boolean referenced = false;
				referenced = isReferenced(resource.getName(), referenceType, referenced);
				String referenceName = resource.getName().replaceFirst("." + resource.getFileExtension(), "");
				referenced = referenced || isReferenced(referenceName, referenceType, referenced);
				if (!referenced) {
					addMarker(file, "Mesh file does not appear to be referenced from another entity. This may or may not be an issue.", 1, IMarker.SEVERITY_WARNING);
				}
			}		
			if (("particle".equalsIgnoreCase(resource.getFileExtension())) && warnEntityNotReferenced) {
				String referenceType = "Particle";
				// check parser and referenced parsers to see if this entity is referenced
				boolean referenced = false;
				referenced = isReferenced(resource.getName(), referenceType, referenced);
				String referenceName = resource.getName().replaceFirst("." + resource.getFileExtension(), "");
				referenced = referenced || isReferenced(referenceName, referenceType, referenced);
				if (!referenced) {
					addMarker(file, "Particle file does not appear to be referenced from another entity. This may or may not be an issue.", 1, IMarker.SEVERITY_WARNING);
				}
			}	
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private boolean isReferenced(String referenceName, String referenceType,
			boolean referenced) throws CoreException {
		if (parser.isReferenced(referenceType, referenceName)) {
			referenced = true;
		}
		if (referenced == false) {
		IProject[] referencedProjects = getReferencedProjects(getProject().getReferencedProjects());
		for (int i=0; i<referencedProjects.length; i++) {
			EntityParser referencedParser = (EntityParser)referencedProjects[i].getSessionProperty(KEY_PARSER);
			if (referencedParser != null) {
				if (referencedParser.isReferenced(referenceType, referenceName)) {
					referenced = true;
				}
			}
		}
		}
		return referenced;
	}
	
	private IProject[] getReferencedProjects(IProject[] referencedProjects) throws CoreException {
		List<IProject> projects = new ArrayList<IProject>();
		
		addReferencedProjects(referencedProjects, projects);
		
		IProject[] projectsArray = new IProject[projects.size()];
		return projects.toArray(projectsArray);
	}

	private void addReferencedProjects(IProject[] referencedProjects,
			List<IProject> projects) throws CoreException {
		
		for (int i=0; i<referencedProjects.length; i++) {
			if (!projects.contains(referencedProjects[i])) {
				projects.add(referencedProjects[i]);
				addReferencedProjects(referencedProjects[i].getReferencedProjects(), projects);
			}
		}
	}

	void checkEntity(IResource resource) {
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			deleteMarkers(file);
			try {
			if (parser.isSupportsFileType(resource.getFileExtension())) {
				EntityErrorHandler reporter = new EntityErrorHandler(file);
				String fileType = file.getFileExtension();
				
//				System.out.println(file.toString());
				boolean projectValidateResearch = parser.isValidateResearchCosts();
				if (projectValidateResearch) {
					// check if the file overrides this
					String fileValidateResearch = resource.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.VALIDATE_RESEARCH_COSTS));
					if (fileValidateResearch != null && !Boolean.parseBoolean(fileValidateResearch)) {
						parser.setValidateResearchCosts(false);
					}
				}
				
				parser.validate(file.getContents(), file.getContents(), fileType, reporter);
				parser.setValidateResearchCosts(projectValidateResearch);

//				System.out.println(reporter.getRoot());
				//getParser().parse(file.getContents(), reporter);
			}
			} catch (Exception e1) {
			}
		}
	}

	private void deleteMarkers(IFile file) {
		try {
			file.deleteMarkers(MARKER_TYPE, false, IResource.DEPTH_ZERO);
		} catch (CoreException ce) {
		}
	}

	protected void fullBuild(final IProgressMonitor monitor)
			throws CoreException {
		try {
//			if (parser == null) {
				parser = setupParser(getProject());
//			} else {
//				parser = resetParser(parser, getProject());
//			}
			fullBuildInProcess = true;
			monitor.beginTask("validating project: " + getProject().getName(), 1000);
			// need to stage everything
			// entities
			// window
			// sounddata
			// brushes
			// etc
			
			// pass one
			getProject().accept(new CheckEntityResourceVisitor(monitor));
			// pass two
			getProject().accept(new CheckEntityResourcePass2Visitor(monitor));
			getProject().accept(new CheckReferencesResourceVisitor(monitor));
			monitor.done();
		} catch (CoreException e) {
			e.printStackTrace();
		} finally {
			fullBuild = false;
			fullBuildInProcess = false;
		}
	}
//
//	private SAXParser getParser() throws ParserConfigurationException,
//			SAXException {
//		if (parserFactory == null) {
//			parserFactory = SAXParserFactory.newInstance();
//		}
//		return parserFactory.newSAXParser();
//	}

	private void setupParser() throws CoreException {
		parser = getParser(getProject());
		
//		parser.setDebug(false);
//		parser.setWarn(false);
//		
//		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
//				
//		// find the GameInfo directory...
//		getProject().accept(new IResourceVisitor() {
//			@Override
//			public boolean visit(IResource resource) {
//				if (resource.getName().equals("GameInfo")) {
//					 parser.setModDirectory(resource.getParent().getLocation().toOSString());
//					 return false;
//				} else {
//					return true;
//				}
//			}
//		}, 2, false);
//		
//		if (parser.getModDirectory() == null) {
//			parser.setModDirectory(getProject().getLocation().toOSString());
//		}
//		parser.setSinsInstallationDirectory(store.getString(PreferenceConstants.SINS_INSTALLATION_PATH));
//		parser.setDiplomacyReferenceDirectory(store.getString(PreferenceConstants.DIPLOMACY_REFERENCE_PATH));
//		parser.setEntrenchmentReferenceDirectory(store.getString(PreferenceConstants.ENTRENCHMENT_REFERENCE_PATH));
//		parser.setVanillaReferenceDirectory(store.getString(PreferenceConstants.VANILLA_REFERENCE_PATH));
//		parser.setStrictValidation(store.getString(PreferenceConstants.STRICT_VERSION));
//		parser.setIgnoreCaseOnFiles(store.getBoolean(PreferenceConstants.IGNORE_CASE_ON_REFERENCE_FILES));
//		
//		parser.setup();
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		setupParser();
		
		monitor.beginTask("validating delta: " + getProject().getName() + ", " + delta.getResource().getName(), 1);
		delta.accept(new CheckEntityDeltaVisitor());
		monitor.done();
	}

	public static EntityParser getParser(IProject project) throws CoreException {
		QualifiedName qn = new QualifiedName("soase", "entityParser");
		EntityParser parser = (EntityParser) project.getSessionProperty(qn);
		if (parser == null) {
			parser = setupParser(project);
	        parser.setup(new SetupMonitor() {
				
				@Override
				public void subTask(String subTask) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void finished(int amount) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void beginTask(String task, int amount) {
					// TODO Auto-generated method stub
					
				}
			});
		}
		return parser;
	}
	
	public static EntityParser setupParser(IProject project) throws CoreException {
		final EntityParser parser = new EntityParser();
		IResource r = null;
		parser.setDebug(false);
		parser.setWarn(false);
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
		// find the GameInfo directory...
		project.accept(new FindGameInfoDirectory(project, parser), 2, false);
		
		if (parser.getModDirectory() == null) {
			parser.setModDirectory(project.getLocation().toOSString());
			project.setPersistentProperty(KEY_MOD_ROOT, "/");
		}
		
		addReferencedModDirectory(project, parser);
		
		if (store.getBoolean(PreferenceConstants.VALIDATE_PARTICLES) == false) {
			parser.addExcludedFileExtension("particle");
		}
		if (PreferenceConstants.STRICT_REBELLION.equals(store.getString(PreferenceConstants.STRICT_VERSION))) {
			parser.setSinsInstallationDirectory(store.getString(PreferenceConstants.REBELLION_INSTALLATION_PATH));
			parser.setVanillaReferenceDirectory(store.getString(PreferenceConstants.REBELLION_REFERENCE_PATH));
		} else {
			parser.setSinsInstallationDirectory(store.getString(PreferenceConstants.SINS_INSTALLATION_PATH));
			parser.setDiplomacyReferenceDirectory(store.getString(PreferenceConstants.DIPLOMACY_REFERENCE_PATH));
			parser.setEntrenchmentReferenceDirectory(store.getString(PreferenceConstants.ENTRENCHMENT_REFERENCE_PATH));
			parser.setVanillaReferenceDirectory(store.getString(PreferenceConstants.VANILLA_REFERENCE_PATH));
		}
		boolean enableProjectSettings = Boolean.parseBoolean(project.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.ENABLE_PROJECT_SETTINGS)));
		if (enableProjectSettings) {
			parser.setStrictValidation(project.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.STRICT_VERSION)));
			parser.setIgnoreCaseOnFiles(Boolean.parseBoolean(project.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.IGNORE_CASE_ON_REFERENCE_FILES))));
		
			parser.setValidateResearchCosts(Boolean.parseBoolean(project.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.VALIDATE_RESEARCH_COSTS))));
		} else {
			// no longer supported pre-rebellion by default
			parser.setStrictValidation(PreferenceConstants.STRICT_REBELLION);
			parser.setIgnoreCaseOnFiles(store.getBoolean(PreferenceConstants.IGNORE_CASE_ON_REFERENCE_FILES));
		
			parser.setValidateResearchCosts(store.getBoolean(PreferenceConstants.VALIDATE_RESEARCH_COSTS));
		}
		setupResearchCosts(parser, store);
		FileInputStream str = null;

		if (project.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.STRING_FILE_LOCATION)) != null &&
				!project.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.STRING_FILE_LOCATION)).isEmpty()) {
			File f = new File(project.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.STRING_FILE_LOCATION)));
			parser.setStringDirectory(f.getParentFile().getPath());
		} else {
			File f = new File(parser.getModDirectory() + "\\String\\English.str");
			if (f.isFile()) {
				// already the string directory
			} else {
				setStringInfoDirectory(project, parser);
			}
		}
		
		try {
		Job job = new Job("Setting up entity validation") {
			public IStatus run(IProgressMonitor monitor) {
				EntityParserSetupMonitor myMonitor = new EntityParserSetupMonitor(monitor);
		         
		        // execute the task ... 
		        monitor.beginTask("setting up entity validator", 1000);
		        parser.setup(myMonitor);
		        	try {
		        		String fileName = parser.getStringDirectory();
		        		if (fileName != null) {
		        			fileName += "\\String\\English.str";
		        		} else {
		        			fileName = parser.getModDirectory() + "\\String\\English.str";
		        		}
		        		parser.setStringInfo(new StringInfo(parser, new FileInputStream(fileName)));
		        	} catch (Exception e) {
		        		// just ignoring
		        	}
		        monitor.done(); 
				return Status.OK_STATUS;
		    } 
		};
		
		job.schedule();
		
		job.join();
		
		} catch(Exception e) {
			e.printStackTrace();
		}

		QualifiedName qn = new QualifiedName("soase", "entityParser");
		project.setSessionProperty(KEY_PARSER, parser);
		return parser;
	}
	
	public static EntityParser resetParser(EntityParser oldParser, IProject project) throws CoreException {
		final EntityParser parser = new EntityParser();
		IResource r = null;
		parser.setDebug(false);
		parser.setWarn(false);
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
				
		// find the GameInfo directory...
		project.accept(new FindGameInfoDirectory(project, parser), 2, false);
		
		if (parser.getModDirectory() == null) {
			parser.setModDirectory(project.getLocation().toOSString());
			project.setPersistentProperty(KEY_MOD_ROOT, "/");
		}
		
		addReferencedModDirectory(project, parser);
		
		if (Boolean.parseBoolean(project.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.VALIDATE_PARTICLES))) == false) {
			parser.addExcludedFileExtension("particle");
		}
		if (PreferenceConstants.STRICT_REBELLION.equals(store.getString(PreferenceConstants.STRICT_VERSION))) {
			parser.setSinsInstallationDirectory(store.getString(PreferenceConstants.REBELLION_INSTALLATION_PATH));
			parser.setVanillaReferenceDirectory(store.getString(PreferenceConstants.REBELLION_REFERENCE_PATH));
		} else {
			parser.setSinsInstallationDirectory(store.getString(PreferenceConstants.SINS_INSTALLATION_PATH));
			parser.setDiplomacyReferenceDirectory(store.getString(PreferenceConstants.DIPLOMACY_REFERENCE_PATH));
			parser.setEntrenchmentReferenceDirectory(store.getString(PreferenceConstants.ENTRENCHMENT_REFERENCE_PATH));
			parser.setVanillaReferenceDirectory(store.getString(PreferenceConstants.VANILLA_REFERENCE_PATH));
		}
		boolean enableProjectSettings = Boolean.parseBoolean(project.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.ENABLE_PROJECT_SETTINGS)));
		if (enableProjectSettings) {
			parser.setStrictValidation(project.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.STRICT_VERSION)));
			parser.setIgnoreCaseOnFiles(Boolean.parseBoolean(project.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.IGNORE_CASE_ON_REFERENCE_FILES))));
		
			parser.setValidateResearchCosts(Boolean.parseBoolean(project.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.VALIDATE_RESEARCH_COSTS))));
		} else {
			// no longer supporting pre-rebellion by default
			parser.setStrictValidation(store.getString(PreferenceConstants.STRICT_REBELLION));
			parser.setIgnoreCaseOnFiles(store.getBoolean(PreferenceConstants.IGNORE_CASE_ON_REFERENCE_FILES));
		
			parser.setValidateResearchCosts(store.getBoolean(PreferenceConstants.VALIDATE_RESEARCH_COSTS));
		
		}
		setupResearchCosts(parser, store);
		if (project.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.STRING_FILE_LOCATION)) != null) {
			File f = new File(project.getPersistentProperty(new QualifiedName(PreferenceConstants.SOASE, PreferenceConstants.STRING_FILE_LOCATION)));
			parser.setStringDirectory(f.getParentFile().getPath());
		} else {
			File f = new File(parser.getModDirectory() + "\\String\\English.str");
			if (f.isFile()) {
				// already the string directory
			} else {
				setStringInfoDirectory(project, parser);
			}
		}

		if (hasChanged(oldParser, parser)) {
		
		try {
		Job job = new Job("Setting up entity validation") {
			public IStatus run(IProgressMonitor monitor) {
				EntityParserSetupMonitor myMonitor = new EntityParserSetupMonitor(monitor);
		         
		        // execute the task ... 
		        monitor.beginTask("setting up entity validator", 1000);
		        parser.setup(myMonitor);
		        if (parser.getStringDirectory() != null) {
		        	try {
		        		String fileName = parser.getStringDirectory();
		        		if (fileName == null) {
		        			fileName = parser.getModDirectory() + "\\String\\English.str";
		        		}
		        		parser.setStringInfo(new StringInfo(parser, new FileInputStream(fileName)));
		        	} catch (Exception e) {
		        		// just ignoring
		        	}
				}
				monitor.done(); 
				return Status.OK_STATUS;
		    } 
		};
		
		job.schedule();
		
		job.join();
		
		} catch(Exception e) {
			e.printStackTrace();
		}

		QualifiedName qn = new QualifiedName("soase", "entityParser");
		project.setSessionProperty(KEY_PARSER, parser);
		return parser;

		} else {
			// save time as nothing changed in the validation...
			try {
				oldParser.resetReferences();
				oldParser.resetValidators();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return oldParser;
		}

	}

	private static void addReferencedModDirectory(IProject project,
			final EntityParser parser) throws CoreException {
		List<IProject> processedProjects = new ArrayList<IProject>(); // prevent bidirectional recursion errors
		
		addReferencedModDirectory(project, parser, processedProjects);
	}

	private static void addReferencedModDirectory(IProject project,
			final EntityParser parser, List<IProject> processedProjects)
			throws CoreException {
		processedProjects.add(project);
		IProject[] referencedProjects = project.getReferencedProjects();
		for (int i=0; i<referencedProjects.length; i++) {
			if (processedProjects.contains(referencedProjects[i]) == false) {
				referencedProjects[i].accept(new AddGameInfoDirectory(referencedProjects[i], parser), 2, false);
				addReferencedModDirectory(referencedProjects[i], parser, processedProjects);
			}
		}
	}
	private static void setStringInfoDirectory(IProject project,
			final EntityParser parser) throws CoreException {
		List<IProject> processedProjects = new ArrayList<IProject>(); // prevent bidirectional recursion errors

		setStringInfoDirectory(project, parser, processedProjects);
	}

	private static void setStringInfoDirectory(IProject project,
			final EntityParser parser, List<IProject> processedProjects)
			throws CoreException {
		processedProjects.add(project);
		IProject[] referencedProjects = project.getReferencedProjects();
		for (int i=0; i<referencedProjects.length; i++) {
			if (processedProjects.contains(referencedProjects[i]) == false) {
				referencedProjects[i].accept(new SetStringInfoDirectory(referencedProjects[i], parser), IResource.DEPTH_INFINITE, false);
				if (parser.getStringDirectory() == null) {
					// try another layer down
					setStringInfoDirectory(referencedProjects[i], parser, processedProjects);
				}
			}
		}
	}
	
	private static boolean hasChanged(EntityParser oldParser,
			EntityParser newParser) {
		boolean changed = false;
		
		if (!changed) {
			changed = !oldParser.getModDirectory().equals(newParser.getModDirectory());
		}
		if (!changed) {
			changed = !oldParser.getBaseModDirectory().containsAll(newParser.getBaseModDirectory());
		}

		if (!changed) {
			changed = !oldParser.getStrictValidation().equals(newParser.getStrictValidation());
		}
		if (!changed) {
			changed = !(oldParser.isIgnoreCaseOnFiles() == newParser.isIgnoreCaseOnFiles());
		}
		if (!changed) {
			changed = !(oldParser.isValidateResearchCosts() == newParser.isValidateResearchCosts());
		}
		return changed;
	}

	private static void setupResearchCosts(EntityParser parser,
			IPreferenceStore store) {
		
		for (int i=0; i<8; i++) {
			int credits = store.getInt("tier" + i + PreferenceConstants.BASECOST + PreferenceConstants.CREDITS);
			parser.addResearchCost(i, PreferenceConstants.BASECOST, PreferenceConstants.CREDITS, credits);
			int metal = store.getInt("tier" + i + PreferenceConstants.BASECOST + PreferenceConstants.METAL);
			parser.addResearchCost(i, PreferenceConstants.BASECOST, PreferenceConstants.METAL, metal);
			int crystal = store.getInt("tier" + i + PreferenceConstants.BASECOST + PreferenceConstants.CRYSTAL);
			parser.addResearchCost(i, PreferenceConstants.BASECOST, PreferenceConstants.CRYSTAL, crystal);

			credits = store.getInt("tier" + i + PreferenceConstants.PERLEVEL + PreferenceConstants.CREDITS);
			parser.addResearchCost(i, PreferenceConstants.PERLEVEL, PreferenceConstants.CREDITS, credits);
			metal = store.getInt("tier" + i + PreferenceConstants.PERLEVEL + PreferenceConstants.METAL);
			parser.addResearchCost(i, PreferenceConstants.PERLEVEL, PreferenceConstants.METAL, metal);
			crystal = store.getInt("tier" + i + PreferenceConstants.PERLEVEL + PreferenceConstants.CRYSTAL);
			parser.addResearchCost(i, PreferenceConstants.PERLEVEL, PreferenceConstants.CRYSTAL, crystal);
			
			int upgradeTime = store.getInt("tier" + i + PreferenceConstants.BASEUPGRADETIME);
			parser.addUpgradeTime(i, PreferenceConstants.BASEUPGRADETIME, upgradeTime);
			upgradeTime = store.getInt("tier" + i + PreferenceConstants.PERLEVELUPGRADETIME);
			parser.addUpgradeTime(i, PreferenceConstants.PERLEVELUPGRADETIME, upgradeTime);
			
		}
	}

	@Override
	protected void startupOnInitialize() {
		// TODO Auto-generated method stub
		super.startupOnInitialize();
		//System.out.println(getProject());
		getProject().getWorkspace().addResourceChangeListener(new ResourceChangedListener(this));
	}
}
