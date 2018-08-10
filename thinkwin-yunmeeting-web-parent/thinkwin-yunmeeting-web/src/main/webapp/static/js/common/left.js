var _userInfo= JSON.parse(localStorage.getItem('userinfo'));
$(function(){
    $.ajaxSetup({cache:false});
    $.ajax({
        type: 'GET',
        url: "/getUserTenantInfo?token="+_userInfo.token,
        success: function(data) {
            //在此处拼接html菜单页面
            if(null != data){
                if(data.ifSuc == 1) {
                    var logoUrl = data.data.inPicture;
                    if (data.data.inPicture==null) {
                        logoUrl = "/static/images/yunmeeting.png";
                    }
                    var tenantInfo = "<img src=" + logoUrl + "><h4>" + data.data.tenantName + "</h4>";
                    $("#leftMenuTop").html(tenantInfo);
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
    // window.setInterval(function () {
    //
    // },300)

    var token = _userInfo.token;
    var title = $('title').text();
    //alert("title :"+title);
    //该方法是加载所有的 菜单功能
    $.ajax({
        type: 'GET',
        url: "/selectMenus?token="+token,
        success: function(data) {
            if(data.ifSuc == 1) {
                if (null != data) {
                    var menuleft = "";
                    for (var i = 0; i < data.data.length; i++) {
                        var data1 = data.data[i];
                        if (data1.menuName == title) {
                           menuleft += "<li class='nav-item'><a id='" + data1.id + "' class='nav-link active' href='javascript:void(0);' onclick='sendRequest(this)'><i class='icon " + data1.icon + "'></i><span>" + data1.menuName + "</span></a> </li>" +
                                "<input type='hidden' id='url" + data1.id + "' value='" + data1.url + "'/>";
                        } else if (data1.menuName != title) {
                            menuleft += "<li class='nav-item'><a id='" + data1.id + "' class='nav-link' href='javascript:void(0);'  onclick='sendRequest(this)'><i class='icon " + data1.icon + "'></i><span>" + data1.menuName + "</span></a> </li>" +
                                "<input type='hidden' id='url" + data1.id + "' value='" + data1.url + "'/>";
                        }
                    };
                    $("#leftMenuLeft").html(menuleft);
                }
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
    });
   /* $('#leftMenuTop').on('click','img',function () {
        window.location.href= "/gotoConsolePage?token="+  _userInfo.token;
    })*/
});
function sendRequest(ob){
    var id=$(ob).attr("id");
    var url=$("#url"+id).val();
    window.location.href = url+"?token="+  _userInfo.token;
};