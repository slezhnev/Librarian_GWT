/**
 * Представление книги на клиенте
 */
package ru.lsv.gwtlib.client.data;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

/**
 * Представление книги на клиенте
 * 
 * @author s.lezhnev
 * 
 */
;
public class ClientBook extends JavaScriptObject {

	/**
	 * Формирует текстовое описание
	 * 
	 * @return Текстовое описание
	 */
	;
	public final String formText() {
		return getTitle();
	}

	/**
	 * Default protected constructor
	 */
	;

	protected ClientBook() {

	}

	/**
	 * Ключ при хранении
	 * 
	 * @return см.описание
	 */
	;

	public final native int getBookId() /*-{
		return this.bookId;
	}-*/;

	/**
	 * Имя файла этой книги в zipFileName
	 * 
	 * @return см.описание
	 */
	;

	public final native String getId() /*-{
		return this.id;
	}-*/;

	/**
	 * Список авторов
	 * 
	 * @return см.описание
	 */
	;

	public final native JsArray<ClientAuthor> getAuthors() /*-{
		return this.authors;
	}-*/;

	/**
	 * Название
	 * 
	 * @return см.описание
	 */
	;

	public final native String getTitle() /*-{
		return this.title;
	}-*/;

	/**
	 * Жанр
	 * 
	 * @return см.описание
	 */
	;

	public final native String getGenre() /*-{
		return this.genre;
	}-*/;

	/**
	 * Язык
	 * 
	 * @return см.описание
	 */
	;

	public final native String getLanguage() /*-{
		return this.language;
	}-*/;

	/**
	 * Исходный язык (для переводных книг)
	 * 
	 * @return см.описание
	 */
	;

	public final native String getSourceLanguage() /*-{
		return this.sourceLanguage;
	}-*/;

	/**
	 * Название серии (если есть)
	 * 
	 * @return см.описание
	 */
	;

	public final native String getSerieName() /*-{
		return this.serieName;
	}-*/;

	/**
	 * Номер в серии (если есть)
	 * 
	 * @return см.описание
	 */
	;

	public final native String getNumInSerie() /*-{
		if (typeof this.numInSerie != "undefined") {
			return "" + this.numInSerie;
		} else {
			return "не указано";
		}
	}-*/;

	/**
	 * Zip файл, где лежит книга
	 * 
	 * @return см.описание
	 */
	;

	public final native String getZipFileName() /*-{
		return this.zipFileName;
	}-*/;

	/**
	 * CRC32 книги. Нужно для устранения дублей в библиотеке
	 * 
	 * @return см.описание
	 */
	;

	public final native String getCrc32() /*-{
		return this.crc32;
	}-*/;

	/**
	 * Время добавления книги в библиотеку
	 * 
	 * @return см.описание
	 */
	;

	public final native Date getAddTime() /*-{
		return this.addTime;
	}-*/;

	/**
	 * Аннотация к книге
	 * 
	 * @return см.описание
	 */
	;

	public final native String getAnnotation() /*-{
		return this.annotation;
	}-*/;

	/**
	 * Отметка о том, что книга прочитана конкретным пользователем
	 * 
	 * @return см.описание
	 */
	;

	public final native boolean isReaded() /*-{
		return this.readed;
	}-*/;

	/**
	 * Изменяет readed
	 * 
	 * @param isReaded
	 *            Новое состояние
	 */
	public final native void setReaded(boolean isReaded) /*-{
		this.readed = isReaded;
	}-*/;

	/**
	 * Отметка о том, что книгу неплохо было бы прочитать конкретному
	 * пользователю
	 * 
	 * @return см.описание
	 */
	;

	public final native boolean isMustRead() /*-{
		return this.mustRead;
	}-*/;

	/**
	 * Изменяет mustRead
	 * 
	 * @param isMustRead
	 *            Новое состояние
	 */
	public final native void setMustRead(boolean isMustRead) /*-{
		this.mustRead = isMustRead;
	}-*/;

	/**
	 * Отметка о том, что книга удалена в библиотеке
	 * 
	 * @return см.описание
	 */
	;

	public final native boolean getDeletedInLibrary() /*-{
		return this.deletedInLibrary;
	}-*/;

	/**
	 * Библиотека
	 * 
	 * @return см.описание
	 */
	;

	public final native ClientLibrary getLibrary() /*-{
		return this.library;
	}-*/;

}
