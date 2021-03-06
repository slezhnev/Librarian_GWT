package ru.lsv.gwtlib.server.data;

import java.util.*;
import java.lang.reflect.Type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Хранилище для книги <br/>
 * Несколько дооптимизировано и содержит набор сериализаторов для Gson
 * 
 * @author s.leznev
 */
public class Book {

	public static final String PRIMARY_KEY = "BOOK_ID";

	/**
	 * Ключ при хранении
	 */
	private Integer bookId;
	/**
	 * Имя файла этой книги в zipFileName
	 */
	private String id;
	/**
	 * Список авторов
	 */
	private List<Author> authors;
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
	 * Исходный язык (для переводных книг)
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
	private List<LibUser> readed;

	/**
	 * Отметка о том, что книгу неплохо было бы прочитать конкретному
	 * пользователю
	 */
	private List<LibUser> mustRead;

	/**
	 * Отметка о том, что книга удалена в библиотеке
	 */
	private Boolean deletedInLibrary;
	/**
	 * Библиотека
	 */
	private Library library;

	public Book() {
		authors = new ArrayList<Author>();
		genre = "";
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void setAuthors(List<Author> authors) {
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

	public String formHTMLDescription() {
		StringBuffer str = new StringBuffer(
				"<html><b>Название:</b>&nbsp;&nbsp;");
		if (title == null)
			str.append("Нет названия");
		else
			str.append(title);
		str.append("<br>");
		if (serieName != null) {
			str.append("<b>Серия:</b>&nbsp;&nbsp;").append(serieName);
			str.append(" - ");
			if (numInSerie == null)
				str.append("б/н");
			else
				str.append(numInSerie);
			str.append("<br><br>");
		}
		str.append("<b>Авторы</b>:<br>");
		for (Author author : authors) {
			str.append(author.makeName()).append("<br>");
		}
		str.append("<br>");
		str.append("<b>Архив:</b><br>").append(zipFileName).append("<br>");
		str.append("<b>Имя файла:</b><br>").append(id);
		if ((deletedInLibrary != null) && (deletedInLibrary)) {
			str.append("<br><br><b>УДАЛЕНА В БИБЛИОТЕКЕ</b><br>");
		}
		return str.toString();
	}

	public String toLongString() {
		String S = "authors:\n";
		for (Author author : authors) {
			S = S + author + "\n";
		}
		return S + "bookId: " + bookId + " id: " + id + " title: " + title
				+ "\ngenre: " + genre + "\nlang: " + language + "\nsrc-lang: "
				+ sourceLanguage + "\nserieName: " + serieName
				+ "\nnumInSerie: " + numInSerie + "\nzipFile: " + zipFileName
				+ "\nCRC: " + crc32;
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

	@Override
	public int hashCode() {
		return Utils.getHash(bookId) + Utils.getHash(id)
				+ Utils.getHash(authors) + Utils.getHash(title)
				+ Utils.getHash(genre) + Utils.getHash(language)
				+ Utils.getHash(sourceLanguage) + Utils.getHash(serieName)
				+ Utils.getHash(numInSerie) + Utils.getHash(zipFileName)
				+ Utils.getHash(crc32);
	}

	@Override
	public boolean equals(Object some) {
		if (some == null)
			return false;
		if (this == some)
			return true;
		if (!(some instanceof Book))
			return false;
		Book book = (Book) some;
		return Utils.areEqual(this.authors, book.authors)
				&& Utils.areEqual(this.bookId, book.bookId)
				&& Utils.areEqual(this.genre, book.genre)
				&& Utils.areEqual(this.title, book.title)
				&& Utils.areEqual(this.id, book.id)
				&& Utils.areEqual(this.language, book.language)
				&& Utils.areEqual(this.serieName, book.serieName)
				&& Utils.areEqual(this.numInSerie, book.numInSerie)
				&& Utils.areEqual(this.crc32, book.crc32)
				&& Utils.areEqual(this.sourceLanguage, book.sourceLanguage)
				&& Utils.areEqual(this.zipFileName, book.zipFileName);
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

	public List<LibUser> getReaded() {
		return readed;
	}

	public void setReaded(List<LibUser> readed) {
		this.readed = readed;
	}

	public List<LibUser> getMustRead() {
		return mustRead;
	}

	public void setMustRead(List<LibUser> mustRead) {
		this.mustRead = mustRead;
	}

	/**
	 * Создает текстовое представление автора (или "Сборник", если авторов
	 * несколько) Используется при экспорте книг
	 * 
	 * @return Текстовое представление автора/ов
	 */
	public String getAuthorsToString() {
		if (authors.size() >= 1) {
			Author author = authors.get(0);
			return author.makeName().trim();
		} else {
			return "Без автора";
		}
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
	public Library getLibrary() {
		return library;
	}

	/**
	 * @param library
	 *            the library to set
	 */
	public void setLibrary(Library library) {
		this.library = library;
	}

	/**
	 * Gson сериализатор
	 * 
	 * @param userId
	 *            Идентификатор текущего пользователя
	 * @return Сериализированный объект
	 */
	public JsonObject json(int userId) {
		JsonObject res = new JsonObject();
		res.addProperty("bookId", bookId);
		res.addProperty("id", id);
		res.addProperty("title", title);
		res.addProperty("genre", genre);
		res.addProperty("language", language);
		res.addProperty("sourceLanguage", sourceLanguage);
		res.addProperty("serieName", serieName);
		res.addProperty("numInSerie", numInSerie);
		res.addProperty("zipFileName", zipFileName);
		res.addProperty("crc32", crc32);
		res.addProperty("addTime", addTime.toString());
		res.addProperty("annotation", annotation);
		boolean bReaded = false;
		for (LibUser user : readed) {
			if (userId == user.getId()) {
				bReaded = true;
				break;
			}
		}
		res.addProperty("readed", bReaded);
		boolean bMustRead = false;
		for (LibUser user : mustRead) {
			if (userId == user.getId()) {
				bMustRead = true;
				break;
			}
		}
		res.addProperty("mustRead", bMustRead);
		res.addProperty("deletedInLibrary", deletedInLibrary);
		res.add("library", library.json());
		return res;
	}

	/**
	 * Gson сериализатор книг с авторами
	 * 
	 * @author s.lezhnev
	 */
	public static class BooksWithAuthorsSerializer implements
			JsonSerializer<Book> {

		/**
		 * Идентификатор текущего пользователя
		 */
		private int userId;

		/**
		 * Default constructor
		 * 
		 * @param userId
		 *            Идентификатор пользователя
		 */
		public BooksWithAuthorsSerializer(int userId) {
			super();
			this.userId = userId;
		}

		@Override
		public JsonElement serialize(Book src, Type typeOfSrc,
				JsonSerializationContext context) {
			JsonObject res = src.json(userId);
			JsonArray authorsArray = new JsonArray();
			for (Author author : src.authors) {
				authorsArray.add(author.json());
			}
			res.add("authors", authorsArray);
			return res;
		}

	}

}
