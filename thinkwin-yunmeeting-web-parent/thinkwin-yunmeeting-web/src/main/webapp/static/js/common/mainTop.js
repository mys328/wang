var _userInfo= JSON.parse(localStorage.getItem('userinfo'));

$(function(){
    var topHtml = '<span class="nophoto" style="background-color: #fff"><span class="nophoto {{userName|charcode}}" style="vertical-align:top;" ng-if="nophoto">{{userName|slice:-2}}</span><img ng-if="smallPicture" style="width:100%; height: 100%;vertical-align: bottom;border-radius: 50%;" src="{{smallPicture}}" /></span><span class="userName">{{userName}}</span>';
    $.ajaxSetup({cache:false});
;
    $.ajax({
        type: 'GET',
        url: "/getSysUserInfoByUserId?token="+_userInfo.token,
        success: function(result) {
            if(result.ifSuc == 1){
                if (result.data.smallPicture==null||result.data.smallPicture==''){
                    result.data.nophoto=1;
                };
                $("#mainTop").html(soda(topHtml,result.data));
                if(result.data.role){ //管理员 显示控制台选项
                  $("#ConsoleLoginPageId").css("display","block");
                }
                var userInfo = JSON.parse(localStorage.getItem('userinfo'));
                if (userInfo) {
                    var user = {'userId':userInfo.userId,'token':userInfo.token,'status':userInfo.status,'userName':result.userName};
                    localStorage.setItem('userinfo',JSON.stringify(user));
                };
                if(result.data.openId==null||result.data.openId==''){
                    $('.company-name').text('扫码绑定微信');
                    var index_state = $('.flagBackground').attr('name');
                    fetchs.post('/setting/bindingoauth',{'phoneNumber':result.data.phoneNumber},function(result){
                        if(result.ifSuc==1){
                            $('#bindWechatQuick .qr-code img').attr('src',result.data.qrCodePath);
                            if(index_state=='首页'){
                                $('#bindWechatQuick').addClass('show');
                                /*setTimeout(function () {
                                    $('#bindWechatQuick').removeClass('show');
                                },5000)*/
                            }
                        };
                    })
                }else{
                    fetchs.post('/setting/bindingoauth',{'phoneNumber':result.data.phoneNumber},function(result){
                        if(result.ifSuc==1){
                            $('#bindWechatQuick .qr-code img').attr('src',result.data.qrCodePath);
                        };
                    })
                    $('.company-name').text('企云会公众号');
                }
                $(".nav").show();
            }else {
                if(result.code==8||result.code==9||result.code==10){
                    notify('danger',result.msg);
                    window.location.href = result.url;
                    return;
                }
            }
        }
    });
    $.ajax({
        type: 'GET',
        url: "/getUserTenantInfo?userId="+_userInfo.userId+"&token="+_userInfo.token,
        success: function(data) {
            //在此处拼接html菜单页面
            if(null != data){
                if(data.ifSuc == 1) {
                    var logoUrl = data.data.smallPicture;
                    if (null == logoUrl) {
                        logoUrl = "/static/images/yunmeeting.png";
                    }
                    var tenantInfo = "<img src=" + logoUrl + ">" + data.data.tenantName;
                    $("#mainTopPoto").html(tenantInfo);
                }/*else{
                    notify('danger',data.msg);
                }*/else {
                    if(data.code==8||data.code==9||data.code==10){
                        notify('danger',data.msg);
                        window.location.href = data.url;
                        return;
                    }
                }
            }
        }
    });


/*    /!**
     * 查看用户是否有未读消息功能
     *!/
    $.ajax({
        type: 'GET',
        url: "/getUnreadMessage?token="+_userInfo.token,
        success: function(data) {
            //在此处拼接html菜单页面
            if(null != data){
                if(data.ifSuc == 1) {
                   var messageNum = data.data;
                   if(null != messageNum && messageNum > 0){
                       $("#redPoint").attr("style","display: block");
                   }
                }else{
                    notify('danger',data.msg);
                }
            }
        }
    });*/
});
//去个人资料页面功能
function personalData1() {
    window.location.href = "/setting/personalpage?token="+  _userInfo.token ;
}

//退出登录功能方法
function logout() {
    window.location.href = "/logout";
}

/**
 * 去我的会议页面
 */
function gotoMyMeeting(){
    $('.flagBackground').removeClass(".flagBackground");
    window.location.href="/search/skipMeetingScreening?token="+  _userInfo.token;
}

/**
 * 去会议预定页面
 */
function gotoMeetingReserve() {
    window.location.href="/gotomeetingreservepage?token="+_userInfo.token;
}

/**
 * 去控制台页面(先用企业设置页面顶替 最后换成控制台登录页面)
 */
// function gotoConsolePage(){
//    // window.location.href="";
//     window.location.href="/gotoEnterpriseSettingsPage?token="+_userInfo.token;
// }

function gotoMainPage1() {
    window.location.href="/gotoIndexPage?token="+_userInfo.token;
}
/**
 * 去控制台页面(先用企业设置页面顶替 最后换成控制台登录页面)
 */
function gotoConsoleLoginPage(){
    window.location.href="/gotoConsoleLoginPage?token="+_userInfo.token;
}

/*忘记密码传参跳转页面*/
function forget(){
    window.location.href="/system/forgetpasswordpage";
}

function gotoViewMoredynamicPage(){
    window.location.href="/dynamic/viewmoredynamicpage?token="+_userInfo.token;
}

function getUnreadMessage(){
   // console.info("zhixingzhong....");
    /**
     * 查看用户是否有未读消息功能
     */
    $.ajax({
        type: 'GET',
        url: "/getUnreadMessage?token="+_userInfo.token,
        success: function(data) {
            //在此处拼接html菜单页面
            if(null != data){
                if(data.ifSuc == 1) {
                    var messageNum = data.data;
                    if(null != messageNum && messageNum > 0){
                        $("#redPoint").attr("style","display: block");
                    }
                }else{
                    if(data.code==8||data.code==9||data.code==10){
                        notify('danger',"未读消息查询异常");
                        window.location.href = data.url;
                        return;
                    }
                }
            }
        }
    });
}


window.onload = function(){
    setTimeout(getUnreadMessage,200);//延时加载消息提示功能
}