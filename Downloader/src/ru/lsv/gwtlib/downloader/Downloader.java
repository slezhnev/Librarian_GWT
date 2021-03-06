package ru.lsv.gwtlib.downloader;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.Arrays;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.jgoodies.looks.LookUtils;
import com.jgoodies.looks.Options;

public class Downloader {

	private JFrame frame;
	private JTree booksTree;
	private DefaultHttpClient httpclient;
	private HttpContext httpContext;
	private ConnectToLibrary connectDialog;
	private JProgressBar progressBar;
	private JButton downloadBtn;
	private JComboBox<String> downloadType;
	/**
	 * Список книг к скачиванию
	 */
	private List<DownloaderBook> books = null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		configureUI();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Downloader window = new Downloader();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Конфигурирование UI
	 */
	private static void configureUI() {
		UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
		Options.setDefaultIconSize(new Dimension(18, 18));

		String lafName = LookUtils.IS_OS_WINDOWS_XP ? Options
				.getCrossPlatformLookAndFeelClassName() : Options
				.getSystemLookAndFeelClassName();

		try {
			UIManager.setLookAndFeel(lafName);
		} catch (Exception e) {
			System.err.println("Can't set look & feel:" + e);
		}
	}

	/**
	 * Create the application.
	 */
	public Downloader() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Librarian downloader");
		frame.setBounds(100, 100, 747, 684);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		frame.setLocationRelativeTo(null);

		booksTree = new JTree();
		booksTree.setRootVisible(false);
		frame.getContentPane().add(booksTree, BorderLayout.CENTER);

		JPanel downPanel = new JPanel();
		downPanel.setLayout(new BorderLayout());
		frame.getContentPane().add(downPanel, BorderLayout.SOUTH);

		String[] downloadTypeItems = { "fb2.zip", "fb2" };
		downloadType = new JComboBox<>(downloadTypeItems);
		downloadType.setSelectedIndex(0);
		downPanel.add(downloadType, BorderLayout.LINE_START);

		downloadBtn = new JButton("Загрузить книги");
		downloadBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doDownload();
			}
		});
		downloadBtn
				.setIcon(new ImageIcon(
						Downloader.class
								.getResource("/ru/lsv/gwtlib/downloader/resources/save_as_16_h.png")));
		downPanel.add(downloadBtn, BorderLayout.CENTER);

		DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
		DefaultMutableTreeNode loading = new DefaultMutableTreeNode(
				"Идет загрузка...");
		root.add(loading);
		((DefaultTreeModel) booksTree.getModel()).setRoot(root);
		((DefaultTreeModel) booksTree.getModel()).reload();
		booksTree.setSelectionPaths(null);

		progressBar = new JProgressBar();
		frame.getContentPane().add(progressBar, BorderLayout.NORTH);

		//
		httpclient = new DefaultHttpClient();
		// Инициализируем cookie store
		CookieStore cookieStore = new BasicCookieStore();
		httpContext = new BasicHttpContext();
		httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		//
		connectDialog = new ConnectToLibrary(frame, httpclient, httpContext);
		connectDialog.setLocationRelativeTo(frame);
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				downloadType.setSelectedIndex(connectDialog.getDownloadType());
				loadBooks();
			}

		});
	}

	/**
	 * Выполняет загрузку...
	 */
	protected void doDownload() {
		if (books == null) {
			// Nothing to export
			JOptionPane.showMessageDialog(frame, "Нет книг к экспорту",
					"Экспорт книг", JOptionPane.ERROR_MESSAGE);
			return;
		}
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Выберите место для сохранения");
		if (connectDialog.getSavePath() != null) {
			File saveTo = new File(connectDialog.getSavePath());
			if (saveTo.exists()) {
				chooser.setSelectedFile(saveTo);
			}
		}
		if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
			final String saveTo = chooser.getSelectedFile().getAbsolutePath();
			// Сохраняем путь...
			connectDialog.setSavePath(saveTo);
			connectDialog.setDownloadType(downloadType.getSelectedIndex());
			connectDialog.saveProperties();
			//
			downloadBtn.setEnabled(false);
			// Запускаем сохранение в отдельном потоке...
			new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								progressBar.setValue(0);
								progressBar.setMaximum(books.size());
							}
						});
					} catch (InterruptedException ignored) {
					} catch (InvocationTargetException ignored) {
					}
					for (final DownloaderBook book : books) {
						final StringBuffer pathToStore = new StringBuffer(
								saveTo
										+ File.separator
										+ cleanFileName(book
												.getAuthorsToString()));
						if (book.getSerieName() != null) {
							pathToStore.append(File.separator).append(
									cleanFileName(book.getSerieName().trim()));
						}
						File outFile = new File(pathToStore.toString());
						if ((!outFile.exists()) && (!outFile.mkdirs())) {
							showError("Ошибка создания пути экспорта \""
									+ pathToStore.toString()
									+ "\"! Экспорт остановлен", null);
							return;
						}
						// Выгружаем книгу
						StringBuilder strUrl = new StringBuilder(connectDialog
								.getUrl()).append("?req=downloadbook&bookid=")
								.append(book.getBookId()).append("&type=")
								.append(downloadType.getSelectedIndex());
						HttpGet httpGet = new HttpGet(strUrl.toString());
						final HttpResponse response;
						try {
							response = httpclient.execute(httpGet, httpContext);
						} catch (final Exception e) {
							showError(
									"Ошибка выгрузки книги с идентификатором  "
											+ book.getBookId() + ". Ошибка - "
											+ e.getClass().getName() + " ("
											+ e.getMessage() + ")"
											+ "! Экспорт остановлен", httpGet);
							return;
						}
						if (response.getStatusLine().getStatusCode() == 200) {
							HttpEntity entity = response.getEntity();
							if (entity == null) {
								showError(
										"Ошибка выгрузки книги с идентификатором  "
												+ book.getBookId()
												+ ". Empty entity! Экспорт остановлен",
										httpGet);
								return;
							}
							//
							try {
								if (response
										.containsHeader("Content-disposition")) {
									Header header = response
											.getFirstHeader("Content-disposition");
									String fileName = header.getValue();
									fileName = URLDecoder.decode(
											fileName.substring(fileName
													.indexOf("\'\'") + 2),
											"UTF-8");
									fileName = pathToStore.toString()
											+ File.separator + fileName;
									File fileTo = new File(fileName);
									if (fileTo.exists()) {
										// Удаляем
										fileTo.delete();
									}
									try (InputStream input = response
											.getEntity().getContent()) {
										try (FileOutputStream output = new FileOutputStream(
												fileName)) {
											byte[] buffer = new byte[65535];
											for (int length; (length = input
													.read(buffer)) > 0;) {
												output.write(buffer, 0, length);
											}
										}
									}
									// Тут все наконец-то сохранилось...
									httpGet.abort();
									// Поехали поставим/снимем отметку
									strUrl = new StringBuilder(connectDialog
											.getUrl())
											.append("?req=updatebook&bookid=")
											.append(book.getBookId())
											.append("&readed=true&mustread=false");
									httpGet = new HttpGet(strUrl.toString());
									HttpResponse response1;
									try {
										response1 = httpclient.execute(httpGet,
												httpContext);
									} catch (final Exception e) {
										showError(
												"Ошибка установки отметок книги с идентификатором  "
														+ book.getBookId()
														+ ". Ошибка - "
														+ e.getClass()
																.getName()
														+ " ("
														+ e.getMessage()
														+ ")"
														+ "! Экспорт остановлен",
												httpGet);
										return;
									}
									if (response1.getStatusLine()
											.getStatusCode() != 200) {
										showError(
												"Ошибка установки отметок книги с идентификатором  "
														+ book.getBookId()
														+ ". Status line - "
														+ response
																.getStatusLine()
														+ "! Экспорт остановлен",
												httpGet);
										return;
									}
									httpGet.abort();
								} else {
									showError(
											"Ошибка сохранения книги с идентификатором  "
													+ book.getBookId()
													+ ". Отсутствует \"Content-disposition\"! Экспорт остановлен",
											httpGet);
									return;
								}
							} catch (final Exception e) {
								showError(
										"Ошибка сохранения книги с идентификатором  "
												+ book.getBookId()
												+ ". Ошибка - "
												+ e.getClass().getName() + " ("
												+ e.getMessage() + ")"
												+ "! Экспорт остановлен",
										httpGet);
								return;
							}
						} else {
							showError(
									"Ошибка выгрузки книги с идентификатором  "
											+ book.getBookId()
											+ ". Status line - "
											+ response.getStatusLine()
											+ "! Экспорт остановлен", httpGet);
							return;
						}
						//
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								progressBar.setValue(progressBar.getValue() + 1);
							}
						});
					}
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showMessageDialog(frame,
										"Выгрузка завершена", "Экспорт книг",
										JOptionPane.INFORMATION_MESSAGE);
								// Валим совсем все...
								System.exit(0);
							}
						});
						return;
					} catch (InterruptedException ignored) {
					} catch (InvocationTargetException ignored) {
					}
				}

				/**
				 * Выдает сообщение об ошибке
				 * 
				 * @param errorMessage
				 *            Сообщение
				 * @param httpGet
				 *            httpGet, которому будет сделан abort
				 */
				private void showError(final String errorMessage,
						HttpGet httpGet) {
					try {
						SwingUtilities.invokeAndWait(new Runnable() {
							@Override
							public void run() {
								JOptionPane.showMessageDialog(frame,
										errorMessage, "Экспорт книг",
										JOptionPane.ERROR_MESSAGE);
							}
						});
					} catch (InterruptedException ignored) {
					} catch (InvocationTargetException ignored) {
					}
					downloadBtn.setEnabled(true);
					if (httpGet != null) {
						httpGet.abort();
					}
				}

			}).start();
		}
	}

	/**
	 * 
	 */
	private void loadBooks() {
		books = null;
		connectDialog.setVisible(true);
		if (!connectDialog.isConnected()) {
			System.exit(0);
			return;
		}
		// Поехали грузить!
		// Формируем строку запроса - в дальнейшем мы ее будем дополнять
		StringBuilder str = new StringBuilder(connectDialog.getUrl())
				.append("?type=forReading&name=&req=books");
		HttpGet httpGet = new HttpGet(str.toString());
		HttpResponse response;
		try {
			response = httpclient.execute(httpGet, httpContext);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame,
					"Ошибка загрузки списка книг к чтению - "
							+ e.getClass().getName() + ", \"" + e.getMessage()
							+ "\"", "Ошибка загрузки",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
			return;
		}
		if (response.getStatusLine().getStatusCode() == 200) {
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				JOptionPane.showMessageDialog(frame,
						"Ошибка загрузки списка книг к чтению - empty entity",
						"Ошибка загрузки", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
				return;
			}
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Gson gson = gsonBuilder.create();
			ContentType contentType = ContentType.getOrDefault(entity);
			Charset charset = contentType.getCharset();
			Reader reader;
			try {
				reader = new InputStreamReader(entity.getContent(), charset);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(
						frame,
						"Ошибка загрузки списка книг к чтению - "
								+ e.getClass().getName() + ", \""
								+ e.getMessage() + "\"", "Ошибка загрузки",
						JOptionPane.ERROR_MESSAGE);
				System.exit(0);
				return;
			}
			JsonParser parser = new JsonParser();
			JsonArray array = parser.parse(reader).getAsJsonArray();
			books = new ArrayList<>();
			for (int i = 0; i < array.size(); i++) {
				books.add(gson.fromJson(array.get(i), DownloaderBook.class));
			}
			DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
			if (books.size() > 0) {
				fillBookTree(root, books);
			} else {
				DefaultMutableTreeNode noExport = new DefaultMutableTreeNode(
						"Книги к выгрузке отсуствуют");
				root.add(noExport);
				downloadBtn.setEnabled(false);
			}
			((DefaultTreeModel) booksTree.getModel()).setRoot(root);
			((DefaultTreeModel) booksTree.getModel()).reload();
			booksTree.setSelectionPaths(null);
			for (int i = 0; i < booksTree.getRowCount(); i++) {
				booksTree.expandRow(i);
			}
		} else {
			JOptionPane.showMessageDialog(
					frame,
					"Ошибка загрузки списка книг к чтению - "
							+ response.getStatusLine(), "Ошибка загрузки",
					JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}

	/**
	 * Заполняет список книг в дереве
	 * 
	 * @param root
	 *            Корень дерева
	 * @param books
	 *            Список книг
	 */
	private void fillBookTree(DefaultMutableTreeNode root,
			List<DownloaderBook> books) {
		// Сортируем - чтобы совсем не ломать hibernate mapping
		TreeSet<DownloaderBook> sortedBooks = new TreeSet<DownloaderBook>(
				new BookComparator());
		sortedBooks.addAll(books);
		DefaultMutableTreeNode serieNode = null;
		for (DownloaderBook book : sortedBooks) {
			if ((book.getSerieName() != null)
					&& (book.getSerieName().trim().length() > 0)) {
				// Есть серия!
				if ((serieNode == null)
						|| (!book.getSerieName().equals(
								serieNode.getUserObject()))) {
					serieNode = new DefaultMutableTreeNode(book.getSerieName());
					root.add(serieNode);
				}
			} else {
				// Серии нет - сбрасываем вершину
				serieNode = null;
			}
			DefaultMutableTreeNode bookNode = new DefaultMutableTreeNode(book);
			if (serieNode != null)
				serieNode.add(bookNode);
			else
				root.add(bookNode);
		}
	}

	/**
	 * Компаратор для сортировки книг
	 */
	private static class BookComparator implements Comparator<DownloaderBook> {
		@Override
		public int compare(DownloaderBook o1, DownloaderBook o2) {
			if ((o1.getSerieName() == null) && (o2.getSerieName() != null))
				// У первой нет серии, у второй - есть -> вторая должна быть
				// выше
				return 1;
			else if ((o2.getSerieName() == null) && (o1.getSerieName() != null))
				// У первой - есть серия, у второй - нет -> первая должна быть
				// выше
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
						// У первой есть номер в серии - сравниваем с ним
						if (o2.getNumInSerie() != null)
							if (o1.getNumInSerie().equals(o2.getNumInSerie())) {
								return (new Integer(o1.getId()))
										.compareTo(new Integer(o2.getId()));
							} else
								return o1.getNumInSerie().compareTo(
										o2.getNumInSerie());
						else
							// У первой - есть номер серии, у второй - нету ->
							// первая будет ниже
							return 1;
					} else
						// У первой номера серии нету - значит пусть она будет
						// выше
						return -1;
				} else
					return o1.getSerieName().compareTo(o2.getSerieName());
			}
		}
	}

	/**
	 * Обработка и уделаление из имени файла всякой ненужной пакости. <br/>
	 * Копипаст фром
	 * http://stackoverflow.com/questions/1155107/is-there-a-cross-
	 * platform-java-method-to-remove-filename-special-chars
	 */
	final static int[] illegalChars = { 34, 60, 62, 124, 0, 1, 2, 3, 4, 5, 6,
			7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23,
			24, 25, 26, 27, 28, 29, 30, 31, 58, 42, 63, 92, 47 };
	static {
		Arrays.sort(illegalChars);
	}

	/**
	 * Выкидывает из имени файла недопустустимые символы
	 * 
	 * @param badFileName
	 *            Имя файла для обработки
	 * @return Имя файла, из которого выкинуты все недопустимые символы
	 */
	public static String cleanFileName(String badFileName) {
		StringBuilder cleanName = new StringBuilder();
		for (int i = 0; i < badFileName.length(); i++) {
			int c = (int) badFileName.charAt(i);
			if (Arrays.binarySearch(illegalChars, c) < 0) {
				cleanName.append((char) c);
			}
		}
		// Дополнительно проверим на '..'
		while (cleanName.indexOf("..") > -1) {
			cleanName.replace(cleanName.indexOf(".."),
					cleanName.indexOf("..") + 2, "__");
		}
		return cleanName.toString();
	}

}
