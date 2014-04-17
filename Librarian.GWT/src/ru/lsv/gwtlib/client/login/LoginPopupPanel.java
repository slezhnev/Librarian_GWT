/**
 * Панель для отображения диалога запроса пароля
 */
package ru.lsv.gwtlib.client.login;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Панель для отображения диалога запроса пароля
 * 
 * @author s.lezhnev
 */
public class LoginPopupPanel extends PopupPanel implements
		PopupPanel.PositionCallback {

	/**
	 * Форма ввода пароля
	 */
	private LoginUI loginUI;

	/**
	 * Default constructor
	 */
	public LoginPopupPanel() {
		super(false);
		loginUI = new LoginUI(this);
		setWidget(loginUI);
		setGlassEnabled(true);
	}

	@Override
	public void setPosition(int offsetWidth, int offsetHeight) {
		int left = (Window.getClientWidth() - loginUI.getOffsetWidth()) / 2;
		int top = (Window.getClientHeight() - loginUI.getOffsetHeight()) / 2;
		setPopupPosition(left, top);
	}
}
