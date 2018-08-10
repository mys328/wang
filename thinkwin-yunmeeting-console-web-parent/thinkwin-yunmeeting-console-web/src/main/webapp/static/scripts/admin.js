'use strict';

$(function () {
    //获取用户信息 userId,token
    var _userInfo = JSON.parse(localStorage.getItem('console-userinfo'));
    if (_userInfo == null) {
        _userInfo = {
            userId: '123',
            token: '123'
        };
    };
    var imginfo ={};//图片信息
    var roommangers = new Array(); //会议室管理员
    var commissioners = new Array(); //会议室预定专员
    var sonmangers = new Array(); //子管理员
    var initialSon = new Array(); //初始化的子管理员
    var initialCom = new Array(); //初始化的会议室预定专员
    var initialRoom = new Array(); //初始化的会议室管理员
    var sysPhoneNum = '';
    var cominfo = '';
    console.log(_userInfo);

    soda.prefix('ng-');
    // $.ajaxSettings.traditional = true; //表单数据序列化
    var $image = $('#image');

    $image.cropper({
        aspectRatio: 1 / 1,
        preview: '.preview',
        crop: function crop(e) {}
    });
    $('.btn-select-photo input').on('change', function () {
        var file = $(this).get(0).files[0];
        if (file==null){
            return;
        }
        if (["image/jpg","image/png","image/jpeg","image/bmp"].indexOf(file.type)<0){
            notify('danger','只能选择jpg、png、jpeg、bmp格式的图片')
            return;
        }
        if (file.size > 5 * 1024 * 1024) {
            notify('danger', '上传的文件大小不能超过5M');
            return false;
        }
        $(this).parents('.logo').hide();
        $('#selectPhoto').collapse('show');
        imginfo.size = file.size;
        imginfo.img = file.name;
        var reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = function (e) {
            imginfo.fileName = e.currentTarget.result;
            $image.cropper('replace', e.target.result);
        };
    });
    $('.btn-change-photo input').on('change', function () {
        var file = $(this).get(0).files[0];
        if (["image/jpg","image/png","image/jpeg","image/bmp"].indexOf(file.type)<0){
            notify('danger','只能选择jpg、png、jpeg、bmp格式的图片')
            return;
        }
        if (file.size > 5 * 1024 * 1024) {
            notify('danger', '上传的文件大小不能超过5M');
            return false;
        }
        console.log(file);
        var reader = new FileReader();
        reader.readAsDataURL(file);
        reader.onload = function (e) {
            console.log(e);
            $image.cropper('replace', e.target.result);
        };
    });
    $('#selectPhoto').on('hidden.bs.collapse', function () {
        $(this).prev('.logo').show();
    });

    $('a[data-toggle="tab"]').on('shown.bs.tab', function (e) {
        if ($(e.target).attr('href') == '#home') {
            getcompanytype();
            getCompanyInfo();
        } else if ($(e.target).attr('href') == '#profile') {
            getsettingPersons();
        }
        console.log($(e.target).attr('href'));
        // console.log(e.relatedTarget);
    });

    // $('#home').on('click', function(){
    //     getCompanyInfo();
    // })
    // $('#profile').on('click', function(){
    //     getsettingPersons();
    // })

    getcompanytype();
    getCompanyInfo();
    //获取企业信息
    function getCompanyInfo() {
        fetchs.post('/setting/querycompanyinfo',{userId:_userInfo.userId,token:_userInfo.token}, function (res) {
            cominfo = res.data;
            console.log(res);
            if (res.data.companyLogo!=null&&res.data.companyLogo.length!=0){
                $('#logo').attr('src', res.data.companyLogo);
            }
            $('#comname').val(res.data.tenantName);
            if (res.data.companyType!=null&&res.data.companyType.length!=0){
                $('#type-name').text(res.data.companyType);
            }else {
                $('#type-name').text('行业类型');
            }
            $('#comaddress').val(res.data.companyAddress);
            $('#description').val(res.data.companyDescription);
            $('#rcode').attr('src', res.data.qrcodePath);
            $('#inviteCode').val(res.data.companyInvitationCode);
            var optionlist = $('.company-type').children();
            $.each(optionlist,function (i,el) {
                if($(this).data('title') == $('#type-name').text()){
                    $(this).addClass('select');
                }
            });
            $("#savecomInfo").attr('disabled', true);
        });
    }
    //查询企业类型
    function getcompanytype() {
        fetchs.get('/setting/selecttenanttype?userId='+_userInfo.userId+'&token='+_userInfo.token, function (res) {
            $('.company-type').html(soda(dropdown, { data: res.data }));

            if (cominfo){
                if (cominfo.companyType!=null&&cominfo.companyType.length!=0){
                    $('#type-name').text(cominfo.companyType);
                }else {
                    $('#type-name').text('行业类型');
                }
                var optionlist = $('.company-type').children();
                $.each(optionlist,function (i,el) {
                    if($(this).data('title')== $('#type-name').text()){
                        $(this).addClass('select');
                    }
                })
            }

        });
    }
    var option = '\n        <option  ng-repeat="item in data">{{item.dictName}}\n        </option>\n         ';
    var dropdown = '\n <a ng-repeat="item in data" class="dropdown-item type-item" data-title="{{item.dictName}}">{{item.dictName}} \n </a>\n'
    // $('.dropdown-item').on('click',function (e) {
    //     $('.dropdown-item').removeClass('select');
    //     $(this).addClass('select');
    //     $('#comtype').val($(this).data('title'));
    // });
    $('body').on('click', '.dropdown-item', function (e) {
        console.log("点击了");
        $('.dropdown-item').removeClass('select');
        $(this).addClass('select');
        $('#type-name').text($(this).data('title'));
        var name = $('#comname').val();
        var type = $('#type-name').text();
        var address = $('#comaddress').val();
        var description = $('#description').val();
        if (($.trim(type)==$.trim(cominfo.companyType)) && ($.trim(name)==$.trim(cominfo.tenantName)) && ($.trim(address)==$.trim(cominfo.companyAddress)) && ($.trim(description)==$.trim(cominfo.companyDescription))){
            $("#savecomInfo").attr('disabled', true);
        }else {
            $("#savecomInfo").attr('disabled', false);
        }
    });
    //获取管理员信息
    function getsettingPersons() {
        fetchs.get('/management_setting_persons?userId='+_userInfo.userId+'&token='+_userInfo.token, function (res) {
            if (res.ifSuc==0){
                notify('danger', res.msg);
                return;
            }
            var mainAdmin = '';
            if (res.data){
                for (var i = 0;i < res.data.mainAdministrator.length; i++) {
                    if (res.data.mainAdministrator[i] != null) {
                        mainAdmin = res.data.mainAdministrator[i];
                        break;
                    }
                }
                var str = mainAdmin.userName + '  ' + mainAdmin.phoneNumber;
                sysPhoneNum = mainAdmin.phoneNumber;
                $('#mainAdmin').html(str);
                $('#phone').html("系统会向" +mainAdmin.phoneNumber + "发送验证码");
                $('.tags').children().not('.btn-add').each(function () {
                    $(this).remove();
                })
                var tempArr = [];

                for(var k=0;k<res.data.sonAdministrator.length;k++){
                    if (res.data.sonAdministrator[k]!=null){
                        tempArr.push(res.data.sonAdministrator[k]);
                    }
                }
                for(var i=0;i<tempArr.length;i++){
                    sonmangers.push(tempArr[i].id);
                }
                initialSon = tempArr;
                $('#sonAdministrator span.icon-addimg').before(soda(tpl, {data:tempArr}));
                var tempArr1 = [];
                for(var i=0;i<res.data.commissioners.length;i++){
                    if (res.data.commissioners[i]!=null){
                        tempArr1.push(res.data.commissioners[i])
                    }
                }
                for(var i=0;i<tempArr1.length;i++){
                    commissioners.push(tempArr1[i].id);
                }
                initialCom = tempArr1;
                $('#commissioners span.icon-addimg').before(soda(tpl,  {data:tempArr1}));
                var tempArr2 = [];
                for(var i=0;i<res.data.boardroommanagers.length;i++){
                    if (res.data.boardroommanagers[i]!=null){
                        tempArr2.push(res.data.boardroommanagers[i])
                    }
                }
                for(var i=0;i<tempArr2.length;i++){
                    roommangers.push(tempArr2[i].id);
                }
                initialRoom = tempArr2;
                $('#boardroommanagers span.icon-addimg').before(soda(tpl, {data:tempArr2}));
                if(tempArr.length>=3){
                    $('#sonAdministrator span.icon-addimg').hide();
                }else {
                    $('#sonAdministrator span.icon-addimg').show();
                }
            }

        });
    }

    var tpl = '\n        <span class="person" ng-repeat="item in data" data-id="{{item.id}}">{{item.userName}}\n            <i class="icon icon-close"></i>\n        </span>\n    ';

    //更换头像
    $('#changeLogo').on('click', function (event) {
        event.preventDefault();
        var croppedCanvas = $image.cropper('getCroppedCanvas');
        console.log(croppedCanvas);
        imginfo.fileName=croppedCanvas.toDataURL('image/jpeg');
        imginfo.token=_userInfo.token;
        console.log(imginfo);
        $('.result').attr('src', croppedCanvas.toDataURL('image/jpeg'));
        fetchs.post('/setting/changelogo',imginfo, function (data) {
            if (data.ifSuc==0){
                //失败
                notify('danger','头像更换失败');
            }
            console.log(data);
            // body...
           window.location.reload();
        });
        // var croppedCanvas = $image.cropper('getCroppedCanvas');
        // $('.result').attr('src', croppedCanvas.toDataURL('image/jpeg'));
        // $.post('/api/upload', {
        //     base64: croppedCanvas.toDataURL('image/jpeg')
        // }, function (data) {
        //     console.log(data);
        //     // body...
        // });
        //将图片base64转码
        // var urlData = croppedCanvas.toDataURL('image/jpeg');
        // var _fileName =  convertBase64UrlToBlob(urlData);//.size
        // console.log(_fileName);
        // var _data=  {
        //   'userId':_userInfo._userId,
        //   'tenantId':_userInfo._userId,
        //   'fileName':_fileName.size,
        //   'token':_userInfo._token
        // };
        // console.log($('.btn-select-photo input')[0].value);
        // //上传图片
        // $.post('http://localhost:8080/setting/changelogo',_data,function(data){
        //     console.log(data);
        // });
        // $image.cropper('replace', '../docs/images/picture-2.jpg');
    });
    //刷新邀请码
    $('#icon-refresh').on('click', function () {
        fetchs.post('/setting/refreshinvitecode',{userId:_userInfo.userId,token:_userInfo.token}, function (res) {
            // body...
            $('#inviteCode').val(res.data.inviteCode);
        });
        // var _data={
        //   'userId':_userInfo._userId,
        //   'tenantId':'a5578cd5e1e844b98910093f05221c6f',
        //   'token':_userInfo._token
        // }
        // $.post('http://localhost:8080/setting/refreshinvitecode',_data,function(datas){
        //   console.log(data);
        //    // console.log(datas.data.inviteCode);
        //     //$(".invite-code input").val(datas.data.inviteCode);
        // })
    });

    //input内容改变
    $('#companyForm input.form-control').on('input',function () {
        // alert(1);
        var name = $('#comname').val();
        var type = $('#type-name').text();
        var address = $('#comaddress').val();
        var description = $('#description').val();
        if (($.trim(name)==$.trim(cominfo.tenantName)) && ($.trim(type)==$.trim(cominfo.companyType)) && ($.trim(address)==$.trim(cominfo.companyAddress)) && ($.trim(description)==$.trim(cominfo.companyDescription))){
            $("#savecomInfo").attr('disabled', true);
        }else {
            $("#savecomInfo").attr('disabled', false);
        }
    });
    $('#description').on('input',function () {
        var name = $('#comname').val();
        var type = $('#type-name').text();
        var address = $('#comaddress').val();
        var description = $('#description').val();
        if (($.trim(name)==$.trim(cominfo.tenantName)) && ($.trim(type)==$.trim(cominfo.companyType)) && ($.trim(address)==$.trim(cominfo.companyAddress)) && ($.trim(description)==$.trim(cominfo.companyDescription))){
            $("#savecomInfo").attr('disabled', true);
        }else {
            $("#savecomInfo").attr('disabled', false);
        }
    });
    //保存企业信息
    $('#savecomInfo').on('click', function (event) {
        event.preventDefault();
        var name = $('#comname').val();
        var type = $('#type-name').text();
        var address = $('#comaddress').val();
        var description = $('#description').val();
        if (type.length == 0){
            $('#comtype').css('border-color', 'red');
            $('#error').show();
            $('#errormsg').html('请选择行业类型');
            return;
        }

        var ifSuc = checkName(name);
        if (!ifSuc) {
            return;
        }
        if ($.trim(name)==$.trim(cominfo.tenantName)){
            name = '';
        }
        $('#comname').css('border-color', 'rgba(0, 0, 0, 0.15)');
        $('#error').hide();
        fetchs.post('/setting/updatecompanyinfo', { 'userId': _userInfo.userId, 'tenantName': name, 'companyType': type, 'companyAddress': address, 'companyDescription': description, 'token': _userInfo.token }, function (res) {
            if (res.ifSuc==0) {
                //公司名字已存在
                $('#comname').css('border-color', 'red');
                $('#error').show();
                $('#errormsg').html(msgForCode(res.code));
            } else if(res.ifSuc==1) {
                notify('success', '修改成功');
                // rgba(0, 0, 0, 0.15);
                $('#comname').css('border-color', 'rgba(0, 0, 0, 0.15)');
                $('#error').hide();
                // $('#logo').attr('src', res.data.companyLogo);
                $('#comname').val(res.data.tenantName?res.data.tenantName:cominfo.tenantName);
                var type = $('#type-name').text();
                $('#comaddress').val(res.data.companyAddress);
                $('#description').val(res.data.companyDescription);
                $("#savecomInfo").attr('disabled', true);
                // $('#rcode').attr('src', res.data.qrcodePath);
                // $('#inviteCode').val(res.data.companyInvitationCode);
            }
        });
    });
    //添加会议室管理员
    function addroommanger(arr,e) {
        fetchs.get('/addboardroommanager?userId=' + _userInfo.userId + '&managerIds=' + arr + '&token=' + _userInfo.token, function (res) {
            if (res.data == false) {
                notify('danger', res.msg);
                $('#boardroommanagers').children().not('.btn-add').each(function () {
                    $(this).remove();
                })
                $('#boardroommanagers span.icon-addimg').before(soda(tpl, {data:initialRoom}));
            } else {
                // notify('success', res.msg);
            }
        });
    }
    //删除会议室管理员
    function delroommanger(id,e) {
        var arr = new Array();
        arr.push(id);
        fetchs.get('/delboardroommanager?userId=' + _userInfo.userId + '&moveIds=' + arr + '&token=' + _userInfo.token, function (res) {
            if (res.data == false) {
                notify('danger', res.msg);
                $('#boardroommanagers').children().not('.btn-add').each(function () {
                    $(this).remove();
                })
                $('#boardroommanagers span.icon-addimg').before(soda(tpl, {data:initialRoom}));
            } else {
                // notify('success', res.msg);
            }
            // getsettingPersons()
        });
    }
    //添加会议室预订员
    function addcommissioner(arr,e) {
        fetchs.get('/addcommissioner?userId=' + _userInfo.userId + '&commissionerIds=' + arr + '&token=' + _userInfo.token, function (res) {
            if (res.data == false) {
                notify('danger', res.msg);
                $('#commissioners').children().not('.btn-add').each(function () {
                    $(this).remove();
                })
                $('#commissioners span.icon-addimg').before(soda(tpl, {data:initialCom}));
            } else {
                // notify('success', res.msg);
            }
            // getsettingPersons()
        });
    }
    //删除会议室预订员
    function delcommissioner(id,e) {
        var arr = new Array();
        arr.push(id);
        fetchs.get('/delcommissioner?userId=' + _userInfo.userId + '&commissionerIds=' + arr + '&token=' + _userInfo.token, function (res) {
            if (res.data == false) {
                notify('danger', res.msg);
                $('#commissioners').children().not('.btn-add').each(function () {
                    $(this).remove();
                })
                $('#commissioners span.icon-addimg').before(soda(tpl, {data:initialCom}));
            } else {
                // notify('success', res.msg);
            }
            // getsettingPersons();
        });
    }
    //添加子管理员
    function addsonmanger(arr,e) {
        fetchs.get('/addSonManager?userId=' + _userInfo.userId + '&sonList=' + arr + '&token=' + _userInfo.token, function (res) {
            if (res.data == false) {
                notify('danger', res.msg);
                $('#sonAdministrator').children().not('.btn-add').each(function () {
                    $(this).remove();
                })
                $('#sonAdministrator span.icon-addimg').before(soda(tpl, {data:initialSon}));
            } else {
                // notify('success', res.msg);
            }
            // getsettingPersons();
        });
    }
    //删除子管理员
    function delsonmanger(id,e) {

        fetchs.get('/delSonManager?userId=' + _userInfo.userId + '&sonUserId=' + id + '&token=' + _userInfo.token, function (res) {
            if (res.data == false) {
                notify('danger', res.msg);
                $('#sonAdministrator').children().not('.btn-add').each(function () {
                    $(this).remove();
                })
                $('#sonAdministrator span.icon-addimg').before(soda(tpl, {data:initialSon}));

            } else {
                // notify('success', res.msg);
                sonmangers.splice(id,1);
            }
            if(sonmangers.length>=3){
                $('#sonAdministrator span.icon-addimg').hide();
            }else {
                $('#sonAdministrator span.icon-addimg').show();
            }
            // getsettingPersons();
        });
    }

    $('#comname').bind('input propertychange', function () {
        checkName($(this).val());
        // var patt = new RegExp('[`~!@#$^&*=|{}\':;\',\\[\\].<>/?~@#￥……&*——|{}【】‘；：”“\'。，、？%+_`¡™£¢∞§¶•ªº–≠œ∑´º®†¥¨ˆøπ“‘«åß∂ƒ©˙∆˚¬…æΩ≈ç√∫˜µ≤≥÷]');
        // if (patt.test($(this).val())) {
        //     $('#comname').css('border-color', 'red');
        //     $('#error').show();
        //     $('#errormsg').html('公司名称不能含有特殊字符')
        // }else {
        //     $('#comname').css('border-color', 'rgba(0, 0, 0, 0.15)');
        //     $('#error').hide();
        // }
        // console.log($(this).val());
        // console.log(patt.test($(this).val()))
        // $('#result').html($(this).val().length + ' characters');  
    });
    //验证公司名
    function checkName(name) {
        if (name.length==0){
            $('#comname').css('border-color', 'red');
            $('#error').show();
            $('#errormsg').html('公司名称不能为空');
            return false;
        }
        var patt = new RegExp('[`~!@#$^&*=|{}\':;\',\\[\\].<>/?~@#￥……&*——|{}【】‘；：”“\'。，、？%+_`¡™£¢∞§¶•ªº–≠œ∑´º®†¥¨ˆøπ“‘«åß∂ƒ©˙∆˚¬…æΩ≈ç√∫˜µ≤≥÷]');
        if (patt.test(name)) {
            $('#comname').css('border-color', 'red');
            $('#error').show();
            $('#errormsg').html('公司名称不能含有特殊字符');
            return false;
        } else {
            $('#comname').css('border-color', 'rgba(0, 0, 0, 0.15)');
            $('#error').hide();
            return true;
        }
    }
    $('#code1').on('click', function (event) {
        console.log($(this));
        settime($(this));
        getCode(sysPhoneNum,2);
    });
    //下一步操作
    $('#next').on('click', function (event) {
        //判断验证码有没有输入
        var code = $('#input-code1').val();
        if (code.length == 0) {
            $('.error').show();
            $('.msg').text("验证码不能为空");
            $('#input-code1').addClass('error-box');
            return;
        }
        $('#input-code1').removeClass('error-box');
        $('.error').hide();
        checkCode(sysPhoneNum, code);
    });
    $('#code2').on('click', function (event) {
        //取到手机号
        var phoneNum = $('#phoneNum').val();
        if (phoneNum.length == 0) {
            $('.error').show();
            $('.msg').text("手机号不能为空");
            $('#phoneNum').addClass('error-box');
            return;
        }
        if(!isPhone(phoneNum)){
            $('.error').show();
            $('.msg').text("手机号格式不正确");
            $('#phoneNum').addClass('error-box');
            return;
        }
        $('.error').hide();
        $('#phoneNum').removeClass('error-box');
        console.log($(this));

        getCode(phoneNum,3);
    });
    //关闭
    $('.close').on('click', function () {
        reset();
    })
    //提交手机号
    $('#submit-phoneNum').on('click', function () {
        //手机号为空
        var phoneNum = $('#phoneNum').val();
        if (phoneNum.length == 0) {
            $('.error').show();
            $('.msg').text("手机号不能为空");
            $('#phoneNum').addClass('error-box');
            return;
        }
        if (!isPhone(phoneNum)){
            //手机号格式
            $('.error').show();
            $('.msg').text("手机号格式不正确");
            $('#phoneNum').addClass('error-box');
            return;
        }
        $('#phoneNum').removeClass('error-box');
        //验证码为空
        var code = $('#input-code2').val();
        if (code.length == 0) {
            $('.error').show();
            $('.msg').text("验证码不能为空");
            $('#input-code2').addClass('error-box');
            return;
        }
        $('.error').hide();
        $('#input-code2').removeClass('error-box');
        changeadmin(phoneNum,code);
    });
    //手机号格式
    //验证输入内容是否是手机号
    function isPhone (string) {
        var pattern = /^1[34578]\d{9}$/;
        if (pattern.test(string)) {
            return true;
        }
        return false;
    };
    $('.btn-clear-secondary').on('click', function () {
        reset();
    });
    function reset() {
        $('#alterModal').hide();
        $('#alterModal').removeClass('show');
        $('.modal-backdrop').remove();
        $('.error').hide();
        $('#input-code1').val('');
        $('#input-code2').val('');
        $('#input-code1').removeClass('error-box');
        $('#input-code2').removeClass('error-box');
        $('#code1').text("获取验证码");
        $('#code2').text("获取验证码");
        $('#code1').attr('disabled', false);
        $('#code2').attr('disabled', false);
        $('#phoneNum').removeClass('error-box');
        $('#phoneNum').val('');
        $('#step2').removeClass('active');
        $('#step1').addClass('active');
    }

    //获取验证码
    function getCode(phoneNumber,type) {
        fetchs.post('/system/getverifycode',{phoneNumber:phoneNumber,type:type},function (res) {
            if(res.ifSuc==0){
                //验证码发送失败
                $('.error').show();
                $('.msg').text(msgForCode(res.code));
            }else {
                if (type==3){
                    settime($('#code2'));
                }

            }
        })
        // fetchs.get('/system/getverifycode?phoneNumber=' + phoneNumber + '&type=0', function (res) {
        //
        // });
    }
    //校验验证码
    function checkCode(phoneNumber, code) {
        fetchs.post('/system/checkverifycode',{phoneNumber:phoneNumber,verifyCode:code},function (res) {
            if(res.ifSuc==1){
                $('.error').hide();
                $('#input-code1').removeClass('error-box');
                $('#step1').removeClass('active');
                $('#step2').addClass('active');
            }else {
                $('.error').show();
                $('.msg').text(msgForCode(res.code));
                $('#input-code1').addClass('error-box');
                $('#phoneNum').addClass('error-box');
            }
        })
        // fetchs.get('/system/checkverifycode?phoneNumber='+phoneNumber+'&code='+code, function(){
        // $('#step1').removeClass('active');
        // $('#step2').addClass('active');
        // })
    }
    //提交手机号验证码
    function changeadmin(phoneNumber, code) {
        fetchs.post('/setting/changerootadmin',{phoneNumber:phoneNumber,verifyCode:code},function (res) {
           if(res.ifSuc==1){
               $('.error').hide();
               $('#alterModal').modal('hide');
               reset();
               window.location(logout());
           }else {
               $('.error').show();
               $('.msg').text(msgForCode(res.code));
               $('#input-code2').addClass('error-box');
           }
        })
        // fetchs.get('/setting/changerootadmin?phoneNumber='+phoneNumber+'&verifyCode='+code, function(){
        // reset();
        // })
    }

    // $('#companyForm input#comname').bind('input propertychange', function(argument) {
    //     console.log($(this).val());
    // })
    // [^\w\s]+
    //保存修改的信息
    // $('.company-info button[type=submit].btn').click(function(){
    //     var _tenantName = $('.company-info input[type=email].form-control').val();
    //     var _companyType = $('.company-info select.form-control').val();
    //     var _companyAddress = $('.company-info input[type=text].form-control').val();
    //     var _companyDescription = $('.company-info textarea.form-control').val();
    //     //企业设置页面的信息
    //     var _data = {
    //       'userId':_userInfo._userId,
    //       'tenantId':'a5578cd5e1e844b98910093f05221c6f',
    //       'tenantName':_tenantName,
    //       'companyType':_companyType,
    //       'companyAddress':_companyAddress,
    //       'companyDescription':_companyDescription,
    //       'token':_userInfo._token
    //     };
    //     //保存企业信息的post上传
    //     $.post('http://localhost:8080/setting/updatecompanyinfo',_data,function(datas){
    //         console.log(datas);
    //     })
    // })
    // $('#custom').popover({
    //   trigger:'click', //触发方式
    //   html: true,
    //   content: '<div class="personnel"><input type="text" class="form-control" placeholder="搜索姓名"><div class="bread"></div><ul class="list groups"></ul><ul class="list persons"></ul></div>'
    // });
    $('#custom').on('shown.bs.popover', function () {
        $('.personnel .groups').html('<li><input id="minp" type="checkbox" value=""><label for="minp"><span class="glyphicon glyphicon-briefcase"></span><span>盛科伟</span></label></li>');
        $('.personnel .persons').html('<li><span class="nophoto">李小四</span><span>李小四</span></li>');
    });

    $('#subManager').on('shown.bs.personnel', function () {
        console.log('shown.bs.personnel');
    });
    //子管理员
    // //添加
    // $('#subManager').on('add.bs.personnel', function (e, id) {
    //     console.log(e);
    //     if (sonmangers.indexOf(id) < 0) {
    //         sonmangers.push(id);
    //     }
    //     console.log(id);
    // });
    //删除
    $('#subManager').on('delete.bs.personnel', function (e, id) {
        console.log($(e).parent());
        var ids = e.delete;
        for (var i=0;i<ids.length;i++){
            var id = ids[i].id.toString();
            console.log(sonmangers);
            if (sonmangers.indexOf(id) >= 0) {
                //如果数组中有这个ID，删除
                delsonmanger(id,e);
            }
        }
        // if (sonmangers.indexOf(id) >= 0) {
        //     //如果数组中有这个ID，从数组中删除，否则调用接口
        //     sonmangers.pop(id);
        // } else {
        //     delsonmanger(id);
        // }
    });
    //隐藏
    $('#subManager').on('hidden.bs.personnel', function (e) {
        console.log('hidden.bs.personnel');
        var arr = e.selected;
        var temp = [];
        for (var i=0;i<arr.length;i++){
            temp.push(arr[i].id);
        }
        addsonmanger(temp,e);
        // if(sonmangers.length>0){
        //     addsonmanger();
        // }
    });
    //会议室管理员
    //添加
    // $('#roomManger').on('add.bs.personnel', function (e, id) {
    //     console.log(e);
    //     if (roommangers.indexOf(id) < 0) {
    //         roommangers.push(id);
    //     }
    //
    //     console.log(id);
    // });
    //删除
    $('#roomManger').on('delete.bs.personnel', function (e, id) {
        var ids = e.delete;
        for (var i=0;i<ids.length;i++){
            var id = ids[i].id.toString();
            if (roommangers.indexOf(id) >= 0) {
                //如果数组中有这个ID，删除
                delroommanger(id,e);
            }
        }
    });
    //隐藏
    $('#roomManger').on('hidden.bs.personnel', function (e) {
        var arr = e.selected;
        var temp = [];
        for (var i=0;i<arr.length;i++){
            temp.push(arr[i].id);
        }
        addroommanger(temp,e);
        // console.log('hidden.bs.personnel');
        // if(roommangers.length>0){
        //     addroommanger();
        // }
    });
    //会议室预定专员
    //添加
    // $('#roomBooker').on('add.bs.personnel', function (e, id) {
    //     console.log(id);
    //     if (commissioners.indexOf(id) < 0) {
    //         commissioners.push(id);
    //     }
    // });
    //删除
    $('#roomBooker').on('delete.bs.personnel', function (e, id) {
        console.log(e);
        var ids = e.delete;
        for (var i=0;i<ids.length;i++){
            var id = ids[i].id.toString();
            if (commissioners.indexOf(id) >= 0) {
                //如果数组中有这个ID，删除
                delcommissioner(id,e);
            }
        }
    });
    //隐藏
    $('#roomBooker').on('hidden.bs.personnel', function (e) {
        console.log('hidden.bs.personnel');
        var arr = e.selected;
        var temp = [];
        for (var i=0;i<arr.length;i++){
            temp.push(arr[i].id);
        }
        addcommissioner(temp,e);
        // if(commissioners.length>0){
        //     addcommissioner();
        // }

    });
});
// -------- 将以base64的图片url数据转换为Blob --------
function convertBase64UrlToBlob(urlData, filetype) {
    //去掉url的头，并转换为byte
    var bytes = window.atob(urlData.split(',')[1]);
    //处理异常,将ascii码小于0的转换为大于0
    var ab = new ArrayBuffer(bytes.length);
    var ia = new Uint8Array(ab);
    var i;
    for (i = 0; i < bytes.length; i++) {
        ia[i] = bytes.charCodeAt(i);
    }
    return new Blob([ab], { type: filetype });
}

var countdown = 60;
function settime(obj) {
    if (countdown == 0) {
        //60s后重发
        obj.attr('disabled', false);
        obj.text('重新发送');
        countdown = 60;
        return;
    } else {
        obj.attr('disabled', true);
        obj.text(countdown + 's');
        countdown--;
    }
    setTimeout(function () {
        settime(obj);
    }, 1000);
}
//# sourceMappingURL=admin.js.map
