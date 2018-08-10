package com.thinkwin.common.model.core;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Table(name = "`saas_tenant`")
public class SaasTenant implements Serializable{
    private static final long serialVersionUID = -3023028457402717410L;
    /**
     * 主键
     */
    @Id
    @Column(name = "`id`")
    private String id;

    /**
     * 租户CODE
     */
    @Column(name = "`tenant_code`")
    private String tenantCode;

    /**
     * 租户名称
     */
    @Column(name = "`tenant_name`")
    private String tenantName;

    /**
     * 联系人
     */
    @Column(name = "`contacts`")
    private String contacts;

    /**
     * 联系人邮箱
     */
    @Column(name = "`contacts_email`")
    private String contactsEmail;

    /**
     * 联系人电话
     */
    @Column(name = "`contacts_tel`")
    private String contactsTel;

    /**
     * 联系人qq
     */
    @Column(name = "`contacts_qq`")
    private String contactsQq;

    /**
     * 允许的帐号数量
     */
    @Column(name = "`expect_number`")
    private Integer expectNumber;

    /**
     * 租户允许的移动设备数量
     */
    @Column(name = "`expect_device`")
    private Integer expectDevice;

    /**
     * 创建时间
     */
    @Column(name = "`create_time`")
    private Date createTime;

    /**
     * 修改时间
     */
    @Column(name = "`modify_time`")
    private Date modifyTime;

    /**
     * 状态（1：正常 0：禁用）
     */
    @Column(name = "`status`")
    private Integer status;

    /**
     * 显示30天续费信息为1；不显示30天续费信息为0；在续费用户续费时，将此值重置为1；默认为1
     */
    @Column(name = "`30click`")
    private String m30click;

    /**
     * 文档加密配置
     */
    @Column(name = "`encrypt_config`")
    private String encryptConfig;

    /**
     * 分配空间
     */
    @Column(name = "`total_space`")
    private String totalSpace;

    /**
     * 占用空间
     */
    @Column(name = "`used_space`")
    private String usedSpace;

    /**
     * 域表Id
     */
    @Column(name = "`saas_db_config_id`")
    private String saasDbConfigId;

    /**
     * 基础套餐类型
     */
    @Column(name = "`base_package_type`")
    private String basePackageType;

    /**
     * 基础套餐有效时间(为空为永久)
     */
    @Column(name = "`base_package_expir`")
    private Date basePackageExpir;

    /**
     * 基础套餐赠送会议室数量
     */
    @Column(name = "`base_package_room_num`")
    private Integer basePackageRoomNum;

    /**
     * 基础套餐赠送空间数量
     */
    @Column(name = "`base_package_space_num`")
    private Integer basePackageSpaceNum;

    /**
     * 基础套餐赠送空间单位
     */
    @Column(name = "`base_package_spac_unit`")
    private String basePackageSpacUnit;

    /**
     * 租户类型（0：免费 1：收费）
     */
    @Column(name = "`tenant_type`")
    private String tenantType;

    /**
     * 用户购买会议室总数
     */
    @Column(name = "`buy_room_num_total`")
    private Integer buyRoomNumTotal;

    /**
     * 用户购买空间数量合计
     */
    @Column(name = "`buy_space_num_total`")
    private Integer buySpaceNumTotal;

    /**
     * 数据库配置信息
     */
    @Column(name = "`db_config`")
    private String dbConfig;
    /**
     * 用户付费开始时间
     */
    @Column(name = "`base_package_start`")
    private Date BasePackageStart;
    /**
     * 文件删除状态
     */
    @Column(name = "`delete_file`")
    private int deleteFile;
    /**
     * 是否内测租户
     */
    @Column(name = "`is_inner_test`")
    private String isInnerTest;
    /**
     * 终端管理员密码
     */
    @Column(name = "`terminal_manager_passwd`")
    private String terminalManagerPasswd;
    /**
     * 登录次数
     */
    @Column(name = "`login_num`")
    private int loginNum;

    /**
     * 终端数量
     */
    @Column(name = "`terminal_count`")
    private Integer terminalCount;

    @Column(name = "`is_customized_tenant`")
    private String isCustomizedTenant;

    /**
     * 安全key公钥
     */
    @Column(name = "`public_key`")
    private String publicKey;

    /**
     * 安全key私钥
     */
    @Column(name = "`private_key`")
    private String privateKey;

    /**
     * sign随机码
     */
    @Column(name = "`sign_code`")

    private String signCode;

    public String getIsCustomizedTenant() {
        return isCustomizedTenant;
    }

    public void setIsCustomizedTenant(String isCustomizedTenant) {
        this.isCustomizedTenant = isCustomizedTenant;
    }

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public String getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    /**
     * 获取租户CODE
     *
     * @return tenant_code - 租户CODE
     */
    public String getTenantCode() {
        return tenantCode;
    }

    /**
     * 设置租户CODE
     *
     * @param tenantCode 租户CODE
     */
    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode == null ? null : tenantCode.trim();
    }

    /**
     * 获取租户名称
     *
     * @return tenant_name - 租户名称
     */
    public String getTenantName() {
        return tenantName;
    }

    /**
     * 设置租户名称
     *
     * @param tenantName 租户名称
     */
    public void setTenantName(String tenantName) {
        this.tenantName = tenantName == null ? null : tenantName.trim();
    }

    /**
     * 获取联系人
     *
     * @return contacts - 联系人
     */
    public String getContacts() {
        return contacts;
    }

    /**
     * 设置联系人
     *
     * @param contacts 联系人
     */
    public void setContacts(String contacts) {
        this.contacts = contacts == null ? null : contacts.trim();
    }

    /**
     * 获取联系人邮箱
     *
     * @return contacts_email - 联系人邮箱
     */
    public String getContactsEmail() {
        return contactsEmail;
    }

    /**
     * 设置联系人邮箱
     *
     * @param contactsEmail 联系人邮箱
     */
    public void setContactsEmail(String contactsEmail) {
        this.contactsEmail = contactsEmail == null ? null : contactsEmail.trim();
    }

    /**
     * 获取联系人电话
     *
     * @return contacts_tel - 联系人电话
     */
    public String getContactsTel() {
        return contactsTel;
    }

    /**
     * 设置联系人电话
     *
     * @param contactsTel 联系人电话
     */
    public void setContactsTel(String contactsTel) {
        this.contactsTel = contactsTel == null ? null : contactsTel.trim();
    }

    /**
     * 获取联系人qq
     *
     * @return contacts_qq - 联系人qq
     */
    public String getContactsQq() {
        return contactsQq;
    }

    /**
     * 设置联系人qq
     *
     * @param contactsQq 联系人qq
     */
    public void setContactsQq(String contactsQq) {
        this.contactsQq = contactsQq == null ? null : contactsQq.trim();
    }

    /**
     * 获取允许的帐号数量
     *
     * @return expect_number - 允许的帐号数量
     */
    public Integer getExpectNumber() {
        return expectNumber;
    }

    /**
     * 设置允许的帐号数量
     *
     * @param expectNumber 允许的帐号数量
     */
    public void setExpectNumber(Integer expectNumber) {
        this.expectNumber = expectNumber;
    }

    /**
     * 获取租户允许的移动设备数量
     *
     * @return expect_device - 租户允许的移动设备数量
     */
    public Integer getExpectDevice() {
        return expectDevice;
    }

    /**
     * 设置租户允许的移动设备数量
     *
     * @param expectDevice 租户允许的移动设备数量
     */
    public void setExpectDevice(Integer expectDevice) {
        this.expectDevice = expectDevice;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取修改时间
     *
     * @return modify_time - 修改时间
     */
    public Date getModifyTime() {
        return modifyTime;
    }

    /**
     * 设置修改时间
     *
     * @param modifyTime 修改时间
     */
    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    /**
     * 获取状态（1：正常 0：禁用）
     *
     * @return status - 状态（1：正常 0：禁用）
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置状态（1：正常 0：禁用）
     *
     * @param status 状态（1：正常 0：禁用）
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取显示30天续费信息为1；不显示30天续费信息为0；在续费用户续费时，将此值重置为1；默认为1
     *
     * @return m30click
     */
    public String getM30click() {
        return m30click;
    }

    /**
     * 设置显示30天续费信息为1；不显示30天续费信息为0；在续费用户续费时，将此值重置为1；默认为1
     *
     * @param m30click
     */
    public void setM30click(String m30click) {
        this.encryptConfig = m30click == null ? null : m30click.trim();
    }

    /**
     * 获取文档加密配置
     *
     * @return encrypt_config - 文档加密配置
     */
    public String getEncryptConfig() {
        return encryptConfig;
    }

    /**
     * 设置文档加密配置
     *
     * @param encryptConfig 文档加密配置
     */
    public void setEncryptConfig(String encryptConfig) {
        this.encryptConfig = encryptConfig == null ? null : encryptConfig.trim();
    }

    /**
     * 获取分配空间
     *
     * @return total_space - 分配空间
     */
    public String getTotalSpace() {
        return totalSpace;
    }

    /**
     * 设置分配空间
     *
     * @param totalSpace 分配空间
     */
    public void setTotalSpace(String totalSpace) {
        this.totalSpace = totalSpace == null ? null : totalSpace.trim();
    }

    /**
     * 获取占用空间
     *
     * @return used_space - 占用空间
     */
    public String getUsedSpace() {
        return usedSpace;
    }

    /**
     * 设置占用空间
     *
     * @param usedSpace 占用空间
     */
    public void setUsedSpace(String usedSpace) {
        this.usedSpace = usedSpace == null ? null : usedSpace.trim();
    }

    /**
     * 获取域表Id
     *
     * @return saas_db_config_id - 域表Id
     */
    public String getSaasDbConfigId() {
        return saasDbConfigId;
    }

    /**
     * 设置域表Id
     *
     * @param saasDbConfigId 域表Id
     */
    public void setSaasDbConfigId(String saasDbConfigId) {
        this.saasDbConfigId = saasDbConfigId == null ? null : saasDbConfigId.trim();
    }

    /**
     * 获取基础套餐类型
     *
     * @return base_package_type - 基础套餐类型
     */
    public String getBasePackageType() {
        return basePackageType;
    }

    /**
     * 设置基础套餐类型
     *
     * @param basePackageType 基础套餐类型
     */
    public void setBasePackageType(String basePackageType) {
        this.basePackageType = basePackageType == null ? null : basePackageType.trim();
    }

    /**
     * 获取基础套餐有效时间(为空为永久)
     *
     * @return basePackageExpir - 基础套餐有效时间(为空为永久)
     */
    public Date getBasePackageExpir() {
        return basePackageExpir;
    }

    /**
     * 设置基础套餐有效时间(为空为永久)
     *
     * @param basePackageExpir 基础套餐有效时间(为空为永久)
     */
    public void setBasePackageExpir(Date basePackageExpir) {
        this.basePackageExpir = basePackageExpir;
    }


    /**
     * 获取基础套餐赠送会议室数量
     *
     * @return base_package_room_num - 基础套餐赠送会议室数量
     */
    public Integer getBasePackageRoomNum() {
        return basePackageRoomNum;
    }

    /**
     * 设置基础套餐赠送会议室数量
     *
     * @param basePackageRoomNum 基础套餐赠送会议室数量
     */
    public void setBasePackageRoomNum(Integer basePackageRoomNum) {
        this.basePackageRoomNum = basePackageRoomNum;
    }

    /**
     * 获取基础套餐赠送空间数量
     *
     * @return base_package_space_num - 基础套餐赠送空间数量
     */
    public Integer getBasePackageSpaceNum() {
        return basePackageSpaceNum;
    }

    /**
     * 设置基础套餐赠送空间数量
     *
     * @param basePackageSpaceNum 基础套餐赠送空间数量
     */
    public void setBasePackageSpaceNum(Integer basePackageSpaceNum) {
        this.basePackageSpaceNum = basePackageSpaceNum;
    }

    /**
     * 获取基础套餐赠送空间单位
     *
     * @return base_package_spac_unit - 基础套餐赠送空间单位
     */
    public String getBasePackageSpacUnit() {
        return basePackageSpacUnit;
    }

    /**
     * 设置基础套餐赠送空间单位
     *
     * @param basePackageSpacUnit 基础套餐赠送空间单位
     */
    public void setBasePackageSpacUnit(String basePackageSpacUnit) {
        this.basePackageSpacUnit = basePackageSpacUnit == null ? null : basePackageSpacUnit.trim();
    }

    /**
     * 获取租户类型（0：免费 1：收费）
     *
     * @return tenant_type - 租户类型（0：免费 1：收费）
     */
    public String getTenantType() {
        return tenantType;
    }

    /**
     * 设置租户类型（0：免费 1：收费）
     *
     * @param tenantType 租户类型（0：免费 1：收费）
     */
    public void setTenantType(String tenantType) {
        this.tenantType = tenantType == null ? null : tenantType.trim();
    }

    /**
     * 获取用户购买会议室总数
     *
     * @return buy_room_num_total - 用户购买会议室总数
     */
    public Integer getBuyRoomNumTotal() {
        return buyRoomNumTotal;
    }

    /**
     * 设置用户购买会议室总数
     *
     * @param buyRoomNumTotal 用户购买会议室总数
     */
    public void setBuyRoomNumTotal(Integer buyRoomNumTotal) {
        this.buyRoomNumTotal = buyRoomNumTotal;
    }

    /**
     * 获取用户购买空间总数
     *
     * @return buy_room_num_total - 用户购买会议室总数
     */
    public Integer getBuySpaceNumTotal() {
        return buySpaceNumTotal;
    }

    /**
     * 设置用户购买空间总数
     *
     * @param buySpaceNumTotal 用户购买会议室总数
     */
    public void setBuySpaceNumTotal(Integer buySpaceNumTotal) {
        this.buySpaceNumTotal = buySpaceNumTotal;
    }

    /**
     * 获取数据库配置信息
     *
     * @return db_config - 数据库配置信息
     */
    public String getDbConfig() {
        return dbConfig;
    }

    /**
     * 设置数据库配置信息
     *
     * @param dbConfig 数据库配置信息
     */
    public void setDbConfig(String dbConfig) {
        this.dbConfig = dbConfig == null ? null : dbConfig.trim();
    }

    public Date getBasePackageStart() {
        return BasePackageStart;
    }

    public void setBasePackageStart(Date basePackageStart) {
        BasePackageStart = basePackageStart;
    }

    public int getDeleteFile() {
        return deleteFile;
    }

    public void setDeleteFile(int deleteFile) {
        this.deleteFile = deleteFile;
    }

    public String getIsInnerTest() {
        return isInnerTest;
//        return (isInnerTest==null)?"0":isInnerTest;
    }

    public void setIsInnerTest(String isInnerTest) {
        this.isInnerTest = isInnerTest==null ?"0": isInnerTest;
    }

    public String getTerminalManagerPasswd() {
        return terminalManagerPasswd;
    }

    public void setTerminalManagerPasswd(String terminalManagerPasswd) {
        this.terminalManagerPasswd = terminalManagerPasswd;
    }

    public int getLoginNum() {
        return loginNum;
    }

    public void setLoginNum(int loginNum) {
        this.loginNum = loginNum;
    }

    public Integer getTerminalCount() {
        return terminalCount;
    }

    public void setTerminalCount(Integer terminalCount) {
        this.terminalCount = terminalCount;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public String getSignCode() {
        return signCode;
    }

    public void setSignCode(String signCode) {
        this.signCode = signCode;
    }
}