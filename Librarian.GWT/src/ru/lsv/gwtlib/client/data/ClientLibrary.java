/**
 * Клиентское представление библиотеки
 */
package ru.lsv.gwtlib.client.data;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Клиентское представление библиотеки
 * 
 * @author s.lezhnev
 */
public class ClientLibrary extends JavaScriptObject {

	/**
	 * Default protected constructor
	 */
	protected ClientLibrary() {

	}

	/**
	 * Идентификатор библиотеки
	 * 
	 * @return см.описание
	 */
	public final native int getLibraryId() /*-{
		return this.libraryId;
	}-*/;

	/**
	 * Название библиотеки
	 * 
	 * @return см.описание
	 */
	public final native String getName() /*-{
		return this.name;
	}-*/;
}
