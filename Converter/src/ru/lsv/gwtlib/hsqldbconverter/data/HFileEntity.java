package ru.lsv.gwtlib.hsqldbconverter.data;

/**
 * Файл
 * User: Lsv
 * Date: 07.11.2010
 * Time: 14:49:08
 */
public class HFileEntity {

    public static final String PRIMARY_KEY = "FILE_ID";

    private Integer id;
    private String name;
    private Long size;

    public HFileEntity(String name, Long size) {
        this.name = name;
        this.size = size;
    }

    public HFileEntity() {
        
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
}
