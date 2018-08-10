/**
 * Created by dell on 2017/5/27.
 */
$(function(){
    //登陆的导航切换
    $("#login-nav").on("click","li a",function(aa){
        $("#login-nav li a.login-active").removeClass("login-active");
        $(this).addClass('login-active');
        var _name= $(this).attr('name');
        if(_name=='wechat'){
            //微信登陆接口的实例化
            weixinLogin();
            $("#login_container").show();//微信
            $("#account_login").hide();//账号密码
        }else{
            $("#login_container").hide();
            $("#account_login").show();
        }
    })
    //登陆提交
    $("#submitBtn").click(function(){
        //表单验证
        fromSubmit();
    });
    $('#account_login .form-group input[id="inputPassword"]').keypress(function (e) {
        if (e.keyCode==13) {
            fromSubmit();
        };
    })
})
//表单验证
function fromSubmit(){
    //1.获取输入手机号
    var phone = $("#phoneNumber").val().trim();
    var pwd = $("#inputPassword").val();
    //错误信息
    $("#warmLabel").html("");
    //2.先判断手机号是否为空
    if(phone==""){
        showWarning("#phoneNumber","手机号不能为空");
    }else {
        if(!isPhone(phone)){
            showWarning("#phoneNumber","手机号码格式不正确");
        }else {
            if($("#warmLabel").html()==""){
                hideWarning("#phoneNumber");
            }
        }
    }
    //3.密码校验
    if(pwd==""){
        showWarning("#inputPassword","密码不能为空");
    }else {
            if($("#warmLabel").html()==""){
                // 登陆操作的代码
                 $("#submitBtn").attr({"disabled":"disabled"});
                 $('.warningBox').hide();
                $.ajax({
                    url : "/logincheck?j_username=" + phone+"&j_password="+pwd,
                    type : "post",
                    success : function(data) {
                        console.log(data);
                        $("#submitBtn").removeAttr("disabled");
                        switch (data.code) {
                            case '1':
                                if(data.msg=="查询成功！"){
                                    $('.warningBox .icon').hide();
                                    var user = {'userId':data.userId,'token':data.token,'userName':""};
                                    var tenantType ={'status':data.tenantType};
                                    localStorage.setItem('tenantType',JSON.stringify(tenantType));
                                    localStorage.setItem('userinfo',JSON.stringify(user));
                                    window.location.href= data.url+"?userId="+data.userId+"&token="+data.token;
                                }else{
                                    showWarning("#inputPassword",data.msg);
                                };
                                break;
                            case '2':
                                showWarning("#inputPassword","您的账户已被禁用，请联系企业管理员");
                                break;
                            case '3':
                                var user = {'userId':data.userId};
                                localStorage.setItem('userinfo',JSON.stringify(user));
                                window.location.href= '/system/corporateaccount?isRegist=1';
                                break;
                            default:
                                showWarning("#inputPassword","手机号与密码不匹配");
                                $("#inputPassword").val("");
                                return true;
                        };
                    },
                    error:function (data) {
                        $("#submitBtn").removeAttr("disabled");
                        showWarning("","请求异常");
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
