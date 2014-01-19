/**
 * Локализация для Librarian_GWT
 */
package ru.lsv.gwtlib.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Локализация для Librarian_GWT
 * 
 * @author s.lezhnev
 */
public interface Librarian_GWTMessages extends Messages {

	@DefaultMessage("Show new")
	String showNew();

	@DefaultMessage("Show for reading")
	String showForReading();

	@DefaultMessage("Show readed")
	String showReaded();

	@DefaultMessage("Work with")
	String workWith();

	@DefaultMessage("Authors")
	String authors();

	@DefaultMessage("Series")
	String series();

	@DefaultMessage("Books")
	String books();

	@DefaultMessage("Search")
	String search();

	@DefaultMessage("Search results")
	String searchResults();

	@DefaultMessage("Book description")
	String bookDescription();

	@DefaultMessage("Logoff")
	String logoff();

	@DefaultMessage("Load status")
	String loadStatus();

	@DefaultMessage("Error while accessing server side")
	String serverSideError();

	@DefaultMessage("Failed to logoff")
	String failLogoff();

	@DefaultMessage("Error while updating book - update does not return TRUE")
	String updateBookErrorNotTrue();

	@DefaultMessage("Error while updating book")
	String updateBookError();

	@DefaultMessage("Could not retrieve JSON")
	String jsonRetrieveError();

	@DefaultMessage("Error - non JSON answer")
	String nonJsonAnswer();

	@DefaultMessage("Loading...")
	String loading();

	@DefaultMessage("Searching...")
	String searching();

	@DefaultMessage("Name")
	String bookName();

	@DefaultMessage("No book name")
	String noBookName();

	@DefaultMessage("Serie")
	String serie();

	@DefaultMessage("Archive")
	String archive();

	@DefaultMessage("File name")
	String fileName();

	@DefaultMessage("DELETED IN LIBRARY")
	String deletedInLibrary();

}
