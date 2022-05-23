package com.marsss.homesecurity;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("HomeSecurity")
@Route("")
public class Main extends VerticalLayout implements BeforeEnterObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Main() {
		add(new H1("Redirecting to Login page..."));
		add(new H3("Please add /login at the end of the link if redirection does not work."));
	}
	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		beforeEnterEvent.forwardTo(Login.class);
	}
}
