webpackJsonp([10],{700:function(l,n,u){"use strict";Object.defineProperty(n,"__esModule",{value:!0});var e,t,a,i,_,s,o,r,c,d=u(0),m=u(1),f=u(31),g=u(173),v=u(51),h=u(39),p=function(){function l(l,n,u,e,t,a,i,_,s){this.params=l,this.nav=n,this.meeting=u,this.alertCtrl=e,this.popover=t,this.events=a,this.native=i,this.navCtrl=_,this.toastCtrl=s,this.detail={conferenceName:"",state:"",userRole:"",takeStartDate:"",takeEndDate:"",confrerenceCreateTime:"",hostUnit:"",userName:"",address:"",conterenceContent:"",count:"",auditWhy:"",userNameUrl:"",examineName:"",examineNameUrl:"",examineTime:"",roomId:""},0==this.nav.getViews().length&&this.nav.goToRoot({})}return l.prototype.ionViewWillEnter=function(){var l=this;this.id=this.params.data.id,this.meeting.setBacklogInfo(this.id).subscribe(function(n){l.detail=n,l.nowtime=(new Date).valueOf()})},l.prototype.goDetail=function(){this.navCtrl.push("DetailPage",{id:this.id})},l.prototype.noPass=function(){var l=this;this.meeting.selectmoreauditmeeting({meetingId:this.id,meetingName:this.detail.conferenceName,roomId:this.detail.roomId,start:this.detail.takeStartDate,end:this.detail.takeEndDate}).subscribe(function(n){if(2==n){var u=l.toastCtrl.create({message:"会议已过期,可通知预订人处理该会议",duration:3e3,position:"middle"});u.onDidDismiss(function(){}),u.present()}else{var e=l.popover.create("examineMeetPage",{id:l.id,address:l.detail.address,roomId:l.detail.roomId});e.present(),e.onDidDismiss(function(n){null!=n&&l.meeting.setBacklogInfo(l.id).subscribe(function(n){l.detail=n})})}})},l.prototype.Pass=function(){var l=this;this.meeting.selectmoreauditmeeting({meetingId:this.id,meetingName:this.detail.conferenceName,roomId:this.detail.roomId,start:this.detail.takeStartDate,end:this.detail.takeEndDate}).subscribe(function(n){if(0==n)l.alertCtrl.create({title:"确定审批通过此会议吗?",buttons:[{text:"取消",handler:function(){}},{text:"确定",handler:function(){l.meeting.auditMeeting({meetingId:l.id,auditStatus:1,address:l.detail.address,roomId:l.detail.roomId}).subscribe(function(n){l.meeting.setBacklogInfo(l.id).subscribe(function(n){l.detail=n})})}}]}).present();else if(1==n){l.alertCtrl.create({title:"<div class='meetinghint'>该会议时间内还有其他预订申请,若审核通过,其他会议将不通过审核,确定通过预订申请吗?</div>",buttons:[{text:"取消",handler:function(){}},{text:"确定",handler:function(){l.meeting.auditMeeting({meetingId:l.id,auditStatus:1,address:l.detail.address,roomId:l.detail.roomId}).subscribe(function(n){l.meeting.setBacklogInfo(l.id).subscribe(function(n){l.detail=n})})}}]}).present()}else if(2==n){var u=l.toastCtrl.create({message:"会议已过期,可通知预订人处理该会议",duration:3e3,position:"middle"});u.onDidDismiss(function(){}),u.present()}})},l}();p=Object(m.__decorate)([Object(d.i)({selector:"page-backlogdetail",templateUrl:"backlogdetail.html"}),Object(m.__metadata)("design:paramtypes",["function"==typeof(e=void 0!==f.m&&f.m)&&e||Object,"function"==typeof(t=void 0!==f.l&&f.l)&&t||Object,"function"==typeof(a=void 0!==v.a&&v.a)&&a||Object,"function"==typeof(i=void 0!==f.b&&f.b)&&i||Object,"function"==typeof(_=void 0!==f.p&&f.p)&&_||Object,"function"==typeof(s=void 0!==f.d&&f.d)&&s||Object,"function"==typeof(o=void 0!==h.a&&h.a)&&o||Object,"function"==typeof(r=void 0!==f.l&&f.l)&&r||Object,"function"==typeof(c=void 0!==f.t&&f.t)&&c||Object])],p);var k=function(){return function(){}}();k=Object(m.__decorate)([Object(d.u)({declarations:[p],imports:[f.h.forChild(p),g.a]})],k);var b=u(381),x=u(382),I=u(383),y=u(384),N=u(385),D=u(386),O=u(387),E=u(388),C=u(389),P=u(36),j=u(4),K=u(134),M=u(15),B=u(176),q=u(60),w=u(8),R=u(75),S=u(37),T=u(11),H=u(21),U=u(92),z=u(61),L=u(38),W=u(76),V=u(27),F=u(6),J=u(10),Y=u(32),A=u(66),G=u(17),Q=u(95),X=u(98),Z=u(43),$=u(138),ll=d._3({encapsulation:2,styles:[],data:{}});function nl(l){return d._29(0,[(l()(),d._6(0,null,null,1,"span",[],null,null,null,null,null)),(l()(),d._27(null,["等待审批中"]))],null,null)}function ul(l){return d._29(0,[(l()(),d._6(0,null,null,1,"span",[],null,null,null,null,null)),(l()(),d._27(null,["审批通过"]))],null,null)}function el(l){return d._29(0,[(l()(),d._6(0,null,null,1,"span",[],null,null,null,null,null)),(l()(),d._27(null,["审批不通过"]))],null,null)}function tl(l){return d._29(0,[(l()(),d._6(0,null,null,1,"div",[["class","meetingdescribeContent"]],null,null,null,null,null)),(l()(),d._27(null,["暂无内容"]))],null,null)}function al(l){return d._29(0,[(l()(),d._6(0,null,null,1,"div",[["class","meetingdescribeContent1"]],null,null,null,null,null)),(l()(),d._27(null,["",""]))],null,function(l,n){l(n,1,0,n.component.detail.conterenceContent)})}function il(l){return d._29(0,[(l()(),d._6(0,null,null,2,"div",[["class","Leftsecondline"]],null,null,null,null,null)),(l()(),d._6(0,null,null,1,"ion-icon",[["name","circle-del"],["role","img"],["style","color: #eb4a42"]],[[2,"hide",null]],null,null,null,null)),d._4(147456,null,0,P.a,[j.a,d.k,d.E],{name:[0,"name"]},null)],function(l,n){l(n,2,0,"circle-del")},function(l,n){l(n,1,0,d._20(n,2)._hidden)})}function _l(l){return d._29(0,[(l()(),d._6(0,null,null,2,"div",[["class","Leftsecondline"]],null,null,null,null,null)),(l()(),d._6(0,null,null,1,"ion-icon",[["name","circle-ok"],["role","img"],["style","color: #21cc68"]],[[2,"hide",null]],null,null,null,null)),d._4(147456,null,0,P.a,[j.a,d.k,d.E],{name:[0,"name"]},null)],function(l,n){l(n,2,0,"circle-ok")},function(l,n){l(n,1,0,d._20(n,2)._hidden)})}function sl(l){return d._29(0,[(l()(),d._6(0,null,null,3,"span",[],[[8,"className",0]],null,null,null,null)),d._21(0,K.a,[]),(l()(),d._27(null,["",""])),d._21(0,M.t,[])],null,function(l,n){var u=n.component;l(n,0,0,d._9(1,"nophoto ",d._28(n,0,0,d._20(n,1).transform(u.detail.userName)),"")),l(n,2,0,d._28(n,2,0,d._20(n,3).transform(u.detail.userName,-2)))})}function ol(l){return d._29(0,[(l()(),d._6(0,null,null,1,"img",[],[[8,"src",4]],null,null,null,null)),d._21(0,B.a,[])],null,function(l,n){var u=n.component;l(n,0,0,d._9(1,"",d._28(n,0,0,d._20(n,1).transform(u.detail.userNameBigPicture)),""))})}function rl(l){return d._29(0,[(l()(),d._6(0,null,null,3,"span",[],[[8,"className",0]],null,null,null,null)),d._21(0,K.a,[]),(l()(),d._27(null,["",""])),d._21(0,M.t,[])],null,function(l,n){var u=n.component;l(n,0,0,d._9(1,"nophoto ",d._28(n,0,0,d._20(n,1).transform(u.detail.examineName)),"")),l(n,2,0,d._28(n,2,0,d._20(n,3).transform(u.detail.examineName,-2)))})}function cl(l){return d._29(0,[(l()(),d._6(0,null,null,1,"img",[],[[8,"src",4]],null,null,null,null)),d._21(0,B.a,[])],null,function(l,n){var u=n.component;l(n,0,0,d._9(1,"",d._28(n,0,0,d._20(n,1).transform(u.detail.examineNameBigPicture)),""))})}function dl(l){return d._29(0,[(l()(),d._6(0,null,null,28,"div",[["class","RightsecondDiv"]],null,null,null,null,null)),(l()(),d._27(null,["\n             "])),(l()(),d._6(0,null,null,0,"div",[["class","triangleDiv"]],null,null,null,null,null)),(l()(),d._27(null,["\n                "])),(l()(),d._6(0,null,null,7,"div",[["class","examineportrait"]],null,null,null,null,null)),(l()(),d._27(null,["\n                  "])),(l()(),d._0(16777216,null,null,1,null,rl)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n                  "])),(l()(),d._0(16777216,null,null,1,null,cl)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n                "])),(l()(),d._27(null,["\n              "])),(l()(),d._6(0,null,null,10,"div",[["class","examineportraitrightDiv"]],null,null,null,null,null)),(l()(),d._27(null,["\n                "])),(l()(),d._6(0,null,null,1,"div",[["class","examineName"]],null,null,null,null,null)),(l()(),d._27(null,["",""])),(l()(),d._27(null,["\n                "])),(l()(),d._6(0,null,null,1,"div",[["class","examinestate"],["style","color: #919199"]],null,null,null,null,null)),(l()(),d._27(null,["审批不通过"])),(l()(),d._27(null,["\n                "])),(l()(),d._6(0,null,null,1,"div",[["class","examineauditWhy"]],null,null,null,null,null)),(l()(),d._27(null,["",""])),(l()(),d._27(null,["\n              "])),(l()(),d._27(null,["\n              "])),(l()(),d._6(0,null,null,2,"div",[["class","examineData"]],null,null,null,null,null)),(l()(),d._27(null,["",""])),d._23(2),(l()(),d._27(null,["\n          "]))],function(l,n){var u=n.component;l(n,7,0,!u.detail.examineNameBigPicture),l(n,10,0,u.detail.examineNameBigPicture)},function(l,n){var u=n.component;l(n,16,0,u.detail.examineName),l(n,22,0,u.detail.auditWhy),l(n,26,0,d._28(n,26,0,l(n,27,0,d._20(n.parent,0),u.detail.examineTime,"yyyy.MM.dd H:mm")))})}function ml(l){return d._29(0,[(l()(),d._6(0,null,null,3,"span",[],[[8,"className",0]],null,null,null,null)),d._21(0,K.a,[]),(l()(),d._27(null,["",""])),d._21(0,M.t,[])],null,function(l,n){var u=n.component;l(n,0,0,d._9(1,"nophoto ",d._28(n,0,0,d._20(n,1).transform(u.detail.examineName)),"")),l(n,2,0,d._28(n,2,0,d._20(n,3).transform(u.detail.examineName,-2)))})}function fl(l){return d._29(0,[(l()(),d._6(0,null,null,1,"img",[],[[8,"src",4]],null,null,null,null)),d._21(0,B.a,[])],null,function(l,n){var u=n.component;l(n,0,0,d._9(1,"",d._28(n,0,0,d._20(n,1).transform(u.detail.examineNameBigPicture)),""))})}function gl(l){return d._29(0,[(l()(),d._6(0,null,null,25,"div",[["class","RightsecondDiv"]],null,null,null,null,null)),(l()(),d._27(null,["\n             "])),(l()(),d._6(0,null,null,0,"div",[["class","triangleDiv"]],null,null,null,null,null)),(l()(),d._27(null,["\n               "])),(l()(),d._6(0,null,null,7,"div",[["class","examineportrait"]],null,null,null,null,null)),(l()(),d._27(null,["\n                  "])),(l()(),d._0(16777216,null,null,1,null,ml)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n                  "])),(l()(),d._0(16777216,null,null,1,null,fl)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n                "])),(l()(),d._27(null,["\n              "])),(l()(),d._6(0,null,null,7,"div",[["class","examineportraitrightDiv"]],null,null,null,null,null)),(l()(),d._27(null,["\n                "])),(l()(),d._6(0,null,null,1,"div",[["class","examineName"]],null,null,null,null,null)),(l()(),d._27(null,["",""])),(l()(),d._27(null,["\n                "])),(l()(),d._6(0,null,null,1,"div",[["class","examinestate"],["style","color: #919199"]],null,null,null,null,null)),(l()(),d._27(null,["审批通过"])),(l()(),d._27(null,["\n              "])),(l()(),d._27(null,["\n              "])),(l()(),d._6(0,null,null,2,"div",[["class","examineData"]],null,null,null,null,null)),(l()(),d._27(null,["",""])),d._23(2),(l()(),d._27(null,["\n          "]))],function(l,n){var u=n.component;l(n,7,0,!u.detail.examineNameBigPicture),l(n,10,0,u.detail.examineNameBigPicture)},function(l,n){var u=n.component;l(n,16,0,u.detail.examineName),l(n,23,0,d._28(n,23,0,l(n,24,0,d._20(n.parent,0),u.detail.examineTime,"yyyy.MM.dd H:mm")))})}function vl(l){return d._29(0,[(l()(),d._6(0,null,null,7,"div",[["class","passDIV"]],null,null,null,null,null)),(l()(),d._27(null,["\n        "])),(l()(),d._6(0,null,null,1,"div",[["style","border-right: 0.5px #e5eaf0 solid ;"]],null,[[null,"click"]],function(l,n,u){var e=!0;"click"===n&&(e=!1!==l.component.noPass()&&e);return e},null,null)),(l()(),d._27(null,["不通过"])),(l()(),d._27(null,["\n        "])),(l()(),d._6(0,null,null,1,"div",[],null,[[null,"click"]],function(l,n,u){var e=!0;"click"===n&&(e=!1!==l.component.Pass()&&e);return e},null,null)),(l()(),d._27(null,["通过"])),(l()(),d._27(null,["\n     "]))],null,null)}function hl(l){return d._29(0,[d._21(0,M.e,[d.t]),(l()(),d._6(0,null,null,10,"ion-header",[],null,null,null,null,null)),d._4(16384,null,0,q.a,[j.a,d.k,d.E,[2,w.a]],null,null),(l()(),d._27(null,["\n  "])),(l()(),d._6(0,null,null,6,"ion-navbar",[["class","toolbar"],["color","primary"]],[[8,"hidden",0],[2,"statusbar-padding",null]],null,null,R.b,R.a)),d._4(49152,null,0,S.a,[T.a,[2,w.a],[2,H.a],j.a,d.k,d.E],{color:[0,"color"]},null),(l()(),d._27(3,["\n    "])),(l()(),d._6(0,null,3,2,"ion-title",[],null,null,null,U.b,U.a)),d._4(49152,null,0,z.a,[j.a,d.k,d.E,[2,L.a],[2,S.a]],null,null),(l()(),d._27(0,["\n      会议室审批\n    "])),(l()(),d._27(3,["\n  "])),(l()(),d._27(null,["\n"])),(l()(),d._27(null,["\n"])),(l()(),d._6(0,null,null,141,"ion-content",[["class","outer-content"]],[[2,"statusbar-padding",null],[2,"has-refresher",null]],[["window","resize"]],function(l,n,u){var e=!0;"window:resize"===n&&(e=!1!==d._20(l,14).resize()&&e);return e},W.b,W.a)),d._4(4374528,null,0,V.a,[j.a,F.a,J.a,d.k,d.E,T.a,Y.a,d.x,[2,w.a],[2,H.a]],null,null),(l()(),d._27(1,["\n    "])),(l()(),d._6(0,null,1,73,"div",[["class","meetingMessageDiv"]],null,null,null,null,null)),(l()(),d._27(null,["\n      "])),(l()(),d._6(0,null,null,20,"div",[["class","meetingNmaeDiv"]],null,[[null,"click"]],function(l,n,u){var e=!0;"click"===n&&(e=!1!==l.component.goDetail()&&e);return e},null,null)),(l()(),d._27(null,["\n        "])),(l()(),d._6(0,null,null,1,"div",[["class","meetingname"]],null,null,null,null,null)),(l()(),d._27(null,["",""])),(l()(),d._27(null,["\n        "])),(l()(),d._6(0,null,null,10,"div",[],[[8,"className",0]],null,null,null,null)),(l()(),d._27(null,["\n            "])),(l()(),d._0(16777216,null,null,1,null,nl)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n            "])),(l()(),d._0(16777216,null,null,1,null,ul)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n            "])),(l()(),d._0(16777216,null,null,1,null,el)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n        "])),(l()(),d._27(null,["\n        "])),(l()(),d._6(0,null,null,2,"div",[["class","detailicon"]],null,null,null,null,null)),(l()(),d._6(0,null,null,1,"ion-icon",[["name","right"],["role","img"],["style","color: #c2c2c2;font-size: 12px;"]],[[2,"hide",null]],null,null,null,null)),d._4(147456,null,0,P.a,[j.a,d.k,d.E],{name:[0,"name"]},null),(l()(),d._27(null,["\n      "])),(l()(),d._27(null,["\n      "])),(l()(),d._6(0,null,null,48,"div",[["class","meetingMessageContent"]],null,null,null,null,null)),(l()(),d._27(null,["\n          "])),(l()(),d._6(0,null,null,9,"div",[["class","messagelist"]],null,null,null,null,null)),(l()(),d._27(null,["\n            "])),(l()(),d._6(0,null,null,6,"ion-label",[],null,null,null,null,null)),d._4(16384,null,0,A.a,[j.a,d.k,d.E,[8,null],[8,null],[8,null],[8,null]],null,null),(l()(),d._6(0,null,null,1,"ion-icon",[["name","xq-time"],["role","img"]],[[2,"hide",null]],null,null,null,null)),d._4(147456,null,0,P.a,[j.a,d.k,d.E],{name:[0,"name"]},null),(l()(),d._27(null,["","-",""])),d._23(2),d._23(2),(l()(),d._27(null,["\n          "])),(l()(),d._27(null,["\n          "])),(l()(),d._6(0,null,null,7,"div",[["class","messagelist"]],null,null,null,null,null)),(l()(),d._27(null,["\n            "])),(l()(),d._6(0,null,null,4,"ion-label",[],null,null,null,null,null)),d._4(16384,null,0,A.a,[j.a,d.k,d.E,[8,null],[8,null],[8,null],[8,null]],null,null),(l()(),d._6(0,null,null,1,"ion-icon",[["name","xq-place"],["role","img"]],[[2,"hide",null]],null,null,null,null)),d._4(147456,null,0,P.a,[j.a,d.k,d.E],{name:[0,"name"]},null),(l()(),d._27(null,["",""])),(l()(),d._27(null,["\n          "])),(l()(),d._27(null,["\n          "])),(l()(),d._6(0,null,null,7,"div",[["class","messagelist"]],null,null,null,null,null)),(l()(),d._27(null,["\n            "])),(l()(),d._6(0,null,null,4,"ion-label",[],null,null,null,null,null)),d._4(16384,null,0,A.a,[j.a,d.k,d.E,[8,null],[8,null],[8,null],[8,null]],null,null),(l()(),d._6(0,null,null,1,"ion-icon",[["name","xq-user"],["role","img"]],[[2,"hide",null]],null,null,null,null)),d._4(147456,null,0,P.a,[j.a,d.k,d.E],{name:[0,"name"]},null),(l()(),d._27(null,["",""])),(l()(),d._27(null,["\n          "])),(l()(),d._27(null,["\n          "])),(l()(),d._6(0,null,null,7,"div",[["class","messagelist"]],null,null,null,null,null)),(l()(),d._27(null,["\n           "])),(l()(),d._6(0,null,null,4,"ion-label",[],null,null,null,null,null)),d._4(16384,null,0,A.a,[j.a,d.k,d.E,[8,null],[8,null],[8,null],[8,null]],null,null),(l()(),d._6(0,null,null,1,"ion-icon",[["name","xq-unit"],["role","img"]],[[2,"hide",null]],null,null,null,null)),d._4(147456,null,0,P.a,[j.a,d.k,d.E],{name:[0,"name"]},null),(l()(),d._27(null,["",""])),(l()(),d._27(null,["\n          "])),(l()(),d._27(null,["\n          "])),(l()(),d._6(0,null,null,7,"div",[["class","messagelist"]],null,null,null,null,null)),(l()(),d._27(null,["\n           "])),(l()(),d._6(0,null,null,4,"ion-label",[],null,null,null,null,null)),d._4(16384,null,0,A.a,[j.a,d.k,d.E,[8,null],[8,null],[8,null],[8,null]],null,null),(l()(),d._6(0,null,null,1,"ion-icon",[["name","xq-attendee"],["role","img"]],[[2,"hide",null]],null,null,null,null)),d._4(147456,null,0,P.a,[j.a,d.k,d.E],{name:[0,"name"]},null),(l()(),d._27(null,["","人参会"])),(l()(),d._27(null,["\n          "])),(l()(),d._27(null,["\n         \n      "])),(l()(),d._27(null,["\n    "])),(l()(),d._27(1,["\n    "])),(l()(),d._6(0,null,1,7,"div",[["class","meetingdescribeDiv"]],null,null,null,null,null)),(l()(),d._27(null,["\n      "])),(l()(),d._0(16777216,null,null,1,null,tl)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n      "])),(l()(),d._0(16777216,null,null,1,null,al)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n    "])),(l()(),d._27(1,["\n\n    "])),(l()(),d._6(0,null,1,50,"div",[["class","examineDiv"]],null,null,null,null,null)),(l()(),d._27(null,["\n        "])),(l()(),d._6(0,null,null,11,"div",[["class","examineLeft"]],null,null,null,null,null)),(l()(),d._27(null,["\n          "])),(l()(),d._6(0,null,null,2,"div",[["class","Leftfirstline"]],null,null,null,null,null)),(l()(),d._6(0,null,null,1,"ion-icon",[["name","circle-ok"],["role","img"],["style","color: #21cc68"]],[[2,"hide",null]],null,null,null,null)),d._4(147456,null,0,P.a,[j.a,d.k,d.E],{name:[0,"name"]},null),(l()(),d._27(null,["\n          "])),(l()(),d._0(16777216,null,null,1,null,il)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n\n          "])),(l()(),d._0(16777216,null,null,1,null,_l)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n        "])),(l()(),d._27(null,["\n        "])),(l()(),d._6(0,null,null,34,"div",[["class","examineRight"]],null,null,null,null,null)),(l()(),d._27(null,["\n          "])),(l()(),d._6(0,null,null,25,"div",[["class","RightfirstDiv"]],null,null,null,null,null)),(l()(),d._27(null,["\n              "])),(l()(),d._6(0,null,null,0,"div",[["class","triangleDiv"]],null,null,null,null,null)),(l()(),d._27(null,["\n              "])),(l()(),d._6(0,null,null,7,"div",[["class","examineportrait"]],null,null,null,null,null)),(l()(),d._27(null,["\n                "])),(l()(),d._0(16777216,null,null,1,null,sl)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n                "])),(l()(),d._0(16777216,null,null,1,null,ol)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n              "])),(l()(),d._27(null,["\n              "])),(l()(),d._6(0,null,null,7,"div",[["class","examineportraitrightDiv"]],null,null,null,null,null)),(l()(),d._27(null,["\n                "])),(l()(),d._6(0,null,null,1,"div",[["class","examineName"]],null,null,null,null,null)),(l()(),d._27(null,["",""])),(l()(),d._27(null,["\n                "])),(l()(),d._6(0,null,null,1,"div",[["class","examinestate"],["style","color: #28aff3"]],null,null,null,null,null)),(l()(),d._27(null,["发起申请"])),(l()(),d._27(null,["\n              "])),(l()(),d._27(null,["\n              "])),(l()(),d._6(0,null,null,2,"div",[["class","examineData"]],null,null,null,null,null)),(l()(),d._27(null,["",""])),d._23(2),(l()(),d._27(null,["\n          "])),(l()(),d._27(null,["\n\n          "])),(l()(),d._0(16777216,null,null,1,null,dl)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n          "])),(l()(),d._0(16777216,null,null,1,null,gl)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(null,["\n        "])),(l()(),d._27(null,["\n     "])),(l()(),d._27(1,["\n     "])),(l()(),d._0(16777216,null,1,1,null,vl)),d._4(16384,null,0,M.k,[d.O,d.K],{ngIf:[0,"ngIf"]},null),(l()(),d._27(1,["\n"]))],function(l,n){var u=n.component;l(n,5,0,"primary"),l(n,26,0,1==u.detail.state),l(n,29,0,0!=u.detail.state&&1!=u.detail.state),l(n,32,0,0==u.detail.state);l(n,37,0,"right");l(n,47,0,"xq-time");l(n,58,0,"xq-place");l(n,67,0,"xq-user");l(n,76,0,"xq-unit");l(n,85,0,"xq-attendee"),l(n,94,0,null==u.detail.conterenceContent||""==u.detail.conterenceContent),l(n,97,0,null!=u.detail.conterenceContent&&""!=u.detail.conterenceContent);l(n,106,0,"circle-ok"),l(n,109,0,0==u.detail.state),l(n,112,0,0!=u.detail.state&&1!=u.detail.state),l(n,124,0,!u.detail.userNameBigPicture),l(n,127,0,u.detail.userNameBigPicture),l(n,145,0,0==u.detail.state),l(n,148,0,0!=u.detail.state&&1!=u.detail.state),l(n,153,0,1==u.detail.state)},function(l,n){var u=n.component;l(n,4,0,d._20(n,5)._hidden,d._20(n,5)._sbPadding),l(n,13,0,d._20(n,14).statusbarPadding,d._20(n,14)._hasRefresher),l(n,21,0,u.detail.conferenceName),l(n,23,0,d._9(3,"meetingstate  ",1==u.detail.state?"list1":"","  ",0!=u.detail.state&&1!=u.detail.state?"list2":""," ",0==u.detail.state?"list3":"","")),l(n,36,0,d._20(n,37)._hidden),l(n,46,0,d._20(n,47)._hidden),l(n,48,0,d._28(n,48,0,l(n,49,0,d._20(n,0),u.detail.takeStartDate,"H:mm")),d._28(n,48,1,l(n,50,0,d._20(n,0),u.detail.takeEndDate,"H:mm  yyyy-MM-dd"))),l(n,57,0,d._20(n,58)._hidden),l(n,59,0,u.detail.address),l(n,66,0,d._20(n,67)._hidden),l(n,68,0,u.detail.userName),l(n,75,0,d._20(n,76)._hidden),l(n,77,0,u.detail.hostUnit),l(n,84,0,d._20(n,85)._hidden),l(n,86,0,u.detail.count),l(n,105,0,d._20(n,106)._hidden),l(n,133,0,u.detail.userName),l(n,140,0,d._28(n,140,0,l(n,141,0,d._20(n,0),u.detail.confrerenceCreateTime,"yyyy.MM.dd H:mm")))})}var pl=d._1("page-backlogdetail",p,function(l){return d._29(0,[(l()(),d._6(0,null,null,1,"page-backlogdetail",[],null,null,null,hl,ll)),d._4(49152,null,0,p,[G.a,H.a,v.a,Q.a,X.a,Z.a,h.a,H.a,$.a],null,null)],null,null)},{},{},[]),kl=u(18),bl=u(174),xl=u(65);u.d(n,"BacklogdetailModuleNgFactory",function(){return Il});var Il=d._2(k,[],function(l){return d._17([d._18(512,d.j,d.Y,[[8,[b.a,x.a,I.a,y.a,N.a,D.a,O.a,E.a,C.a,pl]],[3,d.j],d.v]),d._18(4608,M.m,M.l,[d.t]),d._18(4608,kl.m,kl.m,[]),d._18(4608,kl.c,kl.c,[]),d._18(512,M.c,M.c,[]),d._18(512,kl.l,kl.l,[]),d._18(512,kl.d,kl.d,[]),d._18(512,kl.k,kl.k,[]),d._18(512,bl.a,bl.a,[]),d._18(512,bl.b,bl.b,[]),d._18(512,g.a,g.a,[]),d._18(512,k,k,[]),d._18(256,xl.a,p,[])])})}});