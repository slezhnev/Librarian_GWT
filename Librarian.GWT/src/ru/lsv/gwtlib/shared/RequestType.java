/**
 * Типы запросов для LibrarianService 
 */
package ru.lsv.gwtlib.shared;

/**
 * Типы запросов для LibrarianService
 * 
 * @author s.lezhnev
 */
public enum RequestType {

	/**
	 * Любые
	 */
	ALL("all"),
	/**
	 * Только те, у которых есть непрочитанные книги
	 */
	ONLY_NOT_READED("notReaded"),
	/**
	 * Только "для чтения"
	 */
	ONLY_FOR_READING("forReading"),
	/**
	 * Только прочитанные
	 */
	ONLY_READED("readed");

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return text;
	}

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * Текстовое представление
	 */
	private String text;

	/**
	 * Default constructor
	 * 
	 * @param text
	 *            Текстовое представление
	 */
	private RequestType(String text) {
		this.text = text;
	}

	/**
	 * Формирование из текстового представления
	 * 
	 * @param text
	 *            Текстовое представление
	 * @return Элемент или null, если парсинг не удался
	 */
	public static RequestType fromString(String text) {
		if (text != null) {
			for (RequestType b : RequestType.values()) {
				if (text.equalsIgnoreCase(b.text)) {
					return b;
				}
			}
		}
		return null;
	}

}
