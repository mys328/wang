'use strict';

$(function () {
  var file;
  var $image = $('#image');
  var imginfo ={};//图片信息
  var userinfo;//个人信息
  var counter;//倒计时函数
  $('.change-photo-btn').on('change', function () {
        file = $(this).get(0).files[0];
        var extStart = file.name.lastIndexOf(".");
        var ext = file.name.substring(extStart, file.name.length).toUpperCase();
        if(ext != ".BMP" && ext != ".PNG" && ext != ".JPEG" && ext != ".JPG"){
            notify('danger','仅支持jpg、jpeg、png、bmp格式');
        }else{
            if (file.size > 5 * 1024 * 1024) {
                notify('danger', '上传的文件大小不能超过5M');
                return false;
            }else{
                $(this).parents('.avatar').find('.btn-change-photo').addClass('show');
                imginfo.size = file.size;
                imginfo.img = file.name;
                var reader = new FileReader();
                reader.readAsDataURL(file);
                reader.onload = function (e) {
                    $image.cropper({
                        aspectRatio: 1 / 1,
                        preview: '.preview'
                    });
                    $image.cropper('replace', e.target.result);
                    $('#changePhoto').collapse('show');
                };
            }
        }
    });
  $('#changePhone,#unbindWeixin').on('show.bs.collapse', function () {
    var id = $(this).attr('id');
    $('a[href="#' + id + '"]').eq(0).hide();
    $(this).parents('.rows').addClass('active');
    var that= $(this).find('.btn-get-code');
    if(that.text()!='获取验证码'){
        clearTimeout(counter);
        that.attr('disabled', false);
        that.addClass('get-code');
        that.text('获取验证码');
    }else{
        that.removeClass('get-code');
    }
  });
  $('#changePhone,#unbindWeixin').on('hidden.bs.collapse', function () {
    var id = $(this).attr('id');
    $('a[href="#' + id + '"]').eq(0).show();
    $(this).parents('.rows').removeClass('active');
  });
  $('#bindWeixin').on('show.bs.collapse', function () {
      $(this).parents('.rows').addClass('active');
  });
  $('#bindWeixin').on('hidden.bs.collapse', function () {
      $(this).parents('.rows').removeClass('active');
  });
  $('#changePhoto').on('show.bs.collapse', function () {
    var parents = $(this).parents('.rows');
    parents.find('.pic').hide();
    parents.find('.help-block').show();
    $(this).parents('.rows').addClass('active');
  });
  $('#changePhoto').on('hidden.bs.collapse', function () {
     var parents = $(this).parents('.rows');
     parents.find('.pic').show();
     parents.find('.help-block').hide();
     $(this).parents('.rows').removeClass('active');
   });
  function countDown(obj, time) {
    if (time == 0) {
      $(obj).attr('disabled', false);
      $(obj).text('重新获取');
    } else {
      $(obj).attr('disabled', true);
      $(obj).text(time + 's');
      time--;
      counter= setTimeout(function () {
        countDown(obj, time);
      }, 1000);
    }
  }
  $('.nameform button').attr('disabled', true);
  //个人信息修改按钮可用
  $('.nameform :input').focus(function () {
    $('.nameform button').attr('disabled', false);
  });
  function rule(type, val) {
    switch (type) {
      case 'required':
        return val.trim() == '' ? '不能为空' : true;
        break;
      case 'captcha':
        return val.trim() == '' ? '验证码不能为空' : !/^\d{6}$/.test(val) ? '验证码格式不正确' : true;
        break;
      case 'phone':
        return val.trim() == '' ? '手机号码不能为空' : !isPhone(val) ? '手机号码格式不正确' : true;
        break;
      case 'email':
        return val.trim() == '' ? '邮箱不能为空' : !isEmail(val)? '邮箱格式不正确' : true;
        break;
      case 'name':
        return val.trim() == '' ? '姓名不能为空' : !isName(val)?'姓名格式不正确' : true;
      default:
        return true;
    }
  }
  function validate($el) {
    var val = $el.val();
    var type = $el.data('validate');
    var msg = rule(type, val);
    if (msg != true) {
      $el.parent('.form-group').addClass('has-danger');
      $el.parents('form').find('p.error-msg').html('<i class="icon icon-error"></i>'+msg);
      return false;
    } else {
      $el.parent('.form-group').removeClass('has-danger');
      $el.parents('form').find('p.error-msg').text('');
      return true;
    }
  }
  //验证邮箱与用户名
  function validate1($el) {
    var val = $el.val();
    var type = $el.data('validate');
    var msg = rule(type, val);
    if (msg != true) {
        $('.nameform .col').removeClass('has-danger');
        $el.parents('form').find('p.error-msg').text('');
        if(msg.indexOf("姓名") != -1){
            $el.parent('.col').addClass('has-danger');
            $el.parents('form').find('p.error-msg-name').html('<i class="icon icon-error"></i>'+msg);
        }else {
            $el.parent('.col').addClass('has-danger');
            $el.parents('form').find('p.error-msg-email').html('<i class="icon icon-error"></i>'+msg);
        }
        return false;
    } else {
        $el.parent('.col').removeClass('has-danger');
        $el.parents('form').find('p.error-msg').text('');
        return true;
    }
  }
/*  $('form[data-toggle="validator"] input[data-validate]').on('input.bs.validator', function () {
    validate($(this));
  });
  input 输入时立即校验
  */
  $('form[data-toggle="validator"]').on('submit ', function (event) {
    event.preventDefault();
    var $this = $(this);
    var $inputs = $(this).find('input[data-validate]');
    var isvalidate = false;
    $.when($inputs.each(function (el) {
      if($this[0].id=="nameform"){
          isvalidate = validate1($(this));
      }else{
          isvalidate = validate($(this));
      }
      return isvalidate;
    })).then(function (ev) {
      if (isvalidate) {
        var data = eval('(' + '{' + decodeURIComponent($this.serialize(), true).replace(/&/g, '",').replace(/=/g, ':"') + '"}' + ')');
        $this.trigger('validator', data);
      }
    });
  });
  //个人设置页面的数据初始化
  getItem();
  function getItem() {
    fetchs.post('/setting/querypersonalinfo', {},function (result) {
      userinfo = result.data;
      if (result.ifSuc == 1) {
        $('#profile .pic span.nophoto').empty();
        if (userinfo.inPicture) {
          $('#profile .pic span.nophoto').css({'background-color':'#fff'});
          $('#profile .nophoto').append('<img style="width:100%; height: 100%;background-color:#fff;vertical-align: bottom;border-radius: 50%;" src="' + userinfo.inPicture+'?attname' + '"/>');
          $('.remove-photo').show();
        }else{
            var nophoto_name= userinfo.userName.substring(userinfo.userName.length-2);
            var bg_color =$('#mainTop>.nophoto>span.nophoto').css('background-color');
            $('#profile .pic span.nophoto').css('background-color',bg_color);
            $('#profile .pic span.nophoto').text(nophoto_name);
            $('#profile .cropper .preview .nophoto').text(nophoto_name);
            $('#profile .cropper .preview .nophoto').css('background-color',bg_color);
        }
        var orgname = userinfo.orgName;
        if(orgname==''||orgname==null){
            orgname = '暂时未分配部门';
        }
        $('#oldPhone').text(userinfo.phoneNumber);
        $('#wechatNumber').text(userinfo.wechat);
        $('#department').text(orgname);
        $('.nameform input[name=username]').val(userinfo.userName);
        $('.nameform input[name=email]').val(userinfo.email);
          if(userinfo.wechat == '' || userinfo.wechat == null){
              $('#wechatNumber').text('您还未绑定微信号');
              $('#wechatNumber').css({ 'color': '#aaa' });
              $('.code-change').text('立即绑定');
              $('.btn-colsed').css({ 'color': '#1896f0' });
              $('.col .code-change').attr('href','#bindWeixin');
              $('.code-change').click(function () {
                  bindWechat();
              });
          }else {
              $('#wechatNumber').text(userinfo.wechat);
              $('#wechatNumber').css({ 'color': '#333' });
              $('.col .code-change').attr('href','#unbindWeixin');
              $('.code-change').text('解除绑定');
          };
      } else {
          if(result.code==-1){
              notify('danger','网络异常，请检查网络后重试');
          }
      }
    });
  }
  //绑定微信
  function bindWechat() {
      var phone = $('#oldPhone').text();
      var data = {'phoneNumber':phone};
      fetchs.post('/setting/bindingoauth',data,function(result){
        if (result.ifSuc==1) {
           $('#bindWeixin .code-img img').attr('src',result.data.qrCodePath);
        }
        $('.code-cancel').show();
        $('.code-change').hide();
      })
  }
  //绑定微信的取消按钮操作
  $('.code-cancel').click(function(){
    $(this).hide();
    $('.code-change').show();
  })
  //获取验证码(更换手机号)
  $('#changePhoneForm .btn-get-code').on('click', function () {
    var _phoneNumber = $('#changePhoneForm input[data-validate=phone]').val();
    var state_phone = rule('phone', _phoneNumber);
    var that = $(this);
    if(_phoneNumber==''){
        $('#changePhone .form-control[name=phone]').parent('.form-group').addClass('has-danger');
        $('#changePhoneForm .error-msg').html('<i class="icon icon-error"></i>'+state_phone);
    }else if (state_phone==true) {
      getCode({ 'phoneNumber': _phoneNumber, 'type': '1' }, function (result) {
        $('#changePhoneForm').find('.form-group').removeClass('has-danger');
        $('#changePhoneForm').find('.error-msg').empty();
        if (result.ifSuc == 1) {
          //开始倒计时,同时清除提示信息和标示
            that.attr('disabled',true);
            countDown(that,60);
        } else if (result.ifSuc == 0) {
          if(result.code==-1){
              notify('danger','获取验证码失败，请检查网络后重试');
          }else if(result.code==6502){
              notify('danger',result.msg);
          }if (result.data) {
              if(result.data.isRegist == 1){
                 that.attr('disabled', false);
                 $('#changePhone .form-control[name=phone]').parent('.form-group').addClass('has-danger');
                 $('#changePhone .error-msg').html('<i class="icon icon-error"></i>手机号已注册');
              }
          }
        }else if (result == 500) {
            $('#changePhone .error-msg').html('<i class="icon icon-error"></i>请求异常');
        };
      });
    } else if (state_phone=='手机号码格式不正确') {
      $(this).attr('disabled', false);
      $('#changePhone .form-control[name=phone]').parent('.form-group').addClass('has-danger');
      $('#changePhone .error-msg').html('<i class="icon icon-error"></i>'+state_phone);
    }
  });
  //更换手机号
  $('#changePhoneForm').on('validator', function (event, data ) {
    var _data = {
      'userId': data.userId,
      'phoneNumber': data.phone,
      'verCode': data.code
    };
    fetchs.post('/setting/changephonenumber', _data, function (result) {
      if (result.ifSuc == '1') {
        notify('success','变更手机号成功');
        $('#oldPhone').text(result.data.phoneNumber);//返回的更改后的新手机号
        $('#changePhoneForm')[0].reset();
        $('#changePhoneForm').find('.form-group').removeClass('has-danger');
        $('#changePhoneForm').find('.error-msg').empty();
        clearTimeout(counter);
        $('#changePhoneForm').find('.btn-get-code').text('获取验证码');
        $('#changePhoneForm').find('.btn-get-code').attr('disabled', false);
        $('#changePhone').collapse('hide');
      } else if (result.ifSuc == '0') {
          $('#changePhone .form-control[name=code]').parent('.form-group').addClass('has-danger');
          $('#changePhone .error-msg').html('<i class="icon icon-error"></i>验证码错误');
      } else {
          if(result.code==-1){
              notify('danger','网络异常，请检查网络后重试');
          }else{
              $('#changePhone .error-msg').html('<i class="icon icon-error"></i>网络异常');
          }
      }
    });
  });
  //表单取消重置
  $('form .text-right .btn-cancel').click(function(){
    $(this).parent().parent('form')[0].reset();
    $(this).parent().parent('form').find('.form-group').removeClass('has-danger');
    $(this).parent().parent('form').find('.error-msg').empty();
    $(this).parent().parent('form').find('.btn-get-code').text('获取验证码');
  });

  $(".well a.cancel-photo").click(function(){
      $('.change-photo-btn').val("");
  });

  //微信表单提交
  $('#changeWechatForm').on('validator', function (event, data) {
    var _phoneNumber = $('#oldPhone').text();
    var _wechatAccount = $('#wechatNumber').text();
    var _data = {
      'phoneNumber': _phoneNumber,
      'verifyCode': data.code,
      'wechatAccount': _wechatAccount
    };
    fetchs.post('/setting/removebinding', _data, function (result) {
      if (result.ifSuc == '1') {
        notify('success', '解绑成功');
        $('#changeWechatForm')[0].reset();
        $('#changeWechatForm').find('.form-group').removeClass('has-danger');
        $('#changeWechatForm').find('.error-msg').empty();
        $('#changeWechatForm').find('.btn-get-code').text('获取验证码');
        $('#unbindWeixin').collapse('hide');
        window.location.reload();
      } else if (result.ifSuc == '0') {
        if (result.code == '6015') {
          //验证码错误
          $('#changeWechatForm .error-msg').html('<i class="icon icon-error"></i>验证码错误');
        }else if(result.code==-1){
            notify('danger','获取验证码失败，请检查网络后重试');
        }else {
            notify('danger', '操作数据库失败');
        }
      };
    });
  });
  //个人信息
  $('#nameform').on('validator', function (event, data) {
    var user_data = {
      'userName': data.username == '' ? userinfo.userName :/^[A-Za-z0-9_\.\·\-\(\)\（\）\u4e00-\u9fa5]+$/.test(data.username)? data.username:userinfo.userName,
      'email': data.email,
      'orgId':userinfo.orgId
    };
    fetchs.post('/setting/updatepersonalinfo', user_data, function (result) {
      if (result.ifSuc == 1) {
          userinfo.userName = result.data.userName;
          var userInfo = JSON.parse(localStorage.getItem('userinfo'));
          if (userInfo) {
              var user = {'userId':userInfo.userId,'token':userInfo.token,'status':userInfo.status,'userName':userinfo.userName};
              localStorage.setItem('userinfo',JSON.stringify(user));
          }
          notify('success','保存成功');
          setTimeout(function(){
              window.location.reload();
          },3000)
      } else {
          notify('danger','请求异常');
      };
    });
  });
  //微信变更获取验证码
  $('#changeWechatForm .btn-get-code').click(function () {
    var _phoneText = $('#oldPhone').text();
    var that =  $(this);
    getCode({ 'phoneNumber': _phoneText, 'type': '2' }, function (result) {
      if (result.ifSuc == 1) {
        //开始倒计时,同时清除提示信息和标示
        $('#changeWechatForm .error-msg').hide();
        that.attr('disabled', true);
        countDown(that, 60);
      }else {
        if(result.code==-1){
            notify('danger','获取验证码失败，请检查网络后重试');
        }else{
            $('#changeWechatForm .error-msg').html('<i class="icon icon-error"></i>请求异常');
        }
      };
    });
  });
    //更换头像
    $('.btn-save-photo').on('click', function () {
        var croppedCanvas = $image.cropper('getCroppedCanvas',{
            width: 200,
            height: 200,
            minWidth: 100,
            minHeight: 100,
            maxWidth: 400,
            maxHeight: 400,
            imageSmoothingEnabled: false,
            imageSmoothingQuality: 'high'
        });
        imginfo.fileName=croppedCanvas.toDataURL('image/png');
        var strlength = imginfo.fileName.substring(23).length;
        imginfo.size=parseInt(strlength-(strlength/8)*2);
        fetchs.post('/setting/changephoto',imginfo, function (result) {
            if(result.ifSuc==1){
                window.location.reload();
            }else if(result.ifSuc==0){
                if(result.code==-1){
                    notify('danger','头像更新失败，请重试!');
                }else{
                    notify('danger',result.msg);
                }
            }else{
                notify('danger',result.msg);
            }
        });
    });
    $('body').on('click','.remove-photo',function () {
        fetchs.post('/setting/deletephoto',{'remove':'1'},function (result) {
            if(result.ifSuc==1){
                window.location.reload();
            } else {
                notify('danger',result.msg);
            }
        })
    })
  //获取验证码
  function getCode(data, callBack) {
    fetchs.post('/system/getverifycode', data, function (_data) {
      callBack(_data);
    });
  };
  //获取绑定状态刷新页面
  var get_bind_state=setInterval(function() {
      getState()
      },2000);
  getState();
  function getState() {
    fetchs.post('/setting/querywechatbindingstatus',{},function (res) {
        if(res.ifSuc==1){
            if(res.data!=0){
                clearInterval(get_bind_state);
                $('#wechatNumber').css({ 'color': '#333' });
                $('#wechatNumber').text(res.data);
                $("#profile .rows").eq(2).removeClass('active');
                $('.collapse').removeClass('show');
                $('.col .code-change').attr('href','#unbindWeixin');
                $('.code-change').text('解除绑定');
                $('.code-cancel').hide();
           }
       }
    })
  }
});
