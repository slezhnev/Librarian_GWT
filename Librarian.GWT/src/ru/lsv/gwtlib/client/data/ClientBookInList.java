/**
 * Представление LibraryServlet.BookInList на клиенте
 */
package ru.lsv.gwtlib.client.data;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Представление LibraryServlet.BookInList на клиенте
 * 
 * @author s.lezhnev
 * 
 */
public class ClientBookInList extends JavaScriptObject {

	/**
	 * Формирует текстовое описание
	 * 
	 * @return Текстовое описание
	 */
	public final String formText() {
		return getTitle();
	}

	/**
	 * Default protected constructor
	 */
	protected ClientBookInList() {

	}

	/**
	 * Идентификатор книги
	 * 
	 * @return см.описание
	 */
	public final native int getBookId()  /*-{
		return this.bookId;
	}-*/;

	/**
	 * Название
	 * 
	 * @return см.описание
	 */
	public final native String getTitle()  /*-{
		return this.title;
	}-*/;

}
