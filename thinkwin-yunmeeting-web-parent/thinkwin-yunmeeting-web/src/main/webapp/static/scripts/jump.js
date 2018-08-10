
var _userInfo= JSON.parse(localStorage.getItem('userinfo'));


//跳转我审核的
function gotoMeetingScreeningVerify(){
    var type = $('#pageType').val();
    window.location.href="/search/skipMeetingScreeningVerify?token="+  _userInfo.token + "&type=" + type;
}

//跳转我的会议
function gotoMeetingScreening(){
    var type = $('#verifypageType').val();
    if(type==3){
        type = 0;
    }
    window.location.href="/search/skipMeetingScreening?token="+  _userInfo.token + "&type=" + type;
}
//跳转版本介绍页面
function didClickIntroduce(){
    window.open("/commodity/orderExhibition?token="+  _userInfo.token);
}
//跳转升级页面
function didClickUpgrade(){
  window.open("/commodity/getOrderDetail?token="+  _userInfo.token+"&type="+0);
}

//跳转增容页面
function didClickExpansion(){
    window.open("/commodity/getOrderDetail?token="+  _userInfo.token+"&type="+1);
}
//跳转到续费页
function didClickExtend() {
    window.open("/commodity/getOrderDetail?token="+  _userInfo.token+"&type="+2);
}