package com.marsss.homesecurity;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Reject")
@Route("/reject")
public class NotAccepting extends VerticalLayout implements BeforeEnterObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotAccepting() {
		var titleText = new H1("Sorry, the server is not accepting sessions at the moment.");
		var infoText = new H4("Please try again later");
		titleText.getStyle().set("margin", "0");
		add(titleText, infoText);
	}
	
	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		// TODO Auto-generated method stub
		
	}

}
