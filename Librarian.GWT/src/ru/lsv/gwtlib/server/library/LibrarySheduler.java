/**
 * Шедулер обработки новых книг в библиотеках 
 */
package ru.lsv.gwtlib.server.library;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import ru.lsv.gwtlib.server.data.Book;
import ru.lsv.gwtlib.server.data.Library;
import ru.lsv.gwtlib.server.library.parsers.FileParserListener;

/**
 * Шедулер обработки новых книг в библиотеках
 * 
 * @author s.lezhnev
 */
public class LibrarySheduler {

	/**
	 * Session factory
	 */
	private static SessionFactory libFactory = null;

	/**
	 * Шедулер
	 */
	private static volatile ScheduledExecutorService scheduler = null;

	/**
	 * @return the scheduler
	 */
	public static synchronized ScheduledExecutorService getScheduler(
			SessionFactory factory) {
		libFactory = factory;
		if (scheduler == null) {
			scheduler = Executors.newSingleThreadScheduledExecutor();
			scheduler.scheduleWithFixedDelay(new Runnable() {
				@Override
				public void run() {
					service();
				}

			}, 0, 1, TimeUnit.DAYS);
		}
		return scheduler;
	}

	/**
	 * Класс переноса результатов обработки библиотеки в LoadStatus
	 * 
	 * @author s.lezhnev
	 * 
	 */
	private static class LibraryProcessingCallback implements
			LibraryDiffListener, FileParserListener {

		@Override
		public void inArchiveFilesCounted(int numFilesInZip) {
			LoadStatus.getInstance().setTotalFilesToProcess(numFilesInZip);
			LoadStatus.getInstance().setCurrentFileToProcess(1);
		}

		@Override
		public void inArchiveFileProcessed(String fileName, Book book) {
			LoadStatus.getInstance().nextFileToProcess();
		}

		@Override
		public void inArchiveFileParseFailed(String fileName) {
			LoadStatus.getInstance().nextFileToProcess();
		}

		@Override
		public void totalFilesInDiffCounted(int totalFilesInDiff) {
			LoadStatus.getInstance().setTotalArcsToProcess(totalFilesInDiff);
		}

		@Override
		public void beginNewFile(String fileName) {
			LoadStatus.getInstance().nextArcsToProcess(fileName);
		}

		@Override
		public void fileProcessFailed(String fileName, String msg) {
			// TODO Auto-generated method stub

		}

		@Override
		public void fileProcessSavingBooks(String fileName) {
			// Делаем хитрый ход конем
			LoadStatus.getInstance().setSaveBooksMark();
		}

	}

	/**
	 * Поиск новых книг в библиотеках
	 */
	@SuppressWarnings("unchecked")
	public static void service() {
		if (libFactory == null) {
			// Do nothing
			return;
		}
		Session sess = libFactory.openSession();
		try {
			// Получаем ВСЕ библиотеки
			List<Library> libraries = sess.createQuery("from Library").list();
			if (libraries != null) {
				for (Library library : libraries) {
					LibraryUtils.setCurrentLibrary(library);
					LibraryUtils.setLibFactory(libFactory);
					// Смотрим тип библиотеки - ищем реализацию
					LibraryRealization libRes = null;
					switch (library.getLibraryKind()) {
					case 1: {
						// Либрусек
						libRes = new LibRusEcLibrary();
						break;
					}
					case 2: {
						break;
					}
					default:
					}
					if (libRes != null) {
						// Что-то начинаем делать...
						int res = libRes.IsNewBooksPresent(); 
						if (res == 1) {
							// Чота есть!
							LibraryProcessingCallback callback = new LibraryProcessingCallback();
							// Запущаем!
							libRes.processNewBooks(callback, callback);
							// В завершении - сбросим все в LoadStatus
							LoadStatus.getInstance().clear();
						} else if (res == -1) {
							LoadStatus.getInstance().setWasErrorOnLoad(true);
						}
					}
				}
			}
		} finally {
			sess.close();
		}
	}

}
