package com.thinkwin.common.businessException;
/**
 *@功能：平台系统返回状态枚举
 *@Time 2017-05-31 上午10:03:20
 */
public enum BusinessExceptionStatusEnum {
	Success("1", "操作成功"),
	Failure("0", "操作失败"),
	TimeOut("6500", "后台计算超时"),
	CalcErr("6501", "服务器出现运算故障"),
	ParamErr("6502", "平台提交的参数错误"),
	SignErr("6503", "平台提交的sign参数错误"),
	SkuidErr("6504", "skuid不存在"),
	RepeatSubmitErr("6505", "重复提交"),
	AreaCodeErr("6506", "地区编码code错误"),
	PageParamErr("6508","分页参数错误"),
	StartTimeErr("6509","查询的开始时间格式有误，请核对"),
	EndTimeErr("6510","查询的结束时间格式有误，请核对"),
	TimeErr("6511","查询的结束时间不能早于查询的开始时间，请核对"),
	TimeFormatErr("6512","查询订单的时间格式有误"),
	PageErr("6513","分页数据为空"),
	FdUuidErr("6514","合作渠道标识不能为空"),
	OpenIdErr("6515","合作方提供的openId为空"),
	SourcePayErr("6516","渠道方未支付成功"),
	AllPayAmountNull("6521","支付金额不能全部都为空"),
	AmountFormatErr("6522","金额格式错误"),
	PhoneAndEmailNotNull("6524","电话和邮箱不能同时为空"),
	DataNull("6525","暂无数据"),

	ParameterIsNull("81311", "必填参数为空"),

	FastdfsConnectionFail("81312", "连接fastdfs服务器失败"),

	WaitIdleconnectionTimeout("81313", "等待空闲连接超时"),

	NotExistGroup("81314", "文件组不存在"),

	UploadResultError("81315", "fastdfs文件系统上传返回结果错误"),

	NotExistPortUrl("81316", "未找到对应的端口和访问地址"),

	SysError("81317", "系统错误"),

	FilePathError("81318", "文件访问地址格式不对"),

	DeleteResultError("81319", "fastdfs文件系统删除文件返回结果错误"),

	NotExistFile("81320", "文件不存在"),

	SpaceIsNotEnough("81321", "企业存储空间不足"),

	NotNullTenantId("6011", "TenantId不能为空"),

	OperateDBError("6012","操作数据库失败"),

	PhoneNumberNotRegister("6013","手机号码未注册"),

	PhoneNumberRegister("6014","手机号码已注册"),

	VerifyCodeError("6015","验证码错误"),


	TenantNameExist("6016","公司名称已存在"),

    CheckTenantInvitCode("6017","企业邀请码错误"),

	PhoneNumberNotTenant("6018","手机号不在当前企业下"),

	UserWasDisabled("6021","该用户已被禁用"),

	PermissionDenied("6019", "权限不足"),

	WechatIsBinding("6020","已绑定微信号"),

	HaveSon("8111","含有子级，无法删除"),

	ClassError("8112","根目录不可移动到子级"),



	// 订单错误信息
	MissingOrderId("9112","缺少订单ID"),

	OrderNotExists("9113","订单不存在"),

	CreateOrderFailed("9114","创建订单失败"),

	OrderCanNotCancel("9115","当前订单状态不允许取消"),

	MeetingRoomBeOccupied("85800","该时段会议室已被占用，请重新选择"),

	MeetingRoomNotExists("85802","该会议室已被管理员删除"),

	MeetingNotExists("85801","会议有变动，会议内容不存在。"),

	// 支付错误信息
	MissingPayChannel("91111","缺少支付方式"),

	InvalidClientIp("91112","客户端Ip校验失败"),


	updatePaymentCertRrror("92111","缺少订单ID或图片路径"),


	//终端监控
	TerminalNotExists("100001","找不到终端数据"),
	InvalidSearchRequest("100002","搜索参数错误"),
	TerminalRegister("100003","终端已注册"),
	TerminalNotRegister("100004","终端未注册"),
	DecryptionError("100005","解密失败"),

	//终端版本MSG
    TerminalVerSuccess("110001","上传成功"),
    TerminalVerFail("110002","上传失败"),
    TerminalVerExtErr("110003","文件格式错误"),
    TerminalVerAnalysisErr("110004","文件解析错误"),
    TerminalVerLow("110005","版本code值不可低于已发布版本"),
    TerminalVerNull("110006","该版本不存在"),
    TerminalVerOperate1("110007","设置内测成功"),
    TerminalVerOperate2("110008","取消内测成功"),
    TerminalVerOperate3("110009","删除成功"),
    TerminalVerOperate4("110010","版本发布成功"),
    TerminalVerErr1("110011","删除失败"),
    TerminalVerErr2("110012","版本状态改变，操作失败，刷新后请重试。"),
    TerminalVerErr3("110013","操作失败，已经存在内测版本"),

	//计划开关机
	PlanSwitchAllFail("7001","全部失败"),
	PlanSwitchPartFail("7002","部分失败"),
	PlanSwitchRefresh("7003","任务状态发生变化，请待页面刷新后查看"),
	PlanSwitchDelete("7004","该任务已被删除，您可以另存新任务");



	private String code;
	private String description;
	private BusinessExceptionStatusEnum(String code, String description) {
		this.code = code;
		this.description = description;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


}
