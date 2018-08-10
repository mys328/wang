'use strict';

$(function () {
   var weatherUrl;//天气配置
   var appUrl; //app配置
   var backgroundUrl;//背景图配置
   var blob; //记录本地上传图片的缓存路径
   var imageFile; //上传的图片文件
   var isChange = false;  //判断是否修改背景图
   fetchs.post('/configManager/getConfig',{}, function (res){
     if(res.ifSuc==1){
        if(!parseInt(res.data.isWeather) && !parseInt(res.data.isBackgrounp)){
            $(".nothing").show();
        }else {
            // if(res.data.isWeather){
                weatherUrl = res.data.weatherConfig;
                $(".weatherConfig").show();
            // }
            // if(res.data.isBackgrounp){
                backgroundUrl = res.data.terminalBackgrounp?res.data.terminalBackgrounp:"/static/images/default-bg@1x.png";
                $(".terminalBackgrounp").show();
            // }
               appUrl = res.data.terminalAppUrl;
               $(".appConfig").show();
        }
     }else {
        notify('danger',res.msg);
        if(res.ifSuc==10){
          goLoginPage();
        }
     }
   });
   //天气配置
   $('body').on('click', '.set-weather', function (e) {
      e.stopPropagation();
      $("input[name='weather']").val(weatherUrl);
      $(".inner-box.weather").show();
      $('.config-box').addClass('show');
   });
   //默认图配置
   $('body').on('click', '.set-image', function (e) {
      e.stopPropagation();
      isChange = false;
      $("#bgc_image").attr("src",backgroundUrl);
      $(".inner-box.image").show();
      $('.config-box').addClass('show');
   });
    //app下载地址配置
   $('body').on('click', '.set-app', function (e) {
        e.stopPropagation();
        $("input[name='app']").val(appUrl);
        $(".inner-box.appDownload").show();
        $('.config-box').addClass('show');
    });
   $('body').on('click', '.save-weather', function () {
      var url = $("input[name='weather']").val();
      var self= $(this);
      if(url == ""){
         errorShow(self)
         return;
      }else {
         errorHide(self);
      }
      if(url != weatherUrl){
         fetchs.post('/configManager/setWeatherConfig',{"weatherConfig":url}, function (res){
              hideRightBox();
              if(res.ifSuc==1){
                  weatherUrl = url;
                  notify('success',res.msg);
              }else {
                  notify('danger',res.msg);
                  if(res.ifSuc==10){
                      goLoginPage();
                  }
              }
         });
      }else {
          hideRightBox();
      }
   });
   $('body').on('click', '.save-image', function () {
      if(isChange){
         fetchs.formDataUpload("/configManager/setTerminalBackgrounp",imageFile,function (res) {
             hideRightBox();
             if(res.ifSuc==1){
                 isChange = false;
                 backgroundUrl = blob;
                 notify('success',res.msg);
             }else {
                 notify('danger',res.msg);
                 if(res.ifSuc==10){
                     goLoginPage();
                 }
             }
        });
       }else {
          hideRightBox();
       }
   });
   $('body').on('click', '.save-app', function () {
        var url = $("input[name='app']").val();
        var self= $(this);
        if(url == ""){
            errorShow(self);
            return;
         }else{
            errorHide(self);
        }
        if(url != appUrl){
            fetchs.post('/configManager/terminalAppUrlConfig',{"terminalAppUrl":url}, function (res){
                hideRightBox();
                if(res.ifSuc==1){
                    appUrl = url;
                    notify('success',res.msg);
                }else {
                    notify('danger',res.msg);
                    if(res.ifSuc==10){
                        goLoginPage();
                    }
                }
            });
        }else {
            hideRightBox();
        }
    });
   //点击空白处,收起有弹框
   $('body').on('click', function (e) {
      if (e.target != $('.config-box') && $('.config-box').find(e.target).length < 1) {
          $(".warningBox").hide();
          $("input").removeClass("warring");
          hideRightBox();
      }
   });
   //隐藏侧边栏
    function hideRightBox(){
        $('.config-box').removeClass('show');
        $(".inner-box").hide();
    }
   //点击重新上传图片
   $('body').on('change', '.btn-change-photo .upload', function () {
      verifyImage($(this));
   });
   //验证图片
   function verifyImage(self, type) {
      var file = self.get(0).files[0];
      var extStart = file.name.lastIndexOf(".");
      var ext = file.name.substring(extStart, file.name.length).toUpperCase();
      self.remove();
      var html = '<input class="change-photo-btn upload" type="file" accept="image/jpg,image/png,image/jpeg,image/bmp" style="display:none;">';
      $(".btn-change-photo .label").append(html);
      if (ext != ".BMP" && ext != ".PNG" && ext != ".JPEG" && ext != ".JPG") {
         notify('danger', '仅支持jpg、jpeg、png、bmp格式');
      } else {
         var size = file.size; //得到的是字节
         var maxSize = 5 * 1024 * 1024;
         if (size > maxSize) {
            notify("danger", "上传的图片大小不能大于5M");
         } else {
            blob = URL.createObjectURL(file);
            var image = new Image();
            var height = 0;
            var width = 0;
            image.src = blob;
            image.onload = function() { //图片尺寸校验
               width = image.width;
               height = image.height;
               if(width!=1920 && height!=1080){
                  notify("danger", "图片限定尺寸为1920x1080");
               }else {
                   imageFile = file;
                   isChange = true;
                   $('.photo img').attr('src', blob);
               }
            }
         }
      }
   }
    //输入框的值为空时,显示提示语
    function errorShow(self) {
        $(".warningBox").show();
        self.prev().addClass("warring");
    }
    function errorHide(self) {
        $(".warningBox").hide();
        self.prev().removeClass("warring");
    }
});
//# sourceMappingURL=management.js.map
