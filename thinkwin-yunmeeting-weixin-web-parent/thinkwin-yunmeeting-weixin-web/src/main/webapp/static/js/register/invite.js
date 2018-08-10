var openId=$("#openId").val();
var tenantId =$("#tenantId").val();
var tenantName =$("#tenantName").val();
$('#confirmbtn').attr('disabled',"true");
//obj.tenantName
//验证码页面js
 $(".ipt-real-nick").focus(function () {
 	var len= $(".ipt-real-nick").val().length;
 	if(len==6){
 		len=5;
 	}
 	$(".ipt-fake-box input").eq(len).css("border","2px solid #4cc2fd");
	$(".ipt-fake-box input").eq(len).siblings().css("border","2px solid #e1e1e1");
 })
  $(".ipt-real-nick").blur(function () {
  	$(".ipt-fake-box input").css("border","2px solid #e1e1e1");
  })
	 $(".ipt-real-nick").on("input", function() {
	 var $input = $(".ipt-fake-box input");
	 if(!$(this).val()){//无值光标顶置
	 $('.ipt-active-nick').css('left',$input.eq(0).offset().left-parseInt($('.ipt-box-nick').parent().css('padding-left'))+'px');
	 }
	 if(/^[0-9]*$/g.test($(this).val())){//有值只能是数字
	 var pwd = $(this).val().trim();
	 for (var i = 0, len = pwd.length; i < len; i++) {
	 $input.eq(i).val(pwd[i]);
  	 //("border","2px solid #e1e1e1;");
	 if($input.eq(i).next().length){//模拟光标，先将图片容器定位，控制left值而已
	  $('.ipt-active-nick').css('left',$input.eq(i+1).offset().left-parseInt($('.ipt-box-nick').parent().css('padding-left'))+'px');
	 }
	 }
	 $input.each(function() {//将有值的当前input后的所有input清空
	 var index = $(this).index();
	 if (index >= len) {
	  $(this).val("");
	 }
	 });
	$input.eq(len).css("border","2px solid #4cc2fd");
	$input.eq(len).siblings().css("border","2px solid #e1e1e1");
		 if (len == 6) {
		 //执行其他操作
			$("#confirmbtn").addClass("sign").removeClass("forbidden");
			$('#confirmbtn').removeAttr("disabled"); //移除disabled属性 
		 }else{
		 	$("#confirmbtn").addClass("forbidden").removeClass("sign");
		 	$('#confirmbtn').attr('disabled',"true");//添加disabled属性 
		 }
	 }
	 });

 	$(".inviteCode #confirmbtn").click(function () {
 		if ($('.ipt-real-nick').val().length!=6){
			$('.invitationerrorDiv span').html("邀请码错误，请确认后重新输入");
			$(".invitationerrorDiv").animate({marginLeft: 0, opacity: '1'}, 200);
 		}else{
 			$.ajax({
					url:"/wechat/wxRegister/checkInviteCode",
					type:"post",
					data:{openId:openId,tenantId:tenantId,code:$('.ipt-real-nick').val()},
					success:function (e) {
						 if(e.ifSuc==1){										 	
							window.location.href="/wechat/wxRegister/invitePage?openId="+openId+"&tenantId="+tenantId;
						 }else{
							$('.invitationerrorDiv span').html("邀请码错误，请确认后重新输入");
							$(".invitationerrorDiv").animate({marginLeft: 0, opacity: '1'}, 200);
						 }
					}
			});
		
 		}
		
	})

	$(".inviteCode input").focus(function () {
		$(".invitationerrorDiv").animate({marginLeft: 30, opacity: '0'}, 200);
	});
//用户登录页面js

$(".departmentDiv p").html(tenantName);
	//边框高亮状态
	$(".invite :text").focus(function () {
		$(this).parent().css({
		  "box-shadow":"0 0 6px 0 rgba(71,198,252,0.50)",
		  "border":"1px solid #caecfd"
		  })
		$(".errorAlert").animate({marginLeft: 30, opacity: '0'}, 200);
	});

	$(".invite :text").blur(function () {
		$(this).parent().css({
		  "box-shadow":"none",
		  "border":"1px solid #e2e7ee"
		  })
	});

	$(".invite .sign").click(function () {
        var reg =/^[A-Za-z0-9_\.\·\-\(\)\（\）\u4e00-\u9fa5]+$/;
        var reg1=/^(13[0-9]|14[5-9]|15[012356789]|166|17[0-8]|18[0-9]|19[8-9])[0-9]{8}$/;
        //验证密码格式
        var pwdReg = /^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,20}$/;//8到16位数字与字母组合
		if($(".nameInput").val()==""||$(".phoneInput").val()==""||$(".verifyInput").val()==""||$(".passwordInput").val()==""){
			if($(".nameInput").val()==""){
				$(".nameDiv").css({"border":"1px solid #f74555"})
				$(".errorAlert span").html('姓名不能为空');
			}
			if($(".phoneInput").val()==""){
				$(".phoneDiv").css({"border":"1px solid #f74555"})
				$(".errorAlert span").html('手机号不能为空');
			}
			if($(".verifyInput").val()==""){
				$(".verifyDiv").css({"border":"1px solid #f74555"})
				$(".errorAlert span").html('验证码不能为空');
			}
            if($(".passwordInput").val()==""){
                $(".pwdDiv").css({"border":"1px solid #f74555"})
                $(".errorAlert span").html('密码不能为空');
            }
			$(".errorAlert").animate({marginLeft: 0, opacity: '1'}, 200);
		}
		else if(!reg1.test($(".phoneInput").val())){
				$(".errorAlert span").html('手机号码格式不正确');
				$(".errorAlert").animate({marginLeft: 0, opacity: '1'}, 200);
				$(".phoneDiv").css({"border":"1px solid #f74555"})
				}
		else{
			if(!reg.test($(".nameInput").val())){
				$(".nameDiv").css({"border":"1px solid #f74555"})
				$(".errorAlert span").html('姓名格式不正确');
				$(".errorAlert").animate({marginLeft: 0, opacity: '1'}, 200);
			}else{
				if(!pwdReg.test($(".passwordInput").val())){
                    $(".pwdDiv").css({"border":"1px solid #f74555"})
                    $(".errorAlert span").html('密码格式不正确');
                    $(".errorAlert").animate({marginLeft: 0, opacity: '1'}, 200);
				}else {
                    $.ajax({
                        url:"/wechat/wxRegister/invite",
                        type:"post",
                        data:{openId:openId,tenantId:tenantId,tenantName:tenantName,phoneNumber:$(".phoneInput").val(),code:$(".verifyInput").val(),userName:$(".nameInput").val(),password:$(".passwordInput").val()},
                        success:function (e) {
                            if(e.ifSuc==1){
                                window.location.href="/wechat/wxRegister/inviteSuccessPage?openId="+openId+"&tenantId="+tenantId;
                            }else{
								if(e.msg=='验证码错误'){
									$(".verifyDiv").css({"border":"1px solid #f74555"});
								}
								if(e.msg=='手机号已注册'){
									$(".phoneDiv").css({"border":"1px solid #f74555"});
								}
                                $(".errorAlert span").html(e.msg);
                                $(".errorAlert").animate({marginLeft: 0, opacity: '1'}, 200);
                            }
                        }
                    })
				}

			}
		}
})
	//验证码发送事件
	var wait=60; 
	var phoneInput=$(".phoneInput").val();
	function Countdown() {
	 	var o = $("#bth").get(0);
				if (wait == 0) { 
					o.removeAttribute("disabled");	
					o.value="重新获取"; 
					wait = 60;
					$("#bth").removeClass("jin").addClass("bth");
				} else { 
					$("#bth").removeClass("bth").addClass("jin");
					o.setAttribute("disabled", true); 
					o.value=wait+"s";
					wait--; 
					setTimeout(function() { 
						Countdown();
					}, 
					1000);
				} 
	}

	$(".invite #bth").click(function () {
		var reg=/^1[34578]\d{9}$/;
		if($(".phoneInput").val()==''){
			$(".errorAlert span").html('手机号不能为空');
			$(".errorAlert").animate({marginLeft: 0, opacity: '1'}, 200);
			$(".phoneDiv").css({"border":"1px solid #f74555"})
		}
		else if(!reg.test($(".phoneInput").val())){
			$(".errorAlert span").html('手机号码格式不正确');
			$(".errorAlert").animate({marginLeft: 0, opacity: '1'}, 200);
			$(".phoneDiv").css({"border":"1px solid #f74555"})
		}
		else{
				$.ajax({
				url:"/wechat/wxRegister/getverifycode",
				type:"post",
				data:{phoneNumber:$(".phoneInput").val(),type:0},
				success:function (e) {
					 if(e.ifSuc==1){										 	
							Countdown();
						 }else{
							$(".errorAlert span").html(e.msg);
							$(".errorAlert").animate({marginLeft: 0, opacity: '1'}, 200);
							$(".phoneDiv").css({"border":"1px solid #f74555"})
						 }
						}
					})
		}
	})

