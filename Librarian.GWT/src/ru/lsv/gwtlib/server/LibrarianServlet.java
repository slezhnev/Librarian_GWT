/**
 * Основной сервлет. <br/>
 * Формирует JSON описания заказанных объектов
 */
package ru.lsv.gwtlib.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import ru.lsv.gwtlib.server.data.Author;
import ru.lsv.gwtlib.server.data.Book;
import ru.lsv.gwtlib.server.data.CommonUtils;
import ru.lsv.gwtlib.server.data.LibUser;
import ru.lsv.gwtlib.server.data.Utils;
import ru.lsv.gwtlib.server.library.LibrarySheduler;
import ru.lsv.gwtlib.server.library.LoadStatus;
import ru.lsv.gwtlib.shared.RequestType;

import com.google.gson.GsonBuilder;
import com.google.gwt.http.client.Response;

/**
 * Основной сервлет. <br/>
 * Формирует JSON описания заказанных объектов
 * 
 * @author s.lezhnev
 */
@SuppressWarnings({ "serial", "unchecked" })
public class LibrarianServlet extends HttpServlet {

	/**
	 * Hibernate session factory
	 */
	protected SessionFactory libFactory = null;

	/**
	 * Текущая сессия <br/>
	 * Открывается и закрывается в doGet
	 */
	protected Session sess;

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		Configuration conf = new Configuration()
				.configure("ru/lsv/gwtlib/resources/hibernate.cfg.xml");
		conf.setProperty("hibernate.connection.url",
				config.getInitParameter("storagePath"));
		try {
			libFactory = conf.buildSessionFactory();
		} catch (HibernateException ex) {
			// Чота тут не построилось
			libFactory = null;
		}
		// Один раз создадим сессию - чтобы оно быстрее оживало при начале
		// работы
		sess = libFactory.openSession();
		sess.close();
		// Запускаем шедулер работы с библиотеками
		LibrarySheduler.getScheduler(libFactory);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		HttpSession sess = req.getSession(true);
		// Попытаемся получить юзера из сессии
		LibUser currentUser = (LibUser) sess.getAttribute("user");
		// Первое - если нет сессии - шлем ошибку
		if (currentUser == null) {
			// Нету авторизации. Проверим - а может это как раз запрос
			// на авторизацию?
			if ((req.getParameter("user") != null)
					&& (req.getParameter("psw") != null)
					&& (req.getParameter("staylogged") != null)) {
				// Действительно запрос на авторизацию. Ну поехали
				// авторизоваться...
				LibUser currUser = validateLogin(req.getParameter("user"),
						req.getParameter("psw"));
				if (currUser != null) {
					sess.setAttribute("user", currUser);
					resp.setCharacterEncoding("UTF-8");
					resp.setContentType("application/json");
					try (PrintStream output = new PrintStream(
							resp.getOutputStream(), true, "UTF-8")) {
						output.println("true");
						return;
					}
				} else {
					// Юзер попался невалидный
					resp.sendError(401, "Authentication failed");
					return;
				}
			} else {
				resp.sendError(401, "Authorization required");
				return;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		sess = libFactory.openSession();
		try {
			req.setCharacterEncoding("UTF-8");
			HttpSession httpSess = req.getSession(true);
			// Попытаемся получить юзера из сессии
			LibUser currentUser = (LibUser) httpSess.getAttribute("user");
			if (currentUser == null) {
				// Выкинем 401 - пусть разбирается...
				resp.sendError(Response.SC_UNAUTHORIZED);
				return;
			}
			resp.setCharacterEncoding("UTF-8");
			resp.setContentType("application/json");
			// Заглушка пока
			int userId = currentUser.getId();

			String reqWhat = req.getParameter("req");
			if ("downloadbook".equals(reqWhat)) {
				// Выгрузка книги будет идтить несколько по другому...
				// Обновление книги
				int bookId;
				try {
					bookId = Integer.valueOf(req.getParameter("bookid"));
				} catch (NumberFormatException ex) {
					resp.sendError(400, "Non valid \"bookid\" parameter");
					return;
				}
				int downloadType;
				try {
					downloadType = Integer.valueOf(req.getParameter("type"));
				} catch (NumberFormatException ex) {
					downloadType = 0;
				}
				// Выгружаем нахфиг книгу
				downloadBook(bookId, downloadType, resp);
			} else {
				resp.setCharacterEncoding("UTF-8");
				resp.setContentType("application/json");
				try (PrintStream output = new PrintStream(
						resp.getOutputStream(), true, "UTF-8")) {
					// Создаем билдер
					GsonBuilder gsonBuilder = new GsonBuilder();
					// Enable for testing!
					// gsonBuilder.setPrettyPrinting();
					if ("touch".equals(reqWhat)) {
						// Не делаем ничего. Это запрос для проверки живости и
						// валидности авторизации
					} else if ("loadstatus".equals(reqWhat)) {
						// Выдаем статус
						formLoadStatus(output, gsonBuilder);
					} else if ("forcecheck".equals(reqWhat)) {
						// А тут мы должны НАСИЛЬНО активировать проверку
						// наличия новых файлов в библиотеке
						// Запускаем проверку в отдельном потоке
						new Thread(new Runnable() {

							@Override
							public void run() {
								LibrarySheduler.service();
							}

						}).start();
					} else if ("logoff".equals(reqWhat)) {
						httpSess.removeAttribute("user");
						return;
					} else if ("authors".equals(reqWhat)
							|| "authorslist".equals(reqWhat)) {
						// Запрос ПАЧКИ авторов
						formAuthors(req, resp, userId, reqWhat, output,
								gsonBuilder);
					} else if ("books".equals(reqWhat)
							|| "bookslist".equals(reqWhat)) {
						// Запрос пачки книг
						formBooks(req, resp, userId, reqWhat, output,
								gsonBuilder);
					} else if ("series".equals(reqWhat)) {
						// Запрос серий
						// Название
						String name = req.getParameter("name");
						if (name == null) {
							resp.sendError(400, "Non valid \"name\" parameter");
							return;
						}
						// Тип запроса
						RequestType reqType = RequestType.fromString(req
								.getParameter("type"));
						if (reqType == null) {
							resp.sendError(400, "Non valid \"type\" parameter");
							return;
						}
						List<String> series = getSeries(reqType, name, userId);
						output.print(gsonBuilder.create().toJson(series));
					} else if ("author".equals(reqWhat)) {
						// Запрос одного автора
						try {
							int authorId = Integer.valueOf(req
									.getParameter("authorid"));
							gsonBuilder
									.registerTypeAdapter(
											Author.class,
											new Author.AuthorWithBooksSerializer(
													userId));
							output.print(gsonBuilder.create().toJson(
									getAuthor(authorId)));
						} catch (NumberFormatException ex) {
							resp.sendError(400,
									"Non valid \"authorid\" parameter");
							return;
						}
					} else if ("book".equals(reqWhat)) {
						// Запрос одной книги
						try {
							int bookId = Integer.valueOf(req
									.getParameter("bookid"));
							gsonBuilder
									.registerTypeAdapter(
											Book.class,
											new Book.BooksWithAuthorsSerializer(
													userId));
							output.print(gsonBuilder.create().toJson(
									getBook(bookId)));
						} catch (NumberFormatException ex) {
							resp.sendError(400,
									"Non valid \"bookid\" parameter");
							return;
						}
					} else if ("updatebook".equals(reqWhat)) {
						// Обновление книги
						int bookId;
						try {
							bookId = Integer
									.valueOf(req.getParameter("bookid"));
						} catch (NumberFormatException ex) {
							resp.sendError(400,
									"Non valid \"bookid\" parameter");
							return;
						}
						boolean readed;
						try {
							readed = Boolean
									.valueOf(req.getParameter("readed"));
						} catch (NumberFormatException ex) {
							resp.sendError(400,
									"Non valid \"readed\" parameter");
							return;
						}
						boolean mustRead;
						try {
							mustRead = Boolean.valueOf(req
									.getParameter("mustread"));
						} catch (NumberFormatException ex) {
							resp.sendError(400,
									"Non valid \"mustread\" parameter");
							return;
						}
						output.print(""
								+ updateBook(bookId, readed, mustRead, userId));
					} else {
						resp.sendError(400, "Non valid \"req\" parameter");
						return;
					}
				}
			}
		} finally {
			sess.close();
		}
	}

	/**
	 * Обработка запроса списка книг
	 * 
	 * @param req
	 *            Request
	 * @param resp
	 *            Response
	 * @param userId
	 *            Идентификатор пользователя
	 * @param reqWhat
	 *            Тип запроса
	 * @param output
	 *            Куда выдавать результат
	 * @param gsonBuilder
	 *            GsonBuilder
	 * @throws IOException
	 *             В случае проблем ввода/вывода
	 */
	private void formBooks(HttpServletRequest req, HttpServletResponse resp,
			int userId, String reqWhat, PrintStream output,
			GsonBuilder gsonBuilder) throws IOException {
		// Название
		String name = req.getParameter("name");
		// Тип запроса
		RequestType reqType = RequestType.fromString(req.getParameter("type"));
		if ("bookslist".equals(reqWhat)) {
			if (name == null) {
				resp.sendError(400, "Non valid \"name\" parameter");
				return;
			}
			if (reqType == null) {
				resp.sendError(400, "Non valid \"type\" parameter");
				return;
			}
			BooksWrapper books = getBooks(reqType, name, userId, true);
			output.print(gsonBuilder.create().toJson(books.getListBooks()));
		} else {
			// А тут возможны варианты
			// Может быть мы запрашивали книги по сериям и
			// авторам
			String sAuthorId = req.getParameter("authorid");
			String serie = req.getParameter("serie");
			String bookId = req.getParameter("bookid");
			if (reqType != null) {
				if (name == null) {
					resp.sendError(400, "Non valid \"name\" parameter");
					return;
				}
				// Запрос списка книг по фильтру
				BooksWrapper books = getBooks(reqType, name, userId, false);
				gsonBuilder.registerTypeAdapter(Book.class,
						new Book.BooksWithAuthorsSerializer(userId));
				output.print(gsonBuilder.create().toJson(books.getBooks()));
			} else {
				Set<Book> res;
				if (sAuthorId != null) {
					// Запрос по автору
					try {
						int authorId = Integer.parseInt(sAuthorId);
						res = getBooksByAuthor(authorId);
					} catch (NumberFormatException ex) {
						resp.sendError(400, "Non valid \"authorid\" parameter");
						return;
					}
				} else if (serie != null) {
					// Запрос по серии
					res = getBooksBySerie(serie);
				} else if (bookId != null) {
					// Запрос по ОДНОЙ книге
					res = new HashSet<Book>();
					try {
						Book book = getBook(Integer.valueOf(bookId));
						if (book != null) {
							res.add(book);
						}
					} catch (NumberFormatException ex) {
						resp.sendError(400, "Non valid \"bookid\" parameter");
						return;
					}
				} else {
					resp.sendError(
							400,
							"Non valid request. Must be \"type\" or \"authorid\" or \"serie\" or \"bookid\" parameter");
					return;
				}
				gsonBuilder.registerTypeAdapter(Book.class,
						new Book.BooksWithAuthorsSerializer(userId));
				output.print(gsonBuilder.create().toJson(res));
			}
		}
	}

	/**
	 * Обработка запроса авторов
	 * 
	 * @param req
	 *            Request
	 * @param resp
	 *            Response
	 * @param userId
	 *            Идентификатор юзера
	 * @param reqWhat
	 *            Тип запроса
	 * @param output
	 *            Куда выдавать результат
	 * @param gsonBuilder
	 *            GSONBuilder
	 * @throws IOException
	 *             В случае проблем с вводом/выводом
	 */
	private void formAuthors(HttpServletRequest req, HttpServletResponse resp,
			int userId, String reqWhat, PrintStream output,
			GsonBuilder gsonBuilder) throws IOException {
		// ФИО автора
		String name = req.getParameter("name");
		if (name == null) {
			resp.sendError(400, "Non valid \"name\" parameter");
			return;
		}
		// Тип запроса
		RequestType reqType = RequestType.fromString(req.getParameter("type"));
		if (reqType == null) {
			resp.sendError(400, "Non valid \"type\" parameter");
			return;
		}
		List<Author> authors = getAuthors(reqType, name, userId);
		if ("authorslist".equals(reqWhat)) {
			gsonBuilder.registerTypeAdapter(Author.class,
					new Author.AuthorWithoutBooksSerializer());
		} else {
			gsonBuilder.registerTypeAdapter(Author.class,
					new Author.AuthorWithBooksSerializer(userId));
		}
		output.print(gsonBuilder.create().toJson(authors));
	}

	/**
	 * Формируем описание статуса загрузки книг в библиотеку
	 * 
	 * @param output
	 *            Куда выдавать результат
	 * @param gsonBuilder
	 *            GsonBuilder
	 */
	private void formLoadStatus(PrintStream output, GsonBuilder gsonBuilder) {
		// Получаем клон состояния загрузки (чтобы не ловить изменения)
		LoadStatus status = LoadStatus.getInstance().clone();
		// ДЛЯ ОТЛАДКИ!
		/*
		 * Random rnd = new Random(); int arc = rnd.nextInt(); int file =
		 * rnd.nextInt(); status.setCurrentArcName("arcName-" + arc + ".zip");
		 * status.setCurrentArcsToProcess(arc - 1);
		 * status.setCurrentFileToProcess(file - 1);
		 * status.setTotalArcsToProcess(arc);
		 * status.setTotalFilesToProcess(file);
		 */
		// Выдаем
		output.print(gsonBuilder.create().toJson(status));
	}

	/**
	 * Выгружает в resp указанную книгу
	 * 
	 * @param bookId
	 *            Идентификатор книги
	 * @param downloadType
	 *            Тип загрузки. 0 - fb2.zip, 1 - fb2
	 * @param resp
	 *            Responce, куда выполнять выгрузку
	 * @throws IOException
	 *             В случае проблем ввода/вывода (генерирует resp.sendError и
	 *             т.п.
	 */
	private void downloadBook(int bookId, int downloadType,
			HttpServletResponse resp) throws IOException {
		Book book = getBook(bookId);
		if (book == null) {
			resp.sendError(404, "Cannot find book with bookId");
			return;
		}
		File arcFile = new File(book.getLibrary().getStoragePath()
				+ File.separator + book.getZipFileName());
		if (!arcFile.exists()) {
			resp.sendError(404, "Cannot find zip file for book");
			return;
		}
		String outputFileName = Utils.cleanFileName(book.toString().trim());
		switch (downloadType) {
		case 1: {
			outputFileName = outputFileName + ".fb2";
			break;
		}
		default: {
			outputFileName = outputFileName + ".fb2.zip";
		}
		}
		try {
			URI uri = new URI(null, null, outputFileName, null);
			String disposition = "attachment; filename*=UTF-8''"
					+ uri.toASCIIString();
			resp.setHeader("Content-disposition", disposition);
		} catch (URISyntaxException e1) {
			// Do nothing. Ибо чо тут сделаешь-то?
		}
		try {
			resp.setContentType("application/octet-stream");
			switch (downloadType) {
			case 1: {
				try (OutputStream out = resp.getOutputStream()) {
					doStoreBook(book, arcFile, out);
				}
				break;
			}
			default: {
				try (ZipArchiveOutputStream out = new ZipArchiveOutputStream(
						resp.getOutputStream())) {
					doStoreBook(book, arcFile, out);
				}
			}
			}
		} catch (Throwable e) {
			resp.sendError(502, "Got an exception \"" + e.getClass().getName()
					+ "\" with message \"" + e.getMessage() + "\"");
		}
	}

	/**
	 * Выкачивает файл из архива и выдает его в выходной поток
	 * 
	 * @param book
	 *            Книга
	 * @param arcFile
	 *            Архив
	 * @param out
	 *            Выходной поток (если он ZipArchiveOutputStream, то
	 *            автоматически добавятся ZipArchiveEntry с ее последующим
	 *            заркытием)
	 * @throws IOException
	 *             В случае ошибок ввода/вывода
	 * @throws ZipException
	 *             В случае ошибок работы с zip-потоком
	 */
	private void doStoreBook(Book book, File arcFile, OutputStream out)
			throws IOException, ZipException {
		try (ZipFile zip = new ZipFile(arcFile)) {
			for (Enumeration<ZipArchiveEntry> e = zip.getEntries(); e
					.hasMoreElements();) {
				ZipArchiveEntry ze = (ZipArchiveEntry) e.nextElement();
				String name = ze.getName();
				String id = name.substring(0, name.indexOf("."));
				if (id.equals(book.getId())) {
					// Во - нашли книгу. Поехали ее доставать куда-нито
					try (InputStream in = zip.getInputStream(ze)) {
						if (out instanceof ZipArchiveOutputStream) {
							((ZipArchiveOutputStream) out).setLevel(9);
							((ZipArchiveOutputStream) out)
									.putArchiveEntry(new ZipArchiveEntry(book
											.getId() + ".fb2"));
						}
						byte[] buffer = new byte[100000];
						while (true) {
							int amountRead = in.read(buffer);
							if (amountRead == -1) {
								break;
							}
							out.write(buffer, 0, amountRead);
						}
						if (out instanceof ZipArchiveOutputStream) {
							((ZipArchiveOutputStream) out).closeArchiveEntry();
						}
						break;
					}
				}
			}
		}
	}

	/**
	 * Проверка валидности логина
	 * 
	 * @param userName
	 *            Имя пользователя
	 * @param plainPassword
	 *            Пароль в незакодированном виде
	 * @return true, если такой пользователь существует; false - иначе
	 */
	protected LibUser validateLogin(String userName, String plainPassword) {
		Session sess = libFactory.openSession();
		try {
			return (LibUser) sess
					.createCriteria(LibUser.class)
					.add(Restrictions.and(
							Restrictions.eq("name", userName),
							Restrictions.eq("password",
									CommonUtils.getMD5Password(plainPassword))))
					.uniqueResult();
		} finally {
			sess.close();
		}
	}

	/**
	 * Получение списка авторов
	 * 
	 * @param reqType
	 *            Тип запроса
	 * @param authorName
	 *            ФИО автора
	 * @param userId
	 *            ID юзера запроса
	 * @return Список авторов
	 */
	public List<Author> getAuthors(RequestType reqType, String authorName,
			int userId) {
		List<Author> authors = null;
		switch (reqType) {
		case ALL: {
			authors = sess.createCriteria(Author.class)
					.add(Restrictions.like("lastName", authorName + "%"))
					.addOrder(Order.asc("lastName"))
					.addOrder(Order.asc("firstName"))
					.addOrder(Order.asc("middleName")).list();
			break;
		}
		case ONLY_FOR_READING: {
			DetachedCriteria forReading = DetachedCriteria.forClass(Book.class)
					.setProjection(Property.forName("bookId"))
					.createCriteria("mustRead")
					.add(Restrictions.eq("id", userId));
			authors = sess.createCriteria(Author.class)
					.add(Restrictions.like("lastName", authorName + "%"))
					.addOrder(Order.asc("lastName"))
					.addOrder(Order.asc("firstName"))
					.addOrder(Order.asc("middleName"))
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.createCriteria("books")
					.add(Property.forName("bookId").in(forReading)).list();
			break;
		}
		case ONLY_NOT_READED: {
			// Я хз как это будет работать! :))))
			DetachedCriteria readed = DetachedCriteria.forClass(Book.class)
					.setProjection(Property.forName("bookId"))
					.createCriteria("readed")
					.add(Restrictions.eq("id", userId));
			DetachedCriteria notReaded = DetachedCriteria
					.forClass(Author.class)
					.setProjection(Property.forName("authorId"))
					.createCriteria("books")
					.add(Property.forName("bookId").notIn(readed));
			authors = sess.createCriteria(Author.class)
					.add(Restrictions.like("lastName", authorName + "%"))
					.add(Property.forName("authorId").in(notReaded))
					.addOrder(Order.asc("lastName"))
					.addOrder(Order.asc("firstName"))
					.addOrder(Order.asc("middleName"))
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.createCriteria("books")
					.add(Property.forName("bookId").in(readed)).list();
			break;
		}
		case ONLY_READED: {
			DetachedCriteria readed = DetachedCriteria.forClass(Book.class)
					.setProjection(Property.forName("bookId"))
					.createCriteria("readed")
					.add(Restrictions.eq("id", userId));
			authors = sess.createCriteria(Author.class)
					.add(Restrictions.like("lastName", authorName + "%"))
					.addOrder(Order.asc("lastName"))
					.addOrder(Order.asc("firstName"))
					.addOrder(Order.asc("middleName"))
					.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY)
					.createCriteria("books")
					.add(Property.forName("bookId").in(readed)).list();
			break;
		}
		default:
		}
		return authors;
	}

	/**
	 * Получение списка серий
	 * 
	 * @param reqType
	 *            Тип запроса
	 * @param serieName
	 *            Имя серии
	 * @param userId
	 *            ID юзера
	 * @return Список серий
	 */
	public List<String> getSeries(RequestType reqType, String serieName,
			int userId) {
		Criteria crit = null;
		switch (reqType) {
		case ALL: {
			crit = sess.createCriteria(Book.class)
					.add(Restrictions.like("serieName", serieName + "%"))
					.addOrder(Order.asc("serieName"));
			break;
		}
		case ONLY_FOR_READING: {
			DetachedCriteria forReading = DetachedCriteria.forClass(Book.class)
					.setProjection(Property.forName("bookId"))
					.createCriteria("mustRead")
					.add(Restrictions.eq("id", userId));
			crit = sess.createCriteria(Book.class)
					.add(Restrictions.like("serieName", serieName + "%"))
					.addOrder(Order.asc("serieName"))
					.add(Property.forName("bookId").in(forReading));
			break;
		}
		case ONLY_NOT_READED: {
			DetachedCriteria readed = DetachedCriteria.forClass(Book.class)
					.setProjection(Property.forName("bookId"))
					.createCriteria("readed")
					.add(Restrictions.eq("id", userId));
			DetachedCriteria readedSeries = DetachedCriteria
					.forClass(Book.class)
					.setProjection(Property.forName("serieName"))
					.createCriteria("readed")
					.add(Restrictions.eq("id", userId));
			crit = sess
					.createCriteria(Book.class)
					.add(Restrictions.like("serieName", serieName + "%"))
					.addOrder(Order.asc("serieName"))
					.add(Restrictions.and(
							Property.forName("serieName").in(readedSeries),
							Property.forName("bookId").notIn(readed)));
			break;
		}
		case ONLY_READED: {
			DetachedCriteria readed = DetachedCriteria.forClass(Book.class)
					.setProjection(Property.forName("bookId"))
					.createCriteria("readed")
					.add(Restrictions.eq("id", userId));
			crit = sess.createCriteria(Book.class)
					.add(Restrictions.like("serieName", serieName + "%"))
					.addOrder(Order.asc("serieName"))
					.add(Property.forName("bookId").in(readed));
			break;
		}
		default:
		}
		crit.add(Restrictions.isNotNull("serieName"))
				.add(Restrictions.ne("serieName", ""))
				.setProjection(
						Projections.distinct(Projections.property("serieName")));
		// .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		// List<Object[]> tmpSeries = crit.list();
		List<String> series = crit.list();
		/*
		 * for (Object[] tmpSerie : tmpSeries) { series.add((String)
		 * tmpSerie[0]); }
		 */
		return series;
	}

	/**
	 * Представление книги в списке - СОКРАЩЕННЫЙ вариант Book
	 * 
	 * @author s.lezhnev
	 */
	public class BookInList {
		/**
		 * Идентификатор книги
		 */
		Integer bookId;
		/**
		 * Название
		 */
		String title;

		/**
		 * Default constructor
		 * 
		 * @param bookId
		 *            Идентификатор книги
		 * @param title
		 *            Назвение
		 */
		public BookInList(Integer bookId, String title) {
			super();
			this.bookId = bookId;
			this.title = title;
		}

		/**
		 * @return the bookId
		 */
		public Integer getBookId() {
			return bookId;
		}

		/**
		 * @return the title
		 */
		public String getTitle() {
			return title;
		}
	}

	/**
	 * Класс-враппер результатов запроса книг (поскольку там может приехать как
	 * List<Book>, так и List<BookInList>. А Делать ДВА метода - это охерительно
	 * некрасиво <br/>
	 * 
	 * @author s.lezhnev
	 */
	public class BooksWrapper {

		/**
		 * Список книг
		 */
		private List<Book> books;
		/**
		 * Список упиленный книг
		 */
		private List<BookInList> listBooks;

		/**
		 * Default constructor
		 * 
		 * @param books
		 *            Список книг
		 * @param listBooks
		 *            Список упиленных книг
		 */
		public BooksWrapper(List<Book> books, List<BookInList> listBooks) {
			super();
			this.books = books;
			this.listBooks = listBooks;
		}

		/**
		 * @return the listBooks
		 */
		public List<BookInList> getListBooks() {
			return listBooks;
		}

		/**
		 * @param listBooks
		 *            the listBooks to set
		 */
		public void setListBooks(List<BookInList> listBooks) {
			this.listBooks = listBooks;
		}

		/**
		 * @return the books
		 */
		public List<Book> getBooks() {
			return books;
		}

	}

	/**
	 * Получение списка книг <br/>
	 * ВНИМАНИЕ! <br/>
	 * В связи с универсальностью - НАРКОМАНИЯ УНУТРЕ! :)
	 * 
	 * @param reqType
	 *            Тип запроса
	 * @param bookName
	 *            Название книги
	 * @param userId
	 *            ID юзера
	 * @param shortVariant
	 *            true - приедет List<BookInList>, false - приедет Set<Book>
	 * @return Список книг
	 */
	public BooksWrapper getBooks(RequestType reqType, String bookName,
			int userId, boolean shortVariant) {
		List<BookInList> shortBooks = null;
		List<Book> books = null;
		Criteria crit = null;
		switch (reqType) {
		case ALL: {
			crit = sess.createCriteria(Book.class).addOrder(Order.asc("title"))
					.add(Restrictions.like("title", bookName + "%"));
			break;
		}
		case ONLY_FOR_READING: {
			DetachedCriteria forReading = DetachedCriteria.forClass(Book.class)
					.setProjection(Property.forName("bookId"))
					.createCriteria("mustRead")
					.add(Restrictions.eq("id", userId));
			crit = sess.createCriteria(Book.class)
					.add(Restrictions.like("title", bookName + "%"))
					.addOrder(Order.asc("title"))
					.add(Property.forName("bookId").in(forReading));
			break;
		}
		case ONLY_NOT_READED: {
			// А тут это смысла не имеет вааще! Возвращаем пустой список
			crit = null;
			break;
		}
		case ONLY_READED: {
			DetachedCriteria readed = DetachedCriteria.forClass(Book.class)
					.setProjection(Property.forName("bookId"))
					.createCriteria("readed")
					.add(Restrictions.eq("id", userId));
			crit = sess.createCriteria(Book.class)
					.add(Restrictions.like("title", bookName + "%"))
					.addOrder(Order.asc("title"))
					.add(Property.forName("bookId").in(readed));
			break;
		}
		default:
		}
		if (shortVariant) {
			if (crit != null) {
				crit.setProjection(Projections.projectionList()
						.add(Projections.property("bookId"))
						.add(Projections.property("title")));
				List<Object[]> res = crit.list();
				shortBooks = new ArrayList<>();
				for (int i = 0; i < res.size(); i++) {
					Object[] book = res.get(i);
					shortBooks.add(new BookInList((Integer) book[0],
							(String) book[1]));
				}
			} else {
				shortBooks = new ArrayList<>();
			}
		} else {
			if (crit != null) {
				books = crit.list();
			} else {
				books = new ArrayList<>();
			}
		}
		return new BooksWrapper(books, shortBooks);
	}

	/**
	 * Получние автора по id
	 * 
	 * @param authorId
	 *            id автора
	 * @return Автор или null - если такогового нихрена нету
	 */
	public Author getAuthor(int authorId) {
		// Загрузка сделана не через .load, поскольку оно там упорно догружать
		// lazy не хочет.
		// Комментарий для себя - догружать оно не хочет в связи с окончание
		// транзакции. Вариант был - открывать в doGet транзакцию и т.п. Но
		// зачем :)?
		return (Author) sess.createCriteria(Author.class)
				.add(Restrictions.eq("authorId", authorId)).uniqueResult();
	}

	/**
	 * Получение книги по идентификатору
	 * 
	 * @param bookId
	 *            id книги
	 * @return Книга или null - если таковой нету
	 */
	public Book getBook(int bookId) {
		// см. коммент в getAuthor. Сделано не через .load по той же причине
		return (Book) sess.createCriteria(Book.class)
				.add(Restrictions.eq("bookId", bookId)).uniqueResult();
	}

	/**
	 * Обновляет книгу, выставляя параметры readed и mustRead
	 * 
	 * @param bookId
	 *            id книги
	 * @param readed
	 *            Признак прочитанности
	 * @param mustRead
	 *            Признак "к прочтению"
	 * @param userId
	 *            Идентификатор пользователя
	 * @return true - если все ok, false - иначе
	 */
	public boolean updateBook(int bookId, boolean readed, boolean mustRead,
			int userId) {
		// Откроем транзакцию
		sess.beginTransaction();
		try {
			Book libraryBook = null;
			// Загружаем юзера
			LibUser user = (LibUser) sess.load(LibUser.class, userId);
			if (user == null) {
				// Нету такого пользователя
				return false;
			}
			// Загружаем книгу
			libraryBook = (Book) sess.load(Book.class, bookId);
			if (libraryBook == null) {
				// Нету такой книги
				return false;
			}
			if (readed) {
				if (libraryBook.getReaded() == null) {
					ArrayList<LibUser> userList = new ArrayList<>();
					libraryBook.setReaded(userList);
				}
				if (libraryBook.getReaded().indexOf(user) == -1) {
					libraryBook.getReaded().add(user);
				}
			} else {
				if (libraryBook.getReaded() != null) {
					// Значит тут может чота быть
					libraryBook.getReaded().remove(user);
				}
			}
			if (mustRead) {
				if (libraryBook.getMustRead() == null) {
					ArrayList<LibUser> userList = new ArrayList<>();
					libraryBook.setMustRead(userList);
				}
				if (libraryBook.getMustRead().indexOf(user) == -1) {
					libraryBook.getMustRead().add(user);
				}
			} else {
				if (libraryBook.getMustRead() != null) {
					libraryBook.getMustRead().remove(user);
				}
			}
			sess.update(libraryBook);
			return true;
		} finally {
			sess.getTransaction().commit();
			sess.flush();
		}
	}

	/**
	 * Компаратор для сортировки книг <br/>
	 * Выдрано из обычного Librarian
	 */
	private class BookComparator implements Comparator<Book> {
		@Override
		public int compare(Book o1, Book o2) {
			if ((o1.getSerieName() == null) && (o2.getSerieName() != null))
				// У первой нет серии, у второй - есть -> вторая должна
				// быть выше
				return 1;
			else if ((o2.getSerieName() == null) && (o1.getSerieName() != null))
				// У первой - есть серия, у второй - нет -> первая
				// должна быть выше
				return -1;
			else if ((o2.getSerieName() == null) && (o2.getSerieName() == null)) {
				// У обеих серий нет
				if ((o1.getTitle() != null))
					// Сраниваем по титлу первой
					if (o1.getTitle().equals(o2.getTitle())) {
						// Если заголовки совпадают - сравниваем по
						// идентификатору
						return (new Integer(o1.getId())).compareTo(new Integer(
								o2.getId()));
					} else
						return o1.getTitle().compareTo(o2.getTitle());
				else
					// У первой титлы нет - значит пусть она будет выше
					return -1;
			} else {
				// Тут у обеих есть серии
				if (o1.getSerieName().equals(o2.getSerieName())) {
					// Будем сравнивать по numInSerie
					if (o1.getNumInSerie() != null) {
						// У первой есть номер в серии - сравниваем с
						// ним
						if (o2.getNumInSerie() != null)
							if (o1.getNumInSerie().equals(o2.getNumInSerie())) {
								if (o1.getId().equals(o2.getId())) {
									return (new Integer(o1.getBookId())
											.compareTo(new Integer(o2
													.getBookId())));
								} else {
									return (new Integer(o1.getId()))
											.compareTo(new Integer(o2.getId()));
								}
							} else
								return o1.getNumInSerie().compareTo(
										o2.getNumInSerie());
						else
							// У первой - есть номер серии, у второй -
							// нету -> первая будет ниже
							return 1;
					} else
						// У первой номера серии нету - значит пусть она
						// будет выше
						return -1;
				} else
					return o1.getSerieName().compareTo(o2.getSerieName());
			}
		}
	}

	/**
	 * Получение списка книг по автору
	 * 
	 * @param authorId
	 *            Идентификатор автора
	 * @return Отсортированный список книг или null
	 */
	public Set<Book> getBooksByAuthor(int authorId) {
		Author author = null;
		author = (Author) sess.createCriteria(Author.class)
				.add(Restrictions.eq("authorId", authorId)).uniqueResult();
		if (author == null) {
			return null;
		}
		TreeSet<Book> sortedBooks = new TreeSet<Book>(new BookComparator());
		sortedBooks.addAll(author.getBooks());
		return sortedBooks;
	}

	/**
	 * Получение списка книг по серии
	 * 
	 * @param serieName
	 *            Название серии
	 * @return Отсортированный список книг или null
	 */
	public Set<Book> getBooksBySerie(String serieName) {
		List<Book> books = sess.createQuery("from Book where serieName=?")
				.setString(0, serieName).list();
		if (books == null) {
			return null;
		}
		TreeSet<Book> sortedBooks = new TreeSet<Book>(new BookComparator());
		sortedBooks.addAll(books);
		return sortedBooks;
	}

}
