var _userInfo= JSON.parse(localStorage.getItem('userinfo'));
$(function(){
    var topHtml = '<span class="nophoto" style="background-color: #fff"><span class="nophoto {{userName|charcode}}" ng-if="nophoto">{{userName|slice:-2}}</span><img ng-if="smallPicture" style="width:100%; height: 100%;vertical-align: bottom;border-radius: 50%;" src="{{smallPicture}}" /></span><span class="userName">{{userName}}</span>';
    $.ajaxSetup({cache:false});
    $.ajax({
        type: 'GET',
        url: "/getSysUserInfoByUserId?token="+_userInfo.token,
        success: function(result) {
            if(result.ifSuc == 1){
                if (result.data.smallPicture==null||result.data.smallPicture==''){
                    result.data.nophoto=1;
                };
                $("#menuTop").html(soda(topHtml,result.data));
            }else{
                //notify('danger',data.msg);
                if(result.code==8||result.code==9||result.code==10){
                    notify('danger',result.msg);
                    window.location.href = result.url;
                    return;
                }
            }
        }
    });
});
//去个人资料页面功能
function personalData() {
    window.location.href = "/setting/personalpage?token="+  _userInfo.token;
}

//退出控制台功能方法
function logoutConsole() {
    window.location.href = "/index.do?token="+  _userInfo.token+"&userId="+_userInfo.userId;
}

//退出登录功能方法
function logout() {
    window.location.href = "/logout";
}