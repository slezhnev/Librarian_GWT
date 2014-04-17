package ru.lsv.gwtlib.client.login;

import com.google.gwt.i18n.client.Messages;

/**
 * Сообщения логина
 * 
 * @author s.lezhnev
 * 
 */
public interface LoginMessages extends Messages {

	@DefaultMessage("User name is empty")
	String emptyUserName();

	@DefaultMessage("Password is empty")
	String emptyPassword();

	@DefaultMessage("Authorization failed")
	String authorizationFailed();

	@DefaultMessage("Authorization error - {0}")
	String authorizationError(String error);

	@DefaultMessage("Please wait...")
	String pleaseWait();

}
