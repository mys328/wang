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
    //点击确定按钮执行表单验证方法
    $("#forgetSubmit").click(function(){
        fromSubmit();
    });
    //点击获取验证码
    $("#forgetSend").click(function(){
        //错误信息
        hideWarning("");
        //获取验证码接口
        var phoneNumber = $("#forgetTel").val();//手机号
        //3.判断手机号
        if(phoneNumber==""){
            showWarning("#forgetTel","请输入手机号");
        }else{
            if(!isPhone(phoneNumber)){
                showWarning("#forgetTel","手机格式不正确");
            }else {
                //验证手机号是否存在 (1.注册 2.忘记密码)
                $("#forgetSend").attr({"disabled":"disabled"});
                getverifycode(2, phoneNumber, function (data) {
                    $("#forgetSend").removeAttr("disabled");
                    if(data==500){
                        showWarning("#forgetCode", "请求异常");
                    }else if (data.ifSuc == 1) {
                        //60s倒计时
                        var that = $("#forgetSend");
                        settime(that);
                        hideWarning("#forgetTel");
                    } else {
                        if (data.data.isRegist == 0) {
                            showWarning("#forgetTel", "手机号未注册");
                        }
                    }
                });
            }

        }
    });
});

//表单验证方法的具体实现
function fromSubmit() {
    //1.获取输入手机号
    var phone = $("#forgetTel").val();
    var code = $("#forgetCode").val();
    var pwd = $("#forgetPwd").val();
    var pwdAgain = $("#forgetPwdAgain").val();
    //错误信息
    $("#warmLabel").html("");
    //1.判断手机号是否
    if(phone==""){
        showWarning("#forgetTel","请输入手机号");
    }else{
        if(!isPhone(phone)){
            showWarning("#forgetTel","手机格式不正确");
        }else if($("#warmLabel").html()=="") {
            hideWarning("#forgetTel");
        }
    }

    //2.验证码
    if(code==""){
        showWarning("#forgetCode","请输入验证码");
    }else{
        if(!checkCode(code)){
        showWarning("#forgetCode","验证码格式不正确");
        }else if($("#warmLabel").html()==""){
        hideWarning("#forgetCode");
        }
    }

    //3.密码
    if(pwd==""){
        showWarning("#forgetPwd","请输入密码");
    }else{
        if(!checkPassword(pwd)){
            showWarning("#forgetPwd","密码格式不正确");
        }else if($("#warmLabel").html()==""){
            hideWarning("#forgetPwd");
        }
    }
    //4.确认密码
    if(pwdAgain==""){
        showWarning("#forgetPwdAgain","请输入确认密码");
    }else {
        if(pwd!=pwdAgain){
            showWarning("#forgetPwdAgain","两次密码输入不一致");
        }else if($("#warmLabel").html()==""){
            hideWarning("#forgetPwdAgain");
           //当本地所有参数都校验通过的时候再调用接口
            var param = { //忘记密码的参数
                'phoneNumber':phone,
                'verifyCode':code,
                'password':pwd,
                'againPassword':pwdAgain
            };
            //忘记接口
            $("#forgetSubmit").attr({"disabled":"disabled"});
            forgetState(param,function(data){
                $("#forgetSubmit").removeAttr("disabled");
                console.log((data));
                if(data.ifSuc==1){//请求成功 执行跳转成功页面
                    window.location.href = '/system/successpage?businessType=2';
                }else if(data.data!=null){ //参数异常提示
                    if(data.data.param=="phoneNumber"){

                        if(data.data.paramState=="isBlank"){ //验证手机号是否为空
                            showWarning("#inputTel","请输入手机号");
                        }else if(data.data.paramState=="isNotFormat"){ //验证手机号格式不正确
                            showWarning("#inputTel","手机格式不正确");

                        }else if(data.data.paramState=="isNotRegist"){//手机号未注册
                            showWarning("#inputTel","手机号未注册");
                        }
                    }else if(data.data.param=="password"){ //验证密码格式是否正确,两次输入密码是否一致

                        if(data.data.paramState=="isBlank"){ //密码为空
                            showWarning("#inputCompany","密码为空");
                        }else if(data.data.paramState=="isNotFormat"){//密码格式不正确
                            showWarning("#inputCompany","密码格式不正确");
                        }
                    }else if(data.data.param=="againPassword"){ //验证密码格式是否正确,两次输入密码是否一致

                        if(data.data.paramState=="isBlank"){ //密码为空
                            showWarning("#inputCompany","确认密码为空");
                        }else if(data.data.paramState=="isNotFormat"){//密码格式不正确
                            showWarning("#inputCompany","确认密码格式不正确");
                        }else if(data.data.paramState=="error"){
                            showWarning("#inputCompany","两次密码输入不一致");
                        }
                    }else if(data.data.param=="verifyCode"){
                        if(data.data.paramState=="error"){ //验证码错误
                            showWarning("#forgetCode","验证码错误");
                        }
                    }
                }else {
                    showWarning("","请求异常");
                }
            });
        }
     }
}
