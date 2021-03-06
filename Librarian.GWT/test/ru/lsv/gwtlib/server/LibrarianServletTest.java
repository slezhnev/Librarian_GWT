/**
 * 
 */
package ru.lsv.gwtlib.server;

import static org.junit.Assert.fail;

import java.util.Enumeration;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.junit.BeforeClass;
import org.junit.Test;

import ru.lsv.gwtlib.server.LibrarianServlet;
import ru.lsv.gwtlib.server.LibrarianServlet.BookInList;
import ru.lsv.gwtlib.server.LibrarianServlet.BooksWrapper;
import ru.lsv.gwtlib.server.data.Author;
import ru.lsv.gwtlib.server.data.Book;
import ru.lsv.gwtlib.shared.RequestType;

/**
 * @author admin
 * 
 */
public class LibrarianServletTest {

	private static LibrarianServlet impl;

	private static class MockServletConfig implements ServletConfig {

		@Override
		public String getInitParameter(String arg0) {
			return "jdbc:postgresql://192.168.100.100/librarian";
		}

		@SuppressWarnings("rawtypes")
		@Override
		public Enumeration getInitParameterNames() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public ServletContext getServletContext() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getServletName() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	@BeforeClass
	public static void beforeClass() throws ServletException {
		impl = new LibrarianServlet();
		impl.init(new MockServletConfig());
		impl.sess = impl.libFactory.openSession();
	}

	/**
	 * Test method for
	 * {@link ru.lsv.gwtlib.server.LibrarianServiceImpl#getAuthors(ru.lsv.gwtlib.shared.RequestType, java.lang.String)}
	 * .
	 * 
	 * @throws ServletException
	 */
	@Test
	public void testGetAuthors() throws ServletException {
		System.out
				.println("--------------------------------------------------------------");
		System.out.println("!!!!! Authors");
		List<Author> res;
//		res = impl.getAuthors(RequestType.ONLY_READED, "", 3);
//		System.out.println("------  Only readed!");
//		for (Author el : res) {
//			System.out.println(el);
//		}
//		res = impl.getAuthors(RequestType.ONLY_FOR_READING, "", 3);
//		System.out.println("------  Only for reading!");
//		for (Author el : res) {
//			System.out.println(el);
//		}
		res = impl.getAuthors(RequestType.ONLY_NOT_READED, "", 3);
		System.out.println("------  Only not readed!");
		for (Author el : res) {
			System.out.println(el);
		}
	}

	/**
	 * Test method for
	 * {@link ru.lsv.gwtlib.server.LibrarianServiceImpl#getSeries(ru.lsv.gwtlib.shared.RequestType, java.lang.String)}
	 * .
	 * 
	 * @throws ServletException
	 */
	@Test
	public void testGetSeries() throws ServletException {
		System.out
				.println("--------------------------------------------------------------");
		System.out.println("!!!!! Series");
		List<String> res;
//		res = impl.getSeries(RequestType.ONLY_READED, "", 3);
//		System.out.println("------  Only readed!");
//		for (String el : res) {
//			System.out.println(el);
//		}
//		res = impl.getSeries(RequestType.ONLY_FOR_READING, "", 3);
//		System.out.println("------  Only for reading!");
//		for (String el : res) {
//			System.out.println(el);
//		}
//		res = impl.getSeries(RequestType.ONLY_NOT_READED, "", 3);
//		System.out.println("------  Only not readed!");
//		for (String el : res) {
//			System.out.println(el);
//		}
//		res = impl.getSeries(RequestType.ALL, "", 3);
//		System.out.println("------  All!");
//		for (String el : res) {
//			System.out.println(el);
//		}
	}

	/**
	 * Test method for
	 * {@link ru.lsv.gwtlib.server.LibrarianServiceImpl#getBooks(ru.lsv.gwtlib.shared.RequestType, java.lang.String)}
	 * .
	 * 
	 * @throws ServletException
	 */
	@Test
	public void testGetBooks() throws ServletException {
//		System.out
//				.println("--------------------------------------------------------------");
//		System.out.println("!!!!! Books");
//		System.out.println("------  Only readed - full!");
//		BooksWrapper res = impl.getBooks(RequestType.ONLY_READED, "", 3, false);
//		for (Book el : res.getBooks()) {
//			System.out.println("" + el);
//		}
//		System.out.println("------  Only readed - short!");
//		res = impl.getBooks(RequestType.ONLY_READED, "", 3, true);
//		for (BookInList el : res.getListBooks()) {
//			System.out.println(el.getBookId() + " - " + el.getTitle());
//		}
//		System.out.println("------  Only for reading - full!");
//		res = impl.getBooks(RequestType.ONLY_FOR_READING, "", 3, false);
//		for (Book el : res.getBooks()) {
//			System.out.println("" + el);
//		}
//		System.out.println("------  Only for reading - short!");
//		res = impl.getBooks(RequestType.ONLY_FOR_READING, "", 3, true);
//		for (BookInList el : res.getListBooks()) {
//			System.out.println(el.getBookId() + " - " + el.getTitle());
//		}
//		System.out.println("------  Only not readed - full!");
//		res = impl.getBooks(RequestType.ONLY_NOT_READED, "", 3, false);
//		for (Book el : res.getBooks()) {
//			System.out.println("" + el);
//		}
//		System.out.println("------  Only not readed - short!");
//		res = impl.getBooks(RequestType.ONLY_NOT_READED, "", 3, true);
//		for (BookInList el : res.getListBooks()) {
//			System.out.println(el.getBookId() + " - " + el.getTitle());
//		}
//		Set<Book> set = impl.getBooksByAuthor(78);
//		System.out.println("------  By authors!");
//		for (Book el : set) {
//			System.out.println(el + "(" + el.getSerieName() + " - "
//					+ el.getNumInSerie() + ")");
//		}
//		set = impl.getBooksBySerie("Крысолов");
//		System.out.println("------  By serie!");
//		for (Book el : set) {
//			System.out.println(el + "(" + el.getSerieName() + " - "
//					+ el.getNumInSerie() + ")");
//		}
	}

	/**
	 * Test method for
	 * {@link ru.lsv.gwtlib.server.LibrarianServiceImpl#validateLogin}.
	 */
	@Test
	public void testValidateLogin() {
		System.out.println("------ VALIDATE LOGIN");
		System.out.println("" + impl.validateLogin("lsv", "aquagen3220"));
		System.out.println("" + impl.validateLogin("lsv", "aquagen32201"));
	}

	/**
	 * Test method for
	 * {@link ru.lsv.gwtlib.server.LibrarianServiceImpl#getAuthor(int)}.
	 */
	@Test
	public void testGetAuthor() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link ru.lsv.gwtlib.server.LibrarianServiceImpl#getBook(int)}.
	 */
	@Test
	public void testGetBook() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for
	 * {@link ru.lsv.gwtlib.server.LibrarianServiceImpl#updateBook(ru.lsv.gwtlib.shared.LibBook)}
	 * .
	 */
	@Test
	public void testUpdateBook() {
		fail("Not yet implemented");
	}

}
