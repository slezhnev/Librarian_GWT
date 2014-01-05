package ru.lsv.gwtlib.downloader;

/**
 * Автор книги User: lsv Date: 20.10.2010 Time: 14:52:06
 */
public class DownloaderAuthor {

	public static final String PRIMARY_KEY = "AUTHOR_ID";

	/**
	 * Ключ при хранении
	 */
	private Integer authorId;
	/**
	 * Имя автора
	 */
	private String firstName;
	/**
	 * Отчество, может отсутствовать
	 */
	private String middleName;
	/**
	 * Фамилия
	 */
	private String lastName;
	/**
	 * Библиотека
	 */
	private DownloadLibrary library;

	public DownloaderAuthor() {
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String toString() {
		return "authorId :" + authorId + " firstName: " + firstName
				+ "; middleName: " + middleName + "; lastName: " + lastName;
	}

	public Integer getAuthorId() {
		return authorId;
	}

	public void setAuthorId(Integer authorId) {
		this.authorId = authorId;
	}

	/**
	 * Обрабатывает и создает имя автора
	 * 
	 * @return Сформированное имя без "null"
	 */
	public String makeName() {
		StringBuffer str = new StringBuffer();
		if (lastName != null)
			str.append(lastName);
		if (firstName != null) {
			str.append(" ");
			str.append(firstName);
		}
		if (middleName != null) {
			str.append(" ");
			str.append(middleName);
		}
		return str.toString().trim();
	}

	/**
	 * @return the library
	 */
	public DownloadLibrary getLibrary() {
		return library;
	}

	/**
	 * @param library
	 *            the library to set
	 */
	public void setLibrary(DownloadLibrary library) {
		this.library = library;
	}

}
