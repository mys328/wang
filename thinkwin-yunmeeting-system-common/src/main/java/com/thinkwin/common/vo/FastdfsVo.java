package com.thinkwin.common.vo;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/22.
 */
public class FastdfsVo implements Serializable{
    private static final long serialVersionUID = 7238884233784086243L;

    /**
     * 图片Id
     */

    private String id;

    /**
     * 图片所在组
     */
    private String group;
    /**
     * 图片名称上传后的名称
     */
    private String fileName;
    /**
     * 文件路径
     *
     */
    private String path;
    /**
     * 图片全路径
     * @return
     */
    private String fileUrl;

    /**
     * 图片原文件名
     * @return
     */
    private String name;

    /**
     *  文件后缀
     * @return
     */
    private String ext;
    /**
     * 文件大小
     */
    private String length;

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
