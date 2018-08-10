"use strict";

//使用说明 在Html代码中创建<div class="page-Box" id="page-Box"> </div>  引入该js文件即可,样式在pages.css已经写好全局样式,只需要定位最外层的大盒子即可

var count = 0; //总页数全局变量
var pageCount = Math.ceil(count / 15); //一共多少页
var page = 15; //初始化的每页15条
var currentPage = 1; //初始化当前页
var obj; // 保存初始化对象
var callBack1; //记录回调函数
var Pages = function (window) {
  var Pages = function Pages() {
    return new Pages.fn.init();
  };
  Pages.fn = Pages.prototype = {
    constructor: Pages,
    init: function init() {
      this.setCount = function (totalCount) {
        //初始化分页器总数量
        obj = this;
        count = totalCount; //总页数
        currentPage = 1;
        page=15;
        $(".dropPage").text(page);
        pageCount = Math.ceil(count / page);
        $("#page-Size").val(1);
        $("#lastBtn").attr({ "disabled": "disabled" });
        $("#nextBtn").removeAttr("disabled");
        if (count <= 15) {
          $("#page-Box").hide();
        } else {
          $("#page-Box").show();
        }
        $("#page-count").text('共' + pageCount + '页,');
      };
      this.resetpage = function () {
        //初始化分页器(调用新的请求接口时使用)
        currentPage = 1;
        $("#page-Size").val(1);
        $(".dropPage").text(15);
        page = 15;

      };
      this.callBack = function (callBack) {
        //初始化分页器回调函数(从外部传进来的)
        callBack1 = callBack;
      };
    },
    makeArray: function makeArray(callBack) {
      //回调函数的回传(点击分页器的时候会触发)
      callBack(currentPage, page);
    }
  };
  Pages.fn.init.prototype = Pages.fn;
  return Pages;
}();

$(function () {
    //
    // <div class="dropdown show">
    // <div class="form-control " id="comtype" data-toggle="dropdown" aria-expanded="true" aria-haspopup="true">
    // <span id="type-name" class="">互联网/电子商务</span>
    // <span class="icon icon-organiz-down"></span>
    // </div>
    // <div class="dropdown-menu company-type" aria-labelledby="comtype">
    //  <a class="dropdown-item type-item" >呵呵  </a>\n
    // </div>
    // </div>
    //
    // <select id="sid" onchange="selectcity()"><option value="15">15</option><option value="30">30</option><option value="50">50</option></select>


 var pageContentHtml='<div class="innerPageBox"><div style="height:30px;line-height:30px;margin-top:13px"><span>显示条数</span>  <div class="btn-group dropup"><button type="button" class="btn btn-secondary dropdown-toggle dropPage" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">15</button> <div class="dropdown-menu"> <a class="dropdown-item" href="#" data-number="15">15</a> <a class="dropdown-item" href="#" data-number="30">30</a> <a class="dropdown-item" href="#" data-number="50">50</a> </div> </div>     <span class="page-count" id="page-count"></span>   <span>到第<input type="text" onkeyup="checkInput(this)" class="page-Size" id="page-Size" value="1">页</span><button  class="btn" id="lastBtn">上一页</button><button class="btn" id="nextBtn">下一页</button></div></div>';

    $('body').on('click', '#page-Box .dropdown-item', function (e) {
        var dataId = $(this).attr("data-number");
        $(".dropPage").text(dataId);
        page = dataId;
        pageCount = Math.ceil(count / page);
        currentPage = 1;
        $("#page-Size").val(1); //每次选择页码后 默认显示第一页
        $("#lastBtn").attr({ "disabled": "disabled" });
        if (pageCount == 1) {
            $("#nextBtn").attr({ "disabled": "disabled" });
        } else {
            $("#nextBtn").removeAttr("disabled");
        }
        $("#page-count").text('共' + pageCount + '页,');
        obj.makeArray(callBack1);
    });

  $("#page-Box").append(pageContentHtml);
  $("#lastBtn").attr({ "disabled": "disabled" }); //默认第一页时禁用上一页按钮
  $("#page-Size").bind('keydown', function (event) {
    if (event.keyCode == "13") {
      //数据输入的页码进行跳转到下一页
      if ($("#page-Size").val() == ""||$("#page-Size").val()<=1) {
        //输入为空的时候,点击确认置为首页
        $("#page-Size").val(1);
        currentPage = 1;
        if (pageCount > 1) {
          $("#nextBtn").removeAttr("disabled");
        }
        $("#lastBtn").attr({ "disabled": "disabled" });
        obj.makeArray(callBack1);
      } else if ($("#page-Size").val() > pageCount) {
        currentPage = $("#page-Size").val(pageCount).val();
        $("#nextBtn").attr({ "disabled": "disabled" });
        if ($("#page-Size").val() > 1) {
          $("#lastBtn").removeAttr("disabled");
        }
        obj.makeArray(callBack1);
      } else {
        currentPage = $("#page-Size").val();
        if (currentPage == 1) {
          //首页
          $("#nextBtn").removeAttr("disabled");
          $("#lastBtn").attr({ "disabled": "disabled" });
        } else if (currentPage == pageCount) {
          //尾页
          $("#lastBtn").removeAttr("disabled");
          $("#nextBtn").attr({ "disabled": "disabled" });
        } else {
          //不是首页,也不是尾页
          $("#lastBtn").removeAttr("disabled");
          $("#nextBtn").removeAttr("disabled");
        }
        obj.makeArray(callBack1);
      }
    }
  });
  $("#lastBtn").click(function () {
    var that = $("#lastBtn");
    $("#nextBtn").removeAttr("disabled");
    if($("#page-Size").val()==''||$("#page-Size").val()<= 1){
        $("#page-Size").val(currentPage);
    }
      if(Number($("#page-Size").val())-1>pageCount){
          currentPage = pageCount;
          var number = Number($("#page-Size").val())-1;
          $("#page-Size").val(pageCount);
      }else {
          currentPage = $("#page-Size").val(Number($("#page-Size").val()) - 1).val();
      }
    obj.makeArray(callBack1);
    if ($("#page-Size").val() == 1) {
      //如果是首页,则禁止点击上一页
      that.attr({ "disabled": "disabled" });
    } else {
      that.removeAttr("disabled");
    }
    if(currentPage>=pageCount){
        $('#nextBtn').attr({ "disabled": "disabled" });
    }
  });
  $("#nextBtn").click(function () {
    $("#lastBtn").removeAttr("disabled");
    var that = $("#nextBtn");
      if($("#page-Size").val()==''||$("#page-Size").val()<= 0){
          $("#page-Size").val(currentPage);
      }
      if(Number($("#page-Size").val())>=pageCount){
          currentPage = pageCount;
          $("#page-Size").val(pageCount);
      }else {
          currentPage = $("#page-Size").val(Number($("#page-Size").val()) + 1).val();

      }
    obj.makeArray(callBack1);
    if ($("#page-Size").val() == pageCount) {
      //如果是尾页,则禁止点击下一页
      that.attr({ "disabled": "disabled" });
    } else {
      that.removeAttr("disabled");
    }
  });
});

function checkInput(event) {
  //验证input框输入的格式是否正确
  event.value = event.value.replace(/[^\d]/g, '');
}
