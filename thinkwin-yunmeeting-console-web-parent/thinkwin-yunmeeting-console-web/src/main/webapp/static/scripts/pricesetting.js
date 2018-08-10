$(function(){
  //数据对象
  var _roomDiscountArr = []; //会议室折扣信息
  var _personDiscountArr = []; //人员折扣信息
  var _spaceDiscountArr = [];  //存储空间折扣信息
  var _timeDiscountArr = [];  //时长折扣信息
  var _priceRule = {};
  var _roomDiscountType = 0; //会议室折扣类型
  var _spaceDiscountType = 0; //存储空间折扣类型

  //输入框只能输入数字
  $("body").on('keydown',"input[type='int']",function(e){
    var k = (typeof e.which == "number") ? e.which : e.keyCode; 
      console.log(k); 
      if((k<48 || (k<96&&k>57) || k>105) && k!=8){  
          return false;  
      }  
      if(e.shiftKey) {  
          return false;  
      } 
      return true;
  });
  $("body").on('keydown',"input[type='float']",function(e){  
      var k = (typeof e.which == "number") ? e.which : e.keyCode;  
      var value = $(this).val();
      if((k==190 || k==110) && !e.shiftKey){
        if(value.indexOf('.')>=0) return false;
        return true;
      }
      if((k<48 || (k<96&&k>57) || k>105) && k!=8){  
          return false;  
      }  
      if(e.shiftKey) {  
          return false;  
      }  
      return true; 
  });
  $("body").on('keyup',"input[type='int']",function(e){
      var str = $(this).val();
      $(this).val(str.replace(/[^\d]/g,''));
  });
  $("body").on('keyup',"input[type='float']",function(e){
      var str = $(this).val();
      $(this).val(str.replace(/[^0-9.]/g,''));
  });

  //成为焦点后保存输入的数字
  $('body').on('focus',"input",function(){
    $(this).data('value',$(this).val());
    if(!$(this).data('default')){
      $(this).data('default',$(this).val());
    }
  });

  //失焦之后判断输入的数字是否在指定范围
  $("body").on('blur',".content input[type='int']",function(e){
    //取到input最大最小值
    var max = $(this).data('max');
    var min = $(this).data('min');
    //取到input值
    var value = $(this).val();
    //如果值为空边框标红
    if(value == '' || value.length == 0){
      $(this).val($(this).data('value'));
      // $(this).addClass('error');
      return;
    }
    //判断是否为数字格式
    if(parseInt(value)!=NaN){
      //如果大于最大值，值改为最大值
      if(parseInt(value)>parseInt(max)){
        $(this).val(max);
      }
      //如果小于最小值，值改为最小值
      if(parseInt(value)<parseInt(min)){
        $(this).val(min);
      }
    }else {
      $(this).val($(this).data('value'));
    }
    if($(this).data('default') != $(this).val()){
      $('.header .save-btn').attr('disabled', false);
    }
  });
  $("body").on('blur',".content input[type='float']",function(e){
    //取到input最大最小值
    var max = $(this).data('max');
    var min = $(this).data('min');
    //取到input值
    var value = $(this).val();
    //如果值为空边框标红
    if(value == '' || value.length == 0){
      // $(this).addClass('error');
      $(this).val($(this).data('value'));
      return;
    }
    //判断是否为数字格式
    if(parseFloat(value)!=NaN){
      //保留小数点后两位
      if(value.indexOf('.')>=0){
        value = Math.floor(parseFloat(value) * 100) / 100;
        if(value.toString().indexOf('.')>=0 &&value.toString().split('.')[1].length<2){
          value = value.toString()+'0';
        }
        $(this).val(value);
      }
      //如果大于最大值，值改为最大值
      if(parseFloat(value)>parseFloat(max)){
        $(this).val(max);
      }
      //如果小于最小值，值改为最小值
      if(parseFloat(value)<parseFloat(min)){
        $(this).val(min);
      }
      if(parseFloat(value)==parseFloat(0)){
        $(this).val(0);
      }
    }else {
      $(this).val($(this).data('value'));
    }
    if($(this).data('default') != $(this).val()){
      $('.header .save-btn').attr('disabled', false);
    }
  });
  //免费版员工人数输入框失焦事件
  $("#free-personnumber").on('blur',function(e) {
    //员工人数数量上限，免费版员工人数，员工人数起购数量联动校验
    checknumber1($("#person-maxnumber"),$(this),$("#person-buynumber"));
    //设置员工人数起购数量最小值
    $("#person-buynumber").data('min',$(this).val());
  });
  //免费版会议室数量输入框失焦事件
  $("#free-roomnumber").on('blur',function(e){
    //会议室数量上限，免费版会议室数量，会议室免费数量联动校验
    checknumber1($("#room-maxnumber"),$(this),$("#room-freenumber"));
    //设置会议室免费数量最小值
    $("#room-freenumber").data('min',$(this).val());

  });
  //免费版存储空间输入框失焦事件
  $("#free-spacenumber").on('blur',function(e){
    //存储空间数量上限，免费版存储空间数量，存储空间免费数量联动校验
    checknumber1($("#space-maxnumber"),$(this),$("#space-freenumber"));
    //设置存储空间免费数量最小值
    $("#space-freenumber").data('min',$(this).val());
  });
  //人员数量上限输入框失焦事件
  $("#person-maxnumber").on('blur',function(e){
    //员工人数数量上限，免费版员工人数，员工人数起购数量联动校验
    checknumber1($(this),$("#free-personnumber"),$("#person-buynumber"));
    //设置免费配置数量最大值
    $("#free-personnumber").data('max',$(this).val());
    //设置员工人数起购数量最大值
    $("#person-buynumber").data('max',$(this).val());
    //人数上限，单位步长联动校验
    checknumber1($(this),null,$("#person-stepnumber"));
    //设置单位步长最大值
    $("#person-stepnumber").data('max',$(this).val());
    //更新折扣显示
    removeMax(_personDiscountArr, $(this).val());
    //填充折扣数据
    $("#person-discount").empty();
    for (var i = 0; i < _personDiscountArr.length; i++) {
      var html = '<span class="discount-span">'+_personDiscountArr[i].unit+'人打<font color="#fa553c">'+_personDiscountArr[i].discount+'折</font></span>'
      $("#person-discount").append(html);
    };
  });
  //会议室数量上限输入框失焦事件
  $("#room-maxnumber").on('blur',function(e){
    //会议室数量上限，免费版会议室数量，会议室免费数量联动校验
    checknumber1($(this),$("#free-roomnumber"),$("#room-freenumber"));
    //设置免费配置数量最大值
    $("#free-roomnumber").data('max',$(this).val());
    //设置会议室免费数量最大值
    $("#room-freenumber").data('max',$(this).val());
    //会议室数量上限，单位步长联动校验
    checknumber1($(this),null,$("#room-stepnumber"));
    //设置单位步长最大值
    $("#room-stepnumber").data('max',$(this).val());
    //更新折扣显示
    removeMax(_roomDiscountArr, $(this).val());
    //填充折扣数据
    $("#room-discount").empty();
    for (var i = 0; i < _roomDiscountArr.length; i++) {
      var html = '<span class="discount-span">'+_roomDiscountArr[i].unit+'间打<font color="#fa553c">'+_roomDiscountArr[i].discount+'折</font></span>'
      $("#room-discount").append(html);
    };
  });
  //存储空间数量上限输入框失焦事件
  $("#space-maxnumber").on('blur',function(e){
    //存储空间数量上限，免费版存储空间数量，存储空间免费数量联动校验
    checknumber1($(this),$("#free-spacenumber"),$("#space-freenumber"));
    //设置免费配置数量最大值
    $("#free-spacenumber").data('max',$(this).val());
    //设置存储空间免费数量最大值
    $("#space-freenumber").data('max',$(this).val());
    //存储空间上限，单位步长联动校验
    checknumber1($(this),null,$("#space-stepnumber"));
    //设置单位步长最大值
    $("#space-stepnumber").data('max',$(this).val());
    //更新折扣显示
    removeMax(_spaceDiscountArr, $(this).val());
    //填充折扣数据
    $("#space-discount").empty();
    for (var i = 0; i < _spaceDiscountArr.length; i++) {
      var html = '<span class="discount-span">'+_spaceDiscountArr[i].unit+'GB打<font color="#fa553c">'+_spaceDiscountArr[i].discount+'折</font></span>'
      $("#space-discount").append(html);
    };
  });

  //单选
  $(".form-check-input[name='roomRadioOptions']").on('change',function(e){
    var value = $(this).val();
    if(parseInt(value)==_roomDiscountType){
      return;
    }
    $('.header .save-btn').attr('disabled',false);
    // _roomDiscountArr = [];
    // $("#room-discount").empty();
    _roomDiscountType = parseInt(value);
    if(_roomDiscountType==1){
      //显示修改档位
      $('.room-count .discount-info').show();
    }else {
      $('.room-count .discount-info').hide();
    }
  })
  $(".form-check-input[name='spaceRadioOptions']").on('change',function(e){
    var value = $(this).val();
    if(parseInt(value)==_spaceDiscountType){
      return;
    }
    $('.header .save-btn').attr('disabled',false);
    // _spaceDiscountArr = [];
    // $("#space-discount").empty();
    _spaceDiscountType = parseInt(value);
    if(_spaceDiscountType==1){
      //显示修改档位
      $('.storage-space .discount-info').show();
    }else {
      $('.storage-space .discount-info').hide();
    }
  });
  //修改档位
  //人员数量折扣档位
  $("#personDiscount").on('show.bs.modal', function (event) { 
      var max = 0; 
      if($("#person-maxnumber").val()==''){
        max = 99999;
      }else {
        max = $("#person-maxnumber").val();
      }
      $("#personDiscount input.stall-number").data('max',max);
      $('#personDiscount ul.discount-stall').empty();
      if(_personDiscountArr.length==0){
        var html = '<li class="row"><span class="col-label">满</span><input class="col-form stall-number" placeholder="请输入数值" type="int" data-max="'+ max+'" data-min="1"><span class="col-label">人打</span><input class="col-form discount-number" placeholder="请输入数值" type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract icon icon-fwb-deduct"></span></li>';
        $('#personDiscount ul.discount-stall').append(html);
      }else {
        for (var i = 0; i < _personDiscountArr.length; i++) {
          //折扣信息显示到输入框中
          var html = '<li class="row"><span class="col-label">满</span><input class="col-form stall-number" placeholder="请输入数值" value='+_personDiscountArr[i].unit+' type="int" data-max="'+ max+'" data-min="1"><span class="col-label">人打</span><input class="col-form discount-number" placeholder="请输入数值" value='+ _personDiscountArr[i].discount+' type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract icon icon-fwb-deduct"></span></li>';
          $('#personDiscount ul.discount-stall').append(html);
        }
      }
      
  });
  //添加档位
  $("#personDiscount .addDiscount").on('click',function(e){
    var max = 0; 
    if($("#person-maxnumber").val()==''){
      max = 99999;
    }else {
      max = $("#person-maxnumber").val();
    }
    var html = '<li class="row"><span class="col-label">满</span><input class="col-form stall-number" placeholder="请输入数值" type="int" data-max="'+max+'" data-min="1"><span class="col-label">人打</span><input class="col-form discount-number" placeholder="请输入数值" type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract icon icon-fwb-deduct"></span></li>';
    $('#personDiscount ul.discount-stall').append(html);
    if($("#personDiscount ul li").length > 1){
      $('#personDiscount li span.col-subtract').show();
    }
  });
  //删除档位
  $("body").on('click','#personDiscount li span.col-subtract',function(e){
    var index = $(this).parent().index();
    $(this).parent().remove();
  });
  //修改按钮事件
  $('#personDiscount .btn-primary').on('click',function(e){
    var arr = [];
    //折扣档列表
    var discounts = $('#personDiscount li');
    //判断是否有没输入的值
    var canSave = true;
    for (var i = 0; i < discounts.length; i++) {
      //折扣起始值
      var liObj = $('#personDiscount li').eq(i);
      var stallObj = liObj.find('.stall-number');
      var discountObj = $('#personDiscount li').eq(i).find('.discount-number');
      if(stallObj.val()==''){
        stallObj.addClass('error');
        canSave = false;
      }else {
        stallObj.removeClass('error');
      }
      if(discountObj.val()==''){
        discountObj.addClass('error');
        canSave = false;
      }else {
        discountObj.removeClass('error');
      }
      if(stallObj.val()!='' && discountObj.val()!=''){
        var obj = {};
        obj.unit = stallObj.val();
        obj.discount = discountObj.val();
        arr.push(obj);
      }
    }
    if(canSave){
      //根据unit字段去重
      _personDiscountArr = uniqeByKeys(arr,['unit']);
      //根据unit字段排序
      _personDiscountArr = _personDiscountArr.sort(by('unit'));
      $('#personDiscount').modal('hide');
      //填充折扣数据
      $("#person-discount").empty();
      for (var i = 0; i < _personDiscountArr.length; i++) {
        var html = '<span class="discount-span">'+_personDiscountArr[i].unit+'人打<font color="#fa553c">'+_personDiscountArr[i].discount+'折</font></span>'
        $("#person-discount").append(html);
      };
      $('.header .save-btn').attr('disabled', false);
      notify('success','档位修改成功');
    }
    
  });

  //会议室数折扣档位
  $("#roomDiscount").on('show.bs.modal', function (event) { 
      var max = 0; 
      if($("#room-maxnumber").val()==''){
        max = 99999;
      }else {
        max = $("#room-maxnumber").val();
      }
      $("#roomDiscount input.stall-number").data('max',max);
      $('#roomDiscount ul.discount-stall').empty();
      if(_roomDiscountArr.length==0){
        var html = '<li class="row"><span class="col-label">满</span><input class="col-form stall-number" placeholder="请输入数值" type="int" data-max="'+max+'" data-min="1"><span class="col-label">间打</span><input class="col-form discount-number" placeholder="请输入数值" type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract icon icon-fwb-deduct"></span></li>';
        $('#roomDiscount ul.discount-stall').append(html);
      }else {
        for (var i = 0; i < _roomDiscountArr.length; i++) {
          //折扣信息显示到输入框中
          var html = '<li class="row"><span class="col-label">满</span><input class="col-form stall-number" placeholder="请输入数值" value='+_roomDiscountArr[i].unit+' type="int" data-max="'+max+'" data-min="1"><span class="col-label">间打</span><input class="col-form discount-number" placeholder="请输入数值" value='+ _roomDiscountArr[i].discount+' type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract icon icon-fwb-deduct"></span></li>';
          $('#roomDiscount ul.discount-stall').append(html);
        }
      }
  });
  //添加档位
  $("#roomDiscount .addDiscount").on('click',function(e){
    var max = 0; 
      if($("#room-maxnumber").val()==''){
        max = 99999;
      }else {
        max = $("#room-maxnumber").val();
      }
    var html = '<li class="row"><span class="col-label">满</span><input class="col-form stall-number" placeholder="请输入数值" type="int" data-max="'+max+'" data-min="1"><span class="col-label">间打</span><input class="col-form discount-number" placeholder="请输入数值" type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract icon icon-fwb-deduct"></span></li>';
    $('#roomDiscount ul.discount-stall').append(html);
    if($("#roomDiscount ul li").length > 1){
      $('#roomDiscount li span.col-subtract').show();
    }
  });
  //删除档位
  $("body").on('click','#roomDiscount li span.col-subtract',function(e){
    var index = $(this).parent().index();
    $(this).parent().remove();
  });
  //修改按钮事件
  $('#roomDiscount .btn-primary').on('click',function(e){
    var arr = [];
    //折扣档列表
    var discounts = $('#roomDiscount li');
    //判断是否有没输入的值
    var canSave = true;
    for (var i = 0; i < discounts.length; i++) {
      //折扣起始值
      var liObj = $('#roomDiscount li').eq(i);
      var stallObj = liObj.find('.stall-number');
      var discountObj = $('#roomDiscount li').eq(i).find('.discount-number');
      if(stallObj.val()==''){
        stallObj.addClass('error');
        canSave = false;
      }else {
        stallObj.removeClass('error');
      }
      if(discountObj.val()==''){
        discountObj.addClass('error');
        canSave = false;
      }else {
        discountObj.removeClass('error');
      }
      if(stallObj.val()!='' && discountObj.val()!=''){
        var obj = {};
        obj.unit = stallObj.val();
        obj.discount = discountObj.val();
        arr.push(obj);
      }
    }
    if(canSave){
      //根据unit字段去重
      _roomDiscountArr = uniqeByKeys(arr,['unit']);
      //根据unit字段排序
      _roomDiscountArr = _roomDiscountArr.sort(by('unit'));
      $('#roomDiscount').modal('hide');
      //填充折扣数据
      $("#room-discount").empty();
      for (var i = 0; i < _roomDiscountArr.length; i++) {
        var html = '<span class="discount-span">'+_roomDiscountArr[i].unit+'间打<font color="#fa553c">'+_roomDiscountArr[i].discount+'折</font></span>'
        $("#room-discount").append(html);
      };
      $('.header .save-btn').attr('disabled', false);
        notify('success','档位修改成功');
    }
  });
  //存储空间折扣档位
  $("#spaceDiscount").on('show.bs.modal', function (event) { 
      var max = 0; 
      if($("#space-maxnumber").val()==''){
        max = 99999;
      }else {
        max = $("#space-maxnumber").val();
      }
      $("#spaceDiscount input.stall-number").data('max',max);
      $('#spaceDiscount ul.discount-stall').empty();
      if(_spaceDiscountArr.length==0){
        var html = '<li class="row"><span class="col-label">满</span><input class="col-form stall-number" placeholder="请输入数值" type="int" data-max="'+max+'" data-min="1"><span class="col-label">GB打</span><input class="col-form discount-number" placeholder="请输入数值" type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract icon icon-fwb-deduct"></span></li>';
        $('#spaceDiscount ul.discount-stall').append(html);
      }else {
        for (var i = 0; i < _spaceDiscountArr.length; i++) {
          //折扣信息显示到输入框中
          var html = '<li class="row"><span class="col-label">满</span><input class="col-form stall-number" placeholder="请输入数值" value='+_spaceDiscountArr[i].unit+' type="int" data-max="'+max+'" data-min="1"><span class="col-label">GB打</span><input class="col-form discount-number" placeholder="请输入数值" value='+ _spaceDiscountArr[i].discount+' type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract icon icon-fwb-deduct"></span></li>';
          $('#spaceDiscount ul.discount-stall').append(html);
        }
      }
  });
  //添加档位
  $("#spaceDiscount .addDiscount").on('click',function(e){
    var max = 0; 
    if($("#space-maxnumber").val()==''){
      max = 99999;
    }else {
      max = $("#space-maxnumber").val();
    }
    var html = '<li class="row"><span class="col-label">满</span><input class="col-form stall-number" placeholder="请输入数值" type="int" data-max="'+max+'" data-min="1"><span class="col-label">GB打</span><input class="col-form discount-number" placeholder="请输入数值" type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract icon icon-fwb-deduct"></span></li>';
    $('#spaceDiscount ul.discount-stall').append(html);
    if($("#spaceDiscount ul li").length > 1){
      $('#spaceDiscount li span.col-subtract').show();
    }
  });
  //删除档位
  $("body").on('click','#spaceDiscount li span.col-subtract',function(e){
    var index = $(this).parent().index();
    $(this).parent().remove();
  });
  //修改按钮事件
  $('#spaceDiscount .btn-primary').on('click',function(e){
    var arr = [];
    //折扣档列表
    var discounts = $('#spaceDiscount li');
    //判断是否有没输入的值
    var canSave = true;
    for (var i = 0; i < discounts.length; i++) {
      //折扣起始值
      var liObj = $('#spaceDiscount li').eq(i);
      var stallObj = liObj.find('.stall-number');
      var discountObj = $('#spaceDiscount li').eq(i).find('.discount-number');
      if(stallObj.val()==''){
        stallObj.addClass('error');
        canSave = false;
      }else {
        stallObj.removeClass('error');
      }
      if(discountObj.val()==''){
        discountObj.addClass('error');
        canSave = false;
      }else {
        discountObj.removeClass('error');
      }
      if(stallObj.val()!='' && discountObj.val()!=''){
        var obj = {};
        obj.unit = stallObj.val();
        obj.discount = discountObj.val();
        arr.push(obj);
      }
    }
    if(canSave){
      //根据unit字段去重
      _spaceDiscountArr = uniqeByKeys(arr,['unit']);
      //根据unit字段排序
      _spaceDiscountArr = _spaceDiscountArr.sort(by('unit'));
      $('#spaceDiscount').modal('hide');
      //填充折扣数据
      $("#space-discount").empty();
      for (var i = 0; i < _spaceDiscountArr.length; i++) {
        var html = '<span class="discount-span">'+_spaceDiscountArr[i].unit+'GB打<font color="#fa553c">'+_spaceDiscountArr[i].discount+'折</font></span>'
        $("#space-discount").append(html);
      };
      $('.header .save-btn').attr('disabled', false);
        notify('success','档位修改成功');
    }
  });

  //时长折扣档位
  $("#timeDiscount").on('show.bs.modal', function (event) { 
    $('#timeDiscount ul.discount-stall').empty();
    if(_timeDiscountArr.length==0){
      var html = '<li class="row"><input class="col-form stall-number" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="dropdown-toggle dropdownMenuButton" data-type="0">年</span><span class="col-label">打</span><input class="col-form discount-number" placeholder="请输入数值" type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract icon icon-fwb-deduct"></span></li>';
      $('#timeDiscount ul.discount-stall').append(html);
    }else {
      for (var i = 0; i < _timeDiscountArr.length; i++) {
        //折扣信息显示到输入框中
        var max = 99999;
        var uom = '年';
        var type = 0;
        if(_timeDiscountArr[i].uom==0){
          //年
          max = 99999;
          uom = '年';
          type= 0;
        }else {
          //月
          max = 11;
          uom = '月';
          type = 1;
        }
        var html = '<li class="row"><input class="col-form stall-number" placeholder="请输入数值" value="'+ _timeDiscountArr[i].unit +'" type="int" data-max="'+max+'" data-min="1"><span class="dropdown-toggle dropdownMenuButton" data-type="'+type+'">'+uom+'</span><span class="col-label">打</span><input class="col-form discount-number" placeholder="请输入数值" value="'+_timeDiscountArr[i].discount+'" type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract icon icon-fwb-deduct"></span></li>';
        $('#timeDiscount ul.discount-stall').append(html);
      }
    }
    if($("#timeDiscount ul li").length <= 1){
      $('#timeDiscount li span.col-subtract').hide();
    }
  });
  //下拉框事件
  $('body').on('click','.dropdownMenuButton',function(e){
    e.stopPropagation();
    // $('#timeDiscount .dropdown-menu').data('index',$(this).parent().index());
    var top = $(this).offset().top;
    var left = $(this).offset().left;
    top = top + 31;
    // var scrollTop = $('#timeDiscount ul.discount-stall').scrollTop();
    // if(scrollTop>0){
    //   top += 23;
    // }
    // if(top==377){
    //   top += 10;
    // }

    // if(scrollTop<=0&&top<99){
    //   top = 99;
    // }else if(scrollTop>=48){
    //     top = top + (scrollTop-25);
    // }

    $('.timelength-down.dropdown-menu').css('top',top+'px');
    $('.timelength-down.dropdown-menu').css('left',left+'px');
    $('.timelength-down.dropdown-menu').show();
    $('.timelength-down.dropdown-menu').data('index',$(this).parent().index());
  });
  $('body').on('click',function(e){
    $('.timelength-down.dropdown-menu').hide();
  });
  $('#timeDiscount ul').scroll(function(){
    $('.timelength-down.dropdown-menu').hide();
  })
  //添加档位
  $("#timeDiscount .addDiscount").on('click',function(e){
    var html = '<li class="row"><input class="col-form stall-number" placeholder="请输入数值" type="int" data-max="99999" data-min="1"><span class="dropdown-toggle dropdownMenuButton" data-type="0">年</span><span class="col-label">打</span><input class="col-form discount-number" placeholder="请输入数值" type="int" data-max="100" data-min="0"><span class="col-label">折</span><span class="col-subtract icon icon-fwb-deduct"></span></li>';
    $('#timeDiscount ul.discount-stall').append(html);
    if($("#timeDiscount ul li").length > 1){
      $('#timeDiscount li span.col-subtract').show();
    }
  });
  //删除档位
  $("body").on('click','#timeDiscount li span.col-subtract',function(e){
    var index = $(this).parent().index();
    $(this).parent().remove();
    if($("#timeDiscount ul li").length <= 1){
      $('#timeDiscount li span.col-subtract').hide();
    }
  });
  //修改按钮事件
  $('#timeDiscount .btn-primary').on('click',function(e){
    var arr = [];
    //折扣档列表
    var discounts = $('#timeDiscount li');
    //判断是否有没输入的值
    var canSave = true;
    for (var i = 0; i < discounts.length; i++) {
      //折扣起始值
      var stallObj = $('#timeDiscount li').eq(i).find('.stall-number');
      var uomObj = $('#timeDiscount li').eq(i).find('.dropdownMenuButton');
      var discountObj = $('#timeDiscount li').eq(i).find('.discount-number');
      if(stallObj.val()==''){
        stallObj.addClass('error');
        canSave = false;
      }else {
        stallObj.removeClass('error');
      }
      if(discountObj.val()==''){
        discountObj.addClass('error');
        canSave = false;
      }else {
        discountObj.removeClass('error');
      }
      if(stallObj.val()!='' && discountObj.val()!=''){
        var obj = {};
        obj.unit = stallObj.val();
        obj.uom = uomObj.data('type');
        obj.discount = discountObj.val();
        arr.push(obj);
      }
    }
    if(canSave){
      //根据unit字段去重
      _timeDiscountArr = uniqeByKeys(arr,['unit','uom']);
      //根据uom分成年月两部分，分开排序
      var yearDiscount = []; //年折扣
      var monthDiscount = []; //月折扣
      for (var i = 0; i < _timeDiscountArr.length; i++) {
        if(_timeDiscountArr[i].uom==0){
          yearDiscount.push(_timeDiscountArr[i]);
        }else {
          monthDiscount.push(_timeDiscountArr[i]);
        }
      };
      //根据unit字段排序
      yearDiscount = yearDiscount.sort(by('unit'));
      monthDiscount = monthDiscount.sort(by('unit'))
      _timeDiscountArr = monthDiscount.concat(yearDiscount);
      $('#timeDiscount').modal('hide');
      //填充折扣数据
      $("#time-discount").empty();
      for (var i = 0; i < _timeDiscountArr.length; i++) {
        var html = '';
        var uom = '';
        if(_timeDiscountArr[i].uom==1){
          //月
          uom = '月';
        }else {
          //年
          uom = '年';
        }
        if(_timeDiscountArr[i].discount&&_timeDiscountArr[i].discount!=100){
            html = '<span class="timelength-span">'+_timeDiscountArr[i].unit+uom+'<label class="discount">'+_timeDiscountArr[i].discount+'折</label></span>'
        }else {
            html = '<span class="timelength-span">'+_timeDiscountArr[i].unit+uom+'</span>'
        }
        $("#time-discount").append(html);
      };
      $('.header .save-btn').attr('disabled', false);
        notify('success','档位修改成功');
    }
  });
  $('.timelength-down .dropdown-item').on('click',function(e){
    var type = $(this).data('type');
    var index = $(this).parent().data('index');
    if(type==0){
      //年
      $('#timeDiscount li').eq(index).find('.stall-number').data('max',99999);
    }else {
      //月
      $('#timeDiscount li').eq(index).find('.stall-number').data('max',11);
      //判断最大值超过11改为11
      if(parseInt($('#timeDiscount li').eq(index).find('.stall-number').val())>11){
        $('#timeDiscount li').eq(index).find('.stall-number').val(11);
      }
    }
    $('#timeDiscount li').eq(index).find('.dropdownMenuButton').data('type',type);
    var text = $(this).text();
    $('#timeDiscount li').eq(index).find('.dropdownMenuButton').text(text);
  })

  //弹框失焦变红
    //失焦之后判断输入的数字是否在指定范围
    $("body").on('blur',".modal input[type='int']",function(e){
        //取到input最大最小值
        var max = $(this).data('max');
        var min = $(this).data('min');
        //取到input值
        var value = $(this).val();
        //如果值为空边框标红
        if(value == '' || value.length == 0){
            $(this).val('');
            $(this).addClass('error');
            return;
        }else {
            $(this).removeClass('error');
            if(parseInt($(this).val())==parseInt(0)){
                $(this).val(0);
            }
        }
        //判断是否为数字格式
        if(parseInt(value)!=NaN){
            $(this).val(parseInt(value));
            //如果大于最大值，值改为最大值
            if(parseInt(value)>parseInt(max)){
                $(this).val(max);
            }
            //如果小于最小值，值改为最小值
            if(parseInt(value)<parseInt(min)){
                $(this).val(min);
            }
        }else {
            $(this).val('');
            $(this).addClass('error');
        }
        if($(this).data('default') != $(this).val()){
            $('.header .save-btn').attr('disabled', false);
        }
    });
    $("body").on('blur',".modal input[type='float']",function(e){
        //取到input最大最小值
        var max = $(this).data('max');
        var min = $(this).data('min');
        //取到input值
        var value = $(this).val();
        //如果值为空边框标红
        if(value == '' || value.length == 0){
            $(this).val('');
            $(this).addClass('error');
            return;
        }else {
            $(this).removeClass('error');
            if(parseInt($(this).val())==parseInt(0)){
                $(this).val(0);
            }
        }
        //判断是否为数字格式
        if(parseFloat(value)!=NaN){
            //保留小数点后两位
            if(value.indexOf('.')>=0){
                value = Math.floor(parseFloat(value) * 100) / 100;
                $(this).val(value);
            }
            //如果大于最大值，值改为最大值
            if(parseFloat(value)>parseFloat(max)){
                $(this).val(max);
            }
            //如果小于最小值，值改为最小值
            if(parseFloat(value)<parseFloat(min)){
                $(this).val(min);
            }
            if(parseFloat(value)==parseFloat(0)){
                $(this).val(0);
            }
        }else {
            $(this).val($(this).data('value'));
        }
        if($(this).data('default') != $(this).val()){
            $('.header .save-btn').attr('disabled', false);
        }
    });
  function setData() {
      //取值
      //免费版配置
      _priceRule.id = '';
      for (var i = 0; i < _priceRule.freeAccountConfig.length; i++) {
          if(_priceRule.freeAccountConfig[i].sku==102){
              _priceRule.freeAccountConfig[i].qty = $('#free-personnumber').val();
          }else if(_priceRule.freeAccountConfig[i].sku==100){
              _priceRule.freeAccountConfig[i].qty = $('#free-roomnumber').val();
          }else {
              _priceRule.freeAccountConfig[i].qty = $('#free-spacenumber').val();
          }
      };
      //时长折扣
      _priceRule.serviceTermDiscount = _timeDiscountArr;
      //商品价格配置
      for (var i = 0; i < _priceRule.skuDiscount.length; i++) {
          if(_priceRule.skuDiscount[i].sku==102){
              //员工人数
              //单价
              _priceRule.skuDiscount[i].config.unitPrice = $('#person-price').val();
              //起购数量
              _priceRule.skuDiscount[i].config.min = $('#person-buynumber').val();
              //数量上限
              _priceRule.skuDiscount[i].config.max = $('#person-maxnumber').val();
              //单位步长
              _priceRule.skuDiscount[i].config.step = $('#person-stepnumber').val();
              //折扣信息
              _priceRule.skuDiscount[i].config.tiers = _personDiscountArr;
          }else if(_priceRule.skuDiscount[i].sku==100){
              //会议室数
              //单价
              _priceRule.skuDiscount[i].config.unitPrice = $('#room-price').val();
              //免费间数
              _priceRule.skuDiscount[i].config.free = $('#room-freenumber').val();
              //数量上限
              _priceRule.skuDiscount[i].config.max = $('#room-maxnumber').val();
              //单位步长
              _priceRule.skuDiscount[i].config.step = $('#room-stepnumber').val();
              //折扣类型
              _priceRule.skuDiscount[i].discountOption = _roomDiscountType;
              //折扣信息
              if(_roomDiscountType==1){
                  _priceRule.skuDiscount[i].config.tiers = _roomDiscountArr;
              }else {
                  _priceRule.skuDiscount[i].config.tiers = [];
              }
          }else {
              //存储空间
              //单价
              _priceRule.skuDiscount[i].config.unitPrice = $('#space-price').val();
              //免费间数
              _priceRule.skuDiscount[i].config.free = $('#space-freenumber').val();
              //数量上限
              _priceRule.skuDiscount[i].config.max = $('#space-maxnumber').val();
              //单位步长
              _priceRule.skuDiscount[i].config.step = $('#space-stepnumber').val();
              //折扣类型
              _priceRule.skuDiscount[i].discountOption = _spaceDiscountType;
              //折扣信息
              if(_spaceDiscountType==1){
                  _priceRule.skuDiscount[i].config.tiers = _spaceDiscountArr;
              }else {
                  _priceRule.skuDiscount[i].config.tiers = [];
              }
          }
      };
  }
  
  //预览
  $('.header .preview-btn').on('click',function (e) {
      setData();
      var userInfo = JSON.parse(localStorage.getItem('console-userinfo'));

      $('.header .preview-btn').attr('disabled', true);
      var tempWin = window.open('');
      fetchs.post('/promotion/setPricesettingProviewData',{'data':JSON.stringify(_priceRule)},function(res){
          $('.header .preview-btn').attr('disabled', false);
          if(res.ifSuc==1){
              tempWin.location = "/promotion/gotoPricesettingProviewPage"+"?token="+_userInfo.token;
          }else {
              notify('danger',res.msg);
              tempWin.close();
          }
      })
  })
  //保存
  $('#saveModal .btn-primary').on('click',function(e){
    //保存
    setData();
    console.log(_priceRule);
    var userInfo = JSON.parse(localStorage.getItem('console-userinfo'));

    $('#saveModal .btn-primary').attr('disabled', true);
    fetchs.post('/promotion/rule/update',{'data':JSON.stringify(_priceRule)},function(res){
      $('#saveModal').modal('hide');
      $('#saveModal .btn-primary').attr('disabled', false);
      if(res.ifSuc==1){
        notify('success','定价配置保存成功');
        $('.header .save-btn').attr('disabled', true);
        $('input').data('default','');
        if(_roomDiscountType!=1){
          _roomDiscountArr = [];
          $("#room-discount").empty();
        }
        if(_spaceDiscountType!=1){
          _spaceDiscountArr = [];
          $("#space-discount").empty();
        }
      }else {
        notify('danger',res.msg);
      }
    })
  })


  //数据交互
  //获取定价配置
  getpriceSetting();
  function getpriceSetting(){
    fetchs.get('/promotion/allRules?token='+_userInfo.token,function(res){
      console.log(res);
      if(res.ifSuc==1){
        _priceRule = res.data;
        //赋值
        //免费版配置
        for (var i = 0; i < _priceRule.freeAccountConfig.length; i++) {
          var value = _priceRule.freeAccountConfig[i].qty;
          if(_priceRule.freeAccountConfig[i].sku==102){
            $('#free-personnumber').val(value);
          }else if(_priceRule.freeAccountConfig[i].sku==100){
            $('#free-roomnumber').val(value);
          }else {
            $('#free-spacenumber').val(value);
          }
        };
        //时长折扣
        _timeDiscountArr = _priceRule.serviceTermDiscount;
        var yearDiscount = [];
        var monthDiscount = [];
        for(var i = 0; i <_timeDiscountArr.length; i++){
          if(_timeDiscountArr[i].uom==1){
            monthDiscount.push(_timeDiscountArr[i]);
          }else {
            yearDiscount.push(_timeDiscountArr[i]);
          }
        }
        //根据unit字段排序
        yearDiscount = yearDiscount.sort(by('unit'));
        monthDiscount = monthDiscount.sort(by('unit'))
        _timeDiscountArr = monthDiscount.concat(yearDiscount);
        //填充折扣数据
        $("#time-discount").empty();
        for (var i = 0; i < _timeDiscountArr.length; i++) {
          var html = '';
          var uom = '';
          if(_timeDiscountArr[i].uom==1){
            //月
            uom = '月';
          }else {
            //年
            uom = '年';
          }
          if(_timeDiscountArr[i].discount&&_timeDiscountArr[i].discount!=100){
              html = '<span class="timelength-span">'+_timeDiscountArr[i].unit+uom+'<label class="discount">'+_timeDiscountArr[i].discount+'折</label></span>'
          }else {
              html = '<span class="timelength-span">'+_timeDiscountArr[i].unit+uom+'</span>'
          }
          $("#time-discount").append(html);
        };
        //商品价格配置
        for (var i = 0; i < _priceRule.skuDiscount.length; i++) {
          var discount = _priceRule.skuDiscount[i];
          if(_priceRule.skuDiscount[i].sku==102){
            //员工人数
            //单价
            $('#person-price').val(discount.config.unitPrice);
            //起购数量
            $('#person-buynumber').val(discount.config.min);
            //起购数量最小值
            $('#person-buynumber').data('min',$('#free-personnumber').val());
            if(parseInt($('#person-buynumber').val())<parseInt($('#free-personnumber').val())){
              $('#person-buynumber').val($('#free-personnumber').val());
            }
            //数量上限
            $('#person-maxnumber').val(discount.config.max);
            //起购数量最大值
            $('#person-buynumber').data('max',$('#person-maxnumber').val());
            if(parseInt($('#person-buynumber').val())>parseInt($('#person-maxnumber').val())){
              $('#person-buynumber').val($('#person-maxnumber').val());
            }
            //免费配置最大值
            $('#free-personnumber').data('max',$('#person-maxnumber').val());
            if(parseInt($('#free-personnumber').val())>parseInt($('#person-maxnumber').val())){
              $('#free-personnumber').val($('#person-maxnumber').val());
            }
            //单位步长
            $('#person-stepnumber').val(discount.config.step);
            //单位步长最大值
            $('#person-stepnumber').data('max',$('#person-maxnumber').val());
            if(parseInt($('#person-stepnumber').val())>parseInt($('#person-maxnumber').val())){
              $('#person-stepnumber').val($('#person-maxnumber').val());
            }
            //折扣信息
            _personDiscountArr = discount.config.tiers;
            //排序
            _personDiscountArr = _personDiscountArr.sort(by('unit'));
            //填充折扣数据
            $("#person-discount").empty();
            for (var j = 0; j < _personDiscountArr.length; j++) {
              var html = '<span class="discount-span">'+_personDiscountArr[j].unit+'人打<font color="#fa553c">'+_personDiscountArr[j].discount+'折</font></span>'
              $("#person-discount").append(html);
            };
          }else if(_priceRule.skuDiscount[i].sku==100){
            //会议室数
            //单价
            $('#room-price').val(discount.config.unitPrice);
            //免费间数
            $('#room-freenumber').val(discount.config.free);
            //免费间数最小值
            $('#room-freenumber').data('min',$('#free-roomnumber').val());
            if(parseInt($('#room-freenumber').val())<parseInt($('#free-roomnumber').val())){
              $('#room-freenumber').val($('#free-roomnumber').val());
            }
            //数量上限
            $('#room-maxnumber').val(discount.config.max);
            //免费间数最大值
            $('#room-freenumber').data('max',$('#room-maxnumber').val());
            if(parseInt($('#room-freenumber').val())>parseInt($('#room-maxnumber').val())){
              $('#room-freenumber').val($('#room-maxnumber').val());
            }
            //免费配置最大值
            $('#free-roomnumber').data('max',$('#room-maxnumber').val());
            if(parseInt($('#free-roomnumber').val())>parseInt($('#room-maxnumber').val())){
              $('#free-roomnumber').val($('#room-maxnumber').val());
            }
            //单位步长
            $('#room-stepnumber').val(discount.config.step);
            //单位步长最大值
            $('#room-stepnumber').data('max',$('#room-maxnumber').val());
            if(parseInt($('#room-stepnumber').val())>parseInt($('#room-maxnumber').val())){
              $('#room-stepnumber').val($('#room-maxnumber').val());
            }
            _roomDiscountType = discount.discountOption;
            if(_roomDiscountType==0){
              $('#roomRadio1').attr('checked', true);
              $('#roomRadio2').attr('checked', false);
              $('#roomRadio3').attr('checked', false);
              //隐藏修改档位
              $('.room-count .discount-info').hide();
            }else if(_roomDiscountType==1){
              $('#roomRadio1').attr('checked', false);
              $('#roomRadio2').attr('checked', true);
              $('#roomRadio3').attr('checked', false);
              //显示修改档位
              $('.room-count .discount-info').show();
              //折扣信息
              _roomDiscountArr = discount.config.tiers;
              //排序
                _roomDiscountArr = _roomDiscountArr.sort(by('unit'));
              //填充折扣数据
              $("#room-discount").empty();
              for (var j = 0; j < _roomDiscountArr.length; j++) {
                var html = '<span class="discount-span">'+_roomDiscountArr[j].unit+'间打<font color="#fa553c">'+_roomDiscountArr[j].discount+'折</font></span>'
                $("#room-discount").append(html);
              };
            }else {
              $('#roomRadio1').attr('checked', false);
              $('#roomRadio2').attr('checked', false);
              $('#roomRadio3').attr('checked', true);
              //隐藏修改档位
              $('.room-count .discount-info').hide();
            }
            
          }else {
            //存储空间
            //单价
            $('#space-price').val(discount.config.unitPrice);
            //免费间数
            $('#space-freenumber').val(discount.config.free);
            //免费间数最小值
            $('#space-freenumber').data('min',$('#free-spacenumber').val());
            if(parseInt($('#space-freenumber').val())<parseInt($('#free-spacenumber').val())){
              $('#space-freenumber').val($('#free-spacenumber').val());
            }
            //数量上限
            $('#space-maxnumber').val(discount.config.max);
            //免费间数最大值
            $('#space-freenumber').data('max',$('#space-maxnumber').val());
            if(parseInt($('#space-freenumber').val())>parseInt($('#space-maxnumber').val())){
              $('#space-freenumber').val($('#space-maxnumber').val());
            }
            //免费配置最大值
            $('#free-spacenumber').data('max',$('#space-maxnumber').val());
            if(parseInt($('#free-spacenumber').val())>parseInt($('#space-maxnumber').val())){
              $('#free-spacenumber').val($('#space-maxnumber').val());
            }
            //单位步长
            $('#space-stepnumber').val(discount.config.step);
            //单位步长最大值
            $('#space-stepnumber').data('max',$('#space-maxnumber').val());
            if(parseInt($('#space-stepnumber').val())>parseInt($('#space-maxnumber').val())){
              $('#space-stepnumber').val($('#space-maxnumber').val());
            }
            _spaceDiscountType = discount.discountOption;

            if(_spaceDiscountType==0){
              $('#spaceRadio1').attr('checked', true);
              $('#spaceRadio2').attr('checked', false);
              $('#spaceRadio3').attr('checked', false);
              //隐藏修改档位
              $('.storage-space .discount-info').hide();
            }else if(_spaceDiscountType==1){
              $('#spaceRadio1').attr('checked', false);
              $('#spaceRadio2').attr('checked', true);
              $('#spaceRadio3').attr('checked', false);
              //显示修改档位
              $('.storage-space .discount-info').show();
              //折扣信息
              _spaceDiscountArr = discount.config.tiers;
              //排序
                _spaceDiscountArr = _spaceDiscountArr.sort(by('unit'));
              //填充折扣数据
              $("#space-discount").empty();
              for (var j = 0; j < _spaceDiscountArr.length; j++) {
                var html = '<span class="discount-span">'+_spaceDiscountArr[j].unit+'GB打<font color="#fa553c">'+_spaceDiscountArr[j].discount+'折</font></span>'
                $("#space-discount").append(html);
              };
            }else {
              $('#spaceRadio1').attr('checked', false);
              $('#spaceRadio2').attr('checked', false);
              $('#spaceRadio3').attr('checked', true);
              //隐藏修改档位
              $('.storage-space .discount-info').hide();
            }
          } 
        };
        
      }else {
        notify('danger',res.msg);
      }
    })
  }


});
//免费版配置、数量上限、起购数量（免费数量）校验
  function checknumber1 (maxobj,minobj,freeobj) {
    //取到数量上限
      var maxnumber = maxobj.val();
      //取到起购数量值
      var freenumber = freeobj.val();
      if(minobj!=null){
        //取到该输入框值
        var minnumber =  minobj.val();
        //判断输入框值是否大于数量上限
        if(parseInt(maxnumber)!=NaN&&parseInt(minnumber)!=NaN){
          if(parseInt(minnumber)>parseInt(maxnumber)){
            //当输入框的值大于最大值，输入框值改为数量上限
            minnumber = maxnumber;
          }
        }
        minobj.val(minnumber);
      }
      
      //先判断免费值是否小于最小值
      if(parseInt(freenumber)!=NaN&&parseInt(minnumber)!=NaN){
        //当起购数量小于当前值，起购数量改为输入框的值
        if(parseInt(freenumber)<parseInt(minnumber)){
          freenumber = minnumber;
          freeobj.val(freenumber);
        }
      }
      //再判断免费值是否大于最大值
      if(parseInt(freenumber)!=NaN&&parseInt(maxnumber)!=NaN){
        //免费数量大于当前值，免费数量改为输入框的值
        if(parseInt(freenumber)>parseInt(maxnumber)){
          freenumber = maxnumber;
          freeobj.val(freenumber);
        }
      }
      
  }
  //将对象元素转换成字符串以作比较
  function obj2key(obj, keys){
      var n = keys.length,
          key = [];
      while(n--){
          key.push(obj[keys[n]]);
      }
      return key.join('|');
  }
  //去重操作
  function uniqeByKeys(array,keys){
      var arr = [];
      var hash = {};
      for (var i = 0, j = array.length; i < j; i++) {
          var k = obj2key(array[array.length-i-1], keys);
          if (!(k in hash)) {
              hash[k] = true;
              arr.push(array[array.length-i-1]);
          }else {
            // arr.splice(arr.indexOf(array[i]),1);
            // arr.push(array[i]);
          }
      }
      return arr ;
  }
  function by(property){
    return function(a, b) {
      return a[property] - b[property] > 0;
    }
  }
  //去除超过上限的折扣
  function removeMax(array,max){
    max = parseInt(max);
    var removeArr = [];
    for (var i = 0; i < array.length; i++) {
      if(array[i].unit>max){
        removeArr.push(array[i]);
      }
    }
    for (var i = 0; i < removeArr.length; i++) {
      array.splice(array.indexOf(removeArr[i]),1);
    };
    return array;
  }