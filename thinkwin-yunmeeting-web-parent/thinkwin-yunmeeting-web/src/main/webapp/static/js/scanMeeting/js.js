$(".createbth p").click(function () {
	   layer.open({
              content: '<div class="alertDiv"><div class="alert-icon iconfont icon-true"></div><div class="hint">预订成功</div></div>'
              //content: '<div class="alertDiv1"><div class="alert-icon iconfont icon-false"></div><div class="hint">手机号不在当前企业下</div></div>'
              ,skin: 'msg'
              ,shade: 0
              ,time: 2 //2秒后自动关闭
            });
	// location.href='index.html';
})