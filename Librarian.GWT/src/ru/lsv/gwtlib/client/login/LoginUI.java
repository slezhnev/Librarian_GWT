/**
 * Форма ввода пароля <br/>
 * Будет отображаться в LoginPopupPanel
 */
package ru.lsv.gwtlib.client.login;

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

	/**
	 * Сообщения
	 */
	private LoginMessages messages = GWT.create(LoginMessages.class);

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
		// Выключаем "оставаться залогиненным". Позднее потренируемся с MongoDB
		// для хранения сессий :)
		stayLoggedCB.setValue(false);
		stayLoggedCB.setEnabled(false);
	}

	@UiHandler("loginBtn")
	void onLoginBtnClick(ClickEvent event) {
		errorLabel.setVisible(false);
		if ((userNameEdit.getText() == null)
				|| (userNameEdit.getText().trim().length() == 0)) {
			errorLabel.setText(messages.emptyUserName());
			errorLabel.setVisible(true);
			return;
		}
		if ((passwordEdit.getText() == null)
				|| (passwordEdit.getText().trim().length() == 0)) {
			errorLabel.setText(messages.emptyPassword());
			errorLabel.setVisible(true);
			return;
		}
		StringBuilder strReq = new StringBuilder(URL.encodeQueryString("user"))
				.append("=")
				.append(URL.encodeQueryString(userNameEdit.getText()))
				.append("&").append(URL.encodeQueryString("psw")).append("=")
				.append(URL.encodeQueryString(passwordEdit.getText()))
				.append("&").append(URL.encodeQueryString("staylogged"))
				.append("=").append(stayLoggedCB.getValue());

		loginBtn.setEnabled(false);
		// Авторизуемся...
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST,
				GWT.getModuleBaseURL() + "librarian");
		builder.setHeader("Content-Type", "application/x-www-form-urlencoded");
		try {
			builder.sendRequest(strReq.toString(), new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					if (200 == response.getStatusCode()) {
						loginPopup.hide();
					} else {
						errorLabel.setText(messages.authorizationFailed());
						errorLabel.setVisible(true);
					}
					loginBtn.setEnabled(true);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					errorLabel.setText(messages.authorizationError(exception
							.getClass().getName()
							+ " - "
							+ exception.getLocalizedMessage()));
					errorLabel.setVisible(true);
					loginBtn.setEnabled(true);
				}

			});
		} catch (RequestException e) {
			errorLabel.setText(messages.authorizationError(e.getClass()
					.getName() + " - " + e.getLocalizedMessage()));
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
