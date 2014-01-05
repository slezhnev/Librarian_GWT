/**
 * Элемент в результатах поиска
 */
package ru.lsv.gwtlib.client;

import ru.lsv.gwtlib.client.data.ClientAuthor;
import ru.lsv.gwtlib.client.data.ClientBookInList;

/**
 * Элемент в результатах поиска <br/>
 * Враппер для хранения: <br/>
 * авторов<br/>
 * книг<br/>
 * серий
 * 
 * @author s.lezhnev
 */
public class SearchListElement {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (author != null) {
			return author.formText();
		} else if (book != null) {
			return book.formText();
		} else if (serie != null) {
			return serie;
		} else {
			return null;
		}
	}

	/**
	 * Книга
	 */
	private ClientBookInList book = null;
	/**
	 * Автор
	 */
	private ClientAuthor author = null;
	/**
	 * Серия
	 */
	private String serie = null;

	/**
	 * Конструктор для книги
	 * 
	 * @param book
	 *            Книга
	 */
	public SearchListElement(ClientBookInList book) {
		this.book = book;
	}

	/**
	 * Конструктор для автора
	 * 
	 * @param author
	 *            Автор
	 */
	public SearchListElement(ClientAuthor author) {
		this.author = author;
	}

	/**
	 * Конструктор для серии
	 * 
	 * @param serie
	 *            Серия
	 */
	public SearchListElement(String serie) {
		this.serie = serie;
	}

	/**
	 * @return the book
	 */
	public ClientBookInList getBook() {
		return book;
	}

	/**
	 * @return the author
	 */
	public ClientAuthor getAuthor() {
		return author;
	}

	/**
	 * @return the serie
	 */
	public String getSerie() {
		return serie;
	}

}
