$(function(){
    window.setTimeout("dingshi();",3000);//定时器 3秒执行提示弹窗

});
function dingshi(){
    $('#removeModal').modal('show');
}
function tiaozhuan() {
    window.location.href="/system/loginpage";
}