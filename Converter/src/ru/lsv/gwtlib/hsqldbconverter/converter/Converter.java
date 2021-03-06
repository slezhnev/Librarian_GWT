/**
 * Конвертер данных из десктопного Librarian'а в GWT версию (из HSQLDB в PostreSQL)
 */
package ru.lsv.gwtlib.hsqldbconverter.converter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;

import ru.lsv.gwtlib.hsqldbconverter.data.HAuthor;
import ru.lsv.gwtlib.hsqldbconverter.data.HBook;
import ru.lsv.gwtlib.hsqldbconverter.data.HFileEntity;
import ru.lsv.gwtlib.server.data.Author;
import ru.lsv.gwtlib.server.data.Book;
import ru.lsv.gwtlib.server.data.CommonUtils;
import ru.lsv.gwtlib.server.data.FileEntity;
import ru.lsv.gwtlib.server.data.LibUser;
import ru.lsv.gwtlib.server.data.Library;

/**
 * Конвертер данных из десктопного Librarian'а в GWT версию (из HSQLDB в
 * PostreSQL)
 * 
 * @author s.lezhnev
 * 
 */
public class Converter {

	/**
	 * Сессия работы с postresql
	 */
	private static Session pSession;
	/**
	 * Библиотека, которая конвертируется
	 */
	private static Library library;
	/**
	 * Юзер, для которого будет выставляться отметка "прочитано"
	 */
	private static LibUser user;

	/**
	 * Ищет указанного автора в базе. В случае отсутствия его там - добавляет
	 * 
	 * @param author
	 *            Автора, которого надо найти в базе
	 * @return Найденный в базе автор или сохраненный автор. null, если что-то
	 *         упало в процессе поиска/записи в базу
	 */
	public static ru.lsv.gwtlib.server.data.Author getAuthorFromDB(
			ru.lsv.gwtlib.hsqldbconverter.data.HAuthor author) {
		Transaction trx = null;
		Criteria crit = pSession
				.createCriteria(ru.lsv.gwtlib.server.data.Author.class);
		if (author.getFirstName() != null)
			crit.add(Restrictions.eq("firstName", author.getFirstName()));
		else
			crit.add(Restrictions.isNull("firstName"));
		if (author.getMiddleName() != null)
			crit.add(Restrictions.eq("middleName", author.getMiddleName()));
		else
			crit.add(Restrictions.isNull("middleName"));
		if (author.getLastName() != null)
			crit.add(Restrictions.eq("lastName", author.getLastName()));
		else
			crit.add(Restrictions.isNull("lastName"));
		ru.lsv.gwtlib.server.data.Author tmpAuthor = (ru.lsv.gwtlib.server.data.Author) crit
				.uniqueResult();
		if (tmpAuthor == null) {
			trx = pSession.beginTransaction();
			// Конфертируем...
			tmpAuthor = new ru.lsv.gwtlib.server.data.Author(
					author.getFirstName(), author.getMiddleName(),
					author.getLastName(),
					new ArrayList<ru.lsv.gwtlib.server.data.Book>(), library);
			// Сохраняем
			pSession.save(tmpAuthor);
			// pSession.flush();
			trx.commit();
			return tmpAuthor;
		} else
			return tmpAuthor;
	}

	/**
	 * Сохраняет книги в базе
	 * 
	 * @param books
	 *            Список книг для сохранения
	 */
	private static void storeBooks(ArrayList<Book> books) {
		System.out.println();
		System.out.println("---- Storing " + books.size() + " books");
		System.out.println();
		for (Book book : books) {
			pSession.save(book);
		}
		books.clear();
	}

	/**
	 * Runner
	 * 
	 * @param args
	 */
	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		// Путь до базы. Передавать откуда-то - лениво. Вещь сугубо одноразовая
		String dbPath = "J:/Torrents/Lib.Rus.Ec + MyHomeLib[FB2]/librarian.data/librusec.db";
		//
		Configuration conf = new Configuration()
				.configure("ru/lsv/gwtlib/resources/hibernate.cfg.xml");
		conf.setProperty("hibernate.connection.url",
				"jdbc:postgresql://192.168.100.100/librarian");
		pSession = conf.buildSessionFactory().openSession();
		conf = new Configuration()
				.configure("ru/lsv/gwtlib/hsqldbconverter/resources/library.cfg.xml");
		// Изменяем путь до базы
		conf.setProperty("hibernate.connection.url",
				"jdbc:hsqldb:file:%libdb%;shutdown=true;hsqldb.default_table_type=cached"
						.replace("%libdb%", dbPath));
		Session hSession = conf.buildSessionFactory().openSession();
		Transaction trans;
		// Чистим PostreSQL базу
		// System.out.println("Clearing PostreSQL db");
		// pSession.createSQLQuery("select setval(\'hibernate_sequence\', 1)")
		// .uniqueResult();
		// pSession.createSQLQuery("truncate book cascade").executeUpdate();
		// pSession.createSQLQuery("truncate author cascade").executeUpdate();
		// pSession.createSQLQuery("truncate book_authors cascade")
		// .executeUpdate();
		// pSession.createSQLQuery("truncate fileentity cascade").executeUpdate();
		// pSession.createSQLQuery("truncate library cascade").executeUpdate();
		// pSession.createSQLQuery("truncate libuser cascade").executeUpdate();
		// // Создаем библиотеку
		// library = new Library(
		// "lib.rus.ec",
		// "\\\\MEDIA\\torrents\\unsorted\\_Lib.rus.ec - Официальная\\lib.rus.ec\\",
		// 1,
		// "\\\\MEDIA\\torrents\\unsorted\\_Lib.rus.ec - Официальная\\librusec_local_fb2.inpx");
		// pSession.save(library);
		// user = new LibUser("lsv", CommonUtils.getMD5Password("aquagen3220"));
		// pSession.save(user);
		//
		// trans.commit();
		// Грузим юзера. В базе он должен быть один
		LibUser user = (LibUser) pSession.createQuery("from LibUser")
				.uniqueResult();
		// Считаем общее количество
		Integer total = (Integer) hSession.createSQLQuery(
				"select count(book_id) from Book where readed=true")
				.uniqueResult();
		// Поехали перекачивать!
		List<HBook> hBooks = hSession.createQuery(
				"from HBook where readed=true").list();
		int counter = 0;
		for (HBook hBook : hBooks) {
			// Ищем такую же книгу в PostreSQL
			Book book;
			try {
				book = (Book) pSession.createQuery("from Book where id=?")
						.setString(0, hBook.getId()).uniqueResult();
			} catch (Exception ex) {
				System.out.println("Fail load book");
				continue;
			}
			if (book != null) {
				System.out.print("Processed " + (counter++) + " of " + total
						+ " (" + book + ")");
				if (book.getReaded() == null) {
					List<LibUser> readed = new ArrayList<>();
					book.setReaded(readed);
				}
				if (book.getReaded().indexOf(user) == -1) {
					trans = pSession.beginTransaction();
					book.getReaded().add(user);
					pSession.update(book);
					pSession.flush();
					trans.commit();
					System.out.println(" added");
				} else {
					System.out.println(" skipped");
				}
			}
		}
		//
		hSession.close();
		// Do something
		pSession.close();
	}
}
