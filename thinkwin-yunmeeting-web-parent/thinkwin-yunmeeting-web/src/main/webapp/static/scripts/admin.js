'use strict';

$(function () {
    //获取用户信息 userId,token
    var _userInfo = JSON.parse(localStorage.getItem('userinfo'));
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
    var termangers = new Array(); //会议显示终端管理员
    var initialSon = new Array(); //初始化的子管理员
    var initialCom = new Array(); //初始化的会议室预定专员
    var initialRoom = new Array(); //初始化的会议室管理员
    var initialTer = new Array(); //初始化的会议显示终端管理员
    var sysPhoneNum = '';
    var cominfo = '';
    var countdown = 60;
    var timer;
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
    });
    $('.version-count').hide();
    getcompanytype();
    getCompanyInfo();
    //获取企业信息
    function getCompanyInfo() {
        fetchs.post('/setting/querycompanyinfo',{userId:_userInfo.userId,token:_userInfo.token}, function (res) {
            cominfo = res.data;
            if(res.data){
                sysPhoneNum = res.data.adminPhone;//超级管理员手机号
                if (res.data.inPicture!=null){
                    if(res.data.inPicture.length!=0){
                        $('#logo').attr('src', res.data.inPicture);
                        $('#sctp').text('更换图片');
                        // $('.btn-select-photo').html('更换图片<input type="file" accept="image/jpg,image/png,image/jpeg,image/bmp">');
                    }
                }
                $('#comname').val(res.data.tenantName);
                $('#companyName').text(res.data.tenantName);
                if (res.data.companyType!=null){
                    if(res.data.companyType.length!=0){
                        if(cominfo.companyType != '请选择行业类型'){
                            $('#type-name').text(cominfo.companyType);
                            $('#type-name').removeClass('placeholder-color');
                        }else{
                            $('#type-name').text('请选择行业类型');
                            $('#type-name').addClass('placeholder-color')
                        }
                    }else {
                        $('#type-name').text('请选择行业类型');
                        $('#type-name').addClass('placeholder-color');
                    }
                }else {
                    $('#type-name').text('请选择行业类型');
                    $('#type-name').addClass('placeholder-color');
                }
                $('#comaddress').val(res.data.companyAddress);
                $('#description').val(res.data.companyDescription);
                var length = 0;
                if(res.data.companyDescription==null){
                    length == 0;
                }else {
                    length = res.data.companyDescription.length;
                }
                $('.description-length').text('（'+ length +'/200字）')
                $('#rcode').attr('src', res.data.qrcodePath);
                $('#inviteCode').val(res.data.companyInvitationCode);
                var text = res.data.productName.indexOf('免费')>=0?res.data.productName:res.data.productName.indexOf('专业')>=0?"专业版": ''
                $('#pricing').text(text);
                $('#roomCount').text(res.data.buyRoomNumTotal + "间会议室");
                $('#personCount').text(res.data.expectNumber + "位可开通员工人数")
                $('#capacityCount').text(res.data.spaceTotal + "GB存储空间")

                if(res.data.basePackageExpir!=null){
                    if(res.data.basePackageExpir.length!=0){
                        var time = moment(res.data.basePackageExpir).format('YYYY-MM-DD')
                        if(time!=null||time!=undefined){
                            if(time.length>0){
                                $('#endTime').text(time+"到期")
                            }
                        }
                    }
                }
                if(res.data.productName.indexOf("免费")<0){
                    $('.version-count').show();
                    $('#buyVersion').hide();
                    if(res.data.productName.indexOf(500)>=0){
                        $('#buyVersion').show();
                        $('#buyVersion').text('立即续费')
                    }
                }else {
                    $('.version-count').hide();
                    $('#buyVersion').show();
                    $('#buyVersion').text('购买专业版授权');
                }
                var optionlist = $('.company-type').children();
                $.each(optionlist,function (i,el) {
                    if($(this).data('title') == $('#type-name').text()){
                        $(this).addClass('select');
                    }
                });
                $("#savecomInfo").attr('disabled', true);
                if(_userInfo.userId==res.data.adminId&&_userInfo.userId&&res.data.adminId){
                    $('.company-dissolution').show();
                }
            }
        });
    }
    //查询企业类型
    function getcompanytype() {
        fetchs.get('/setting/selecttenanttype?userId='+_userInfo.userId+'&token='+_userInfo.token, function (res) {
            $('.company-type').html(soda(dropdown, { data: res.data }));
            if (cominfo){
                if (cominfo.companyType!=null){
                    if(cominfo.companyType.length!=0){
                        if(cominfo.companyType != '请选择行业类型'){
                            $('#type-name').text(cominfo.companyType);
                            $('#type-name').removeClass('placeholder-color');
                        }else{
                            $('#type-name').text('请选择行业类型');
                            $('#type-name').addClass('placeholder-color')
                        }
                    }else {
                        $('#type-name').text('请选择行业类型');
                        $('#type-name').addClass('placeholder-color')
                    }
                }else {
                    $('#type-name').text('请选择行业类型');
                    $('#type-name').addClass('placeholder-color');
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
    $('body').on('click', '.type-item', function (e) {
        $('#type-name').removeClass('placeholder-color');
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
    $('#comname').bind('focus','input',function () {
        $(this).removeClass('error-box');
        $('#error').hide();
    })
    $('#comname').bind('blur','input',function () {
        checkName($(this).val());
    })
    $('#description').bind('input propertychange', function () {
        var length = $(this).val().length;
        $('.description-length').text('（'+length+'/200字）');
    })
    //购买版本
    $('#buyVersion').on('click',function () {
        window.location.href = "/commodity/orderManger?token="+_userInfo.token;
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
                var str = mainAdmin.userName + '<span>' + mainAdmin.phoneNumber + '</span>';
                sysPhoneNum = mainAdmin.phoneNumber;
                $('#mainAdmin').html(str);
                $('#phone').html("系统会向" +mainAdmin.phoneNumber + "发送验证码");
                $('.tags').children().not('.btn-add').each(function () {
                    if(!$(this).hasClass('persons')){
                        $(this).remove();
                    }
                })

                var tempArr = [];
                for(var k=0;k<res.data.sonAdministrator.length;k++){
                    if (res.data.sonAdministrator[k]!=null){
                        tempArr.push(res.data.sonAdministrator[k]);
                    }
                }
                sonmangers = [];
                for(var i=0;i<tempArr.length;i++){
                    sonmangers.push(tempArr[i].id);
                }
                initialSon = tempArr;
                $('#sonAdministrator span.icon-add-personnel').before(soda(tpl, {data:tempArr}));

                var tempArr1 = [];
                for(var i=0;i<res.data.commissioners.length;i++){
                    if (res.data.commissioners[i]!=null){
                        tempArr1.push(res.data.commissioners[i])
                    }
                }
                commissioners = [];
                for(var i=0;i<tempArr1.length;i++){
                    commissioners.push(tempArr1[i].id);
                }
                initialCom = tempArr1;
                $('#commissioners span.icon-add-personnel').before(soda(tpl,  {data:tempArr1}));
                var tempArr2 = [];
                for(var i=0;i<res.data.boardroommanagers.length;i++){
                    if (res.data.boardroommanagers[i]!=null){
                        tempArr2.push(res.data.boardroommanagers[i])
                    }
                }
                roommangers = [];
                for(var i=0;i<tempArr2.length;i++){
                    roommangers.push(tempArr2[i].id);
                }
                initialRoom = tempArr2;
                $('#boardroommanagers span.icon-add-personnel').before(soda(tpl, {data:tempArr2}));
                if(tempArr.length>=3){
                    $('#sonAdministrator span.icon-add-personnel').hide();
                }else {
                    $('#sonAdministrator span.icon-add-personnel').show();
                }

                var tempArr3 = [];
                for(var k=0,len=res.data.terminalAdministrators.length;k<len;k++){
                    if (res.data.terminalAdministrators[k]!=null){
                        tempArr3.push(res.data.terminalAdministrators[k]);
                    }
                }

                if(res.data.isDisplay){
                    termangers = [];
                    for(var i=0;i<tempArr3.length;i++){
                        termangers.push(tempArr3[i].id);
                    }
                    initialTer = tempArr3;
                    $('#terminals span.icon-add-personnel').before(soda(tpl, {data:tempArr3}));
                    if(tempArr3.length>=3){
                        $('#terminals span.icon-add-personnel').hide();
                    }else {
                        $('#terminals span.icon-add-personnel').show();
                    }
                }else {
                    $("#terminalManager").hide();
                }

            }
        });
    }
    var tpl = '\n        <span class="add" ng-repeat="item in data" data-id="{{item.id}}">{{item.userName}}\n            <i class="icon icon-delete-personnel"></i>\n        </span>\n    ';
    $('.form-control').on('keypress',function (e) {
        if(e.keyCode==13){
            e.preventDefault();
        }
    });
    //更换头像
    $('#changeLogo').on('click', function (event) {
        event.preventDefault();
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
        imginfo.token=_userInfo.token;
        var strlength = imginfo.fileName.substring(23).length;
        imginfo.size=parseInt(strlength-(strlength/8)*2);
        $('.result').attr('src', croppedCanvas.toDataURL('image/png'));
        fetchs.post('/setting/changelogo',imginfo, function (data) {
            if (data.ifSuc==0){
                //失败
                if(data.code==-1){
                    notify('danger','LOGO更换失败，请检查网络后重试');
                }else {
                    notify('danger',data.msg);
                }
            }else {
                window.location.reload();
            }
        });
    });
    //刷新邀请码
    $('#icon-refresh').on('click', function () {
        fetchs.post('/setting/refreshinvitecode',{userId:_userInfo.userId,token:_userInfo.token}, function (res) {
            // body...
            $('#inviteCode').val(res.data.inviteCode);
        });
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
        var ifSuc = checkName(name);
        if (!ifSuc) {
            return;
        }
        if (type.length == 0 || type == '请选择行业类型'){
            $('#comtype').addClass('error-box');
            $('#error').show();
            $('#errormsg').html('请选择行业类型');
            return;
        }
        if ($.trim(name)==$.trim(cominfo.tenantName)){
            name = '';
        }
        $('#comname').removeClass('error-box');
        $('#comtype').removeClass('error-box');
        $('#error').hide();
        fetchs.post('/setting/updatecompanyinfo', { 'userId': _userInfo.userId, 'tenantName': name, 'companyType': type, 'companyAddress': address, 'companyDescription': description, 'token': _userInfo.token }, function (res) {
            if (res.ifSuc==0) {
                //公司名字已存在
                $('#comname').addClass('error-box');
                $('#error').show();
                $('#errormsg').html(msgForCode(res.code));
            } else if(res.ifSuc==1) {
                notify('success', '修改成功');
                // rgba(0, 0, 0, 0.15);
                $('#comname').removeClass('error-box');
                $('#error').hide();
                // $('#logo').attr('src', res.data.companyLogo);
                $('#comname').val(res.data.tenantName?res.data.tenantName:cominfo.tenantName);
                var type = $('#type-name').text();
                $('#comaddress').val(res.data.companyAddress);
                $('#description').val(res.data.companyDescription);
                $("#savecomInfo").attr('disabled', true);
                window.location.reload();
            }
        });
    });
    //添加会议室管理员
    function addroommanger(arr,e) {
        var deltemp = roommangers;
        var temp = [];
        for(var  i = 0; i<arr.length; i++){
            for(var  j = 0; j<deltemp.length; j++){
                if(arr[i] == deltemp[j]) {
                    temp.push(arr[i]); //重复的id
                }
            }
        }
        if(temp.length>0){
            for(var k = 0; k<temp.length; k++){
                deltemp.splice(deltemp.indexOf(temp[k]),1);
            }
        }
        if(deltemp.length>0){
            fetchs.get('/delboardroommanager?userId=' + _userInfo.userId + '&moveIds=' + deltemp + '&token=' + _userInfo.token, function (res) {
                if (res.data == false) {
                    $('#boardroommanagers').children().not('.btn-add').each(function () {
                        $(this).remove();
                    })
                    $('#boardroommanagers span.icon-add-personnel').before(soda(tpl, {data:initialRoom}));
                } else {
                }
                getsettingPersons()
            });
        }
        if(arr.length>0){
            fetchs.get('/addboardroommanager?userId=' + _userInfo.userId + '&managerIds=' + arr + '&token=' + _userInfo.token, function (res) {
                if (res.data == false) {
                    notify('danger', res.msg);
                    $('#boardroommanagers').children().not('.btn-add').each(function () {
                        $(this).remove();
                    })
                    $('#boardroommanagers span.icon-add-personnel').before(soda(tpl, {data:initialRoom}));
                } else {
                    // notify('success', res.msg);
                }
                getsettingPersons()
            });
        }
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
                $('#boardroommanagers span.icon-add-personnel').before(soda(tpl, {data:initialRoom}));
            } else {
                notify('success', res.msg);
            }
            getsettingPersons()
        });
    }
    //添加会议室预订员
    function addcommissioner(arr,e) {
        //找出删除的预订员
        var deltemp = commissioners;
        var temp = [];
        for(var  i = 0; i<arr.length; i++){
            for(var  j = 0; j<deltemp.length; j++){
                if(arr[i] == deltemp[j]) {
                    temp.push(arr[i]); //重复的id
                }
            }
        }
        if(temp.length>0){
            for(var k = 0; k<temp.length; k++){
                deltemp.splice(deltemp.indexOf(temp[k]),1);
                // arr.pop(temp[k]);
            }
        }
        //删除所有的
        if(deltemp.length>0){
            fetchs.get('/delcommissioner?userId=' + _userInfo.userId + '&commissionerIds=' + deltemp + '&token=' + _userInfo.token, function (res) {
                if (res.data == false) {
                    $('#commissioners').children().not('.btn-add').each(function () {
                        $(this).remove();
                    })
                    $('#commissioners span.icon-add-personnel').before(soda(tpl, {data:initialCom}));
                }
                getsettingPersons();
            });
        }
        if(arr.length>0){
            fetchs.get('/addcommissioner?userId=' + _userInfo.userId + '&commissionerIds=' + arr + '&token=' + _userInfo.token, function (res) {
                if (res.data == false) {
                    notify('danger', res.msg);
                    $('#commissioners').children().not('.btn-add').each(function () {
                        $(this).remove();
                    })
                    $('#commissioners span.icon-add-personnel').before(soda(tpl, {data:initialCom}));
                } else {
                    // notify('success', res.msg);
                }
                getsettingPersons()
            });
        }
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
                $('#commissioners span.icon-add-personnel').before(soda(tpl, {data:initialCom}));
            } else {
                notify('success', res.msg);
            }
            getsettingPersons();
        });
    }
    //添加子管理员
    function addsonmanger(arr,e) {
        var deltemp = sonmangers;
        var temp = [];
        for(var  i = 0; i<arr.length; i++){
            for(var  j = 0; j<deltemp.length; j++){
                if(arr[i] == deltemp[j]) {
                    temp.push(arr[i]); //重复的id
                }
            }
        }
        if(temp.length>0){
            for(var k = 0; k<temp.length; k++){
                deltemp.splice(deltemp.indexOf(temp[k]),1);
                // arr.pop(temp[k]);
            }
        }
        if(deltemp.length>0){
            for(var i = 0; i< deltemp.length; i++){
                fetchs.get('/delSonManager?userId=' + _userInfo.userId + '&sonUserId=' + deltemp[i] + '&token=' + _userInfo.token, function (res) {
                    if (res.ifSuc == 0) {
                        $('#sonAdministrator').children().not('.btn-add').each(function () {
                            $(this).remove();
                        })
                        $('#sonAdministrator span.icon-add-personnel').before(soda(tpl, {data:initialSon}));
                    } else {
                        sonmangers.pop(id);
                    }
                    if(sonmangers.length>=3){
                        $('#sonAdministrator span.icon-add-personnel').hide();
                    }else {
                        $('#sonAdministrator span.icon-add-personnel').show();
                    }
                    getsettingPersons();
                });
            }
        }
        if(arr.length>0){
            fetchs.get('/addSonManager?userId=' + _userInfo.userId + '&sonList=' + arr + '&token=' + _userInfo.token, function (res) {
                if (res.ifSuc == 0) {
                    notify('danger', res.msg);
                    $('#sonAdministrator').children().not('.btn-add').each(function () {
                        $(this).remove();
                    })
                    $('#sonAdministrator span.icon-add-personnel').before(soda(tpl, {data:initialSon}));
                } else {
                    // notify('success', res.msg);
                }
                if(arr.length>=3){
                    $('#sonAdministrator span.icon-add-personnel').hide();
                }else {
                    $('#sonAdministrator span.icon-add-personnel').show();
                }
                getsettingPersons();
            });
        }
    }
    //添加会议显示终端管理员
    function addterminals(arr,e) {
        //找出删除的预订员
        var deltemp = termangers;
        var temp = [];
        for(var  i = 0; i<arr.length; i++){
            for(var  j = 0; j<deltemp.length; j++){
                if(arr[i] == deltemp[j]) {
                    temp.push(arr[i]); //重复的id
                }
            }
        }
        if(temp.length>0){
            for(var k = 0; k<temp.length; k++){
                deltemp.splice(deltemp.indexOf(temp[k]),1);
            }
        }
        //删除取消勾选的人员
        if(deltemp.length>0){
           delterminals(deltemp,1);
        }
        if(arr.length>0){
            fetchs.get('/addTerminalAdministrator?userId=' + _userInfo.userId + '&terminalAdminIds=' + arr + '&token=' + _userInfo.token, function (res) {
                if (res.ifSuc == 0) {
                    notify('danger', res.msg);
                    $('#terminals').children().not('.btn-add').each(function () {
                        $(this).remove();
                    })
                    $('#terminals span.icon-add-personnel').before(soda(tpl, {data:initialTer}));
                }
                getsettingPersons()
            });
        }
    }
    //删除会议显示终端管理员
    function delterminals(id,type){
        var arr = new Array();
        if(type == 0){
           arr.push(id);
        }else {
           arr = id;
        }
        fetchs.get('/delTerminalAdministrator?userId=' + _userInfo.userId + '&terminalAdminIds=' + arr + '&token=' + _userInfo.token, function (res) {
            if (res.ifSuc == 0) {
                notify('danger', res.msg);
                $('#terminals').children().not('.btn-add').each(function () {
                    $(this).remove();
                })
                $('#terminals span.icon-add-personnel').before(soda(tpl, {data:initialTer}));
            } else {
                notify('success', res.msg);
            }
            getsettingPersons();
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
                $('#sonAdministrator span.icon-add-personnel').before(soda(tpl, {data:initialSon}));

            } else {
                notify('success', res.msg);
                sonmangers.pop(id);
            }
            if(sonmangers.length>=3){
                $('#sonAdministrator span.icon-add-personnel').hide();
            }else {
                $('#sonAdministrator span.icon-add-personnel').show();
            }
            getsettingPersons();
        });
    }
    //验证公司名
    function checkName(name) {
        if (name.length==0){
            $('#comname').addClass('error-box');
            $('#error').show();
            $('#errormsg').html('公司名称不能为空');
            return false;
        }
        var patt = new RegExp('[`~!@#$^&*=|{}\':;\',\\[\\].<>/?~@#￥……&*——|{}【】‘；：”“\'。，、？%+_`¡™£¢∞§¶•ªº–≠œ∑´º®†¥¨ˆøπ“‘«åß∂ƒ©˙∆˚¬…æΩ≈ç√∫˜µ≤≥÷]');
        if (patt.test(name)) {
            $('#comname').addClass('error-box');
            $('#error').show();
            $('#errormsg').html('公司名称不能含有特殊字符');
            return false;
        } else {
            $('#comname').removeClass('error-box');
            $('#error').hide();
            return true;
        }
    }
    $('#code1').on('click', function (event) {
        $(this).attr('disabled',true);
        countdown = 60;
        getCode(sysPhoneNum,2);
    });
    //下一步操作
    $('#next').on('click', function (event) {
        //判断验证码有没有输入
        var code = $('#input-code1').val();
        if (code.length == 0) {
            $('.error').show();
            $('.msg').text("手机验证码不能为空");
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
        var pattern = /^(13[0-9]|14[5-9]|15[012356789]|166|17[0-8]|18[0-9]|19[8-9])[0-9]{8}$/;
        if (pattern.test(string)) {
            return true;
        }
        return false;
    };
    //表单取消重置
    $('.btn-clear').on('click', function () {
        reset();
    });
     $('body').on('hidden.bs.modal','#alterModal',function (e) {
         reset();
     })
    //重置验证码事件
    function reset() {
        countdown = -1;
        $('#input-code1').val('');
        $('#input-code2').val('');
        $('#input-code1').removeClass('error-box');
        $('#input-code2').removeClass('error-box');
        $('#phoneNum').removeClass('error-box');
        $('#phoneNum').val('');
        $('#step2').removeClass('active');
        $('#step1').addClass('active');
        $('.error').hide();
        $('.modal-backdrop').remove();
        $('#alterModal').removeClass('show');
    }
    //获取验证码
    function getCode(phoneNumber,type) {
        $('.error').hide();
        $('#phoneNum').removeClass('error-box');
        fetchs.post('/system/getverifycode',{phoneNumber:phoneNumber,type:type},function (res) {
            if(res.ifSuc==0){
                //验证码发送失败
                $('.error').show();
                if(res.code==-1){
                    $('.msg').text('验证码获取失败，请检查网络后重试');
                }else {
                    $('#phoneNum').addClass('error-box');
                    $('.msg').text(msgForCode(res.code));
                }
            }else {
                if (type==3){
                    settime($('#code2'));
                }else {
                    settime($('#code1'));
                }
            }
        })
    }
    //校验验证码
    function checkCode(phoneNumber, code) {
        fetchs.post('/system/checkverifycode',{phoneNumber:phoneNumber,verifyCode:code},function (res) {
            if(res.ifSuc==1){
                $('.error').hide();
                $('#input-code1').removeClass('error-box');
                $('#step1').removeClass('active');
                $('#step2').addClass('active');
                countdown = 60;
                clearInterval(timer);
                $('#code1').text("获取验证码");
                $('#code1').attr('disabled', false);
                $('#code1').removeClass('time-down');
            }else {
                $('.error').show();
                $('.msg').text(msgForCode(res.code));
                $('#input-code1').addClass('error-box');
                $('#phoneNum').addClass('error-box');
            }
        })
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
    }
    $('#custom').on('shown.bs.popover', function () {
        $('.personnel .groups').html('<li><input id="minp" type="checkbox" value=""><label for="minp"><span class="glyphicon glyphicon-briefcase"></span><span>盛科伟</span></label></li>');
        $('.personnel .persons').html('<li><span class="nophoto">李小四</span><span>李小四</span></li>');
    });
    $('#subManager').on('shown.bs.personnel', function () {
        console.log('shown.bs.personnel');
    });
    //倒计时逻辑
    function settime(obj) {
        timer = setInterval(function () {
            if (countdown == 0) {
                //60s后重发
                obj.attr('disabled', false);
                obj.text('重新发送');
                obj.removeClass('time-down');
                countdown = 60;
                clearInterval(timer);
                return;
            }else if(countdown == -1){
                obj.attr('disabled', false);
                obj.text('获取验证码');
                obj.removeClass('time-down');
                countdown = 60;
                clearInterval(timer);
                return;
            }
            else {
                obj.attr('disabled', true);
                obj.addClass('time-down');
                obj.text(countdown + 's');
                countdown--;
            }
        },1000)
    }

    //切换重置时间
     $('#setmanagetime').on('click', function () {
        reset();
    });

    //新增人员组件调用 （添加人员）   子管理员
    $("#sonAdministrator").persons({//人员组件调用
        class:'person',
        type:2,
        orgId:1,
        personsLimit:3,
        container:'#sonAdministrator',
        template:'<ul class="list groups">' +
        '<li class="nopt" ng-repeat="item in data.sysOrganizations" data-id="{{item.sysOrganization.id}}"><p data-leaf="{{item.leaf}}"><span class="icon icon-organiz-unit"></span><span class="name" ng-html="item.sysOrganization.orgName|keylight:key"></span></p></li>' +
        '</ul>' +
        '<div class="line" ng-if="data.sysUser.length>0"></div>' +
        '<ul class="list personnel">' +
        '<li ng-repeat="item in data.sysUser" class="" data-id="{{item.id}}"><span class="nophoto small" ng-if="!item.smallPicture"><i>{{item.icon}}</i></span><img class="user-url" ng-if="item.smallPicture" src="{{item.smallPicture}}"><span class="name" ng-html="item.name|keylight:key"></span><span ng-if="item.participantStatus" class="conflict">(<i class="icon icon-error"></i>有会议冲突)</span></li>' +
        '</ul>'
    });

    $("#terminals").persons({  //会议显示终端管理员
        class:'person',
        type:2,
        orgId:1,
        personsLimit:3,
        container:'#terminals',
        template:'<ul class="list groups">' +
        '<li class="nopt" ng-repeat="item in data.sysOrganizations" data-id="{{item.sysOrganization.id}}"><p data-leaf="{{item.leaf}}"><span class="icon icon-organiz-unit"></span><span class="name" ng-html="item.sysOrganization.orgName|keylight:key"></span></p></li>' +
        '</ul>' +
        '<div class="line" ng-if="data.sysUser.length>0"></div>' +
        '<ul class="list personnel">' +
        '<li ng-repeat="item in data.sysUser" class="" data-id="{{item.id}}"><span class="nophoto small" ng-if="!item.smallPicture"><i>{{item.icon}}</i></span><img class="user-url" ng-if="item.smallPicture" src="{{item.smallPicture}}"><span class="name" ng-html="item.name|keylight:key"></span><span ng-if="item.participantStatus" class="conflict">(<i class="icon icon-error"></i>有会议冲突)</span></li>' +
        '</ul>'
    });

    $("#sonAdministrator").on('hidden.bs.persons',function (e) {
        var arr = e.selected;
        var temp = [];
        for (var i=0;i<arr.length;i++){
            temp.push(arr[i].id);
        }
        addsonmanger(temp,e);
    })
    $("#sonAdministrator").on('delete.bs.persons',function (e) {
        var ids = e.delete;
        for (var i=0;i<ids.length;i++){
            var id = ids[i].id.toString();
            //console.log(sonmangers);
            if (sonmangers.indexOf(id) >= 0) {
                //如果数组中有这个ID，删除
                delsonmanger(id,e);
            }
        }
    })
    //会议室管理员
    $("#boardroommanagers").persons({//人员组件调用
        class:'person',
        type:2,
        orgId:1,
        container:'#boardroommanagers',
        template:'<ul class="list groups">' +
        '<li class="nopt" ng-repeat="item in data.sysOrganizations" data-id="{{item.sysOrganization.id}}"><p data-leaf="{{item.leaf}}"><span class="icon icon-organiz-unit"></span><span class="name" ng-html="item.sysOrganization.orgName|keylight:key"></span></p></li>' +
        '</ul>' +
        '<div class="line" ng-if="data.sysUser.length>0"></div>' +
        '<ul class="list personnel">' +
        '<li ng-repeat="item in data.sysUser" class="" data-id="{{item.id}}"><span class="nophoto small" ng-if="!item.smallPicture"><i>{{item.icon}}</i></span><img class="user-url" ng-if="item.smallPicture" src="{{item.smallPicture}}"><span class="name" ng-html="item.name|keylight:key"></span><span ng-if="item.participantStatus" class="conflict">(<i class="icon icon-error"></i>有会议冲突)</span></li>' +
        '</ul>'
    });
    $('#boardroommanagers').on('delete.bs.persons', function (e, id) {
        var ids = e.delete;
        for (var i=0;i<ids.length;i++){
            var id = ids[i].id.toString();
            if (roommangers.indexOf(id) >= 0) {
                delroommanger(id,e);//如果数组中有这个ID，删除
            }
        }
    });
    $('#boardroommanagers').on('hidden.bs.persons', function (e) {
        var arr = e.selected;
        var temp = [];
        for (var i=0;i<arr.length;i++){
            temp.push(arr[i].id);
        }
        addroommanger(temp,e);
    });
    //会议室预定专员
    $("#commissioners").persons({
        class:'person',
        type:2,
        orgId:1,
        container:'#commissioners',
        template:'<ul class="list groups">' +
        '<li class="nopt" ng-repeat="item in data.sysOrganizations" data-id="{{item.sysOrganization.id}}"><p data-leaf="{{item.leaf}}"><span class="icon icon-organiz-unit"></span><span class="name" ng-html="item.sysOrganization.orgName|keylight:key"></span></p></li>' +
        '</ul>' +
        '<div class="line" ng-if="data.sysUser.length>0"></div>' +
        '<ul class="list personnel">' +
        '<li ng-repeat="item in data.sysUser" class="" data-id="{{item.id}}"><span class="nophoto small" ng-if="!item.smallPicture"><i>{{item.icon}}</i></span><img class="user-url" ng-if="item.smallPicture" src="{{item.smallPicture}}"><span class="name" ng-html="item.name|keylight:key"></span><span ng-if="item.participantStatus" class="conflict">(<i class="icon icon-error"></i>有会议冲突)</span></li>' +
        '</ul>'
    });
    $('#commissioners').on('delete.bs.persons', function (e, id) {
        var ids = e.delete;
        for (var i=0;i<ids.length;i++){
            var id = ids[i].id.toString();
            if (commissioners.indexOf(id) >= 0) {
                delcommissioner(id,e);//如果数组中有这个ID，删除
            }
        }
    });
    $('#commissioners').on('hidden.bs.persons', function (e) {
        var arr = e.selected;
        var temp = [];
        for (var i=0;i<arr.length;i++){
            temp.push(arr[i].id);
        }
        addcommissioner(temp,e);
    });
    //会议显示终端管理员
    $('#terminals').on('delete.bs.persons', function (e) {
        var ids = e.delete;
        for (var i=0;i<ids.length;i++){
            var id = ids[i].id.toString();
            if (termangers.indexOf(id) >= 0) {
                delterminals(id,0);//如果数组中有这个ID，删除
            }
        }
    });
    //会议显示终端管理员
    $('#terminals').on('hidden.bs.persons', function (e) {
        var arr = e.selected;
        var temp = [];
        for (var i=0;i<arr.length;i++){
            temp.push(arr[i].id);
        }
        addterminals(temp);
    });
    //解散企业
    $('#dissolution').on('show.bs.collapse', function () {
        reset();
        countdown = 60;
        var id = $(this).attr('id');
        $('body a[href="#' + id + '"]').eq(0).hide();
    })

    $('#dissolution').on('hide.bs.collapse', function () {
        reset();
        countdown = 60;
        var id = $(this).attr('id');
        $('body a[href="#' + id + '"]').eq(0).show();
        $('#dissolutionForm input[name="code"]').val('');
        $('#dissolution .error-msg').html('');
        $('#dissolutionForm input[name="code"]').removeClass('error-box');
        clearInterval(timer);
        $('#dissolutionForm .time-down').attr('disabled', false).text('获取验证码').removeClass('time-down');
    })
    //解散企业获取验证码
    $('#btn-get-code1').on('click', function (event) {
        var that = $(this);
        that.attr('disabled',true);
        if(sysPhoneNum){
            getCodeSys({ 'phoneNumber': sysPhoneNum, 'type': '2' }, function (result) {
                if (result.ifSuc == 1) {//开始倒计时,同时清除提示信息和标示
                    settime(that);
                } else if (result.ifSuc == 0) {
                    if(result.code==-1){
                        notify('danger','获取验证码失败，请检查网络后重试');
                    }
                }else if (result == 500) {
                    $('#dissolution .error-msg').html('<i class="icon icon-error"></i>请求异常');
                };
            });
        }else{
            notify('danger','发送验证码失败');
        };
    });
    //企业解散表单提交
    $('.btn-save').on('click',function(){
        var code= $('#dissolutionForm input[name="code"]').val();
        if(code==''||code==null){
            $('#dissolutionForm input[name="code"]').addClass('error-box');
            $('#dissolution .error-msg').html('<i class="icon icon-error"></i>手机验证码不能为空');
        }else if(code){
            $('#dissolutionForm input[name="code"]').removeClass('error-box');
            $('#dissolution .error-msg').html('');
            console.log(!/^\d{6}$/.test(code));
            if(!/^\d{6}$/.test(code)){
                $('#dissolutionForm input[name="code"]').addClass('error-box');
                $('#dissolution .error-msg').html('<i class="icon icon-error"></i>验证码错误');
            }else{
                $.get('/setting/businessBankruptcy?userId='+_userInfo.userId+'&code='+code+'&phone='+sysPhoneNum+'&token='+_userInfo.token,function (result) {
                    if(result.ifSuc==1){
                        if(result.data.success){
                            $('#dissolutionForm input[name="code"]').removeClass('error-box');
                            $('#dissolution .error-msg').html('');
                            clearInterval(timer);
                            window.location.href="/system/loginpage";
                        }else{
                            $('#dissolutionForm input[name="code"]').addClass('error-box');
                            $('#dissolution .error-msg').html('<i class="icon icon-error"></i>解散失败');
                        }
                    }else{
                        $('#dissolutionForm input[name="code"]').addClass('error-box');
                        $('#dissolution .error-msg').html('<i class="icon icon-error"></i>'+result.msg);
                    }
                });
            }
        }else{
            $('#dissolutionForm input[name="code"]').addClass('error-box');
            $('#dissolution .error-msg').html('<i class="icon icon-error"></i>验证码错误');
        }
    })
    function getCodeSys(data, callBack) {
        fetchs.post('/system/getverifycode', data, function (_data) {
            callBack(_data);
        });
    };
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