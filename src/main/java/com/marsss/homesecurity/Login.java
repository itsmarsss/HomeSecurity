package com.marsss.homesecurity;

import java.util.Properties;
import java.util.regex.Pattern;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.Notification.Position;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@PageTitle("Login")
@Route("/login")
public class Login extends VerticalLayout implements BeforeEnterObserver {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String emails = "home.cameras.info@gmail.com";
	private String username = "admin";
	private String password = "admin";
	private VaadinSession vs;
	public Login() {
		setSizeFull();
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
		var headText = new H1("HomeSecurity");
		var shieldIcon = new Icon(VaadinIcon.SHIELD);
		HorizontalLayout titleLayout = new HorizontalLayout(headText, shieldIcon);

		headText.getStyle().set("margin", "0").set("font-weight", "bold");
		titleLayout.setAlignItems(Alignment.CENTER);

		add(titleLayout, loginForm());
	}

	private LoginForm loginForm() {
		LoginForm loginForm = new LoginForm();

		loginForm.addLoginListener(e -> {
			if(!e.getUsername().equalsIgnoreCase(username) || 
					!e.getPassword().equals(password)) {

				Notification.show("Incorrect username or password.").setDuration(750);

				loginForm.setError(true);

			}else {
				vs.setAttribute("authorized", "true");
				UI.getCurrent().navigate(Dashboard.class);
			}
		});

		loginForm.addForgotPasswordListener(e -> {
			var dialog = new Dialog();
			var infoText = new H4("Enter system stored email.");
			var emailField = new EmailField("Email");
			var okButton = new Button("Send");
			var cancelButton = new Button("Cancel", e1 -> dialog.close());
			HorizontalLayout buttonLayout = new HorizontalLayout(okButton, cancelButton);

			infoText.getStyle().set("margin", "0").set("font-size", "1.5em").set("font-weight", "bold");
			emailField.setErrorMessage("Please enter a valid email address");
			emailField.setClearButtonVisible(true);
			emailField.setValueChangeMode(ValueChangeMode.EAGER);
			emailField.addInputListener(e1 -> {
				String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +"[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
		        Pattern pattern = Pattern.compile(emailRegex);
		        if (pattern.matcher(emailField.getValue()).matches()) {
		            okButton.setEnabled(true);
		        }else {
		            okButton.setEnabled(false);
		        }
			});

            okButton.setEnabled(false);
			okButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
			buttonLayout.setAlignItems(Alignment.END);
			
			okButton.addClickListener(e1 -> {
				try {
					if(emails.contains(emailField.getValue())) {
						sendMail(emailField.getValue());
						Notification.show("Email sent", 2000, Position.BOTTOM_STRETCH);
					}else {
						Notification.show("Email not found", 2000, Position.BOTTOM_STRETCH);
					}
				}catch (MessagingException e2) {
					Notification.show("Email unsuccessful", 2000, Position.BOTTOM_STRETCH);
				}
			});
			okButton.addClickShortcut(Key.ENTER);

			dialog.add(infoText, emailField, buttonLayout);
			dialog.open();
		});
		return loginForm;
	}

	private void sendMail(String email) throws AddressException, MessagingException {
		Properties prop = new Properties();
		prop.put("mail.smtp.auth", true);
		prop.put("mail.smtp.host", "smtp.gmail.com");
		prop.put("mail.smtp.port", "587");
		prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");
		prop.put("mail.smtp.starttls.enable", "true");
		prop.put("mail.smtp.ssl.protocols", "TLSv1.2");

		Session session = Session.getInstance(prop, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("home.security.info@gmail.com", "kenny1668");
			}
		});

		Message message = new MimeMessage(session);
		message.setFrom(new InternetAddress("home.security.info@gmail.com"));
		message.setRecipients(
				Message.RecipientType.TO, InternetAddress.parse("kenny1234567890wu@gmail.com"));
		message.setSubject("HomeSecurity - Username & Password");

		String msg = "Username: " + username + "\nPassword: " + password;

		MimeBodyPart mimeBodyPart = new MimeBodyPart();
		mimeBodyPart.setContent(msg, "text/html; charset=utf-8");

		Multipart multipart = new MimeMultipart();
		multipart.addBodyPart(mimeBodyPart);

		message.setContent(multipart);

		Transport.send(message);
	}

	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
		vs = VaadinSession.getCurrent();
		if(vs.getAttribute("authorized") != null) {
			beforeEnterEvent.forwardTo(Dashboard.class);
		}
	}

}
