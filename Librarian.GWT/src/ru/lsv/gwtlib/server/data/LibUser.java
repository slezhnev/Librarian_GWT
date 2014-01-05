package ru.lsv.gwtlib.server.data;

/**
 * Пользователь
 */
public class LibUser {

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0) {
		return (arg0 instanceof LibUser) && (((LibUser) arg0).getId() == id);
	}

	public static final String PRIMARY_KEY = "USER_ID";

	private Integer id;
	private String name;
	private String password;

	public LibUser(String name, String password) {
		this.name = name;
		this.password = password;
	}

	public LibUser() {

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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
