package ru.lsv.gwtlib.server.data;

/**
 * Файл User: Lsv Date: 07.11.2010 Time: 14:49:08
 */
public class FileEntity {

	public static final String PRIMARY_KEY = "FILE_ID";

	private Integer id;
	private String name;
	private Long size;
	private Library library;

	public FileEntity(String name, Long size, Library library) {
		this.name = name;
		this.size = size;
		this.setLibrary(library);
	}

	public FileEntity() {

	}

	@Override
	public String toString() {
		return name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	/**
	 * @return the library
	 */
	public Library getLibrary() {
		return library;
	}

	/**
	 * @param library the library to set
	 */
	public void setLibrary(Library library) {
		this.library = library;
	}
}
