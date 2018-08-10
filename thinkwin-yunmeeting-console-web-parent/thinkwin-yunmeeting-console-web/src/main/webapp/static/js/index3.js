$(function(){
    var userName = $("#userNameId").val();
    $.ajax({
        url : "/logincheck?j_username=" + userName+"&j_password="+userName,
        type : "post",
        success : function(data) {
            //返回值为登陆的状态
            console.log(data);
            $("#submitBtn").removeAttr("disabled");
            if(data.code==1){
                if(data.msg=="查询成功！"){
                    var user = {'userId':data.userId,'token':data.token};
                    localStorage.setItem('console-userinfo',JSON.stringify(user));
                    window.location.href= data.url+"?userId="+data.userId+"&token="+data.token;
                }
            }
        },
        error:function (data) {
            showWarning("#inputPassword","请求异常");
        }
    });
});