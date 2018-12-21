/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.zombiesrus5.plugin.sose.properties;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.osgi.service.prefs.BackingStoreException;

import com.zombiesrus5.plugin.sose.preferences.PreferenceConstants;

/**
 * A class to handle editing of the Line delimiter preferences in core.
 * 
 * @since 3.1
 */
public class LineDelimiterEditor {

	private static final String STRICT_VALIDATION = PreferenceConstants.STRICT_VERSION;

	private Button vanillaButton;
	
	private Button entrenchmentButton;
	
	private Button diplomacyButton;
	
	private Button rebellionButton;

	/*
	 * The project whose preferences should be set. In some cases this class
	 * will be used to edit project preferences.
	 */
	private IProject project;

	private Group group;

	/**
	 * Creates a new encoding field editor with the given preference name, label
	 * and parent.
	 * 
	 * @param composite
	 *            the parent of the field editor's control
	 */
	public LineDelimiterEditor(Composite composite) {
		this(composite, null);
	}

	/**
	 * Creates a new encoding field editor with the given preference name, label
	 * and parent.
	 * 
	 * @param composite
	 *            the parent of the field editor's control
	 * @param project
	 *            the project to set preferences on
	 * 
	 */
	public LineDelimiterEditor(Composite composite, IProject project) {
		this.project = project;
		createControl(composite);
	}

	/**
	 * Creates this field editor's main control containing all of its basic
	 * controls.
	 * 
	 * @param parent
	 *            the parent control
	 */
	protected void createControl(Composite parent) {
		Font font = parent.getFont();
		group = new Group(parent, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(data);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		group.setLayout(layout);
		group.setText("Strict Version Validation");
		group.setFont(font);

		SelectionAdapter buttonListener = new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				if (e.widget.equals(defaultButton)) {
//					updateState(true);
//				} else {
//					updateState(false);
//				}
			}
		};

		vanillaButton = new Button(group, SWT.RADIO);
		if (project == null) {
			vanillaButton.setText("Vanilla");
		} else {
			vanillaButton.setText("Vanilla");
		}
		
		data = new GridData();
		data.horizontalSpan = 2;
		vanillaButton.setLayoutData(data);
		vanillaButton.addSelectionListener(buttonListener);
		vanillaButton.setFont(font);

		entrenchmentButton = new Button(group, SWT.RADIO);
		if (project == null) {
			entrenchmentButton.setText("Rebellion");
		} else {
			entrenchmentButton.setText("Rebellion");
		}
		
		data = new GridData();
		data.horizontalSpan = 2;
		entrenchmentButton.setLayoutData(data);
		entrenchmentButton.addSelectionListener(buttonListener);
		entrenchmentButton.setFont(font);
	
		diplomacyButton = new Button(group, SWT.RADIO);
		if (project == null) {
			diplomacyButton.setText("Rebellion193");
		} else {
			diplomacyButton.setText("Rebellion193");
		}
		
		data = new GridData();
		data.horizontalSpan = 2;
		diplomacyButton.setLayoutData(data);
		diplomacyButton.addSelectionListener(buttonListener);
		diplomacyButton.setFont(font);
	
		rebellionButton = new Button(group, SWT.RADIO);
		if (project == null) {
			rebellionButton.setText("Rebellion185");
		} else {
			rebellionButton.setText("Rebellion185");
		}
		
		data = new GridData();
		data.horizontalSpan = 2;
		rebellionButton.setLayoutData(data);
		rebellionButton.addSelectionListener(buttonListener);
		rebellionButton.setFont(font);
	}

	/**
	 * Initializes this field editor with the preference value from the
	 * preference store.
	 */
	public void loadDefault() {
		updateState("Rebellion");
	}

	/**
	 * Initializes this field editor with the preference value from the
	 * preference store.
	 */
	public void doLoad() {
		String val = null;
		try {
			val = project.getPersistentProperty(new QualifiedName("soase", STRICT_VALIDATION));
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateState(val);
	}

	/**
	 * Returns the value that is currently stored for the encoding.
	 * 
	 * @return the currently stored encoding
	 */
	public String getStoredValue() {
		try {
			return project.getPersistentProperty(new QualifiedName("soase", STRICT_VALIDATION));
		} catch (CoreException e) {
			
		}
		return null;
	}

	/**
	 * Answer the <code>IScopeContext</code> for the receiver, this will be a
	 * project scope if the receiver is editing project preferences, otherwise
	 * instance scope.
	 * 
	 * @return the scope context
	 */
	private IScopeContext getScopeContext() {
		if (project != null) {
			return new ProjectScope(project);
		}

		return new InstanceScope();
	}

	private void updateState(String state) {
		if ("Vanilla".equals(state)) {
			vanillaButton.setSelection(true);
			entrenchmentButton.setSelection(false);
			diplomacyButton.setSelection(false);
			rebellionButton.setSelection(false);
		} else if ("Entrenchment".equals(state)){
			vanillaButton.setSelection(false);
			entrenchmentButton.setSelection(true);
			diplomacyButton.setEnabled(false);
			rebellionButton.setEnabled(false);
		} else if ("Rebellion".equals(state)){
			vanillaButton.setSelection(false);
			entrenchmentButton.setSelection(false);
			diplomacyButton.setEnabled(false);
			rebellionButton.setEnabled(true);
		} else {
			vanillaButton.setSelection(false);
			entrenchmentButton.setSelection(false);
			diplomacyButton.setSelection(true);
			rebellionButton.setEnabled(false);
		}
	}

	/**
	 * Store the currently selected line delimiter value in the preference
	 * store.
	 */
	public void store() {
		String val = "Diplomacy";
		
		if (vanillaButton.getSelection()) {
			val = "Vanilla";
		}
		if (entrenchmentButton.getSelection()) {
			val = "Entrenchment";
		}
		if (rebellionButton.getSelection()) {
			val = "Rebellion";
		}
		try {
			project.setPersistentProperty(new QualifiedName("soase", STRICT_VALIDATION), val);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	 /**
     * Set whether or not the controls in the field editor
     * are enabled.
     * @param enabled The enabled state.
     */
    public void setEnabled(boolean enabled) {
        group.setEnabled(enabled);
        Control [] children = group.getChildren();
        for (int i = 0; i < children.length; i++) {
			children[i].setEnabled(enabled);
			
		}
    }

}