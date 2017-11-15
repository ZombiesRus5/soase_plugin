package com.zombiesrus5.plugin.sose.builder;

import org.eclipse.core.runtime.IProgressMonitor;

import sose.tools.SetupMonitor;

class EntityParserSetupMonitor implements SetupMonitor {
	IProgressMonitor monitor = null;
	
	public EntityParserSetupMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
	}

	@Override
	public void finished(int amount) {
		monitor.worked(1);
	}

	@Override
	public void subTask(String attribute) {
		monitor.subTask(attribute);
	}

	@Override
	public void beginTask(String type, int length) {
		monitor.subTask("Setting up " + type + "...");
	}
	
}