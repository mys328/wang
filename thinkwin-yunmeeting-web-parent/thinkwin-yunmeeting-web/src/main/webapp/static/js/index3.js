$(function(){
    var userName = $("#userNameId").val();
    $.ajax({
        url : "/logincheck?j_username=" + userName+"&j_password="+userName,
        type : "post",
        success : function(data) {
            //返回值为登陆的状态
            console.log(data);
            $("#submitBtn").removeAttr("disabled");
            /*if(data.code==1){
                if(data.msg=="查询成功！"){
                    var user = {'userId':data.userId,'token':data.token};
                    localStorage.setItem('userinfo',JSON.stringify(user));
                    window.location.href= data.url+"?userId="+data.userId+"&token="+data.token;
                }
            }*/
            if(data.code !== 0){
                if(data.msg=="查询成功！"){
                    var user = {'userId':data.userId,'token':data.token};
               /*     localStorage.setItem('userinfo',JSON.stringify(user));
                    console.log("登录成功：data.url="+data.url+"?userId="+data.userId+"&token="+data.token);*/
                    var tenantType ={'status':data.tenantType};
                    localStorage.setItem('tenantType',JSON.stringify(tenantType));
                    localStorage.setItem('userinfo',JSON.stringify(user));
                    console.log("登录成功：data.url="+data.url+"?userId="+data.userId+"&token="+data.token);
                    window.location.href= data.url+"?userId="+data.userId+"&token="+data.token;
                }else {
                    console.log("登录失败：data.url="+data.url);
                    window.location.href= data.url;
                }
            }

        },
        error:function (data) {
            showWarning("#inputPassword","请求异常");
        }
    });
});