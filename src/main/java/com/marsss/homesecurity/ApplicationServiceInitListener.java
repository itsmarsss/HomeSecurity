package com.marsss.homesecurity;

import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

public class ApplicationServiceInitListener implements VaadinServiceInitListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void serviceInit(ServiceInitEvent event) {
		RunOnceServer ros = new RunOnceServer();
		ros.run();
	}
}
