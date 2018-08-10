
//使用说明 在Html代码中创建<div class="page-Box" id="page-Box"> </div>  引入该js文件即可,样式在pages.css已经写好全局样式,只需要定位最外层的大盒子即可
//初始化分页控件的使用方法  
//html代码---------
// #page1
//   .page-Box#page-Box
// #page2
//   .page-Box#page-Box
// #page3
//   .page-Box#page-Box
//用到分页器控件的js代码-----------
 //  var p= Pages("#page1");//创建分页器
 // var p1= Pages("#page2");//创建分页器
 //  p.callBack=function callBack(currentPage,page){ 
 //    console.log(currentPage,page);
   
 //  } ;
 //  p.setCount(50); //设置分页器请求的总数 
  
 // var p2= Pages("#page2");//创建分页器
 //  p2.callBack=function callBack2(currentPage,page){ 
 //    console.log(currentPage,page);
 //  } ;//初始化分页器自定义回调函数
 //  p2.setCount(50); //设置分页器请求的总数 
 //  -----
 //如需重置分页器, 调用 对应Pages的resetpage方法

  var Pages = (function(window) {
    var Pages = function(element) {
      return new Pages.fn.init(element);
    }
    Pages.fn = Pages.prototype = {
      constructor: Pages,
      init: function(element) {
        var self=this;
        this.count=0;//总页数全局变量
        this.pageCount=Math.ceil(this.count/15);//一共多少页
        this.page=15; //初始化的每页15条
        this.currentPage=1;//初始化当前页
        this.obj;   // 保存初始化对象
        this.callBack; //记录回调函数
        this.ele=element;
        this.element=$(element);
        this.pageContentHtml='<div class="innerPageBox"><div style="height:30px;line-height:30px;margin-top:13px"><span>显示条数</span>  <div class="btn-group dropup"><button type="button" class="btn btn-secondary dropdown-toggle dropPage" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">15</button> <div class="dropdown-menu"> <a class="dropdown-item" href="#" data-number="15">15</a> <a class="dropdown-item" href="#" data-number="30">30</a> <a class="dropdown-item" href="#" data-number="50">50</a> </div> </div>     <span class="page-count" id="page-count"></span>   <span>到第<input type="text"  class="page-Size" id="page-Size" value="1">页</span><button  class="btn" id="lastBtn">上一页</button><button class="btn" id="nextBtn">下一页</button></div></div>';
        $(this.element.children()[0]).append(this.pageContentHtml);
        $(self.ele+" "+'#page-Box').hide();
        this.setCount=function(totalCount){//初始化分页器总数量
          self.count=totalCount;//总页数
          self.currentPage=1;
          self.pageCount=Math.ceil(self.count/15);
          $(self.ele+" "+'#page-Size').val(1);

          if (self.count<=15){
            $(self.ele+" "+'#page-Box').hide();
          }else{
            $(self.ele+" "+'#page-Box').show();
          }
          $(self.ele+" "+'#page-count').text('共'+self.pageCount+'页,');
        };
        this.resetpage = function(){//初始化分页器(调用新的请求接口时使用)
          self.currentPage = 1;
          $(self.ele+" "+'#page-Size').val(1);
          $(self.ele+" "+'#sid').val(15);
          self.page = 15;
          $(self.ele+" "+'#lastBtn').attr({'disabled':'disabled'});
          $(self.ele+" "+'#nextBtn').removeAttr('disabled');
        };
        this.bindEvent(self);
      },
      bindEvent: function(self) { 
      var self = self; //注册监听事件    
        $('body').on('click', self.ele+" "+'.dropdown-item', function (e) {
            var dataId = $(this).attr("data-number");
            $(self.ele+" "+".dropPage").text(dataId);
            self.page = dataId;
            self.pageCount = Math.ceil(self.count / self.page);
            self.currentPage = 1;
            $(self.ele+" "+"#page-Size").val(1); //每次选择页码后 默认显示第一页
            $(self.ele+" "+"#lastBtn").attr({ "disabled": "disabled" });
            if (self.pageCount == 1) {
                $(self.ele+" "+"#nextBtn").attr({ "disabled": "disabled" });
            } else {
                $(self.ele+" "+"#nextBtn").removeAttr("disabled");
            }
            $(self.ele+" "+"#page-count").text('共' + self.pageCount + '页,');

            self.callBack(Number(self.currentPage),Number(self.page));
        });
        //监听输入框失去焦点
        $('body').on('blur',self.ele+" "+'#page-Size',function () {
            if($(self.ele+" "+'#page-Size').val()==''){
                $(self.ele+" "+'#page-Size').val(self.currentPage);
            }
        })
        $(self.ele+" "+'#lastBtn').attr({'disabled':'disabled'});
        //默认第一页时禁用上一页按钮
        //输入框回车
        $(self.ele+" "+'#page-Size').bind('keydown',function(event){
            if(event.keyCode == '13') {
                //数据输入的页码进行跳转到下一页
                if ($(self.ele+" "+'#page-Size').val()==''||$(self.ele+" "+'#page-Size').val()==0) {//输入为空的时候,点击确认置为首页
                    $(self.ele+" "+'#page-Size').val(1);
                    self.currentPage=1;
                    if (self.pageCount>1) {
                      $(self.ele+" "+'#nextBtn').removeAttr('disabled');
                    }
                    $(self.ele+" "+'#lastBtn').attr({'disabled':'disabled'});
                     self.callBack(Number(self.currentPage),Number(self.page));
                }else if ($(self.ele+" "+'#page-Size').val()>self.pageCount) {
                    //alert( '没有第'+$(self.ele+" "+'#page-Size').val()+'页,已帮您跳到最后一页');
                    self.currentPage=$(self.ele+" "+'#page-Size').val(self.pageCount).val();
                    $(self.ele+" "+'#nextBtn').attr({'disabled':'disabled'});
                    if ($(self.ele+" "+'#page-Size').val()>1) {
                       $(self.ele+" "+'#lastBtn').removeAttr('disabled');
                    }
                   self.callBack(Number(self.currentPage),Number(self.page));
                 }else{
                    self.currentPage=$(self.ele+" "+'#page-Size').val();
                    if (self.currentPage==1) {//首页
                      $(self.ele+" "+'#nextBtn').removeAttr('disabled');
                      $(self.ele+" "+'#lastBtn').attr({'disabled':'disabled'});
                    }else if(self.currentPage==self.pageCount){//尾页
                      $(self.ele+" "+'#lastBtn').removeAttr('disabled');
                      $(self.ele+" "+'#nextBtn').attr({'disabled':'disabled'});
                    }else{//不是首页,也不是尾页
                      $(self.ele+" "+'#lastBtn').removeAttr('disabled');
                      $(self.ele+" "+'#nextBtn').removeAttr('disabled');
                    }
                     self.callBack(Number(self.currentPage),Number(self.page));
                 }
           }
         }); 
        //上一页
        $(self.ele+" "+'#lastBtn').click(function(){
            var that= $(self.ele+" "+'#lastBtn');
            $(self.ele+" "+'#nextBtn').removeAttr('disabled');
            if($(self.ele+" "+'#page-Size').val()==''||$(self.ele+" "+'#page-Size').val()<=1){
                $(self.ele+" "+'#page-Size').val(self.currentPage);
            };
            if(Number($(self.ele+" "+'#page-Size').val())-1>self.pageCount){
                self.currentPage = self.pageCount;
                var number = Number($(self.ele+" "+'#page-Size').val())-1;
                $(self.ele+" "+'#page-Size').val(self.pageCount);
            }else {
                self.currentPage = $(self.ele+" "+'#page-Size').val(Number($(self.ele+" "+'#page-Size').val()) - 1).val();
            }
            // self.currentPage=$(self.ele+" "+'#page-Size').val(Number($(self.ele+" "+'#page-Size').val())-1).val();
            self.callBack(Number(self.currentPage),Number(self.page));
            if ($(self.ele+" "+'#page-Size').val()==1){ //如果是首页,则禁止点击上一页
                that.attr({'disabled':'disabled'});
            }else{
                that.removeAttr('disabled');
            }
        });
        //下一页
        $(self.ele+" "+'#nextBtn').click(function(){
            $(self.ele+" "+'#lastBtn').removeAttr('disabled');
            var that= $(self.ele+" "+'#nextBtn');
            if($(self.ele+" "+'#page-Size').val()==''||$(self.ele+" "+'#page-Size').val()==0){
                $(self.ele+" "+'#page-Size').val(self.currentPage);
            }
            if(Number($(self.ele+" "+'#page-Size').val())>self.pageCount){
                self.currentPage = self.pageCount;
                var number = Number($(self.ele+" "+'#page-Size').val())+1;
                $(self.ele+" "+'#page-Size').val(self.pageCount);
            }else {
                self.currentPage = $(self.ele+" "+'#page-Size').val(Number($(self.ele+" "+'#page-Size').val()) + 1).val();
            }
            // self.currentPage=$(self.ele+" "+'#page-Size').val(Number($(self.ele+" "+'#page-Size').val())+1).val();
            self.callBack(Number(self.currentPage),Number(self.page));
            if ($(self.ele+" "+'#page-Size').val()==self.pageCount){ //如果是尾页,则禁止点击下一页
                that.attr({'disabled':'disabled'});
            }else{
                that.removeAttr('disabled');
            }
        }); 
        //按键移开
        $(self.ele+" "+'.page-Size').keyup(function (event) {
            this.value= this.value.replace(/[^\d]/g,'');
        });
      },
     }
    Pages.fn.init.prototype = Pages.fn;
    return Pages;
  })();
