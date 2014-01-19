/**
 * Popup для отображения статуса загрузок библиотеки
 */
package ru.lsv.gwtlib.client;

import ru.lsv.gwtlib.client.data.ClientLoadStatus;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Popup для отображения статуса загрузок библиотеки
 * 
 * @author s.lezhnev
 * 
 */
public class StatusPopup extends Composite {

	/**
	 * Инстанс темплейта для архивов
	 */
	private static final StatusPopupMessages POPUPMESSAGES = GWT
			.create(StatusPopupMessages.class);
	/**
	 * UI Binder
	 */
	private static StatusPopupUiBinder uiBinder = GWT
			.create(StatusPopupUiBinder.class);

	@UiField
	Label statusLabelArcs;
	@UiField
	Label statusLabelFiles;
	@UiField
	Label statusLabelLibrary;
	@UiField
	Button refreshBtn;
	@UiField
	Button forceCheckBtn;

	interface StatusPopupUiBinder extends UiBinder<Widget, StatusPopup> {
	}

	/**
	 * Непосредственно сама панель для отображения
	 */
	private PopupPanel popupPanel;
	/**
	 * Текущий статус загрузки
	 */
	private volatile ClientLoadStatus currentStatus = null;
	/**
	 * Таймер обновления
	 */
	private Timer refreshTimer;

	/**
	 * Default constructor
	 */
	public StatusPopup() {
		initWidget(uiBinder.createAndBindUi(this));
		// Перекрытие hide - это, видимо, ЕДИНСТВЕННЫЙ СПОСОБ для autohide
		// PopupPanel перехватить ее закрытие. Поскольку addCloseHandler
		// НИХЕРА(!) не работает. Оно, видимо, так и задумано (повбывав бы!)
		popupPanel = new PopupPanel(true, true) {
			@Override
			public void hide(boolean autoClosed) {
				refreshTimer.cancel();
				super.hide(autoClosed);
			}
		};
		popupPanel.add(this);
		// Зафиксируем размер окна
		popupPanel.setSize("50em", "12em");
	}

	/**
	 * Отобразить панель статуса
	 */
	public void show() {
		currentStatus = null;
		updateStatus();
		loadStatus();
		// Запускаем таймер-обновлятор
		refreshTimer = new Timer() {

			@Override
			public void run() {
				// Загружаем статус
				loadStatus();
			}

		};
		refreshTimer.scheduleRepeating(5000);
		popupPanel.center();
	}

	/**
	 * Обновляет отображение статуса (статус берется из currentStatus)
	 */
	private void updateStatus() {
		if (currentStatus == null) {
			statusLabelLibrary.setText(POPUPMESSAGES.requesting());
			statusLabelArcs.setText("");
			statusLabelFiles.setText("");
			refreshBtn.setEnabled(true);
			forceCheckBtn.setEnabled(false);
		} else {
			if (currentStatus.getTotalArcsToProcess() > 0) {
				statusLabelLibrary.setText(currentStatus.getCurrentLibrary());
				statusLabelArcs.setText(POPUPMESSAGES.arcsDesc(
						currentStatus.getCurrentArcsToProcess(),
						currentStatus.getTotalArcsToProcess(),
						currentStatus.getCurrentArcName()).asString());
				if (currentStatus.getCurrentFileToProcess() > currentStatus
						.getTotalFilesToProcess()) {
					statusLabelFiles.setText(POPUPMESSAGES.storingBooks());
				} else {
					statusLabelFiles.setText(POPUPMESSAGES.filesDesc(
							currentStatus.getCurrentFileToProcess(),
							currentStatus.getTotalFilesToProcess()).asString());
				}
				forceCheckBtn.setEnabled(false);
			} else {
				// А там нихрена не делается
				statusLabelLibrary.setText(POPUPMESSAGES.noCheck());
				statusLabelArcs.setText("");
				statusLabelFiles.setText("");
				forceCheckBtn.setEnabled(true);
			}
			refreshBtn.setEnabled(true);
		}
	}

	/**
	 * Загружает статус с сервера
	 */
	private void loadStatus() {
		StringBuilder strUrl = new StringBuilder(GWT.getModuleBaseURL())
				.append("librarian?req=loadstatus");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				URL.encode(strUrl.toString()));
		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					if (200 == response.getStatusCode()) {
						try {
							currentStatus = JsonUtils.safeEval(response
									.getText());
							updateStatus();
						} catch (IllegalArgumentException ex) {
							currentStatus = null;
							updateStatus();
						}
					}
				}

				@Override
				public void onError(Request request, Throwable exception) {
					currentStatus = null;
					updateStatus();
				}

			});
		} catch (RequestException e) {
			currentStatus = null;
			updateStatus();
		}

	}

	@UiHandler("forceCheckBtn")
	void onForceCheckBtnClick(ClickEvent event) {
		forceCheckBtn.setEnabled(false);
		StringBuilder strUrl = new StringBuilder(GWT.getModuleBaseURL())
				.append("librarian?req=forcecheck");
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
				URL.encode(strUrl.toString()));
		try {
			builder.sendRequest(null, new RequestCallback() {

				@Override
				public void onResponseReceived(Request request,
						Response response) {
					if (200 == response.getStatusCode()) {
						loadStatus();
					}
					forceCheckBtn.setEnabled(true);
				}

				@Override
				public void onError(Request request, Throwable exception) {
					forceCheckBtn.setEnabled(true);
				}

			});
		} catch (RequestException e) {
			forceCheckBtn.setEnabled(true);
		}
	}

	@UiHandler("refreshBtn")
	void onRefreshBtnClick(ClickEvent event) {
		refreshBtn.setEnabled(false);
		loadStatus();
	}
}
