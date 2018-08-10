var _userInfo= JSON.parse(localStorage.getItem('console-userinfo'));
$(function(){
    var url = window.location.pathname;
    // var title = '';
    $.ajax({
        type: 'POST',
        url: "/saasRolePermission/findSaasPermissionsByParentId",
        data: {parentId:0,token:_userInfo.token},
        success: function(data) {
            //在此处拼接html菜单页面
            if(null != data){
                if(data.ifSuc == 1) {
                    if(data.data.length==0){
                        window.location.href = '/access.jsp';
                        return;
                    }
                    if(url=="/gotoIndexPage"){
                        url = data.data[0].url;
                    }
                    var menuleft = "";
                    for (var i = 0; i < data.data.length; i++) {
                        var data1 = data.data[i];

                        if (data1.url == url) {
                            menuleft += "<li class='nav-item'><span class='select-left'></span><a id='" + data1.orgCode + "' url='" + data1.url + "' class='nav-link active-left' href='javascript:void(0);' onclick='sendRequest(this)'><i class='icon " + data1.icon + "'></i><span class='title-orgName'>" + data1.orgName + "</span></a> </li>" +
                                "<input type='hidden' id='" + data1.orgCode + "' value='" + data1.url + "'/>";
                        } else if (data1.url != url) {
                            menuleft += "<li class='nav-item'><a id='" + data1.orgCode + "' url='" + data1.url + "' class='nav-link' href='javascript:void(0);'  onclick='sendRequest(this)'><i class='icon " + data1.icon + "'></i><span class='title-orgName'>" + data1.orgName + "</span></a> </li>" +
                                "<input type='hidden' id='" + data1.orgCode + "' value='" + data1.url + "'/>";
                        }
                    };
                    $("#leftMenu").html(menuleft);
                }else{
                 notify('danger',data.msg);
                 window.location.href = '/access.jsp';
                }/*else {
                 //    if(data.code==8||data.code==9||data.code==10){
                 //        notify('danger',data.msg);
                 //        window.location.href = data.url;
                 //        return;
                   }
                }*/
            }
        }
    });
    // $('#leftMenuTop').on('click','img',function () {
    //     window.location.href= "/gotoConsolePage?token="+  _userInfo.token;
    // })
});
function sendRequest(ob){
    var url = $(ob).attr('url');
    // var id=$(ob).attr("id");
    // var url=$("#url"+id).val();
    window.location.href = url+"?token="+  _userInfo.token;
};


//跳转到企业租户信息管理
function gotoTenantPage() {
    window.location.href = "/tenant/gotoTenantPage?&token="+_userInfo.token;
}

//跳转到节目管理页面
function gotoOrderPage() {
    window.location.href = "/gotoOrderPage?&token="+_userInfo.token;
}

//跳转到开票管理
function gotoInvoicePage() {
    window.location.href = "/invoice/gotoInvoicePage?&token="+_userInfo.token;
}

//跳转到寄送
function gotoInvoiceSendPage() {
    window.location.href = "/invoice/gotoInvoiceSendPage?&token="+_userInfo.token;
}

//跳转到用户管理
function gotoUserPage() {
    window.location.href = "/saasUser/gotoUserPage?&token="+_userInfo.token;
}

//跳转到角色管理
function gotoRolePage() {
    window.location.href = "/saasRole/gotoRolePage?&token="+_userInfo.token;
}

//跳转到权限管理
function gotoPermissionPage() {
    window.location.href = "/saasPermission/gotoPermissionPage?&token="+_userInfo.token;
}

//跳转到定价配置
function gotoPriceSettingPage() {
    window.location.href = "/saasPermission/gotoPriceSettingPage?&token="+_userInfo.token;
}

//跳转到定价配置
function gotoMeetingShowPage() {
    window.location.href = "/gotoMeetingShowPage?&token="+_userInfo.token;
}