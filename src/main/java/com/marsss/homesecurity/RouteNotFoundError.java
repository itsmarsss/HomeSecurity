package com.marsss.homesecurity;

import javax.servlet.http.HttpServletResponse;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.ErrorParameter;
import com.vaadin.flow.router.HasErrorParameter;
import com.vaadin.flow.router.NotFoundException;

@Tag(Tag.DIV)
public class RouteNotFoundError extends VerticalLayout implements HasErrorParameter<NotFoundException> {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    public int setErrorParameter(BeforeEnterEvent event, ErrorParameter<NotFoundException> parameter) {
        String request = event.getLocation().getPath();
        add(new H3("Uh oh, cannot navigate to '" + request + "'"));
        add(new H6("Try ip and port only."));
        return HttpServletResponse.SC_NOT_FOUND;
    }
}