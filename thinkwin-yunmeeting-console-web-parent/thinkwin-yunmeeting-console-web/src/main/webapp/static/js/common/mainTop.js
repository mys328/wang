var _userInfo= JSON.parse(localStorage.getItem('console-userinfo'));
$(function(){
    $.ajax({
        type: 'GET',
        url: "/getSysUserInfoByUserId?token="+_userInfo.token,
        success: function(data) {
            if(data.ifSuc == 1){
                var dataa = data.data;
                var userName = dataa.userName;
                var datainfo = "";
                if(userName.indexOf("管理") > 0){
                    datainfo += "<span class='nophoto'>管理</span><span>"+userName+"</span>";
                }else{
                    var Abbreviation = userName.substring(userName.length-2);
                    datainfo += "<span class='nophoto'>"+Abbreviation+"</span><span>"+userName+"</span>";
                }
                $("#mainTop").html(datainfo);
            }/*else{
                notify('danger',data.msg);
            }*/
        }
    });
    $.ajax({
        type: 'GET',
        url: "/getUserTenantInfo?userId="+_userInfo.userId+"&token="+_userInfo.token,
        success: function(data) {
            //在此处拼接html菜单页面
            if(null != data){
                if(data.ifSuc == 1) {
                    var logoUrl = data.data.companyLogo;
                    if (null == logoUrl) {
                        logoUrl = "/static/images/logo@2x.png";
                    }
                    var tenantInfo = "<img src=" + logoUrl + ">" + data.data.tenantName;
                    $("#mainTopPoto").html(tenantInfo);
                }/*else{
                    notify('danger',data.msg);
                }*/
            }
        }
    });
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
function gotoConsolePage(){
   // window.location.href="";
    window.location.href="/gotoEnterpriseSettingsPage?token="+_userInfo.token;
}

function gotoMainPage1() {
    window.location.href="/gotoIndexPage?token="+_userInfo.token;
}