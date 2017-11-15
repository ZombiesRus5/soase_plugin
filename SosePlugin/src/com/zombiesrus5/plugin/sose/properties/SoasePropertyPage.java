/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Remy Chi Jian Suen <remy.suen@gmail.com> - Bug 175069 [Preferences] ResourceInfoPage is not setting dialog font on all widgets
 *******************************************************************************/
package com.zombiesrus5.plugin.sose.properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.PropertyPage;

import com.zombiesrus5.plugin.sose.preferences.PreferenceConstants;

/**
 * The ResourceInfoPage is the page that shows the basic info about the
 * resource.
 */
public class SoasePropertyPage extends PropertyPage {

	private Button enableProjectSettingsBox;
	
	private Button ignoreCaseOnReferenceFilesBox;

	private Button allowDecimalBox;

	private Button requireDecimalBox;
	
	private Button warnEntityNotReferencedBox;

	private Button validateResearchBox;
	
	private Button selectStringFile;
	
	private Text selectedStringFileText;

	private boolean previousEnableProjectSettingsValue;

	private boolean previousIgnoreCaseValue;

	private boolean previousAllowDecimalValue;

	private boolean previousWarnEntityNotReferencedValue;
	
	private boolean previousRequireDecimalValue;

	private boolean previousValidateResearchValue;
	
	private String previousStringFileValue;

	private IContentDescription cachedContentDescription;

//	private ResourceEncodingFieldEditor encodingEditor;

	private LineDelimiterEditor lineDelimiterEditor;
	
	private static String SOASE = PreferenceConstants.SOASE;

	private static String ENABLE_PROJECT_SETTINGS = PreferenceConstants.ENABLE_PROJECT_SETTINGS;
	
	private static String IGNORE_CASE = PreferenceConstants.IGNORE_CASE_ON_REFERENCE_FILES;

	private static String ALLOW_DECIMAL = PreferenceConstants.ALLOW_DECIMAL_POINT_ON_INTEGER;
	
	private static String WARN_ENTITY_NOT_REFERENCED = PreferenceConstants.WARN_ENTITY_NOT_REFERENCED;

	private static String REQUIRE_DECIMAL = PreferenceConstants.REQUIRES_DECIMAL_POINT_ON_DECIMAL;

	private static String VALIDATE_RESEARCH = PreferenceConstants.VALIDATE_RESEARCH_COSTS;
	
	private static String STRING_FILE_LOCATION = PreferenceConstants.STRING_FILE_LOCATION;

//	private static String TYPE_TITLE = "soase_TYPE_TITLE";
//
//	private static String LOCATION_TITLE = "soase_LOCATION_TITLE";
//
//	private static String RESOLVED_LOCATION_TITLE = "soase_RESOLVED_LOCATION_TITLE";
//	
//	private static String SIZE_TITLE = "soase_SIZE_TITLE";
//
//	private static String PATH_TITLE = "soase_PATH_TITLE";
//
//	private static String TIMESTAMP_TITLE = "soase_TIMESTAMP_TITLE";

	private static String FILE_ENCODING_TITLE = "soase_FILE_ENCODING_TITLE";

	private static String CONTAINER_ENCODING_TITLE = "soase_CONTAINER_ENCODING_TITLE";

	// Max value width in characters before wrapping
	private static final int MAX_VALUE_WIDTH = 80;

	/**
	 * Create the group that shows the name, location, size and type.
	 * 
	 * @param parent
	 *            the composite the group will be created in
	 * @param resource
	 *            the resource the information is being taken from.
	 * @return the composite for the group
	 */
	private Composite createBasicInfoGroup(Composite parent, IResource resource) {
		Composite basicInfoComposite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		basicInfoComposite.setLayout(layout);
		GridData data = new GridData();
		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		basicInfoComposite.setLayoutData(data);

//		// The group for path
//		Label pathLabel = new Label(basicInfoComposite, SWT.NONE);
//		pathLabel.setText(PATH_TITLE);
//		GridData gd = new GridData();
//		gd.verticalAlignment = SWT.TOP;
//		pathLabel.setLayoutData(gd);
//
//		// path value label
//		Text pathValueText = new Text(basicInfoComposite, SWT.WRAP
//				| SWT.READ_ONLY);
//		String pathString = TextProcessor.process(resource.getFullPath()
//				.toString());
//		pathValueText.setText(pathString);
//		gd = new GridData();
//		gd.widthHint = convertWidthInCharsToPixels(MAX_VALUE_WIDTH);
//		gd.grabExcessHorizontalSpace = true;
//		gd.horizontalAlignment = GridData.FILL;
//		pathValueText.setLayoutData(gd);
//		pathValueText.setBackground(pathValueText.getDisplay().getSystemColor(
//				SWT.COLOR_WIDGET_BACKGROUND));
//
//		// The group for types
//		Label typeTitle = new Label(basicInfoComposite, SWT.LEFT);
//		typeTitle.setText(TYPE_TITLE);
//
//		Text typeValue = new Text(basicInfoComposite, SWT.LEFT | SWT.READ_ONLY);
//		typeValue.setText("" + resource.getType());
//		typeValue.setBackground(typeValue.getDisplay().getSystemColor(
//				SWT.COLOR_WIDGET_BACKGROUND));
//
//		// The group for location
//		Label locationTitle = new Label(basicInfoComposite, SWT.LEFT);
//		locationTitle.setText(LOCATION_TITLE);
//		gd = new GridData();
//		gd.verticalAlignment = SWT.TOP;
//		locationTitle.setLayoutData(gd);

//		Text locationValue = new Text(basicInfoComposite, SWT.WRAP
//				| SWT.READ_ONLY);
//		String locationStr = resource.getLocation().toOSString();
//		locationValue.setText(locationStr);
//		gd = new GridData();
//		gd.widthHint = convertWidthInCharsToPixels(MAX_VALUE_WIDTH);
//		gd.grabExcessHorizontalSpace = true;
//		gd.horizontalAlignment = GridData.FILL;
//		locationValue.setLayoutData(gd);
//		locationValue.setBackground(locationValue.getDisplay().getSystemColor(
//				SWT.COLOR_WIDGET_BACKGROUND));
//
//		if (isPathVariable(resource)) {
//			Label resolvedLocationTitle = new Label(basicInfoComposite,
//					SWT.LEFT);
//			resolvedLocationTitle.setText(RESOLVED_LOCATION_TITLE);
//			gd = new GridData();
//			gd.verticalAlignment = SWT.TOP;
//			resolvedLocationTitle.setLayoutData(gd);
//
//			Text resolvedLocationValue = new Text(basicInfoComposite, SWT.WRAP
//					| SWT.READ_ONLY);
//			resolvedLocationValue.setText(resource.getLocation().toString());
//			gd = new GridData();
//			gd.widthHint = convertWidthInCharsToPixels(MAX_VALUE_WIDTH);
//			gd.grabExcessHorizontalSpace = true;
//			gd.horizontalAlignment = GridData.FILL;
//			resolvedLocationValue.setLayoutData(gd);
//			resolvedLocationValue.setBackground(resolvedLocationValue
//					.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
//		}
//		if (resource.getType() == IResource.FILE) {
//			// The group for size
//			Label sizeTitle = new Label(basicInfoComposite, SWT.LEFT);
//			sizeTitle.setText(SIZE_TITLE);
//
//			Text sizeValue = new Text(basicInfoComposite, SWT.LEFT
//					| SWT.READ_ONLY);
//			sizeValue.setText("0");
//			gd = new GridData();
//			gd.widthHint = convertWidthInCharsToPixels(MAX_VALUE_WIDTH);
//			gd.grabExcessHorizontalSpace = true;
//			gd.horizontalAlignment = GridData.FILL;
//			sizeValue.setLayoutData(gd);
//			sizeValue.setBackground(sizeValue.getDisplay().getSystemColor(
//					SWT.COLOR_WIDGET_BACKGROUND));
//		}

		return basicInfoComposite;
	}

	protected Control createContents(Composite parent) {
		try {
		// layout the page
		IResource resource = (IResource) getElement().getAdapter(
				IResource.class);
		
		if (resource == null) {
			Label label = new Label(parent, SWT.NONE);
			label.setText("ResourceInfoPage_noResource");
			return label;
		}
		
		if (resource.getType() != IResource.PROJECT) {
			String validateResearch = resource.getPersistentProperty(new QualifiedName(SOASE, VALIDATE_RESEARCH));
			if (validateResearch == null) {
				previousValidateResearchValue = true;
			} else {
				previousValidateResearchValue = Boolean.parseBoolean(validateResearch);
			}
		} else {
			previousEnableProjectSettingsValue = Boolean.parseBoolean(resource.getPersistentProperty(new QualifiedName(SOASE, ENABLE_PROJECT_SETTINGS)));
			previousIgnoreCaseValue = Boolean.parseBoolean(resource.getPersistentProperty(new QualifiedName(SOASE, IGNORE_CASE)));
			previousRequireDecimalValue = Boolean.parseBoolean(resource.getPersistentProperty(new QualifiedName(SOASE, REQUIRE_DECIMAL)));
			previousAllowDecimalValue = Boolean.parseBoolean(resource.getPersistentProperty(new QualifiedName(SOASE, ALLOW_DECIMAL)));
			previousWarnEntityNotReferencedValue = Boolean.parseBoolean(resource.getPersistentProperty(new QualifiedName(SOASE, WARN_ENTITY_NOT_REFERENCED)));
			previousValidateResearchValue = Boolean.parseBoolean(resource.getPersistentProperty(new QualifiedName(SOASE, VALIDATE_RESEARCH)));
			previousStringFileValue = resource.getPersistentProperty(new QualifiedName(SOASE, STRING_FILE_LOCATION));
		}

		// top level group
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL);
		data.grabExcessHorizontalSpace = true;
		composite.setLayoutData(data);

//		createBasicInfoGroup(composite, resource);
//		createSeparator(composite);
		createStateGroup(composite, resource);
		new Label(composite, SWT.NONE); // a vertical spacer
//		encodingEditor = new ResourceEncodingFieldEditor(
//				getFieldEditorLabel(resource), composite, resource);
//		encodingEditor.setPage(this);
//		encodingEditor.load();
//
//		encodingEditor.setPropertyChangeListener(new IPropertyChangeListener() {
//			/*
//			 * (non-Javadoc)
//			 * 
//			 * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
//			 */
//			public void propertyChange(PropertyChangeEvent event) {
//				if (event.getProperty().equals(FieldEditor.IS_VALID)) {
//					setValid(encodingEditor.isValid());
//				}
//
//			}
//		});

		
		if (resource.getType() == IResource.PROJECT) {
			lineDelimiterEditor = new LineDelimiterEditor(composite, resource
					.getProject());
			lineDelimiterEditor.doLoad();
			updateState(previousEnableProjectSettingsValue);
		} 
		
//		//We can't save the preferences for close
//		if (resource.getType() == IResource.PROJECT && !((IProject)resource).isOpen()){
//			encodingEditor.setEnabled(false, composite);
//			lineDelimiterEditor.setEnabled(false);
//		}
		
		Dialog.applyDialogFont(composite);

		return composite;
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Return the label for the encoding field editor for the resource.
	 * 
	 * @param resource -
	 *            the resource to edit.
	 * @return String
	 */
	private String getFieldEditorLabel(IResource resource) {
		if (resource instanceof IContainer) {
			return CONTAINER_ENCODING_TITLE;
		}
		return FILE_ENCODING_TITLE;
	}

	private void updateState(boolean enableProjectSettings) {
		if (!enableProjectSettings) {
			if (ignoreCaseOnReferenceFilesBox != null) {
				ignoreCaseOnReferenceFilesBox.setEnabled(false);
			}
			if (allowDecimalBox != null) {
				allowDecimalBox.setEnabled(false);
			}
			if (requireDecimalBox != null) {
				requireDecimalBox.setEnabled(false);
			}
			if (warnEntityNotReferencedBox != null) {
				warnEntityNotReferencedBox.setEnabled(false);
			}	
			if (validateResearchBox != null) {
				validateResearchBox.setEnabled(false);
			}
			if (lineDelimiterEditor != null) {
				lineDelimiterEditor.setEnabled(false);
			}
		} else {
			if (ignoreCaseOnReferenceFilesBox != null) {
				ignoreCaseOnReferenceFilesBox.setEnabled(true);
			}
			if (allowDecimalBox != null) {
				allowDecimalBox.setEnabled(true);
			}
			if (warnEntityNotReferencedBox != null) {
				warnEntityNotReferencedBox.setEnabled(true);
			}
			if (requireDecimalBox != null) {
				requireDecimalBox.setEnabled(true);
			}
			if (validateResearchBox != null) {
				validateResearchBox.setEnabled(true);
			}
			if (lineDelimiterEditor != null) {
				lineDelimiterEditor.setEnabled(true);
			}
		}
	}
	private void createEnableProjectSettingsButton(Composite composite) {

		SelectionAdapter buttonListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (e.widget.equals(enableProjectSettingsBox)) {
					if (enableProjectSettingsBox.getSelection()) {
						updateState(true);
					} else {
						updateState(false);
					}
				}
			}

		};
		
		this.enableProjectSettingsBox = new Button(composite, SWT.CHECK | SWT.RIGHT);
		this.enableProjectSettingsBox.setAlignment(SWT.LEFT);
		this.enableProjectSettingsBox.setText("&Enable project specific settings");
		this.enableProjectSettingsBox.setSelection(this.previousEnableProjectSettingsValue);
		GridData data = new GridData();
		data.horizontalSpan = 3;
		this.enableProjectSettingsBox.setLayoutData(data);
		this.enableProjectSettingsBox.addSelectionListener(buttonListener);
	}
	
	/**
	 * Create the isEditable button and it's associated label as a child of
	 * parent using the editableValue of the receiver. The Composite will be the
	 * parent of the button.
	 * 
	 * @param composite
	 *            the parent of the button
	 */
	private void createEditableButton(Composite composite) {

		this.ignoreCaseOnReferenceFilesBox = new Button(composite, SWT.CHECK | SWT.RIGHT);
		this.ignoreCaseOnReferenceFilesBox.setAlignment(SWT.LEFT);
		this.ignoreCaseOnReferenceFilesBox.setText("&Ignore Case on Reference Files");
		this.ignoreCaseOnReferenceFilesBox.setSelection(this.previousIgnoreCaseValue);
		GridData data = new GridData();
		data.horizontalSpan = 3;
		this.ignoreCaseOnReferenceFilesBox.setLayoutData(data);
	}

	/**
	 * Create the isExecutable button and it's associated label as a child of
	 * parent using the editableValue of the receiver. The Composite will be the
	 * parent of the button.
	 * 
	 * @param composite
	 *            the parent of the button
	 */
	private void createExecutableButton(Composite composite) {

		this.allowDecimalBox = new Button(composite, SWT.CHECK | SWT.RIGHT);
		this.allowDecimalBox.setAlignment(SWT.LEFT);
		this.allowDecimalBox.setText("&Allow Decimal Point in Integer Values");
		this.allowDecimalBox.setSelection(this.previousAllowDecimalValue);
		GridData data = new GridData();
		data.horizontalSpan = 3;
		this.allowDecimalBox.setLayoutData(data);
	}

	private void createWarnEntityNotReferencedButton(Composite composite) {

		this.warnEntityNotReferencedBox = new Button(composite, SWT.CHECK | SWT.RIGHT);
		this.warnEntityNotReferencedBox.setAlignment(SWT.LEFT);
		this.warnEntityNotReferencedBox.setText("&Warn when Entity file is not referenced from another Entity");
		this.warnEntityNotReferencedBox.setSelection(this.previousWarnEntityNotReferencedValue);
		GridData data = new GridData();
		data.horizontalSpan = 3;
		this.warnEntityNotReferencedBox.setLayoutData(data);
	}
	
	/**
	 * Create the isArchive button and it's associated label as a child of
	 * parent using the editableValue of the receiver. The Composite will be the
	 * parent of the button.
	 * 
	 * @param composite
	 *            the parent of the button
	 */
	private void createArchiveButton(Composite composite) {

		this.requireDecimalBox = new Button(composite, SWT.CHECK | SWT.RIGHT);
		this.requireDecimalBox.setAlignment(SWT.LEFT);
		this.requireDecimalBox.setText("&Require Decimal Point in Decimal Values");
		this.requireDecimalBox.setSelection(this.previousRequireDecimalValue);
		GridData data = new GridData();
		data.horizontalSpan = 3;
		this.requireDecimalBox.setLayoutData(data);
	}

	/**
	 * Create the derived button and it's associated label as a child of parent
	 * using the derived of the receiver. The Composite will be the parent of
	 * the button.
	 * 
	 * @param composite
	 *            the parent of the button
	 */
	private void createDerivedButton(Composite composite) {

		this.validateResearchBox = new Button(composite, SWT.CHECK | SWT.RIGHT);
		this.validateResearchBox.setAlignment(SWT.LEFT);
		this.validateResearchBox.setText("&Validate Research Costs");
		this.validateResearchBox.setSelection(this.previousValidateResearchValue);
		GridData data = new GridData();
		data.horizontalSpan = 3;
		this.validateResearchBox.setLayoutData(data);
	}

	/**
	 * Create a separator that goes across the entire page
	 * 
	 * @param composite
	 *            The parent of the seperator
	 */
	private void createSeparator(Composite composite) {

		Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		separator.setLayoutData(gridData);
	}

	/**
	 * Create the group that shows the read only state and the timestamp.
	 * 
	 * @param parent
	 *            the composite the group will be created in
	 * @param resource
	 *            the resource the information is being taken from.
	 */
	private void createStateGroup(Composite parent, IResource resource) {

		Font font = parent.getFont();

		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout(3, false);
		//layout.numColumns = 2;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		GridData data = new GridData();
		data.horizontalAlignment = GridData.FILL;
		composite.setLayoutData(data);

//		Label timeStampLabel = new Label(composite, SWT.NONE);
//		timeStampLabel.setText(TIMESTAMP_TITLE);
//
//		// timeStamp value label
//		Text timeStampValue = new Text(composite, SWT.READ_ONLY);
//		timeStampValue.setText(IDEResourceInfoUtils
//				.getDateStringValue(resource));
//		timeStampValue.setBackground(timeStampValue.getDisplay()
//				.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
//		timeStampValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
//				| GridData.GRAB_HORIZONTAL));

		// Not relevant to projects
		if (resource.getType() != IResource.PROJECT) {
			createDerivedButton(composite);
		} else {
			createEnableProjectSettingsButton(composite);
			createEditableButton(composite);
			createExecutableButton(composite);
			createArchiveButton(composite);
			createDerivedButton(composite);
			createWarnEntityNotReferencedButton(composite);
			createSelectStringFileButton(composite);
		}
		
	}

	private void createSelectStringFileButton(Composite composite) {
		GridData data = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);

		Label label = new Label(composite, SWT.NONE);
		label.setLayoutData(data);
		label.setText("String File (Optional)");
		
		this.selectedStringFileText = new Text(composite, SWT.BORDER);
		this.selectedStringFileText.setLayoutData(data);
		if (previousStringFileValue != null) {
			this.selectedStringFileText.setText(previousStringFileValue);
		}
		
		this.selectStringFile = new Button(composite, SWT.NONE);
		this.selectStringFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				selectStringFile();
			}
		});
		this.selectStringFile.setText("&Select");
		this.selectStringFile.setLayoutData(data);
	}

	protected void selectStringFile() {
	      FileDialog fd = new FileDialog(getShell(), SWT.OPEN); 
	        String selectedFile = fd.open(); 
	        if (selectedFile != null) { 
	            selectedStringFileText.setText(selectedFile); 
	        } 
	}

	private IContentDescription getContentDescription(IResource resource) {
		if (resource.getType() != IResource.FILE) {
			return null;
		}

		if (cachedContentDescription == null) {
			try {
				cachedContentDescription = ((IFile) resource)
						.getContentDescription();
			} catch (CoreException e) {
				// silently ignore
			}
		}
		return cachedContentDescription;
	}

	/**
	 * Returns whether the given resource is a linked resource bound to a path
	 * variable.
	 * 
	 * @param resource
	 *            resource to test
	 * @return boolean <code>true</code> the given resource is a linked
	 *         resource bound to a path variable. <code>false</code> the given
	 *         resource is either not a linked resource or it is not using a
	 *         path variable.
	 */
	private boolean isPathVariable(IResource resource) {
		if (!resource.isLinked()) {
			return false;
		}

		IPath resolvedLocation = resource.getLocation();
		if (resolvedLocation == null) {
			// missing path variable
			return true;
		}
		IPath rawLocation = resource.getRawLocation();
		if (resolvedLocation.equals(rawLocation)) {
			return false;
		}

		return true;
	}

	/**
	 * Reset the editableBox to the false.
	 */
	protected void performDefaults() {

		IResource resource = (IResource) getElement().getAdapter(
				IResource.class);
		
		if (resource == null)
			return;

		// Nothing to update if we never made the box
		if (this.ignoreCaseOnReferenceFilesBox != null) {
			this.ignoreCaseOnReferenceFilesBox.setSelection(false);
		}

		// Nothing to update if we never made the box
		if (this.allowDecimalBox != null) {
			this.allowDecimalBox.setSelection(false);
		}
		
		if (this.warnEntityNotReferencedBox != null) {
			this.warnEntityNotReferencedBox.setSelection(false);
		}

		// Nothing to update if we never made the box
		if (this.requireDecimalBox != null) {
			this.requireDecimalBox.setSelection(false);
		}
		
		if (this.validateResearchBox != null) {
			this.validateResearchBox.setSelection(true);
		}

		if (lineDelimiterEditor != null) {
			lineDelimiterEditor.loadDefault();
		}

	}

	/**
	 * Apply the read only state and the encoding to the resource.
	 */
	public boolean performOk() {

		IResource resource = (IResource) getElement().getAdapter(
				IResource.class);
		
		if (resource == null)
			return true;

		if (lineDelimiterEditor != null) {
			lineDelimiterEditor.store();
		}

		try {
				boolean hasChange = false;
				// Nothing to update if we never made the box
				if (enableProjectSettingsBox != null
						&& enableProjectSettingsBox.getSelection() != previousEnableProjectSettingsValue) {
					resource.setPersistentProperty(
							new QualifiedName(SOASE, ENABLE_PROJECT_SETTINGS),
							"" + enableProjectSettingsBox.getSelection());
					hasChange = true;
				}
				if (ignoreCaseOnReferenceFilesBox != null
						&& ignoreCaseOnReferenceFilesBox.getSelection() != previousIgnoreCaseValue) {
					resource.setPersistentProperty(
							new QualifiedName(SOASE, IGNORE_CASE),
							"" + ignoreCaseOnReferenceFilesBox.getSelection());
					hasChange = true;
				}
				if (allowDecimalBox != null
						&& allowDecimalBox.getSelection() != previousAllowDecimalValue) {
					resource.setPersistentProperty(
							new QualifiedName(SOASE, ALLOW_DECIMAL),
							"" + allowDecimalBox.getSelection());
					hasChange = true;
				}
				if (warnEntityNotReferencedBox != null
						&& warnEntityNotReferencedBox.getSelection() != previousWarnEntityNotReferencedValue) {
					resource.setPersistentProperty(
							new QualifiedName(SOASE, WARN_ENTITY_NOT_REFERENCED),
							"" + warnEntityNotReferencedBox.getSelection());
					hasChange = true;
				}
				if (requireDecimalBox != null
						&& requireDecimalBox.getSelection() != previousRequireDecimalValue) {
					resource.setPersistentProperty(
							new QualifiedName(SOASE, REQUIRE_DECIMAL),
							"" + requireDecimalBox.getSelection());
					hasChange = true;
				}
				if (validateResearchBox != null
						&& validateResearchBox.getSelection() != previousValidateResearchValue) {
					resource.setPersistentProperty(
							new QualifiedName(SOASE, VALIDATE_RESEARCH),
							"" + validateResearchBox.getSelection());
					hasChange = true;
				}
				if (selectedStringFileText != null
						&& selectedStringFileText.getText() != previousStringFileValue) {
					resource.setPersistentProperty(new QualifiedName(SOASE, STRING_FILE_LOCATION), selectedStringFileText.getText());
					hasChange = true;
				}
				if (hasChange) {
					previousEnableProjectSettingsValue = Boolean.parseBoolean(resource.getPersistentProperty(new QualifiedName(SOASE, ENABLE_PROJECT_SETTINGS)));
					previousIgnoreCaseValue = Boolean.parseBoolean(resource.getPersistentProperty(new QualifiedName(SOASE, IGNORE_CASE)));
					previousRequireDecimalValue = Boolean.parseBoolean(resource.getPersistentProperty(new QualifiedName(SOASE, REQUIRE_DECIMAL)));
					previousAllowDecimalValue = Boolean.parseBoolean(resource.getPersistentProperty(new QualifiedName(SOASE, ALLOW_DECIMAL)));
					previousWarnEntityNotReferencedValue = Boolean.parseBoolean(resource.getPersistentProperty(new QualifiedName(SOASE, WARN_ENTITY_NOT_REFERENCED)));
					previousValidateResearchValue = Boolean.parseBoolean(resource.getPersistentProperty(new QualifiedName(SOASE, VALIDATE_RESEARCH)));
					previousStringFileValue = resource.getPersistentProperty(new QualifiedName(SOASE, STRING_FILE_LOCATION));

					if (enableProjectSettingsBox != null) {
						enableProjectSettingsBox.setSelection(previousEnableProjectSettingsValue);
					}
					if (ignoreCaseOnReferenceFilesBox != null) {
						ignoreCaseOnReferenceFilesBox.setSelection(previousIgnoreCaseValue);
					}
					if (allowDecimalBox != null) {
						allowDecimalBox.setSelection(previousRequireDecimalValue);
					}
					if (warnEntityNotReferencedBox != null) {
						warnEntityNotReferencedBox.setSelection(previousWarnEntityNotReferencedValue);
					}
					if (requireDecimalBox != null) {
						requireDecimalBox.setSelection(previousAllowDecimalValue);
					}
					if (validateResearchBox != null) {
						validateResearchBox.setSelection(previousValidateResearchValue);
					}
					if (selectedStringFileText != null && previousStringFileValue != null) {
						selectedStringFileText.setText(previousStringFileValue);
					}
				}
		} catch (CoreException exception) {
			ErrorDialog.openError(getShell(),
					"InternalError", exception
							.getLocalizedMessage(), exception.getStatus());
			return false;
		}
		return true;
	}
}
