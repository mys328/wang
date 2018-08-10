var _userInfo = JSON.parse(localStorage.getItem('console-userinfo'));
$(function () {
    $.ajax({
        type: 'GET',
        url: "/getSysUserInfoByUserId?token=" + _userInfo.token,
        success: function (data) {
            if (data.ifSuc == 1) {
                var dataa = data.data;
                var userName = dataa.userName;
                var datainfo = "";
                if (userName.indexOf("管理") > 0) {
                    datainfo += "<span class='nophoto'>管理</span><span>" + userName + "</span>";
                } else {
                    var Abbreviation = userName.substring(userName.length - 2);
                    datainfo += "<span class='nophoto'>" + Abbreviation + "</span><span>" + userName + "</span>";
                }
                $("#menuTop").html(datainfo);
            }/* else {
                notify('danger', data.msg);
            }*/
        }
    });
});


//退出登录功能方法
function logout() {
    window.location.href = "/logout";
}