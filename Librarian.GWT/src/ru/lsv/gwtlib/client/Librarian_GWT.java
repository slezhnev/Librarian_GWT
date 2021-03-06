package ru.lsv.gwtlib.client;

import java.util.ArrayList;
import java.util.List;

import ru.lsv.gwtlib.client.data.ClientAuthor;
import ru.lsv.gwtlib.client.data.ClientBook;
import ru.lsv.gwtlib.client.data.ClientBookInList;
import ru.lsv.gwtlib.client.login.LoginPopupPanel;
import ru.lsv.gwtlib.shared.RequestType;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.dom.client.AnchorElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CaptionPanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Librarian_GWT implements EntryPoint {
	/**
	 * Идентификатор текущего пользователя
	 */
	private int currentUserId = 3;

	/**
	 * check box отображения только новых
	 */
	private CheckBox cbShowNew;
	/**
	 * check box отображения только для чтения
	 */
	private CheckBox cbShowForReading;
	/**
	 * check box отображения только прочитанных
	 */
	private CheckBox cbShowReaded;
	/**
	 * radio button работы с авторами
	 */
	private RadioButton rbAuthors;
	/**
	 * radio button работы с сериями
	 */
	private RadioButton rbSeries;
	/**
	 * radio button работы с книгами
	 */
	private RadioButton rbBooks;
	/**
	 * Поле для поиска
	 */
	private TextBox searchBox;
	/**
	 * Описание книги
	 */
	private HTML htmlBookDescription;
	/**
	 * div выдачи сообщений об ошибках
	 */
	private Label errorLabel;
	/**
	 * Кнопка поиска...
	 */
	private Button searchBtn;
	/**
	 * Грид отображения результатов поиска
	 */
	private DataGrid<SearchListElement> searchResultDataGrid;
	/**
	 * Дерево отображение книг
	 */
	private Tree booksTree;
	/**
	 * Сохранение ранее выбранного элемента - для сброса стилей
	 */
	private TreeItem lastSelectedItem = null;
	/**
	 * Кнопка разлогинивания
	 */
	private Button logoffBtn;
	/**
	 * Сообщения для локализации
	 */
	private Librarian_GWTMessages messages = GWT
			.create(Librarian_GWTMessages.class);

	/**
	 * Обработчик нажатия кнопок на дереве
	 * 
	 * @author s.lezhnev
	 */
	private final class BooksKeyClickProcessor implements KeyPressHandler {
		@Override
		public void onKeyPress(KeyPressEvent event) {
			// Проверяем на то, что выбрана именно книга
			if ((booksTree.getSelectedItem() != null)
					&& (booksTree.getSelectedItem().getWidget() != null)
					&& (booksTree.getSelectedItem().getUserObject() != null)) {
				if ((event.getCharCode() == 'r')
						|| (event.getCharCode() == 'к')) {
					updateBook(booksTree.getSelectedItem(),
							!((ClientBook) booksTree.getSelectedItem()
									.getUserObject()).isReaded(),
							((ClientBook) booksTree.getSelectedItem()
									.getUserObject()).isMustRead());
				} else if ((event.getCharCode() == 'd')
						|| (event.getCharCode() == 'в')) {
					updateBook(booksTree.getSelectedItem(),
							((ClientBook) booksTree.getSelectedItem()
									.getUserObject()).isReaded(),
							!((ClientBook) booksTree.getSelectedItem()
									.getUserObject()).isMustRead());
				}
			}
		}
	}

	/**
	 * Бандлы картинок для дерева
	 * 
	 * @author s.lezhnev
	 */
	public interface BooksImages extends ClientBundle {
		/**
		 * Книга
		 * 
		 * @return см.описание
		 */
		ImageResource book();

		/**
		 * Закрытый folder
		 * 
		 * @return см.описание
		 */
		ImageResource folder();

		/**
		 * Открытый folder
		 * 
		 * @return см.описание
		 */
		ImageResource opened_folder();
	}

	/**
	 * Картинки для дерева
	 * 
	 * @author s.lezhnev
	 */
	public class BooksTreeImageResource implements Tree.Resources {
		// Непосредственно сами картинки
		BooksImages treeImages = GWT.create(BooksImages.class);

		@Override
		public ImageResource treeClosed() {
			return treeImages.folder();
		}

		@Override
		public ImageResource treeLeaf() {
			return treeImages.book();
		}

		@Override
		public ImageResource treeOpen() {
			return treeImages.opened_folder();
		}

	}

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		CBGroupHanlder cbGroupH = new CBGroupHanlder();

		RBGroupHandler rbGroupHandler = new RBGroupHandler();

		RootPanel rootPanel = RootPanel.get("rootPane");
		rootPanel.setSize("100%", "100%");

		DockLayoutPanel dockLayoutPanel = new DockLayoutPanel(Unit.EM);
		rootPanel.add(dockLayoutPanel, 10, 10);
		dockLayoutPanel.setSize("100%", "100%");

		Window.enableScrolling(false);
		Window.setMargin("0px");

		SimplePanel simplePanel_1 = new SimplePanel();
		dockLayoutPanel.addNorth(simplePanel_1, 1.0);
		errorLabel = new Label();
		simplePanel_1.setWidget(errorLabel);
		errorLabel.setSize("100%", "100%");
		errorLabel.setStylePrimaryName("serverResponseLabelError");

		SplitLayoutPanel splitLayoutPanel = new SplitLayoutPanel();
		dockLayoutPanel.add(splitLayoutPanel);

		DockLayoutPanel dockLayoutPanel_3 = new DockLayoutPanel(Unit.EM);
		splitLayoutPanel.addWest(dockLayoutPanel_3, 340.0);
		dockLayoutPanel_3.setWidth("");

		VerticalPanel verticalPanel = new VerticalPanel();
		dockLayoutPanel_3.addNorth(verticalPanel, 12.0);
		verticalPanel.setWidth("100%");

		HorizontalPanel horizontalPanel = new HorizontalPanel();
		verticalPanel.add(horizontalPanel);
		horizontalPanel.setSpacing(10);

		cbShowNew = new CheckBox();
		cbShowNew.addValueChangeHandler(cbGroupH);
		cbShowNew.setHTML(messages.showNew());
		horizontalPanel.add(cbShowNew);

		cbShowForReading = new CheckBox();
		cbShowForReading.setHTML(messages.showForReading());
		cbShowForReading.addValueChangeHandler(cbGroupH);
		horizontalPanel.add(cbShowForReading);

		cbShowReaded = new CheckBox();
		cbShowReaded.setText(messages.showReaded());
		cbShowReaded.addValueChangeHandler(cbGroupH);
		horizontalPanel.add(cbShowReaded);

		CaptionPanel captionPanel = new CaptionPanel();
		verticalPanel.add(captionPanel);
		captionPanel.setCaptionText(messages.workWith() + ":");

		HorizontalPanel horizontalPanel_2 = new HorizontalPanel();
		horizontalPanel_2.setSpacing(10);
		captionPanel.setContentWidget(horizontalPanel_2);

		rbAuthors = new RadioButton("WorkWithRBGroup", messages.authors());
		rbAuthors.addValueChangeHandler(rbGroupHandler);
		horizontalPanel_2.add(rbAuthors);

		rbSeries = new RadioButton("WorkWithRBGroup", messages.series());
		rbSeries.addValueChangeHandler(rbGroupHandler);
		horizontalPanel_2.add(rbSeries);

		rbBooks = new RadioButton("WorkWithRBGroup", messages.books());
		rbBooks.addValueChangeHandler(rbGroupHandler);
		horizontalPanel_2.add(rbBooks);

		HorizontalPanel horizontalPanel_3 = new HorizontalPanel();
		verticalPanel.add(horizontalPanel_3);
		horizontalPanel_3.setSpacing(5);
		horizontalPanel_3.setWidth("100%");

		searchBox = new TextBox();
		horizontalPanel_3.add(searchBox);
		searchBox.setWidth("100%");
		horizontalPanel_3.setCellWidth(searchBox, "70%");
		// Обрабатываем Enter для запуска поиска
		searchBox.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					searchBtn.click();
				}
			}

		});

		searchBtn = new Button();
		searchBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				doSearch();
			}
		});
		searchBtn.setText(messages.search());
		horizontalPanel_3.add(searchBtn);
		horizontalPanel_3.setCellHorizontalAlignment(searchBtn,
				HasHorizontalAlignment.ALIGN_RIGHT);
		horizontalPanel_3.setCellWidth(searchBtn, "30%");

		searchResultDataGrid = new DataGrid<SearchListElement>();
		dockLayoutPanel_3.add(searchResultDataGrid);
		searchResultDataGrid.setSize("100%", "100%");
		final SingleSelectionModel<SearchListElement> selectionModel = new SingleSelectionModel<SearchListElement>();
		searchResultDataGrid.setSelectionModel(selectionModel);
		selectionModel
				.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
					public void onSelectionChange(SelectionChangeEvent event) {
						SearchListElement selected = selectionModel
								.getSelectedObject();
						if (selected != null) {
							doLoadBooks(selected);
						}
					}
				});
		TextColumn<SearchListElement> searchResultColumn = new TextColumn<SearchListElement>() {
			@Override
			public String getValue(SearchListElement object) {
				return object.toString();
			}
		};
		searchResultDataGrid.addColumn(searchResultColumn,
				messages.searchResults());
		searchResultDataGrid.setRowData(new ArrayList<SearchListElement>());

		rbAuthors.setValue(true, true);

		DockLayoutPanel dockLayoutPanel_2 = new DockLayoutPanel(Unit.EM);
		splitLayoutPanel.add(dockLayoutPanel_2);

		DockLayoutPanel dockLayoutPanel_1 = new DockLayoutPanel(Unit.EM);
		dockLayoutPanel_2.addSouth(dockLayoutPanel_1, 20.0);

		Label lblNewLabel_1 = new Label(messages.bookDescription());
		dockLayoutPanel_1.addNorth(lblNewLabel_1, 1.5);
		lblNewLabel_1
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		lblNewLabel_1.setWidth("100%");

		SimplePanel simplePanel_2 = new SimplePanel();
		dockLayoutPanel_1.addWest(simplePanel_2, 1.0);

		htmlBookDescription = new HTML("", true);
		htmlBookDescription.setStyleName("bordered");
		dockLayoutPanel_1.add(htmlBookDescription);
		htmlBookDescription.setSize("100%", "100%");
		// Обрабатываем нажатие на автора
		htmlBookDescription.addDomHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Element element = event.getNativeEvent().getEventTarget()
						.cast();
				if (element.getTagName().equals("A")) {
					AnchorElement anchor = element.cast();
					if ((anchor.getHref() != null)
							&& (anchor.getHref().indexOf("#") > -1)) {
						selectAuthor(anchor.getHref().substring(
								anchor.getHref().indexOf("#") + 1));
					}
				}
			}

		}, ClickEvent.getType());

		SimplePanel simplePanel = new SimplePanel();
		dockLayoutPanel_2.addWest(simplePanel, 1.0);

		DockLayoutPanel dockLayoutPanel_4 = new DockLayoutPanel(Unit.EM);
		dockLayoutPanel_2.addNorth(dockLayoutPanel_4, 3.4);

		DockLayoutPanel dockLayoutPanel_5 = new DockLayoutPanel(Unit.EM);
		dockLayoutPanel_4.addNorth(dockLayoutPanel_5, 2.0);
		dockLayoutPanel_5.setSize("100", "33");

		logoffBtn = new Button();
		dockLayoutPanel_5.addEast(logoffBtn, 5.0);
		logoffBtn.setText(messages.logoff());
		logoffBtn.setEnabled(false);

		Button loadStatusBtn = new Button(messages.loadStatus());
		loadStatusBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				(new StatusPopup()).show();
			}
		});
		dockLayoutPanel_5.addWest(loadStatusBtn, 7.0);
		logoffBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				doLogoff();
			}

		});

		Label lblBooks = new Label(messages.books());
		lblBooks.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		dockLayoutPanel_4.add(lblBooks);

		ScrollPanel scrollPanel_1 = new ScrollPanel();
		scrollPanel_1.setStyleName("bordered");
		dockLayoutPanel_2.add(scrollPanel_1);
		scrollPanel_1.setSize("100%", "100%");

		booksTree = new Tree(new BooksTreeImageResource(), true);
		scrollPanel_1.setWidget(booksTree);
		booksTree.addSelectionHandler(new SelectionHandler<TreeItem>() {

			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				if ((lastSelectedItem != null)
						&& (lastSelectedItem.getWidget() != null)
						&& (lastSelectedItem != event.getSelectedItem())) {
					lastSelectedItem.getWidget().removeStyleDependentName(
							"selected");
				}
				if (event.getSelectedItem().getWidget() != null) {
					// Что-то мы тут делаем ТОЛЬКО для книг!
					event.getSelectedItem().getWidget()
							.addStyleDependentName("selected");
					lastSelectedItem = event.getSelectedItem();
					doFormBookDescription((ClientBook) event.getSelectedItem()
							.getUserObject());
				} else {
					lastSelectedItem = null;
				}
			}
		});
		// Обрабатываем нажатие r (readed) и d (must read)
		booksTree.addKeyPressHandler(new BooksKeyClickProcessor());

		// Делаем запрос для проверки авторизации
		StringBuilder strUrl = new StringBuilder(GWT.getModuleBaseURL())
				.append("librarian?req=touch");

		final StartupPopupPanel startupPopup = new StartupPopupPanel();
		startupPopup.setPopupPositionAndShow(startupPopup);

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				URL.encode(strUrl.toString()));

		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					if (200 != response.getStatusCode()) {
						// Если авторизация не прошла - выдаем запрос
						// авторизации
						startupPopup.hide();
						LoginPopupPanel loginPopup = new LoginPopupPanel();
						loginPopup.setPopupPositionAndShow(loginPopup);
					} else {
						startupPopup.hide();
					}
					logoffBtn.setEnabled(true);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					startupPopup.setMessage(messages.serverSideError());
				}

			});
		} catch (RequestException e) {
			errorLabel.setText(messages.serverSideError());
		}

	}

	/**
	 * Выполняет выход из системы
	 */
	protected void doLogoff() {
		StringBuilder strUrl = new StringBuilder(GWT.getModuleBaseURL())
				.append("librarian?req=logoff");
		// Ищем авторов...
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				URL.encode(strUrl.toString()));
		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					// Книга обновилась
					if (200 == response.getStatusCode()) {
						logoffBtn.setEnabled(false);
						// Выдаем запрос авторизации
						LoginPopupPanel loginPopup = new LoginPopupPanel();
						loginPopup.setPopupPositionAndShow(loginPopup);
					} else {
						errorLabel.setText(messages.failLogoff());
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					errorLabel.setText(messages.failLogoff());
				}

			});
		} catch (RequestException e) {
			errorLabel.setText(messages.failLogoff());
		}
	}

	/**
	 * Выполняет обновление книги с изменение состояний readed и mustread
	 * 
	 * @param selectedItem
	 *            Элемент в дереве
	 * @param readed
	 *            Новое состояние readed
	 * @param mustRead
	 *            Новое состояние mustRead
	 */
	protected void updateBook(final TreeItem selectedItem,
			final boolean readed, final boolean mustRead) {
		if ((selectedItem != null) && (selectedItem.getWidget() != null)
				&& (selectedItem.getUserObject() != null)) {
			// Значит это книга
			// Поехали обновлять
			StringBuilder strUrl = new StringBuilder(GWT.getModuleBaseURL())
					.append("librarian?userid=")
					.append(currentUserId)
					.append("&req=updatebook&bookid=")
					.append(((ClientBook) selectedItem.getUserObject())
							.getBookId()).append("&readed=").append(readed)
					.append("&mustread=").append(mustRead);
			// Ищем авторов...
			RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
					URL.encode(strUrl.toString()));
			try {
				builder.sendRequest(null, new RequestCallback() {

					@Override
					public void onResponseReceived(Request request,
							Response response) {
						// Книга обновилась
						if ("true".equals(response.getText())) {
							// Поехали менять стейты
							((ClientBook) selectedItem.getUserObject())
									.setMustRead(mustRead);
							((ClientBook) selectedItem.getUserObject())
									.setReaded(readed);
							setBookLabelStyles(
									(ClientBook) selectedItem.getUserObject(),
									(Label) selectedItem.getWidget());
						} else {
							errorLabel.setText(messages
									.updateBookErrorNotTrue());
						}
					}

					@Override
					public void onError(Request request, Throwable exception) {
						errorLabel.setText(messages.updateBookError());
					}

				});
			} catch (RequestException e) {
				errorLabel.setText(messages.jsonRetrieveError());
			}
		}
	}

	/**
	 * Загружает в результатах поиска автора с указанным ID, выбирает его и
	 * загружает список авторов
	 * 
	 * @param authorId
	 *            Идентификатор автора
	 */
	protected void selectAuthor(String authorId) {
		searchResultDataGrid.setVisibleRangeAndClearData(
				searchResultDataGrid.getVisibleRange(), true);

		StringBuilder strUrl = new StringBuilder(GWT.getModuleBaseURL())
				.append("librarian?userid=").append(currentUserId)
				.append("&req=author&authorid=").append(authorId);
		// Ищем авторов...
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				URL.encode(strUrl.toString()));
		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					try {
						ClientAuthor author = JsonUtils.safeEval(response
								.getText());
						List<SearchListElement> result = new ArrayList<SearchListElement>();
						SearchListElement res = new SearchListElement(author);
						result.add(res);
						searchResultDataGrid.setRowData(result);
						searchResultDataGrid.getSelectionModel().setSelected(
								res, true);
					} catch (IllegalArgumentException ex) {
						errorLabel.setText(messages.nonJsonAnswer());
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					errorLabel.setText(messages.jsonRetrieveError());
				}

			});
		} catch (RequestException e) {
			errorLabel.setText(messages.jsonRetrieveError());
		}
	}

	/**
	 * Формирует описание книги по самой книге
	 * 
	 * @param userObject
	 *            Книга
	 */
	protected void doFormBookDescription(ClientBook book) {
		SafeHtmlBuilder str = new SafeHtmlBuilder();
		str.append(SafeHtmlUtils.fromTrustedString("<b>" + messages.bookName()
				+ ": </b>&nbsp;"));
		if (book.getTitle() == null)
			str.appendEscaped(messages.noBookName());
		else
			str.appendEscaped(book.getTitle());
		str.append(SafeHtmlUtils.fromTrustedString("&nbsp;(&nbsp;"));
		StringBuilder strUrl = new StringBuilder(GWT.getModuleBaseURL())
				.append("librarian?&req=downloadbook&bookid=").append(
						book.getBookId());
		str.appendHtmlConstant("<a href=" + strUrl.toString()
				+ " target=_blank>");
		str.append(SafeHtmlUtils.fromTrustedString("fb2.zip"));
		str.appendHtmlConstant("</a>");
		str.append(SafeHtmlUtils.fromTrustedString("&nbsp;&nbsp;"));
		strUrl = new StringBuilder(GWT.getModuleBaseURL()).append(
				"librarian?&req=downloadbook&type=1&bookid=").append(book.getBookId());
		str.appendHtmlConstant("<a href=" + strUrl.toString()
				+ " target=_blank>");
		str.append(SafeHtmlUtils.fromTrustedString("fb2"));
		str.appendHtmlConstant("</a>");
		str.append(SafeHtmlUtils.fromTrustedString("&nbsp;)"));
		str.appendHtmlConstant("<br>");
		if ((book.getSerieName() != null)
				&& (book.getSerieName().trim().length() > 0)) {
			str.append(
					SafeHtmlUtils.fromTrustedString("<b>" + messages.serie()
							+ ": </b>&nbsp;")).appendEscaped(
					book.getSerieName() + " - " + book.getNumInSerie());
			str.append(SafeHtmlUtils.fromTrustedString("<br>"));
		}
		str.appendHtmlConstant("<br>");
		str.append(SafeHtmlUtils.fromTrustedString("<b>" + messages.authors()
				+ ":</b><br>"));
		for (int i = 0; i < book.getAuthors().length(); i++) {
			ClientAuthor author = book.getAuthors().get(i);
			str.appendHtmlConstant("<a href=#" + author.getAuthorId() + ">")
					.appendEscaped(author.getLastName()).appendEscaped(" ");
			if (author.getFirstName() != null) {
				str.appendEscaped(author.getFirstName() + " ");
			}
			if (author.getMiddleName() != null) {
				str.appendEscaped(author.getMiddleName() + " ");
			}
			str.appendHtmlConstant("</a>").appendHtmlConstant("<br>");
		}
		str.appendHtmlConstant("<br>");
		str.append(
				SafeHtmlUtils.fromTrustedString("<b>" + messages.archive()
						+ ":</b><br>")).appendEscaped(book.getZipFileName())
				.appendHtmlConstant("<br>");
		str.append(
				SafeHtmlUtils.fromTrustedString("<b>" + messages.fileName()
						+ ":</b><br>")).appendEscaped(book.getId());
		if (book.getDeletedInLibrary()) {
			str.append(SafeHtmlUtils
					.fromTrustedString("<br><br><b><font color=red>"
							+ messages.deletedInLibrary() + "</red></b><br>"));
		}
		htmlBookDescription.setHTML(str.toSafeHtml());
	}

	/**
	 * Устанавливает стили для отображения книги <br/>
	 * 
	 * @param book
	 *            Книга, для которой делать формирование
	 * @return Сформированно описание
	 */
	private void setBookLabelStyles(ClientBook book, Label bookLabel) {
		if (book.isReaded()) {
			bookLabel.addStyleDependentName("readed");
		} else {
			bookLabel.removeStyleDependentName("readed");
		}
		if (book.isMustRead()) {
			bookLabel.addStyleDependentName("mustread");
		} else {
			bookLabel.removeStyleDependentName("mustread");
		}
		if (book.getDeletedInLibrary()) {
			bookLabel.addStyleDependentName("deleted");
		}
	}

	/**
	 * Выполняет загрузку книг для выбранного элемента
	 * 
	 * @param selected
	 *            Выбранный элемент
	 */
	private void doLoadBooks(SearchListElement selected) {
		/**
		 * Callback при загрузке списка книг
		 * 
		 * @author s.lezhnev
		 */
		class LoadBookCallback implements RequestCallback {

			@Override
			public void onResponseReceived(Request request, Response response) {
				try {
					if (200 == response.getStatusCode()) {
						JsArray<ClientBook> books = JsonUtils.safeEval(response
								.getText());
						booksTree.clear();
						// Теперь эту радость надо перекачать в дерево...
						TreeItem serieItem = null;
						for (int i = 0; i < books.length(); i++) {
							ClientBook book = books.get(i);
							if ((book.getSerieName() != null)
									&& (book.getSerieName().trim().length() > 0)) {
								// Есть серия!
								if ((serieItem == null)
										|| (!book.getSerieName().equals(
												serieItem.getText()))) {
									serieItem = new TreeItem();
									serieItem.setText(book.getSerieName());
									booksTree.addItem(serieItem);
									serieItem.setState(true);
								}
							} else {
								// Серии нет - сбрасываем вершину
								serieItem = null;
							}
							Label bookLabel = new Label(book.getTitle());
							bookLabel.setStylePrimaryName("bookTree");
							setBookLabelStyles(book, bookLabel);
							TreeItem bookItem = new TreeItem(bookLabel);
							bookItem.setUserObject(book);
							if (serieItem != null) {
								serieItem.addItem(bookItem);
								serieItem.setState(true);
							} else {
								booksTree.addItem(bookItem);
							}
							// А если оно одно - то сразу и выберем
							if (books.length() == 1) {
								booksTree.setSelectedItem(bookItem);
							}
						}
						errorLabel.setText("");
					} else {
						errorLabel.setText(messages.jsonRetrieveError());
					}
				} catch (IllegalArgumentException e) {
					errorLabel.setText(messages.nonJsonAnswer());
				}
			}

			@Override
			public void onError(Request request, Throwable exception) {
				errorLabel.setText(messages.jsonRetrieveError());
			}

		}
		booksTree.clear();
		lastSelectedItem = null;
		booksTree.addItem(new TreeItem(SafeHtmlUtils.fromString(messages
				.loading())));
		StringBuilder strUrl = new StringBuilder(GWT.getModuleBaseURL())
				.append("librarian?userid=").append(currentUserId)
				.append("&req=books");
		// Поехали получать нужное
		if (selected.getAuthor() != null) {
			// Это автор
			strUrl.append("&authorid=").append(
					selected.getAuthor().getAuthorId());
		} else if (selected.getBook() != null) {
			strUrl.append("&bookid=").append(selected.getBook().getBookId());
		} else {
			strUrl.append("&serie=").append(selected.getSerie());
		}
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				URL.encode(strUrl.toString()));
		try {
			builder.sendRequest(null, new LoadBookCallback());
		} catch (RequestException e) {
			errorLabel.setText(messages.jsonRetrieveError());
		}
	}

	/**
	 * Выполняет поиск
	 */
	private void doSearch() {
		//
		searchResultDataGrid.setVisibleRangeAndClearData(
				searchResultDataGrid.getVisibleRange(), true);
		searchBtn.setText(messages.searching());
		searchBtn.setEnabled(false);
		//
		// Формируем тип запроса
		RequestType rt;
		if (cbShowNew.getValue()) {
			rt = RequestType.ONLY_NOT_READED;
		} else if (cbShowForReading.getValue()) {
			rt = RequestType.ONLY_FOR_READING;
		} else if (cbShowReaded.getValue()) {
			rt = RequestType.ONLY_READED;
		} else {
			rt = RequestType.ALL;
		}
		String searchStr = searchBox.getText();
		if (searchStr == null) {
			searchStr = "";
		}

		/**
		 * Хелпер для обработки результатов запроса
		 * 
		 * @author s.lezhnev
		 */
		class SearchRequestCallback implements RequestCallback {

			/**
			 * Тип запроса:<br/>
			 * 0 - авторы<br/>
			 * 1 - серии<br/>
			 * 2 - книги
			 */
			private int requestType;

			/**
			 * Default constructor
			 * 
			 * @param requestType
			 *            Тип запроса:<br/>
			 *            0 - авторы<br/>
			 *            1 - серии<br/>
			 *            2 - книги
			 */
			public SearchRequestCallback(int requestType) {
				this.requestType = requestType;
			}

			@Override
			public void onResponseReceived(Request request, Response response) {
				errorLabel.setText("");
				searchBtn.setText(messages.search());
				searchBtn.setEnabled(true);
				List<SearchListElement> result = new ArrayList<SearchListElement>();
				try {
					switch (requestType) {
					case 0: {
						JsArray<ClientAuthor> authors = JsonUtils
								.safeEval(response.getText());
						for (int i = 0; i < authors.length(); i++) {
							result.add(new SearchListElement(authors.get(i)));
						}
						break;
					}
					case 1: {
						JsArrayString series = JsonUtils.safeEval(response
								.getText());
						for (int i = 0; i < series.length(); i++) {
							result.add(new SearchListElement(series.get(i)));
						}
						break;
					}
					case 2: {
						JsArray<ClientBookInList> books = JsonUtils
								.safeEval(response.getText());
						for (int i = 0; i < books.length(); i++) {
							result.add(new SearchListElement(books.get(i)));
						}
						break;
					}
					default:
					}
				} catch (IllegalArgumentException e) {
					errorLabel.setText(messages.nonJsonAnswer());
				}
				searchResultDataGrid.setRowData(result);
			}

			@Override
			public void onError(Request request, Throwable exception) {
				searchCallbackOnError();
			}

		}

		StringBuilder strUrl = new StringBuilder(GWT.getModuleBaseURL())
				.append("librarian?userid=").append(currentUserId)
				.append("&type=").append(rt).append("&name=").append(searchStr);
		int requestType = -1;
		if (rbAuthors.getValue()) {
			strUrl.append("&req=authorslist");
			requestType = 0;
		} else if (rbSeries.getValue()) {
			strUrl.append("&req=series");
			requestType = 1;
		} else {
			strUrl.append("&req=bookslist");
			requestType = 2;
		}
		// Ищем авторов...
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				URL.encode(strUrl.toString()));
		try {
			builder.sendRequest(null, new SearchRequestCallback(requestType));
		} catch (RequestException e) {
			errorLabel.setText(messages.jsonRetrieveError());
		}
	}

	/**
	 * Обработка ошибки запроса в doSearch
	 */
	private void searchCallbackOnError() {
		errorLabel.setText(messages.jsonRetrieveError());
		searchBtn.setText(messages.search());
		searchBtn.setEnabled(true);
	}

	/**
	 * Обработчик изменения состояния radio button'ов - группы "Авторы",
	 * "Серии", "Книги"
	 * 
	 * @author s.lezhnev
	 */
	private class RBGroupHandler implements ValueChangeHandler<Boolean> {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			if (event.getValue()) {
				// Обрабатываем событие только установки
				if (event.getSource().equals(rbAuthors)) {
					rbSeries.setValue(false, false);
					rbBooks.setValue(false, false);
				} else if (event.getSource().equals(rbBooks)) {
					rbAuthors.setValue(false, false);
					rbSeries.setValue(false, false);
				} else {
					rbAuthors.setValue(false, false);
					rbBooks.setValue(false, false);
				}
			}
		}
	}

	/**
	 * Обработчик группы checkbox'ов - только прочитанное / только к прочтению /
	 * только прочтенное
	 * 
	 * @author admin
	 * 
	 */
	private class CBGroupHanlder implements ValueChangeHandler<Boolean> {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			if (event.getValue()) {
				// Обрабатываем только включение
				if (event.getSource().equals(cbShowNew)) {
					cbShowForReading.setValue(false, false);
					cbShowReaded.setValue(false, false);
				} else if (event.getSource().equals(cbShowForReading)) {
					cbShowNew.setValue(false, false);
					cbShowReaded.setValue(false, false);
				} else {
					cbShowForReading.setValue(false, false);
					cbShowNew.setValue(false, false);
				}
			}
		}

	}
}
