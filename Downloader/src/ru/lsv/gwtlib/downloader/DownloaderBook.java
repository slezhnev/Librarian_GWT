package ru.lsv.gwtlib.downloader;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Хранилище для книги <br/>
 * Несколько дооптимизировано и содержит набор сериализаторов для Gson
 * 
 * @author s.leznev
 */
public class DownloaderBook {

	public static final String PRIMARY_KEY = "BOOK_ID";

	/**
	 * Ключ при хранении
	 */
	private Integer bookId;
	/**
	 * �?мя файла этой книги в zipFileName
	 */
	private String id;
	/**
	 * Список авторов
	 */
	private List<DownloaderAuthor> authors;
	/**
	 * Название
	 */
	private String title;
	/**
	 * Жанр
	 */
	private String genre;
	/**
	 * Язык
	 */
	private String language;
	/**
	 * �?сходный язык (для переводных книг)
	 */
	private String sourceLanguage;
	/**
	 * Название серии (если есть)
	 */
	private String serieName;
	/**
	 * Номер в серии (если есть)
	 */
	private Integer numInSerie;
	/**
	 * Zip файл, где лежит книга
	 */
	private String zipFileName;
	/**
	 * CRC32 книги. Нужно для устранения дублей в библиотеке
	 */
	private Long crc32;
	/**
	 * Время добавления книги в библиотеку
	 */
	private Date addTime;

	/**
	 * Аннотация к книге
	 */
	private String annotation;

	/**
	 * Отметка о том, что книга прочитана конкретным пользователем
	 */
	private boolean readed;

	/**
	 * Отметка о том, что книгу неплохо было бы прочитать конкретному
	 * пользователю
	 */
	private boolean mustRead;

	/**
	 * Отметка о том, что книга удалена в библиотеке
	 */
	private Boolean deletedInLibrary;
	/**
	 * Библиотека
	 */
	private DownloadLibrary library;

	public DownloaderBook() {
		authors = new ArrayList<DownloaderAuthor>();
		genre = "";
	}

	public List<DownloaderAuthor> getAuthors() {
		return authors;
	}

	public void setAuthors(List<DownloaderAuthor> authors) {
		this.authors = authors;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getSourceLanguage() {
		return sourceLanguage;
	}

	public void setSourceLanguage(String sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}

	public String getSerieName() {
		return serieName;
	}

	public void setSerieName(String serieName) {
		this.serieName = serieName;
	}

	public Integer getNumInSerie() {
		return numInSerie;
	}

	public void setNumInSerie(Integer numInSerie) {
		this.numInSerie = numInSerie;
	}

	public String toString() {
		StringBuffer str = new StringBuffer();
		if (title == null)
			str.append("Нет названия");
		else
			str.append(title);
		if ((serieName != null) && (serieName.trim().length() > 0)) {
			str.append(" (").append(serieName).append(" - ");
			if (numInSerie != null)
				str.append(numInSerie);
			else
				str.append("нет");
			str.append(")");
		}
		if (authors != null) {
			str.append(" (");
			for (int i = 0; i < authors.size(); i++) {
				if (i != 0) {
					str.append(" ,");
				}
				str.append(authors.get(i).makeName());
			}
			str.append(")");
		}
		return str.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getZipFileName() {
		return zipFileName;
	}

	public void setZipFileName(String zipFileName) {
		this.zipFileName = zipFileName;
	}

	public Integer getBookId() {
		return bookId;
	}

	public void setBookId(Integer bookId) {
		this.bookId = bookId;
	}

	public Long getCrc32() {
		return crc32;
	}

	public void setCrc32(Long crc32) {
		this.crc32 = crc32;
	}

	public Date getAddTime() {
		return addTime;
	}

	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}

	public String getAnnotation() {
		return annotation;
	}

	public void setAnnotation(String annotation) {
		this.annotation = annotation;
	}

	public boolean getReaded() {
		return readed;
	}

	public boolean getMustRead() {
		return mustRead;
	}

	public Boolean getDeletedInLibrary() {
		return deletedInLibrary;
	}

	public void setDeletedInLibrary(Boolean deletedInLibrary) {
		this.deletedInLibrary = deletedInLibrary;
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
	
    /**
     * Создает текстовое представление автора (или "Сборник", если авторов несколько)
     * Используется при экспорте книг
     *
     * @return Текстовое представление автора/ов
     */
    public String getAuthorsToString() {
        if (authors.size() >= 1) {
            DownloaderAuthor author = authors.get(0);
            return author.makeName().trim();
        } else {
            return "Без автора";
        }
    }
	

}
