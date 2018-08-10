'use strict';

var fetch = function fetch(opt) {
  this.options = opt;
  this.server = '';
  // this.server     = 'http://10.10.11.54:8080';
  this.userId = '';
  this.token = '';
  this.status = '';
  this.userName='';//记录当前用户名
  this.init();
  // body...
};
fetch.prototype.init = function () {
  var userInfo = JSON.parse(localStorage.getItem('userinfo'));
  var tenantType = JSON.parse(localStorage.getItem('tenantType'));
  if (userInfo) {
    this.userId = userInfo.userId;
    this.token = userInfo.token;
    this.userName='';
  }
  if(tenantType){
    this.status= tenantType.status;
  }
};
fetch.prototype.post = function (url, data, callBack) {
    var userInfo = JSON.parse(localStorage.getItem('userinfo'));
    if (userInfo) {
        this.userId = userInfo.userId;
        this.token = userInfo.token;
        data['userId'] = userInfo.userId;
        data['token'] =  userInfo.token;
    }

  $.post(this.server + url, data, function (data) {
    callBack(data);
  });
};

fetch.prototype.get = function (url, callBack) {
  $.get(this.server + url, function (data) {
    callBack(data);
  });
};
fetch.prototype.qyh_get = function (url,data, callBack) {
    $.get(this.server + url,data, function (data) {
        callBack(data);
    });
};

fetch.prototype.upload = function(url,file,callBack) {
    var data = new FormData();
    data.append('userId', '1212121');
    data.append('fileName', file);
    data.append('token', '232234243');
    $.ajax({
        url: this.server+url,
        type: 'POST',
        data: data,
        cache: false,
        contentType: false,    //不可缺
        processData: false,    //不可缺
        success:function(data){
            callBack(data)
        }
    })
}
//文件上传 段威杰
fetch.prototype.fileupload = function (url, fileid,formId, callBack) {
  var formData = new FormData($(formId)[0]);
  var userInfo = JSON.parse(localStorage.getItem('userinfo'));
  var URL = this.server + url+'?userId='+userInfo.userId+'&token='+userInfo.token+'';
  if(fileid||fileid==''){
     this.server + url+'?userId='+userInfo.userId+'&fileId='+fileid+'&token='+userInfo.token+''
  };
  $.ajax({
    url: URL,
    type: 'POST',
    data: formData,
    cache: false,
    contentType: false, //不可缺
    processData: false, //不可缺
    success: function success(data) {
      callBack(data);
    }
  });
};
//导入的ajax请求
fetch.prototype.fileimport= function (url,state,data, callBack) {
    var userInfo = JSON.parse(localStorage.getItem('userinfo'));
    $.ajax({
        url: this.server + url+'?userId='+userInfo.userId+'&selctStatus='+state+'&token='+userInfo.token+'',
        type: 'POST',
        data: JSON.stringify(data),
        cache: false,
        contentType: "application/json",
        processData: false,
        dataType: 'json',
        success: function success(data) {
            callBack(data);
        }
    });
};

fetch.prototype.login = function (url, data, callBack) {
  var self = this;

  $.post(this.server + url, data, function (data) {
    if (data.code == 1) {
      self.userId = data.userId;
      self.token = data.token;
      callBack(data);
    }
  });
};
var fetchs = new fetch();
//# sourceMappingURL=fetch.js.map
