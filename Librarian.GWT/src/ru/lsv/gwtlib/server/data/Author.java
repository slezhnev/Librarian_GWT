package ru.lsv.gwtlib.server.data;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * Автор книги User: lsv Date: 20.10.2010 Time: 14:52:06
 */
public class Author {

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
	 * Книги выбранного автора
	 */
	private List<Book> books;
	/**
	 * Библиотека
	 */
	private Library library;

	public Author() {
		books = new ArrayList<Book>();
	}

	public Author(String firstName, String middleName, String lastName,
			List<Book> books, Library library) {
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.books = books;
		this.setLibrary(library);
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

	@Override
	public boolean equals(Object some) {
		if (some == null)
			return false;
		if (this == some)
			return true;
		if (!(some instanceof Author))
			return false;
		Author author = (Author) some;
		return Utils.areEqual(author.firstName, author.firstName)
				&& Utils.areEqual(author.middleName, author.middleName)
				&& Utils.areEqual(author.lastName, author.lastName);
	}

	@Override
	public int hashCode() {
		return Utils.getHash(firstName) + Utils.getHash(middleName)
				+ Utils.getHash(lastName);

	}

	public List<Book> getBooks() {
		return books;
	}

	public void setBooks(List<Book> books) {
		this.books = books;
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
		str.append(" ");
		if (firstName != null)
			str.append(firstName);
		str.append(" ");
		if (middleName != null)
			str.append(middleName);
		return str.toString().trim();
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
	 * Gson сериализатор для текущего объекта
	 * 
	 * @return Сериализированный объект
	 */
	public JsonObject json() {
		JsonObject res = new JsonObject();
		res.addProperty("authorId", authorId);
		res.addProperty("firstName", firstName);
		res.addProperty("middleName", middleName);
		res.addProperty("lastName", lastName);
		res.add("library", library.json());
		return res;
	}

	/**
	 * Gson сериализатор автора с книгами
	 * 
	 * @author s.lezhnev
	 * 
	 */
	public static class AuthorWithBooksSerializer implements
			JsonSerializer<Author> {

		/**
		 * Идентификатор пользователя
		 */
		private int userId;

		/**
		 * Default constructor
		 * 
		 * @param userId
		 *            Идентификатор пользователя
		 */
		public AuthorWithBooksSerializer(int userId) {
			this.userId = userId;
		}

		@Override
		public JsonElement serialize(Author src, Type typeOfSrc,
				JsonSerializationContext context) {
			JsonObject res = src.json();
			JsonArray booksArray = new JsonArray();
			for (Book book : src.books) {
				booksArray.add(book.json(userId));
			}
			res.add("books", booksArray);
			return res;
		}

	}

	/**
	 * Gson сериализатор автора БЕЗ книг
	 * 
	 * @author s.lezhnev
	 * 
	 */
	public static class AuthorWithoutBooksSerializer implements
			JsonSerializer<Author> {

		@Override
		public JsonElement serialize(Author src, Type typeOfSrc,
				JsonSerializationContext context) {
			return src.json();
		}

	}

}
