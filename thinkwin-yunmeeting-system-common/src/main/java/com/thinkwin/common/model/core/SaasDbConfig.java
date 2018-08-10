package com.thinkwin.common.model.core;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "`saas_db_config`")
public class SaasDbConfig implements Serializable{
    private static final long serialVersionUID = -5733717432661877319L;
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 域名
     */
    @Column(name = "`server_id`")
    private String serverId;

    /**
     * 数据库url
     */
    @Column(name = "`url`")
    private String url;

    /**
     * 端口
     */
    @Column(name = "`port`")
    private String port;

    /**
     * 用户名
     */
    @Column(name = "`username`")
    private String username;

    /**
     * 密码
     */
    @Column(name = "`password`")
    private String password;

    /**
     * 状态 （1;正常 0;禁用）
     */
    @Column(name = "`status`")
    private Integer status;

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取域名
     *
     * @return server_id - 域名
     */
    public String getServerId() {
        return serverId;
    }

    /**
     * 设置域名
     *
     * @param serverId 域名
     */
    public void setServerId(String serverId) {
        this.serverId = serverId == null ? null : serverId.trim();
    }

    /**
     * 获取数据库url
     *
     * @return url - 数据库url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置数据库url
     *
     * @param url 数据库url
     */
    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    /**
     * 获取端口
     *
     * @return port - 端口
     */
    public String getPort() {
        return port;
    }

    /**
     * 设置端口
     *
     * @param port 端口
     */
    public void setPort(String port) {
        this.port = port == null ? null : port.trim();
    }

    /**
     * 获取用户名
     *
     * @return username - 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    /**
     * 获取密码
     *
     * @return password - 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    /**
     * 获取状态 （1;正常 0;禁用）
     *
     * @return status - 状态 （1;正常 0;禁用）
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态 （1;正常 0;禁用）
     *
     * @param status 状态 （1;正常 0;禁用）
     */
    public void setStatus(Integer status) {
        this.status = status;
    }
}