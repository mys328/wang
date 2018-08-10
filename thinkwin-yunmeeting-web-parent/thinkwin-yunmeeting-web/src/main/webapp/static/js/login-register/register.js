/**
 * Created by dell on 2017/5/28.
 */
// /*60s发送验证码*/
var countdown=60;
function settime(obj) {
    if (countdown == 0) {
        //60s后重发
        obj.attr("disabled",false);
        obj.val("重新发送");
        countdown = 60;
        return;
    } else {
        obj.attr("disabled", true);
        obj.val(countdown + 's');
        countdown--;
    }
    setTimeout(function() {
        settime(obj)
    },1000);
}
$(function(){
    //微信注册的头像信息显示
    $("#weChat-register").hide();
    var _uid= $("#uid").val();
    var _wechatName= $("#wechatName").val();
    var _wechatPhoto= $("#wechatPhoto").val();
    //获取微信用户的id
    if(_uid){
        $("#weChat-register").show();
        $("#weChat-register").append('<img src='+_wechatPhoto+'><span>'+_wechatName+'您好，请完善您的信息</span>')
    }

    //获取验证码
    $("#registerCode").click(function(){

        //获取验证码接口
        var phoneNumber = $("#inputTel").val();//手机号
        //3.判断手机号是否
        if(phoneNumber==""){
            hideWarning("");
            showWarning("#inputTel","手机号不能为空");
        }else{
            if(!isPhone(phoneNumber)){
                hideWarning("");
                showWarning("#inputTel","手机号码格式不正确");
            }else{
                //验证手机号是否存在
                $("#registerCode").attr({"disabled":"disabled"});
                getverifycode(1, phoneNumber, function (data) {
                    $("#registerCode").removeAttr("disabled");
                    if(data==500){
                        hideWarning("");
                        showWarning("#inputTel", "请求异常");
                    }else if (data.ifSuc == 1) {
                        //60s倒计时
                        hideWarning("");
                        var that = $("#registerCode");
                        settime(that);
                        hideWarning("#inputTel");
                    } else {
                        if (data.data.isRegist == 1) {
                            hideWarning("");
                            showWarning("#inputTel", "手机号已注册");
                        }
                    }
                });

            }
        }
    });
    //注册按钮触发表单验证
    $("#registerBtn").click(function(){
        fromSubmit();
    })
    $('.form-horizontal .form-group input[id="inputPassword"]').keypress(function (e) {
        if (e.keyCode==13) {
            fromSubmit();
        };
    })
});
//表单验证
function fromSubmit() {
    //公司名称
    var company = $("#inputCompany").val();
    //姓名
    var name = $("#inputName").val();
    //手机号
    var tel = $("#inputTel").val();
    //验证码
    var code = $("#inputCode").val();
    //密码
    var password = $("#inputPassword").val();
    //错误信息
    $("#warmLabel").html("");
    //1.先判断公司名称否输入
    if (company =='') {
        showWarning("#inputCompany","公司名称不能为空");
    }else {
        if(!checkComName(company)){
            showWarning("#inputCompany","公司名称不能含有特殊字符");

        }else if($("#warmLabel").html()==""){
            hideWarning("#inputCompany");
        }
    }
    //2.判断姓名是否输入
    if(name==""){
        showWarning("#inputName","姓名不能为空");
    }else{
        if(!isName(name)){
            showWarning("#inputName","姓名格式不正确");
        }else if($("#warmLabel").html()==""){
            hideWarning("#inputName");
        }
    }
    //3.判断手机号是否
    if(tel==""){
        showWarning("#inputTel","手机号不能为空");
    }else{
        if(!isPhone(tel)){
            showWarning("#inputTel","手机号码格式不正确");
        }else if($("#warmLabel").html()=="") {
            hideWarning("#inputTel");
        }
    }
    //4.验证码
    if(code==""){
        showWarning("#inputCode","验证码不能为空");
    }else{
        if(!checkCode(code)){
            showWarning("#inputCode","验证码格式不正确");
        }else if($("#warmLabel").html()==""){
            hideWarning("#inputCode");
        }
    }

    //5.密码
    if(password==""){
        showWarning("#inputPassword","密码不能为空");
    }else{
        if(!checkPassword(password)){
            showWarning("#inputPassword","密码格式不正确");
        }else if($("#warmLabel").html()==""){
            hideWarning("#inputPassword");
            $("#registerBtn").attr({"disabled":"disabled"});
            //当本地所有参数都校验通过的时候再调用接口
            var wechatId= $("#uid").val();
            regist({"tenantName":company,"userName":name,"phoneNumber":tel,"password":password,"verifyCode":code,"uId":wechatId},function(data){
                // console.log(data);
                $("#registerBtn").removeAttr("disabled");
                if(data.ifSuc==1){//请求成功 执行跳转成功页面
                    hideWarning("");
                    window.location.href = '/system/successpage?businessType=1';
                }else if(data.data!=null){ //参数异常提示
                    if(data.data.param=="phoneNumber"){

                        if(data.data.paramState=="isBlank"){ //验证手机号是否为空
                            showWarning("#inputTel","手机号不能为空");
                        }else if(data.data.paramState=="isNotFormat"){ //验证手机号格式不正确
                            showWarning("#inputTel","手机号码格式不正确");

                        }else if(data.data.paramState=="isRegist"){//手机号已经注册
                            showWarning("#inputTel","手机号已注册");
                        }
                    }else if(data.data.param=="tenantName"){

                        if(data.data.paramState=="isBlank"){ //验证公司名称是否为空
                            showWarning("#inputCompany","公司名称不能为空");
                        }else if(data.data.paramState=="isExist"){//验证公司名称是否存在
                            showWarning("#inputCompany","公司名称已经存在");
                        }
                    }else if(data.data.param=="verifyCode"){
                        if(data.data.paramState=="error"){ //验证码错误
                            showWarning("#inputCode","验证码错误");
                        }
                    }
                }else {
                    showWarning("","请求异常");
                }
            });
        }
    }
}

