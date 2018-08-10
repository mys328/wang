/**
 * Created by dell on 2017/5/27.
 */
//初始化验证码
var verifyCode = new GVerify("container");
$(function(){

    //登陆提交
    $("#submitBtn").click(function(){
        //表单验证
        fromSubmit();
    })
    $('#account_login .form-group input[id="inputCode"]').keypress(function (e) {
        if (e.keyCode==13) {
            fromSubmit();
        };
    })
    //阻止浏览器回退
    if (window.history && window.history.pushState) {
        $(window).on('popstate', function () {
            window.history.pushState('forward', null, '#');
            window.history.forward(-1);
        });
    }
    window.history.pushState('forward', null, '#'); //在IE中必须得有这两行
    window.history.forward(-1);
})
//表单验证
function fromSubmit(){
    localStorage.clear();
    //1.获取输入手机号
    var phone = $("#phoneNumber").val();
    var pwd = $("#inputPassword").val();
    //错误信息
    $("#warmLabel").html("");
    //2.先判断手机号是否为空
    if(phone==""){
        showWarning("#phoneNumber","请输入用户名");
    }else {
        if(!isEmail(phone)){
            showWarning("#phoneNumber","用户名格式不正确");
        }else {
            if($("#warmLabel").html()==""){
                hideWarning("#phoneNumber");
            }
        }
    }
    //3.密码校验
    if(pwd==""){
        showWarning("#inputPassword","请输入密码");
    }else {
        var code = $('#inputCode').val();
        if(code.length==0){
            showWarning("#inputCode","请输入验证码");
            return;
        }
        if(verifyCode.validate(code) == false){
            $('#inputCode').val('');
            showWarning("#inputCode","验证码不正确");
            return;
        }
            hideWarning("#inputCode");
            if($("#warmLabel").html()==""){
                // 登陆操作的代码
                 $("#submitBtn").attr({"disabled":"disabled"});
                $.ajax({
                    url : "/logincheck?j_username=" + phone+"&j_password="+pwd,
                    type : "post",
                    success : function(data) {
                        //返回值为登陆的状态
                        console.log(data);
                         $("#submitBtn").removeAttr("disabled");
                        if(data.code==1){
                            console.log(data);
                            if(data.msg=="查询成功！"){
                                var user = {'userId':data.userId,'token':data.token,'roleId':data.roleId};
                                localStorage.setItem('console-userinfo',JSON.stringify(user));

                                window.location.href= data.url+"?userId="+data.userId+"&token="+data.token;
                            }else{
                                showWarning("",data.msg);
                            }
                        }else {
                            showWarning("#inputPassword","用户名与密码不匹配");
                            $("#inputPassword").val("");
                        }
                    },
                    error:function (data) {
                        $("#submitBtn").removeAttr("disabled");
                        showWarning("#inputPassword","请求异常");
                    }
                });
              hideWarning("#inputPassword");

        }
    }
}
/*忘记密码传参跳转页面*/
function forget(){
    var _phoneNumber =$("#phoneNumber").val();
    if(!isPhone(_phoneNumber)){ //如果输入的不是手机号,则不带入下一个界面
       _phoneNumber="";
    }
    location.href="/system/forgetpasswordpage?phoneNumber="+_phoneNumber;
}
