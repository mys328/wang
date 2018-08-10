/**
/**
 * Created by dell on 2017/5/28.
 */
$(function(){
    //三秒之后页面跳转
    function jump(count) {
        window.setTimeout(function(){
            count--;
            if(count >=0) {
                $("#count").text(count);
                jump(count);
            } else {
                window.location.href="/system/loginpage";
            }
        }, 1000);
    }
    jump(3);
    $('#backLogin').click(function(){
        window.location.href="/system/loginpage";
    })
})