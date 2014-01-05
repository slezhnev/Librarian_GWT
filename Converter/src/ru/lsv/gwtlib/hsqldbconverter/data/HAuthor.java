package ru.lsv.gwtlib.hsqldbconverter.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Автор книги
 * User: lsv
 * Date: 20.10.2010
 * Time: 14:52:06
 */
public class HAuthor {

    public static final String PRIMARY_KEY = "AUTHOR_ID";

    /**
     * Ключ при хранении
     */
    private Integer authorId;
    /**
     * Имя автора
     */
    private String firstName;
    /**
     * Отчество, может отсутствовать
     */
    private String middleName;
    /**
     * Фамилия
     */
    private String lastName;
    /**
     * Книги выбранного автора
     */
    private List<HBook> books;

    public HAuthor() {
        books = new ArrayList<HBook>();
    }

    public HAuthor(String firstName, String middleName, String lastName, List<HBook> books) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.books = books;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String toString() {
        return "authorId :" + authorId + " firstName: " + firstName + "; middleName: " + middleName + "; lastName: " + lastName;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    @Override
    public boolean equals(Object some) {
        if (some == null) return false;
        if (this == some) return true;
        if (!(some instanceof HAuthor)) return false;
        HAuthor author = (HAuthor) some;
        return Utils.areEqual(author.firstName, author.firstName) &&
                Utils.areEqual(author.middleName, author.middleName) &&
                Utils.areEqual(author.lastName, author.lastName);
    }

    @Override
    public int hashCode() {
        return Utils.getHash(firstName) +
                Utils.getHash(middleName) +
                Utils.getHash(lastName);

    }

    public List<HBook> getBooks() {
        return books;
    }

    public void setBooks(List<HBook> books) {
        this.books = books;
    }

    /**
     * Обрабатывает и создает имя автора
     *
     * @return Сформированное имя без "null"
     */
    public String makeName() {
        StringBuffer str = new StringBuffer();
        if (lastName != null) str.append(lastName);
        str.append(" ");
        if (firstName != null) str.append(firstName);
        str.append(" ");
        if (middleName != null) str.append(middleName);
        return str.toString().trim();
    }

}
