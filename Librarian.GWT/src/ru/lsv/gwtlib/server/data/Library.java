package ru.lsv.gwtlib.server.data;

import com.google.gson.JsonObject;

/**
 * Библиотека User: Lsv Date: 06.11.2010 Time: 17:18:52
 */
public class Library {

	/**
	 * Наименование ключевого поля при хранении
	 */
	public static final String PRIMARY_KEY = "LIBRARY_ID";

	/**
	 * Ключ при хранении
	 */
	private Integer libraryId;
	/**
	 * Название библиотеки
	 */
	private String name;
	/**
	 * Место, где лежат файлы библиотеки
	 */
	private String storagePath;
	/**
	 * Место, где лежит база данных библитеки
	 */
	private String dbPath;
	/**
	 * Тип библиотеки. Требуется для ассоциации с чем-нибудь внешним <br/>
	 * Поддерживаемые типы: <br/>
	 * 0 - просто библиотека, отсутствуют все внешние связи <br/>
	 * 1 - копия библиотеки lib.rus.ec. Поддерживается просмотр данных из нее <br/>
	 * 2 - копия библиотеки flibusta.net. Поддерживается просмотр данных из нее
	 */
	private Integer libraryKind;
	/**
	 * Ссылка на inpx файл библиотеки
	 */
	private String inpxPath;

	public Library(String name, String storagePath, Integer libraryKind,
			String inpxPath) {
		this.name = name;
		this.storagePath = storagePath;
		this.libraryKind = libraryKind;
		this.inpxPath = inpxPath;
	}

	public Library() {

	}

	public Integer getLibraryId() {
		return libraryId;
	}

	public void setLibraryId(Integer libraryId) {
		this.libraryId = libraryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStoragePath() {
		return storagePath;
	}

	public void setStoragePath(String storagePath) {
		this.storagePath = storagePath;
	}

	public String getDbPath() {
		return dbPath;
	}

	public void setDbPath(String dbPath) {
		this.dbPath = dbPath;
	}

	public Integer getLibraryKind() {
		return libraryKind;
	}

	public void setLibraryKind(Integer libraryKind) {
		this.libraryKind = libraryKind;
	}

	@Override
	public String toString() {
		return "library: id=" + libraryId + ", name=" + name + ", storagePath="
				+ storagePath + ", kind=" + libraryKind + ", inpxPath="
				+ inpxPath;
	}

	public String getInpxPath() {
		return inpxPath;
	}

	public void setInpxPath(String inpxPath) {
		this.inpxPath = inpxPath;
	}

	/**
	 * Gson сериализатор
	 * 
	 * @return Сериализированный класс
	 */
	public JsonObject json() {
		JsonObject res = new JsonObject();
		res.addProperty("libraryId", libraryId);
		res.addProperty("name", name);
		res.addProperty("libraryKind", libraryKind);
		return res;
	}
}
