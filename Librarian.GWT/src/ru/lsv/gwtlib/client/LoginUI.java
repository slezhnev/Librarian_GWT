/**
 * Форма ввода пароля <br/>
 * Будет отображаться в LoginPopupPanel
 */
package ru.lsv.gwtlib.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.event.dom.client.KeyDownEvent;

/**
 * Форма ввода пароля <br/>
 * Будет отображаться в LoginPopupPanel
 * 
 * @author s.lezhnev
 */
public class LoginUI extends Composite {

	private static LoginUIUiBinder uiBinder = GWT.create(LoginUIUiBinder.class);
	@UiField
	TextBox userNameEdit;
	@UiField
	TextBox passwordEdit;
	@UiField
	Button loginBtn;
	@UiField
	Label errorLabel;
	@UiField
	CheckBox stayLoggedCB;
	private PopupPanel loginPopup;

	interface LoginUIUiBinder extends UiBinder<Widget, LoginUI> {
	}

	/**
	 * Default constructor
	 * 
	 * @param loginPopup
	 *            Popup для отображения логина. Должен прятаться по завершению
	 *            логина
	 */
	public LoginUI(final PopupPanel loginPopup) {
		initWidget(uiBinder.createAndBindUi(this));
		this.loginPopup = loginPopup;
		errorLabel.setVisible(false);
	}

	@UiHandler("loginBtn")
	void onLoginBtnClick(ClickEvent event) {
		errorLabel.setVisible(false);
		if ((userNameEdit.getText() == null)
				|| (userNameEdit.getText().trim().length() == 0)) {
			errorLabel.setText("User name is empty");
			errorLabel.setVisible(true);
			return;
		}
		if ((passwordEdit.getText() == null)
				|| (passwordEdit.getText().trim().length() == 0)) {
			errorLabel.setText("Password is empty");
			errorLabel.setVisible(true);
			return;
		}
		StringBuilder strUrl = new StringBuilder(GWT.getModuleBaseURL())
				.append("librarian?user=").append(userNameEdit.getText())
				.append("&psw=").append(passwordEdit.getText())
				.append("&staylogged=").append(stayLoggedCB.getValue());
		loginBtn.setEnabled(false);
		// Авторизуемся...
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				URL.encode(strUrl.toString()));
		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					if (200 == response.getStatusCode()) {
						loginPopup.hide();
					} else {
						errorLabel.setText("Authorization failed");
						errorLabel.setVisible(true);
					}
					loginBtn.setEnabled(true);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					errorLabel.setText("Authorization failed");
					errorLabel.setVisible(true);
					loginBtn.setEnabled(true);
				}

			});
		} catch (RequestException e) {
			errorLabel.setText("Authorization failed");
			errorLabel.setVisible(true);
			loginBtn.setEnabled(true);
		}
	}

	@UiHandler("userNameEdit")
	void onUserNameEditKeyDown(KeyDownEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			loginBtn.click();
		}
	}

	@UiHandler("passwordEdit")
	void onPasswordEditKeyDown(KeyDownEvent event) {
		if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
			loginBtn.click();
		}
	}
}
