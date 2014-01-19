package ru.lsv.gwtlib.client;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * Темплейт для выдачи архивов
 * 
 * @author s.lezhnev
 * 
 */
public interface StatusPopupMessages extends Messages {
	/**
	 * Сформировать описание обработки архивов
	 * 
	 * @param currentArc
	 *            Номер текущего обрабатываемого архива
	 * @param totalArcs
	 *            Общее количество обрабатываемых архивов
	 * @param currentArcName
	 *            Название текущего архивного файла
	 * @return Сформированное описание для засовывания в label
	 */
	@DefaultMessage("Process arcs {0}/{1} (current - {2})")
	SafeHtml arcsDesc(int currentArc, int totalArcs, String currentArcName);

	/**
	 * Сформировать описание обработки файлов
	 * 
	 * @param currentFile
	 *            Номер текущего файла
	 * @param totalFiles
	 *            Общее количество файлов
	 * @return Сформированное описание для засовывания в label
	 */
	@DefaultMessage("Process files {0}/{1}")
	SafeHtml filesDesc(int currentFile, int totalFiles);

	/**
	 * Запрос
	 * 
	 * @return см.описание
	 */
	@DefaultMessage("Requesting...")
	String requesting();

	/**
	 * Сохранение книг
	 * 
	 * @return см.описание
	 */
	@DefaultMessage("Storing books...")
	String storingBooks();

	/**
	 * На текущий момент загрузка не проводится
	 * 
	 * @return см.описание
	 */
	@DefaultMessage("No current checks")
	String noCheck();
}