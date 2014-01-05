/**
 * Utility класс для всяких вспомогательных вещей
 */
package ru.lsv.gwtlib.server.data;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author admin
 * 
 */
public class CommonUtils {

	/**
	 * Hide default constructor
	 */
	private CommonUtils() {

	}

	/**
	 * Соль для расчета хэша пароля
	 */
	private static String salt = "alksdfoiuyqweroiq$%^$%^$%";

	/**
	 * Расчет MD5 с солью от пароля для сохранения в базу
	 * 
	 * @param password
	 *            Пароль
	 * @return MD5 пароля
	 */
	public static String getMD5Password(String password) {
		return getMD5(password + salt);
	}

	/**
	 * Выполняет расчет MD5 от заданной строки
	 * 
	 * @param forHash
	 *            Строка для расчета хэша
	 * @return MD5-хэш
	 */
	public static String getMD5(String forHash) {
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(forHash.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
		return (new BigInteger(messageDigest.digest())).toString(16);
	}
}
