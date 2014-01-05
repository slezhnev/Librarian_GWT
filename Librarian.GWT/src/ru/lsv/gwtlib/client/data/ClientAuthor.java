/**
 * Представление автора на клиентской стороне
 */
package ru.lsv.gwtlib.client.data;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Представление автора на клиентской стороне
 * 
 * @author s.lezhnev
 */
public class ClientAuthor extends JavaScriptObject {

	/**
	 * Формирует текстовое описание
	 * 
	 * @return Текстовое описание
	 */
	public final String formText() {
		StringBuilder res = new StringBuilder();
		if (getLastName() != null) {
			res.append(getLastName()).append(" ");
		}
		if (getFirstName() != null) {
			res.append(getFirstName()).append(" ");
		}
		if (getMiddleName() != null) {
			res.append(getMiddleName()).append(" ");
		}
		return res.toString();
	}

	/**
	 * Default protected constructor
	 */
	protected ClientAuthor() {

	}

	/**
	 * Ключ при хранении
	 * 
	 * @return см.описание
	 */
	public final native int getAuthorId() /*-{
		return this.authorId;
	}-*/;

	/**
	 * Имя автора
	 * 
	 * @return см.описание
	 */
	public final native String getFirstName() /*-{
		return this.firstName;
	}-*/;

	/**
	 * Отчество, может отсутствовать
	 * 
	 * @return см.описание
	 */
	public final native String getMiddleName() /*-{
		return this.middleName;
	}-*/;

	/**
	 * Фамилия
	 * 
	 * @return см.описание
	 */
	public final native String getLastName() /*-{
		return this.lastName;
	}-*/;

	/**
	 * Книги выбранного автора
	 * 
	 * @return см.описание
	 */
	public final native JsArray<ClientBook> getBooks() /*-{
		return this.books;
	}-*/;

	/**
	 * Библиотека
	 * 
	 * @return см.описание
	 */
	public final native ClientLibrary getLibrary() /*-{
		return this.library;
	}-*/;

}
