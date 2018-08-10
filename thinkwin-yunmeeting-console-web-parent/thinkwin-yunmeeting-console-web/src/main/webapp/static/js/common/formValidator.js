
//验证输入内容是否是手机号
function isPhone (string) {
    var pattern = /^(13[0-9]|14[5-9]|15[012356789]|166|17[0-8]|18[0-9]|19[8-9])[0-9]{8}$/;
    if (pattern.test(string)) {
        return true;
    }
    return false;
};
//验证用户名是否为邮箱
function isEmail (string) {
    var pattern = /^[a-zA-Z0-9_.-]+@[a-zA-Z0-9-]+(\.[a-zA-Z0-9-]+)*\.[a-zA-Z0-9]{2,6}$/;
    if (pattern.test(string)) {
        return true;
    }
    return false;
};

//验证密码格式是否正确
function  checkPassword(password){
    var pwdReg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,20}$/;//8到16位数字与字母组合
    if(!pwdReg.test(password)){
        return false;
    }else{
        return true;
    }
}
//验证公司名称是否正确
function checkComName(name) {
    var pattern = /^[\u4e00-\u9fa5a-zA-Z0-9\\\-()（）]{0,100}$/;  //数组,字母,汉字,(),-,\
      //  /[(\u4e00-\u9fa5|0-9|a-z|\\\-)(0-100)]/im;
    if(!pattern.test(name)){
        return false;
    }else{
        return true;
    }
}

//验证码格式校验
function checkCode(code) {
    var isCode=/^\d{6}$/;
    if(!isCode.test(code)){
        return false;
    }else{
        return true;
    }
}


//如果格式不正确的时候,显示错误信息
function showWarning(idName,warningLabel) {
    $(idName).addClass("warring");
    $(".warningBox").show();
    if($("#warmLabel").html()==""){
        $("#warmLabel").html(warningLabel);
    }
}
//隐藏错误信息
function hideWarning(idName) {
    $(idName).removeClass("warring");
    $(".warningBox").hide();
    $("#warmLabel").html("");
}

//验证手机号码是否存在
// function phoneExist(phone,id){
//     var url = "/system/checkphonenumber";
//       $.post(url,phone,function(data){
//         // console.log(data.isRegist+'手机号是否存在的状态0为未注册 1为已注册可以登陆');
//         if(data.isRegist==0){
//             $("#warmLabel").html("");
//             showWarning(id,"手机号未注册");
//         }else if($("#warmLabel").html()=="") {
//             hideWarning(id);
//         }
//         console.log((data.isRegist));
//         return data.isRegigetverifycodst;
//     })
// }

//获取验证码接口
function getverifycode(type,phone,callBack) {
    $.post("/system/getverifycode",{'type':type,'phoneNumber':phone},function(data){
        callBack(data);
    }).fail(function(){
        callBack(500);
    });
}

//注册登录接口
function regist(data,callBack){
    $.post("/system/regist",data,function(backData){
        callBack(backData);
    }).fail(function(){
        callBack(500);
    });
}
//绑定账户的接口
function bindCount(data,callBind){
    $.post("/system/bindaccount",data,function(backData){
        callBind(backData);
    }).fail(function(){
        callBind(500);
    });
}
//创建企业接口
function creatCompany(data,callCreat){
    $.post("/system/createeterprise",data,function(backData){//创建企业的接口未改
        callCreat(backData);
    }).fail(function(){
        callCreat(500);
    });
}
//忘记密码接口
function forgetState(data,callBack){
    //路径
    var url= "/system/changepassword";
    $.post(url,data,function(backData){
        callBack(backData);
    }).fail(function(){
        callBack(500);
    });
}