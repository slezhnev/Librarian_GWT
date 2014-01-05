/**
 * Клиентское представление результатов загрузки книг в библиотеку
 */
package ru.lsv.gwtlib.client.data;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * Клиентское представление результатов загрузки книг в библиотеку
 * 
 * @author s.lezhnev
 * 
 */
public class ClientLoadStatus extends JavaScriptObject {

	/**
	 * Hide constructor
	 */
	protected ClientLoadStatus() {

	}

	/**
	 * Название текущей обрабатываемой библиотеки
	 */
	public final native String getCurrentLibrary() /*-{
		return this.currentLibrary;
	}-*/;

	/**
	 * Общее количество архивов для обработки
	 */
	public final native int getTotalArcsToProcess() /*-{
		return this.totalArcsToProcess;
	}-*/;

	/**
	 * Номер текушего обрабатываемого архива
	 */
	public final native int getCurrentArcsToProcess() /*-{
		return this.currentArcsToProcess;
	}-*/;

	/**
	 * Имя текущего обрабатываемого архива
	 */
	public final native String getCurrentArcName() /*-{
		return this.currentArcName;
	}-*/;

	/**
	 * Общее количество файлов в текущем обрабатываемом архиве
	 */
	public final native int getTotalFilesToProcess() /*-{
		return this.totalFilesToProcess;
	}-*/;

	/**
	 * Номер nекущего обрабатываемого файл
	 */
	public final native int getCurrentFileToProcess() /*-{
		return this.currentFileToProcess;
	}-*/;

}
