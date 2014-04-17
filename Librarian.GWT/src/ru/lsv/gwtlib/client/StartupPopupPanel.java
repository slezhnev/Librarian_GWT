package ru.lsv.gwtlib.client;

import ru.lsv.gwtlib.client.login.LoginMessages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Popup, отображающийся в самом начале - при проверке логина в систему
 * 
 * @author s.lezhnev
 * 
 */
public class StartupPopupPanel extends PopupPanel implements
		PopupPanel.PositionCallback {

	private LoginMessages messages = GWT.create(LoginMessages.class);
	private Label infoLabel;

	public StartupPopupPanel() {

		SimplePanel simplePanel = new SimplePanel();
		setWidget(simplePanel);
		simplePanel.setSize("100%", "100%");

		infoLabel = new Label(messages.pleaseWait());
		simplePanel.setWidget(infoLabel);
		infoLabel.setSize("100%", "100%");

		setGlassEnabled(true);
	}

	@Override
	public void setPosition(int offsetWidth, int offsetHeight) {
		int left = (Window.getClientWidth() - this.getOffsetWidth()) / 2;
		int top = (Window.getClientHeight() - this.getOffsetHeight()) / 2;
		setPopupPosition(left, top);
	}

	/**
	 * Выдает текст на infoLabel
	 * 
	 * @param message
	 *            Сообщение для выдачи
	 */
	public void setMessage(String message) {
		infoLabel.setText(message);
	}

}
