package ru.lsv.gwtlib.downloader;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@SuppressWarnings("serial")
public class ConnectToLibrary extends JDialog {
	/**
	 * Адрес
	 */
	private JTextField addressText;
	/**
	 * Имя пользователя
	 */
	private JTextField userNameText;
	/**
	 * Пароль
	 */
	private JPasswordField passwordText;
	/**
	 * Label для выдачи ошибок
	 */
	private JLabel errorLabel;
	/**
	 * Кнопка коннекта
	 */
	private JButton connectBtn;
	/**
	 * http client
	 */
	private DefaultHttpClient httpclient;
	/**
	 * http context
	 */
	private HttpContext httpContext;
	/**
	 * Результат попытки коннекта
	 */
	private boolean connected = false;
	/**
	 * URL коннекта до сервера
	 */
	private String url = null;
	/**
	 * Путь на сервере для запросов. Читается из properties - и туда же
	 * сохраняется
	 */
	private String requestUrl = null;
	/**
	 * Путь сохранения. Используется в Downloader
	 */
	private String savePath = null;

	/**
	 * Обработчик нажатия Enter и Esc в edit'ах
	 * 
	 * @author s.lezhnev
	 */
	private class EnterEscHandler extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent ev) {
			switch (ev.getKeyCode()) {
			case KeyEvent.VK_ENTER: {
				connectBtn.doClick();
			}
			case KeyEvent.VK_ESCAPE: {
				setVisible(false);
			}
			}
		}
	}

	/**
	 * Create the dialog.
	 * 
	 * @param frame
	 *            Owner frame
	 * @param httpclient
	 *            http client
	 * @param httpContext
	 */
	public ConnectToLibrary(Frame frame, DefaultHttpClient httpclient,
			HttpContext httpContext) {
		super(frame, true);
		this.httpclient = httpclient;
		this.httpContext = httpContext;

		EnterEscHandler enterHandler = new EnterEscHandler();

		setTitle("\u041F\u043E\u0434\u043A\u043B\u044E\u0447\u0435\u043D\u0438\u0435 \u043A \u0431\u0438\u0431\u043B\u0438\u043E\u0442\u0435\u043A\u0435");
		setBounds(100, 100, 441, 196);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 1.0, 1.0, 0.0, 1.0,
				Double.MIN_VALUE };
		getContentPane().setLayout(gridBagLayout);
		{
			JLabel label = new JLabel(
					"\u0410\u0434\u0440\u0435\u0441 \u0438 \u043F\u043E\u0440\u0442:");
			label.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.ipadx = 20;
			gbc_label.anchor = GridBagConstraints.EAST;
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.gridx = 0;
			gbc_label.gridy = 0;
			getContentPane().add(label, gbc_label);
		}
		{
			addressText = new JTextField();
			addressText.addKeyListener(enterHandler);
			GridBagConstraints gbc_addressText = new GridBagConstraints();
			gbc_addressText.insets = new Insets(0, 0, 5, 0);
			gbc_addressText.fill = GridBagConstraints.HORIZONTAL;
			gbc_addressText.gridx = 1;
			gbc_addressText.gridy = 0;
			getContentPane().add(addressText, gbc_addressText);
			addressText.setColumns(10);
		}
		{
			JLabel label = new JLabel(
					"\u0418\u043C\u044F \u043F\u043E\u043B\u044C\u0437\u043E\u0432\u0430\u0442\u0435\u043B\u044F:");
			label.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.anchor = GridBagConstraints.EAST;
			gbc_label.ipadx = 20;
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.gridx = 0;
			gbc_label.gridy = 1;
			getContentPane().add(label, gbc_label);
		}
		{
			userNameText = new JTextField();
			userNameText.addKeyListener(enterHandler);
			GridBagConstraints gbc_userNameText = new GridBagConstraints();
			gbc_userNameText.insets = new Insets(0, 0, 5, 0);
			gbc_userNameText.fill = GridBagConstraints.HORIZONTAL;
			gbc_userNameText.gridx = 1;
			gbc_userNameText.gridy = 1;
			getContentPane().add(userNameText, gbc_userNameText);
			userNameText.setColumns(10);
		}
		{
			JLabel label = new JLabel("\u041F\u0430\u0440\u043E\u043B\u044C:");
			label.setHorizontalAlignment(SwingConstants.CENTER);
			GridBagConstraints gbc_label = new GridBagConstraints();
			gbc_label.anchor = GridBagConstraints.EAST;
			gbc_label.ipadx = 20;
			gbc_label.insets = new Insets(0, 0, 5, 5);
			gbc_label.gridx = 0;
			gbc_label.gridy = 2;
			getContentPane().add(label, gbc_label);
		}
		{
			passwordText = new JPasswordField();
			passwordText.addKeyListener(enterHandler);
			GridBagConstraints gbc_passwordText = new GridBagConstraints();
			gbc_passwordText.insets = new Insets(0, 0, 5, 0);
			gbc_passwordText.fill = GridBagConstraints.HORIZONTAL;
			gbc_passwordText.gridx = 1;
			gbc_passwordText.gridy = 2;
			getContentPane().add(passwordText, gbc_passwordText);
		}
		{
			errorLabel = new JLabel("Error Label");
			errorLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
			errorLabel.setForeground(Color.RED);
			GridBagConstraints gbc_errorLabel = new GridBagConstraints();
			gbc_errorLabel.insets = new Insets(0, 0, 5, 0);
			gbc_errorLabel.gridx = 1;
			gbc_errorLabel.gridy = 3;
			getContentPane().add(errorLabel, gbc_errorLabel);
			errorLabel.setText(" ");
		}
		{
			connectBtn = new JButton(
					"\u041F\u043E\u0434\u043A\u043B\u044E\u0447\u0438\u0442\u044C\u0441\u044F");
			connectBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (tryToConnect()) {
						saveProperties();
						ConnectToLibrary.this.errorLabel.setText("");
						ConnectToLibrary.this.setVisible(false);
					}
				}
			});
			GridBagConstraints gbc_button = new GridBagConstraints();
			gbc_button.gridwidth = 2;
			gbc_button.gridx = 0;
			gbc_button.gridy = 4;
			getContentPane().add(connectBtn, gbc_button);
		}
		// Загружаем параметры из пропертей
		loadProperties();
	}

	/**
	 * Сохраняет параметры подключения
	 */
	protected void saveProperties() {
		Properties prop = new Properties();
		prop.setProperty("address", addressText.getText());
		prop.setProperty("username", userNameText.getText());
		prop.setProperty("url", requestUrl);
		prop.setProperty("path", savePath);
		try {
			prop.store(new FileOutputStream("downloader.properties"),
					"downloader.properties");
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	/**
	 * Выполнить попытку подключения к серверу <br/>
	 * Дополнительно - формируется сообщение об ошибке в errorLabel
	 * 
	 * @return true, если подключение удалось, false - иначе
	 */
	protected boolean tryToConnect() {
		//
		connected = false;
		StringBuilder str = new StringBuilder("http://").append(
				addressText.getText()).append(requestUrl);
		// Сохраним URL
		url = str.toString();
		str.append("?user=").append(userNameText.getText()).append("&psw=")
				.append(passwordText.getPassword()).append("&staylogged=false");
		HttpGet httpGet = new HttpGet(str.toString());
		HttpResponse response;
		try {
			response = httpclient.execute(httpGet, httpContext);
		} catch (ClientProtocolException e) {
			httpGet.abort();
			errorLabel.setText("Неверно задан адрес");
			return false;
		} catch (IOException e) {
			httpGet.abort();
			errorLabel.setText("Ошибка ввода/вывода");
			e.printStackTrace();
			return false;
		}
		if (response.getStatusLine().getStatusCode() == 200) {
			httpGet.abort();
			saveProperties();
			connected = true;
			return true;
		} else {
			errorLabel.setText("Ошибка авторизации");
			httpGet.abort();
			return false;
		}
	}

	/**
	 * Выполняет загрузку параметров из пропертей
	 */
	private void loadProperties() {
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("downloader.properties"));
			addressText.setText(prop.getProperty("address"));
			userNameText.setText(prop.getProperty("username"));
			requestUrl = prop.getProperty("url");
			savePath = prop.getProperty("path");
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
		if ((requestUrl == null) || (requestUrl.trim().length() == 0)) {
			requestUrl = "/librarian_gwt/librarian_gwt/librarian";
		}
		if (savePath == null) {
			savePath = "";
		}
	}

	public boolean isConnected() {
		return connected;
	}

	public String getUrl() {
		return url;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

}
