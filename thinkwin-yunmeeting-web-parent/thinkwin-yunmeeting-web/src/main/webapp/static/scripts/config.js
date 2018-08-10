'use strict';

var fetch = function fetch(opt) {
    this.options = opt;
    this.server = '';
    // this.server     = 'http://10.10.11.54:8080';
    this.userId = '';
    this.token = '';
    this.status=''; //记录用户的付费状态  tenantType=“0” “1”是当前用户是不是付费用户 0为免费用户 1为付费用户
    this.userName='';//记录当前用户名
    this.init();
    // body...
};
fetch.prototype.init = function () {
    var userInfo = JSON.parse(localStorage.getItem('userinfo'));
    var tenantType = JSON.parse(localStorage.getItem('tenantType'));
    if (userInfo) {
        this.userId = userInfo.userId;
        this.token = userInfo.token;
        this.userName="";
    }
    if(tenantType){
        this.status= tenantType.status;
    }
};
fetch.prototype.post = function (url, data, callBack) {
    var userInfo = JSON.parse(localStorage.getItem('userinfo'));
    if (userInfo) {
        this.userId = userInfo.userId;
        this.token = userInfo.token;
        data['userId'] = userInfo.userId;
        data['token'] = userInfo.token;
    }
    $.ajax({
        type: 'post',
        traditional: true,
        url: this.server + url,
        data: data,
        success: function (data) {
            if(data.code==8||data.code==9||data.code==10){
                notify('danger',data.msg);
                setTimeout(function () {
                    window.location.href = data.url;
                }, 3000)
                return;
            }
            callBack(data);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            data = {
                ifSuc: 0,
                code: -1,
                msg: "网络连接失败，请检查网络"
            }
            callBack(data);
        }
    });
    // $.post(this.server + url, data, function (data) {
    //   callBack(data);
    // });
};
fetch.prototype.get = function (url, callBack) {
    var userInfo = JSON.parse(localStorage.getItem('userinfo'));
    if (userInfo) {
        this.userId = userInfo.userId;
        this.token = userInfo.token;
        // data['userId'] = userInfo.userId;
        // data['token'] =  userInfo.token;
    }
    $.ajax({
        type: 'get',
        traditional: true,
        url: this.server + url,
        success: function (data) {
            if(data.code==8||data.code==9||data.code==10){
                notify('danger',data.msg);
                setTimeout(function () {
                    window.location.href = data.url;
                }, 3000)
                return;
            }
            callBack(data);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var data = {
                ifSuc: 0,
                code: -1,
                msg: "网络连接失败，请检查网络"
            }
            callBack(data);
        }
    });
    // $.get(this.server + url, function (data) {
    //   callBack(data);
    // });
};
fetch.prototype.qyh_get = function (url, data, callBack) {
    var userInfo = JSON.parse(localStorage.getItem('userinfo'));
    if (userInfo) {
        this.userId = userInfo.userId;
        this.token = userInfo.token;
        data['userId'] = userInfo.userId;
        data['token'] = userInfo.token;
    }
    $.ajax({
        type: 'get',
        traditional: true,
        url: this.server + url,
        data: data,
        success: function (data) {
            if(data.code==8||data.code==9||data.code==10){
                notify('danger',data.msg);
                setTimeout(function () {
                    window.location.href = data.url;
                }, 3000)
                return;
            }
            callBack(data);
        },
        error: function (XMLHttpRequest, textStatus, errorThrown) {
            var data = {
                ifSuc: 0,
                code: -1,
                msg: "网络连接失败，请检查网络"
            }
            callBack(data);
        }
    });
    // $.get(this.server + url,data,function (data) {
    //     callBack(data);
    // });
};

fetch.prototype.upload = function (url, file, callBack) {
    var data = new FormData();
    data.append('userId', '1212121');
    data.append('fileName', file);
    data.append('token', '232234243');
    $.ajax({
        url: this.server + url,
        type: 'POST',
        data: data,
        cache: false,
        contentType: false,    //不可缺
        processData: false,    //不可缺
        success: function (data) {
            if(data.code==8||data.code==9||data.code==10){
                notify('danger',data.msg);
                setTimeout(function () {
                    window.location.href = data.url;
                }, 3000)
                return;
            }
            callBack(data)
        }
    })
}
//文件上传 段威杰
fetch.prototype.fileupload = function (url, fileid, formId, callBack) {
    var formData = new FormData($(formId)[0]);
    var userInfo = JSON.parse(localStorage.getItem('userinfo'));
    var URL = this.server + url + '?userId=' + userInfo.userId + '&token=' + userInfo.token + '';
    if (fileid || fileid == '') {
        this.server + url + '?userId=' + userInfo.userId + '&fileId=' + fileid + '&token=' + userInfo.token + ''
    }
    ;
    $.ajax({
        url: URL,
        type: 'POST',
        data: formData,
        cache: false,
        contentType: false, //不可缺
        processData: false, //不可缺
        success: function success(data) {
            if(data.code==8||data.code==9||data.code==10){
                notify('danger',data.msg);
                setTimeout(function () {
                    window.location.href = data.url;
                }, 3000)
                return;
            }
            callBack(data);
        }
    });
};

//处理需要同时传递formData与对象数据的情况
//url : 接口路径
//dataObj : 所需传输的map集合(对象数据)
fetch.prototype.uploadMixture= function (url,dataObj,callBack) {
    var formData = new FormData();
    if(dataObj){
        for (var key in dataObj){
            formData.append(key, dataObj[key]);
        }
    }
    var userInfo = JSON.parse(localStorage.getItem('userinfo'));
    var URL = this.server + url+'?userId='+userInfo.userId+'&token='+userInfo.token+'';
    $.ajax({
        url: URL,
        type: 'POST',
        data: formData,
        cache: false,
        contentType: false, //不可缺
        processData: false, //不可缺
        success: function success(data) {
            callBack(data);
        }
    });
};

//导入的ajax请求
fetch.prototype.fileimport = function (url, state, data, callBack) {
    var userInfo = JSON.parse(localStorage.getItem('userinfo'));
    $.ajax({
        url: this.server+url+'?userId='+userInfo.userId+'&selctStatus='+state+'&token='+userInfo.token+'',
        type: 'POST',
        data: JSON.stringify(data),
        cache: false,
        contentType: "application/json",
        processData: false,
        dataType: 'json',
        success: function success(data) {
            if(data.code==8||data.code==9||data.code==10){
                notify('danger',data.msg);
                setTimeout(function () {
                    window.location.href = data.url;
                }, 3000)
                return;
            }
            callBack(data);
        }

    });
};

fetch.prototype.login = function (url, data, callBack) {
    var self = this;

    $.post(this.server + url, data, function (data) {
        if (data.code == 1) {
            self.userId = data.userId;
            self.token = data.token;
            callBack(data);
        }
    });
};
var fetchs = new fetch();

// 自定义sodajs前缀
soda.prefix('ng-');

soda.filter('name', function (name) {
    if(name==null) return '';
    if (name.length == 0) {
        return '';
    } else if (name.length <= 2) {
        return name;
    }
    return name.substring(name.length - 2, name.length);
});

// 过滤器：过去时，多久之前，eg:3分钟前
soda.filter('timeago', function (val) {
    var now = new Date().getTime();
    var zero = new Date(new Date().toLocaleDateString()).getTime();
    var date = {
        year: new Date(parseInt(val)).getFullYear(),
        month: new Date(parseInt(val)).getMonth() + 1,
        day: new Date(parseInt(val)).getDate(),
        hour: new Date(parseInt(val)).getHours(),
        minute: new Date(parseInt(val)).getMinutes(),
        second: new Date(parseInt(val)).getSeconds(),
        millis: new Date(parseInt(val)).getMilliseconds()
    };
    if (parseInt(val) > zero) {
        var m = (now - parseInt(val)) / 60000;
        if (m < 60) {
            if(parseInt(m)==0){
                return '刚刚';
            }
            return parseInt(m) + '分钟前';
        } else {
            return (date.hour < 10 ? '0' + date.hour : date.hour) + ':' + (date.minute < 10 ? '0' + date.minute : date.minute);
        }
    } else {
        return date.year + '.' + date.month + '.' + date.day + ' ' + date.hour + ':' + date.minute;
    }
});

// 过滤器：字符Unicode 编码，返回姓氏编码背景
soda.filter('charcode', function (val) {
    var bgcs = ['bgc9', 'bgc1', 'bgc2', 'bgc3', 'bgc4', 'bgc5', 'bgc6', 'bgc7', 'bgc8', 'bgc9'];
    if(val==null||val=='') return bgcs[0];
    var code = val.charCodeAt(0) % 10;
    return bgcs[code];
});

// 过滤器：关键字高亮
soda.filter('keylight', function (string, word) {
    if(string==null||word==null) return string;
    if (string.length==0||word.length==0) return string;
    var reg = word.replace(/([\*|\$|\*|\(|\)|\+])/g, "\\" + "$1");
    if((string.toUpperCase()).match(reg.toUpperCase())){
        var len = reg.length;
        var index = (string.toUpperCase()).indexOf(reg.toUpperCase());
        var text = string.substr(index , len);
        return word == '' ? string : string.replace(new RegExp(text, 'g'), '<font color="#FA702">' + text + '</font>');
    }else {
        return word == '' ? string : string.replace(new RegExp(reg, 'g'), '<font color="#FA702">' + word + '</font>');
    }
});
// 过滤器：字符串截取
soda.filter('slice', function (str, end) {
    if(str==null||str=='') return '';
    return end > 0 ? str.slice(0, end) : str.slice(end);
});

// 过滤器：会议时间格式化
soda.filter('period', function (start, end) {
    var start = parseInt(start);
    var end = parseInt(end);
    if (moment().format('YYYY') == moment(start).format('YYYY')) {
        return moment(start).format('M月D日') + '<br/>' + moment(start).format('H:mm') + '~' + moment(end).format('H:mm');
    } else {
        return moment(start).format('YYYY年M月D日') + '<br/>' + moment(start).format('H:mm') + '~' + moment(end).format('H:mm');
    }
});

//时间时长格式化 xx小时xx分钟
soda.filter('formattime',function (times) {
    var hours = Math.floor(times / (60 * 60 * 1000))
    var minutes = Math.floor((times - hours * (60 * 60 * 1000)) / (60 * 1000))
    return (hours > 0) ?  hours + '小时' + minutes + '分钟' : minutes + '分钟';
})
function formattime(times) {
    var hours = Math.floor(times / (60 * 60 * 1000))
    var minutes = Math.floor((times - hours * (60 * 60 * 1000)) / (60 * 1000))
    return (hours > 0) ?  hours + '小时' + minutes + '分钟' : minutes + '分钟';
}
// 周格式化
soda.filter('weekly', function(val, format){
    var weekly = ['周日','周一','周二','周三','周四','周五','周六']
    if (val == '1,2,3,4,5') {
        return '工作日';
    }else if (val == '1,2,3,4,5,6,0') {
        return '每天'
    }else{
        var weeks = val.split(',');
        var strs = [];
        for (var i = 0; i < weeks.length; i++) {
            if (i == 0) {
                strs.push('每'+weekly[weeks[i]])
            }else{
                strs.push(weekly[weeks[i]])
            }
        }
        return strs.join('，');
    }
});
// 时间戳格式化
soda.filter('date', function(val, format){
    if (typeof val == 'string' && !isNaN(val)) {
        return moment(parseInt(val)).format(format);
    }else{
        return moment(val).format(format);
    }
});
// 时长
soda.filter('duration', function (start, end, type) {
    var duration = parseInt(end) - parseInt(start);
    var date = moment(parseInt(start)).format('YYYY年M月D日') + ' （' + ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'][moment(parseInt(start)).format('d')] + '）';
    var hours = Math.floor(duration / (60 * 60 * 1000))
    var minutes = Math.floor((duration - hours * (60 * 60 * 1000)) / (60 * 1000))
    return (hours > 0) ? date + ' 时长' + hours + '小时' + minutes + '分钟' : date + ' 时长' + minutes + '分钟';
});
// 会议状态过滤器
soda.filter('status', function (val) {
    var status = ['审核未通过', '待审核', '未开始', '进行中', '已结束', '已取消'];
    // console.log(isNaN(val));
    return '<span class="' + (val == 2 ? 'notbegin' : val == 3 ? 'inmeeting' : val == 1 ? 'notaudited' : 'nomal') + '">' + status[val] + '</span>';
});
//内容为空统一处理
soda.filter('text', function (text) {
    if (text == null) return '';
    return text;
});
soda.filter('br', function (text) {
    console.log(text.replace(/\n/g,"<br/>"));
    return text.replace(/\n/g,"<br/>");
})
// 过滤器：会议时间格式化
soda.filter('dateTime', function (val) {
    return moment(val).format('YYYY-MM-DD HH:mm');
});
//校验日期是否合法
function checkDate(d){
    var ds=d.match(/\d+/g),ts=['getFullYear','getMonth','getDate'];
    var d=new Date(d.replace(/-/g,'/')),i=3;
    ds[1]--;
    while(i--)if( ds[i]*1!=d[ts[i]]()) return false;
    return true;
}


// 通知方法
function notify(type, msg) {
    var icon = type == "success" ? "toast-right" : "toast-error";
    if (type == 'success' && msg == null) {
        msg = '请求成功'
    } else if (type == 'danger' && msg == null) {
        msg = '服务器异常'
    }
    $('body').append('<div class="alert notify-' + type + '" role="alert"><i class="icon icon-' + icon + '"></i>' + msg + '<span class="icon icon-close" data-dismiss="alert"></span></div>');
    $('.alert').delay(2000).fadeOut(1000, function () {
        $('.alert').alert('close');
    });
}
//索取订单通知方法
function notify1(type, msg) {
    var icon = type == "success" ? "toast-right" : "toast-error";
    if (type == 'success' && msg == null) {
        msg = '请求成功'
    } else if (type == 'danger' && msg == null) {
        msg = '服务器异常'
    }
    $('body').append('<div class="alert notify-' + type + '" role="alert" style="width: 730px"><i class="icon icon-' + icon + '"></i>' + msg + '<span class="icon icon-close" data-dismiss="alert"></span></div>');
     $('.alert').delay(2000).fadeOut(1000, function () {
     $('.alert').alert('close');
     });
}
// 通知方法
function notifyByCode(type, code) {
    var msg = msgForCode(code);
    var icon = type == "success" ? "toast-right" : "toast-error";
    $('body').append('<div class="alert notify-' + type + '" role="alert"><i class="icon icon-' + icon + '"></i>' + msg + '<span class="icon icon-close" data-dismiss="alert"></span></div>');
    $('.alert').delay(2000).fadeOut(1000, function () {
        $('.alert').alert('close');
    });
}
function timeRange(staTime, endTime) {
    return moment(Number(staTime)).format('YYYY-MM-DD')+' '+moment(Number(staTime)).format('HH:mm')+'-'+moment(Number(endTime)).format('HH:mm')
}
function duration(meseconds) {
    var seconds = meseconds / 1000; //秒
    var minutes = Math.floor(seconds / 60); //分
    var hours = Math.floor(minutes / 60); //小时
    var days = Math.floor(hours / 24); //天
    var CDay = days;
    var CHour = hours % 24;
    var CMinute = minutes % 60;
    var CSecond = Math.floor(seconds % 60); //"%"是取余运算，可以理解为60进一后取余数，然后只要余数。
    var c = new Date();
    var millseconds = c.getMilliseconds();
    var Cmillseconds = Math.floor(millseconds % 100);
    if (CSecond < 10) {
        //如果秒数为单数，则前面补零
        CSecond = "0" + CSecond;
    }
    if (CMinute < 10) {
        //如果分钟数为单数，则前面补零
        CMinute = "0" + CMinute;
    }
    if (CHour < 10) {
        //如果小时数为单数，则前面补零
        CHour = "0" + CHour;
    }
    /*   if(Cmillseconds<10) {//如果毫秒数为单数，则前面补零
     Cmillseconds="0"+Cmillseconds;
     }*/
    return CHour+"小时"+CMinute+"分钟"; //<span class='timen'>"+Cmillseconds+"</span>+CSecond +"秒"
}
function msgForCode(code) {
    var msg = '请求失败';
    code = parseInt(code);
    switch (code) {
        case 0: {
            msg = '请求失败';
            break;
        }
        case 1: {
            msg = '操作成功';
            break;
        }
        case 6500: {
            //6500 后台计算超时
            msg = '请求超时';
            break;
        }
        case 6501: {
            //6501 服务器出现运算故障
            msg = '服务器出故障了';
            break;
        }
        case 6502: {
            //6502 平台提交的参数错误
            msg = '提交参数有误';
            break;
        }
        case 6503: {
            //6503 平台提交的sign参数错误
            msg = '提交参数有误';
            break;
        }
        case 6504: {
            //6504 skuid不存在
            msg = 'skuid不存在';
            break;
        }
        case 6505: {
            //6505 重复提交
            msg = '重复提交';
            break;
        }
        case 6506: {
            //6506 地区编码code错误
            msg = '地区编码code错误';
            break;
        }
        case 6508: {
            // 6508 分页参数错误
            msg = '分页参数错误';
            break;
        }
        case 6509: {
            // 6509 查询的开始时间格式有误，请核对
            msg = '查询的开始时间格式有误，请核对';
            break;
        }
        case 6510: {
            // 6510 查询的结束时间格式有误，请核对
            msg = '查询的结束时间格式有误，请核对';
            break;
        }
        case 6511: {
            // 6511 查询的结束时间不能早于查询的开始时间，请核对
            msg = '查询的结束时间不能早于查询的开始时间，请核对';
            break;
        }
        case 6512: {
            // 6512 查询订单的时间格式有误
            msg = '查询订单的时间格式有误，请核对';
            break;
        }
        case 6513: {
            // 6513 分页数据为空
            msg = '分页数据为空';
            break;
        }
        case 6514: {
            // 6514 合作渠道标示不能为空
            msg = '合作渠道标示不能为空';
            break;
        }
        case 6515: {
            // 6515 合作方提供的openId为空
            msg = '合作方提供的openId为空';
            break;
        }
        case 6516: {
            // 6516 渠道方未支付成功
            msg = '支付失败';
            break;
        }
        case 6521: {
            // 6521 支付金额不能全部都为空
            msg = '支付金额不能为空';
            break;
        }
        case 6522: {
            // 6522 金额格式错误
            msg = '金额格式错误';
            break;
        }
        case 6524: {
            // 6524 电话和邮箱不能同时为空
            msg = '电话和邮箱不能同时为空';
            break;
        }
        case 6525: {
            // 6525 暂无数据
            msg = '暂无数据';
            break;
        }
        case 6001: {
            // 6001 必填参数为空
            msg = '必填参数为空';
            break;
        }
        case 6002: {
            // 6002 连接fastdfs服务器失败
            msg = '连接服务器失败';
            break;
        }
        case 6003: {
            // 6003 等待空闲连接超时
            msg = '等待空闲连接超时';
            break;
        }
        case 6004: {
            // 6004 文件组不存在
            msg = '文件路径不存在';
            break;
        }
        case 6005: {
            // 6005 fastdfs文件系统上传返回结果错误
            msg = '文件上传返回结果错误';
            break;
        }
        case 6006: {
            // 6006 未找到对应的端口和访问地址
            msg = '未找到对应的端口和访问地址';
            break;
        }
        case 6007: {
            // 6007 系统错误
            msg = '系统错误';
            break;
        }
        case 6008: {
            // 6008 文件访问地址格式不对
            msg = '文件地址格式不对';
            break;
        }
        case 6009: {
            // 6009 fastdfs文件系统删除文件返回结果错误
            msg = '删除文件返回结果错误';
            break;
        }
        case 6010: {
            // 6010 文件不存在
            msg = '文件不存在';
            break;
        }
        case 6011: {
            // 6011 TenantId不能为空
            msg = '企业ID不能为空';
            break;
        }
        case 6012: {
            // 6012 操作数据库失败
            msg = '请求失败';
            break;
        }
        case 6013: {
            // 6013 手机号码未注册
            msg = '手机号码未注册';
            break;
        }
        case 6014: {
            // 6014 手机号码已注册
            msg = '手机号码已注册';
            break;
        }
        case 6015: {
            // 6015 验证码错误
            msg = '验证码错误';
            break;
        }
        case 6016: {
            // 6016 公司名称已存在
            msg = '公司名称已存在';
            break;
        }
        case 6017: {
            // 6017 磁盘空间不足
            msg = '磁盘空间不足';
            break;
        }
        case 6018: {
            // 6018 手机号不在当前企业下
            msg = '手机号不在当前企业下';
            break;
        }
        case 6019: {
            // 6019 权限不足
            msg = '新管理员手机号和原管理员手机号相同';
            break;
        }
        case 6020: {
            // 6020 已绑定微信号
            msg = '已绑定微信号';
            break;
        }
        case 6021:{
            // 6021 该用户已被禁用
            msg = '该用户已被禁用';
            break;
        }
        default: {
            msg = '请求失败';
            break;
        }
    }
    return msg;
}
//输入框正则方法
function inputReplace(type,value) {
   var valuehtml=''
    switch (type) {
        //终端名称（允许输入中文汉字，英文区分大小写，半角括号，下横线）
        case 0: {
            valuehtml = /[^a-zA-Z0-9\u4E00-\u9FA5\\\_()（）]*$/;
            break;
        }
    }
    return value=value.replace(valuehtml,'');
}

$(function(){
    //文字超出省略...鼠标滑过显示黑色提示框
    $('body').on('mouseenter', '.ellipsis', function (e) {
        var title = $(this).data('title').toString();
        var size = parseInt($(this).css('fontSize'));
        if (this.offsetWidth < this.scrollWidth || this.offsetWidth < size*title.length ) {
            $(this).tooltip({
                title: title,
                placement: function (tip, element) {
                    return (window.scrollY+50 < $(element).offset().top) ? 'top':'bottom';
                },
                trigger: 'hover'
            })
            $(this).tooltip('toggle');
        }
    })
})
