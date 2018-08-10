/**
 * Created by dell on 2017/9/26.
 */// /*60s发送验证码*/
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
    $('#inputCompany').val($('#companyName').val());
    if($('#success').val()!=1){
        $('#deleteOrga').modal('show');
    };
    // 获取验证码
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
                getverifycode(4, phoneNumber, function (data) {
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
                        $('form .form-control').removeClass('warring');
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
    $('.form-horizontal').on('submit', function (event) {
        event.preventDefault();
        var $this = $(this);
        var data = eval('(' + '{' + decodeURIComponent($this.serialize(), true).replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
        fromSubmit(data);
    })
    //表单提交
    function fromSubmit(data) {
        $("#warmLabel").html("");
        if(data.userName==""||data.phoneNumber==""||data.code==""||data.password==""){
            hideWarning1();
            if(data.userName==""){
                showWarning("#inputName","姓名不能为空");
            }
            if(data.phoneNumber==""){
                showWarning("#inputTel","手机号不能为空");
            }
            if(data.code==""){
                showWarning("#inputCode","验证码不能为空");
            }
            if(data.password==""){
                showWarning("#inputPassword","密码不能为空");
            }

        }else if(!isName(data.userName)){
            hideWarning1();
            showWarning("#inputName","姓名格式不正确");
        }
        else if(!isPhone(data.phoneNumber)){
            hideWarning1();
            showWarning("#inputTel","手机号码格式不正确");
        }
        else if(!checkCode(data.code)){
            hideWarning1();
            showWarning("#inputCode","验证码格式不正确");
        }
        else if(!checkPassword(data.password)){
            hideWarning1();
            showWarning("#inputPassword","密码格式不正确");
        }
        else{
        //密码
                data.tenantId = $('#tenantId').val();
                inviteCode(data,function(result){
                    if(result.ifSuc==1){//请求成功 执行跳转成功页面
                        hideWarning1();
                        window.location.href = '/system/successpage?businessType=1';
                    }else if(result.ifSuc==0){ //参数异常提示
                        if(result.code==6014){
                            hideWarning1();
                            showWarning("#inputTel","手机号已注册");
                        }else if(result.code==6015){
                            hideWarning1();
                            showWarning("#inputCode","验证码错误");
                        }else{
                            hideWarning1();
                            showWarning("",result.msg);
                        }
                    }else {
                        hideWarning1();
                        showWarning("","请求异常");
                    }
                });


    }
    }
})