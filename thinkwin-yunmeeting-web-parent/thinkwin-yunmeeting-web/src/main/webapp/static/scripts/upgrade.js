$(function(){ 
  soda.prefix('ng-');
  var userInfo= JSON.parse(localStorage.getItem('userinfo'));
  if (userInfo==null) {
      userInfo = {
        userId:'123',
        token:'123'
      }
  };
/*  soda.filter('text',function (text) {
    if (text==null) return '';
    if (text.length ==0) return '';
    if(text.indexOf(':')>=0){
        var arr = text.split(':');
        return '<span class="label">'+ arr[0] +'：' + '</span>' + arr[1];
    }else if(text.indexOf('：')>=0){
        var arr = text.split('：');
        return '<span class="label">'+ arr[0] + '：' + '</span>' + arr[1];
    }else {
      return text;
    }

  });*/
  var currentIndex = -1; //当前版本的下标
  var list = '<ul ><li ng-repeat="item in list">{{item}}</li></ul>';
  var freehtml = ' <li class="user-count" ng-repeat="list in free.items" ng-html="list|text:list"></li>';
  var specialhtml = '<li class="user-count" ng-repeat="list in pay.items" ng-html="list|text:list"></li>';
  getData();
  function getData(){
    var data = {};
    fetchs.post('/commodity/selectOrderInformationModify',data,function (res) {
        if(res.ifSuc==1){
          $('#free-info').html(soda(freehtml,{free:res.data.free}));
          $('#pay-info').html(soda(specialhtml,{pay:res.data.pay}));
          $('#freeprice').html(parseFloat(res.data.free.price));
          $('#payprice').html(parseFloat(res.data.pay.price));
          $('#rommPrice').html(parseFloat(res.data.pay.rommPrice));
          $('#spacePrice').html(parseFloat(res.data.pay.spacePrice));
          var height = $('.specialty').height();
          $('.free').height(height);

          $('.btn-lower').attr('disabled',true); //設置較低版本按鈕不可點擊
        }
    });
  }
/*  $('body').on('click','[data-toggle="update"]',function () {
      var id = $(this).data('id');
      // alert(id);
      var data = {productId:id};
      //type: 續費-0 升級-1
      var type = 1;
      var isFront = $(this).data('isfront');
      if(isFront == 1){
          type = 0;
      }
      window.location.href="/commodity/immediatelyUpgrade?token="+  userInfo.token + "&productId=" + id + "&type=" + type;
      // fetchs.post('/commodity/selectProductIdInfoAndList',data,function (res) {
      //     notify('success',res.ifSuc);
      // })

  })*/

})