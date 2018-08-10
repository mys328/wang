/**
 * Created by dell on 2017/7/7.
 */
$(function(){
    //60s发送验证码
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
        },1000)
    }
    //获取验证码
    $("#bindCode").click(function(){
        hideWarning("");
        //获取验证码接口
        var phoneNumber = $("#inputTel").val();//手机号
        //3.判断手机号是否
        if(phoneNumber==""){
            showWarning("#inputTel","手机号不能为空");
        }else{
            if(!isPhone(phoneNumber)){
                showWarning("#inputTel","手机号码格式不正确");
            }else{
                //验证手机号是否存在
                $("#bindCode").attr({"disabled":"disabled"});
                getverifycode('2', phoneNumber, function (data) {
                    $("#bindCode").removeAttr("disabled");
                    console.log(data);
                    if(data==500){
                        showWarning("#inputCode", "请求异常");
                    }else if (data.ifSuc == 1) {
                        //60s倒计时
                        var that = $("#bindCode");
                        settime(that);
                        hideWarning("#inputTel");
                    } else {
                        if (data.data.isRegist == 1) {
                            showWarning("#inputTel", "手机号已注册");
                        }else if(data.msg=="6013"||data.data.isRegist == 0){
                            showWarning("#inputTel", "手机号未注册");
                        };
                    }
                });

            }
        }
    })

    //点击立即绑定上传
    $("#bindBtn").click(function(){
        bindCountForm();
    })
    //绑定微信号的表单验证
    function bindCountForm(){
        //错误信息
        $("#warmLabel").html("");
        //手机号
        var tel = $("#inputTel").val();
        //验证码
        var code = $("#inputCode").val();
        var uid = $("#uid").val();
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
        if(code==""){
            showWarning("#inputCode","验证码不能为空");
        }else{
            if(!checkCode(code)){
                showWarning("#inputCode","验证码格式不正确");
            }else if($("#warmLabel").html()==""){
                hideWarning("#inputCode");
                bindCount({"phoneNumber":tel,"verifyCode":code,"uId":uid},function(data){
                    $("#bindBtn").removeAttr("disabled");
                    console.log(data);
                    if(data.ifSuc==1){//请求成功 执行跳转成功页面
                        window.location.href = '/system/successpage?businessType=4';
                    }else if(data.code=="6015") { //参数异常提示
                        showWarning("#inputCode","验证码错误");
                    }else if(data.code=="6018"){
                        showWarning("#inputTel","手机号不在当前企业下");
                    }else{
                        showWarning("",data.msg);
                    }
                })
            }
        }
    }
})