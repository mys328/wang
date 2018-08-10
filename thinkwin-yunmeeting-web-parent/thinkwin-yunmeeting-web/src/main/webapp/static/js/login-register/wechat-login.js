/**
 * Created by dell on 2017/5/27.
 */
var appId = $("#appId").val();
var callBackUrl = $("#callBackUrl").val();
var webSiteDomain = $("#webSiteDomain").val();
//微信登陆的方法
function weixinLogins(_id,_state,_href){
    var obj = new WxLogin({
        id:_id,
        appid: appId,
        scope: "snsapi_login",
        redirect_uri: encodeURIComponent(callBackUrl),
        state: _state,
        style: "black",
        href: _href
    });
    return obj;
}

//延时调用
function weixinLogin(){
    var register_state = 'register';
    //var register_href = webSiteDomain+'/static/css/login-register/wechat-style.css';
    var register_href = webSiteDomain+'/static/css/weixin/wechat-style.css';
    var login_state='login';
    var login_href = webSiteDomain+'/static/css/weixin/login-style.css';
    //var login_href = webSiteDomain+'/static/css/login-register/login-style.css';
    var back_register= document.getElementById('register_container');
    var back_login = document.getElementById('login_container');
    //判断是微信注册还是微信登陆页面
    if(back_login!==null){
        weixinLogins('login_container',login_state,login_href);
    }else if(back_register!==null){
        weixinLogins('register_container',register_state,register_href);
    }
}
//微信登陆的初始化
window.onload = function(){
    weixinLogin();
};
//点击浏览器回退按钮时触发微信事件
window.onpopstate= function(){
    weixinLogin();
};
$(function(){
    //判断是否为已注册用户
    $(".warningBox").hide();
    var _isRegister =$("#warmLabel").text();
    if(_isRegister){
        $(".warningBox").show();
    }
    //返回登录页面
    $('.backLogin').click(function () {
        window.location.href = '/system/loginpage';
        weixinLogin();
    })
})