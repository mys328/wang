/**
 * Created by dell on 2017/7/10.
 */
var _userInfo= JSON.parse(localStorage.getItem('userinfo'));

$(function(){
    $("#save").click(function(){
        creatCompanyForm();
    })
    $("#inputCompany").on("focus",function () {
        $("#save").attr("disabled",false);
    });
    function creatCompanyForm(){
        $("#warmLabel").html("");
        var _companyName=$("#inputCompany").val();
        if(_companyName==''){
            showWarning("#inputCompany","公司名称不能为空");
        }else{
            if(!checkComName(_companyName)){
                showWarning("#inputCompany","公司名称不能含有特殊字符");
            }else if($("#warmLabel").html()==""){
                hideWarning("#inputCompany");
                $('.warningBox').hide();
                $("#save").attr("disabled",true);
                //当本地所有参数都校验通过的时候再调用接口
                var wechatId= $("#uid").val();
                var user_Id = $('#userId').val();
                var userIdd = null;
                if(user_Id){
                    userIdd = user_Id
                }else{
                    if(null != _userInfo){
                        var userId = _userInfo.userId;
                        if(null != userId){
                            userIdd = userId;
                        }
                    }
                }
                creatCompany({"tenantName":_companyName,"uId":wechatId,"userId":userIdd},function(data){
                    if(data.ifSuc==1){//请求成功 执行跳转成功页面
                        $("#save").removeAttr("disabled");
                        window.location.href = '/system/successpage?businessType=3';
                    }else{
                        showWarning("#inputCompany",data.msg);
                    }
                })
            }
        }
    }
})