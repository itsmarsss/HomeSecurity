package com.marsss.homesecurity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.grid.dnd.GridDropLocation;
import com.vaadin.flow.component.grid.dnd.GridDropMode;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H5;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import com.marsss.homesecurity.ApplicationServiceInitListener;

@PageTitle("Dashboard")
@Route("/dashboard")
public class Dashboard extends VerticalLayout implements BeforeEnterObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//var stream = new StreamResource("foo", () -> {
	//byte[] data = getBytesFromFileMP3(soundfile);
	//return new ByteArrayInputStream(data); })
	//.setContentType("audio/mpeg"); // For MP3
	
	//byte[] imageBytes = getImageFromDB();
	//StreamResource resource = new StreamResource("fakeImageName.jpg", () -> new ByteArrayInputStream(imageBytes));
	//Image image = new Image(resource, "alternative image text");
	//layout.add(image);
	private Camera draggedItem;
	private ArrayList<Camera> cameras = new ArrayList<Camera>();
	private ArrayList<Dialog> dialogs = new ArrayList<Dialog>();

	public Dashboard() {
		var textTitle = new H1("Home Security - Cameras");

		textTitle.getStyle().set("margin", "0").set("font-size", "1.5em").set("font-weight", "bold");

		add(textTitle, buttons(), createGrid());
	}

	private HorizontalLayout buttons() {
		var settingsButton = new Button("Settings", new Icon(VaadinIcon.COG));
		var logoutButton = new Button("Logout", new Icon(VaadinIcon.SIGN_OUT));
		HorizontalLayout topButtonLayout = new HorizontalLayout(settingsButton, logoutButton);

		settingsButton.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
		settingsButton.addClickListener(e -> {
			settingsDialog();
		});

		logoutButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
		logoutButton.addClickListener(e -> {
			logoutDialog();
		});
		return topButtonLayout;
	}

	private void settingsDialog() {
		Dialog dialog = new Dialog();
		var titleText = new H1("HomeSecurity - Settings");

		dialog.setHeight("calc(100vh - (2*var(--lumo-space-m)))");
		dialog.setWidth("calc(100vw - (4*var(--lumo-space-m)))");
		dialog.open();
	}

	private void logoutDialog() {
		var dialog = new Dialog();
		var warnText = new H1("Are you sure you want you logout?");
		var infoText = new H5("You will need to login again.");
		var okButton = new Button("Logout");
		var cancelButton = new Button("Cancel", e1 -> dialog.close());
		HorizontalLayout buttonLayout = new HorizontalLayout(okButton, cancelButton);

		warnText.getStyle().set("margin", "0").set("font-size", "1.5em").set("font-weight", "bold");
		infoText.getStyle().set("color", "gray");
		okButton.addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_PRIMARY);

		okButton.addClickListener(e1 -> {
			VaadinSession.getCurrent().setAttribute("authorized", null);
			dialog.close();
			Notification.show("Successfully logged out!", 2500, Position.TOP_CENTER);
			UI.getCurrent().navigate(Dashboard.class);
		}); 

		dialog.add(warnText, infoText, buttonLayout);
		dialog.open();
	}

	private VerticalLayout createGrid() {
		for(int i = 0; i < 20; i+=4) {
			cameras.add(new Camera(i, "Name"+i, "Online"));
			cameras.add(new Camera(i+1, "Name"+(i+1), "Offline"));
			cameras.add(new Camera(i+2, "Name"+(i+2), "Pending"));
			cameras.add(new Camera(i+3, "Name"+(i+3), "L"));
		}
		for(Camera cam: cameras) {
			Dialog dialog = new Dialog();
			dialog.add(new Button(cam.getName()));
			dialog.setDraggable(true);
			dialog.setResizable(true);
			dialog.setModal(false);
			dialog.setMinHeight("15%");
			dialog.setMinWidth("15%");
			dialog.setMaxHeight("100%");
			dialog.setMaxWidth("100%");
			
			dialogs.add(dialog);
		}

		Grid<Camera> grid = new Grid<>(Camera.class, false);
		Binder<Camera> binder = new Binder<>(Camera.class);
		Editor<Camera> editor = grid.getEditor();
		TextField firstNameField = new TextField();

		Grid.Column<Camera> nameColumn = grid.addColumn(Camera::getName).setHeader("Device").setFooter(createNameFooter(cameras)).setSortable(true).setResizable(true).setFrozen(true);
		Grid.Column<Camera> statusColumn = grid.addColumn(createStatusComponentRenderer()).setHeader("Status").setFooter(createStatusFooter(cameras)).setResizable(true);

		GridListDataView<Camera> dataView = grid.setItems(cameras);

		firstNameField.setWidthFull();
		binder
		.forField(firstNameField)
		.bind(Camera::getName, Camera::setName);
		editor.setBinder(binder);
		editor.setBuffered(true);
		nameColumn.setEditorComponent(firstNameField);

		addCloseHandler(firstNameField, editor, grid);

		grid.setDropMode(GridDropMode.BETWEEN);
		grid.setRowsDraggable(true);

		grid.addDragStartListener(e -> draggedItem = e.getDraggedItems().get(0));
		grid.addDragEndListener(e -> draggedItem = null);

		grid.addDropListener(e -> {
			Camera targetCamera = e.getDropTargetItem().orElse(null);
			GridDropLocation dropLocation = e.getDropLocation();

			boolean cameraWasDroppedOntoItself = draggedItem.equals(targetCamera);

			if (targetCamera == null || cameraWasDroppedOntoItself)
				return;

			dataView.removeItem(draggedItem);

			if (dropLocation == GridDropLocation.BELOW) {
				dataView.addItemAfter(draggedItem, targetCamera);
			} else {
				dataView.addItemBefore(draggedItem, targetCamera);
			}
		});


		grid.addItemClickListener(e -> {
			switch(e.getClickCount()) {
			case 1:
				break;
			case 2:
				if(grid.getEditor().isOpen()) {
					grid.getEditor().save();
					e.getItem().setName(firstNameField.getValue());
				}else {
					grid.getEditor().editItem(e.getItem());
					grid.setRowsDraggable(false);
				}
				break;
			default:
				Notification.show("Chill there Willy Billy!");
				break;
			}
		});

		TextField searchField = new TextField("Name/Status");
		Button showList = new Button("Expand", new Icon(VaadinIcon.CHEVRON_DOWN_SMALL));
		Button selectMethod = new Button("Single");
		HorizontalLayout buttonLayout = new HorizontalLayout(showList, selectMethod);

		searchField.setWidth("100%");
		searchField.setPlaceholder("Search");
		searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
		searchField.setValueChangeMode(ValueChangeMode.EAGER);
		searchField.addValueChangeListener(e -> dataView.refreshAll());
		searchField.getStyle().set("margin", "0");

		showList.setWidth("40%");
		selectMethod.setWidth("60%");

		buttonLayout.setWidth("100%");
		buttonLayout.setAlignItems(Alignment.STRETCH);
		buttonLayout.setJustifyContentMode(JustifyContentMode.EVENLY);

		showList.addClickListener(e -> {
			if(grid.isAllRowsVisible()) {
				showList.setText("Expand");
				showList.setIcon(new Icon(VaadinIcon.CHEVRON_DOWN_SMALL));
			}else {
				showList.setText("Contract");
				showList.setIcon(new Icon(VaadinIcon.CHEVRON_UP_SMALL));
			}
			grid.setAllRowsVisible(!grid.isAllRowsVisible());
		});
		selectMethod.addClickListener(e -> {
			grid.deselectAll();
			if(selectMethod.getText().equals("Single")) {
				grid.setSelectionMode(Grid.SelectionMode.SINGLE);
				selectMethod.setText("Multi");
				selectMethod.setIcon(new Icon("lumo", "menu"));
			}else {
				grid.setSelectionMode(Grid.SelectionMode.MULTI);
				selectMethod.setText("Single");
				selectMethod.setIcon(new Icon("lumo", "minus"));
			}
			grid.addSelectionListener(e1 -> {
				dialogs.forEach(dialog -> dialog.close());
				ArrayList<Camera> selected = new ArrayList<>(e1.getAllSelectedItems());
				ArrayList<Camera> available = new ArrayList<>(dataView.getItems().collect(Collectors.toList()));
				Collections.sort(selected);
				for(Camera cam: selected) {
					if(available.contains(cam)) {
						dialogs.get(cam.getId()).open();
					}
				}
			});
		});
		
		selectMethod.clickInClient();

		dataView.addFilter(camera -> {
			String searchTerm = searchField.getValue().trim();

			if(searchTerm.isEmpty())
				return true;

			boolean matchesName = matchesTerm(camera.getName(), searchTerm);
			boolean matchesStatus = matchesTerm(camera.getStatus(), searchTerm);

			return matchesName || matchesStatus;
		});

		VerticalLayout cameraLayout = new VerticalLayout(searchField, buttonLayout, grid);
		cameraLayout.setPadding(false);
		return cameraLayout;
	}

	private static final SerializableBiConsumer<Button, Camera> statusComponentUpdater = (button, cam) -> {
		String status = cam.getStatus();
		if(status.equals("Online")) {
			button.addThemeVariants(ButtonVariant.LUMO_SUCCESS);
			button.setIcon(new Icon(VaadinIcon.CHECK_CIRCLE_O));
		}else if(status.equals("Offline")) {
			button.addThemeVariants(ButtonVariant.LUMO_ERROR);
			button.setIcon(new Icon(VaadinIcon.EXCLAMATION_CIRCLE_O));
		}else if(status.equals("Pending")) {
			button.setIcon(new Icon(VaadinIcon.CLOCK));
		}else {
			button.addThemeVariants(ButtonVariant.LUMO_CONTRAST);
			button.setIcon(new Icon(VaadinIcon.QUESTION_CIRCLE_O));
			status = "Unknown";
		}
		button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
		button.setText(status);
	};



	private static ComponentRenderer<Button, Camera> createStatusComponentRenderer() {
		return new ComponentRenderer<>(Button::new, statusComponentUpdater);
	}

	private String createNameFooter(ArrayList<Camera> cameras) {
		return String.format("%s devices", cameras.size());
	}

	private String createStatusFooter(ArrayList<Camera> cameras) {
		long onlineCount = cameras.stream()
				.filter(cam -> "Online".equals(cam.getStatus()))
				.count();
		long offlineCount = cameras.stream()
				.filter(cam -> "Offline".equals(cam.getStatus()))
				.count();
		long pendingCount = cameras.stream()
				.filter(cam -> "Pending".equals(cam.getStatus()))
				.count();
		long unknownCount = cameras.size()-onlineCount-offlineCount-pendingCount;
		return String.format("%s online, %s offline, %s pending, and %s unknown", onlineCount, offlineCount, pendingCount, unknownCount);
	}

	private boolean matchesTerm(String value, String searchTerm) {
		return value.toLowerCase().contains(searchTerm.toLowerCase());
	}

	private void addCloseHandler(TextField textField, Editor<Camera> editor, Grid<Camera> grid) {
		textField.addKeyDownListener(Key.ENTER, e-> {
			Camera camera = editor.getItem();
			if(textField.getValue().equals(camera.getName())) {
				editor.cancel();
				grid.setRowsDraggable(true);
				return;
			}
			Dialog dialog = new Dialog();
			var infoText = new H1("Save name for this session?");
			var saveButton = new Button("Save");
			var cancelButton = new Button("Cancel", e1 -> dialog.close());
			HorizontalLayout buttonLayout = new HorizontalLayout(saveButton, cancelButton);

			infoText.getStyle().set("margin", "0").set("font-size", "1.5em").set("font-weight", "bold");
			saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			cancelButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

			saveButton.addClickListener(e1 -> {
				editor.save();
				dialog.close();
				grid.setRowsDraggable(true);
				VaadinSession vs = VaadinSession.getCurrent();
				vs.setAttribute(String.valueOf(camera.getId()), textField.getValue());
				System.out.println(vs.getAttribute(String.valueOf(camera.getId())));
//				Object[] a = grid.getSelectedItems().toArray();
//				a[0].setName(textField.getValue());
				Notification.show("Session name saved");
			});
			saveButton.addClickShortcut(Key.ENTER);

			dialog.add(infoText, buttonLayout);
			dialog.open();
		});
		textField.addKeyDownListener(Key.ESCAPE, e-> {
			editor.cancel();
		});
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		VaadinSession vs = VaadinSession.getCurrent();
		if(vs.getAttribute("authorized") == null) {
			beforeEnterEvent.forwardTo(Login.class);
		}else {
			Notification.show("Successfully logged in!", 2500, Position.BOTTOM_START);
		}
	}

}
