package com.thinkwin.common.utils;

/**
 * fastdfs 图片实体工具类
 */
public class FastDFSFile  extends FileManagerConfig {
    private static final long serialVersionUID = 1L;

    private byte[] content;
    private String name;
    private String ext;
    private String length;
    private String author = FILE_DEFAULT_AUTHOR;

    public FastDFSFile(byte[] content, String ext) {
        this.content = content;
        this.ext = ext;
    }

    public FastDFSFile(byte[] content, String name, String ext) {
        this.content = content;
        this.name = name;
        this.ext = ext;
    }

    public FastDFSFile(byte[] content, String name, String ext, String length,
                       String author) {
        this.content = content;
        this.name = name;
        this.ext = ext;
        this.length = length;
        this.author = author;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
