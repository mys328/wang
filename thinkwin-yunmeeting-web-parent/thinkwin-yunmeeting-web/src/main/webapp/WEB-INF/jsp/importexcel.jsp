<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <script type="text/javascript" src="https://cdn.bootcss.com/jquery/3.2.1/jquery.min.js"></script>
    <script type="text/javascript" src="https://cdn.bootcss.com/jquery-json/2.6.0/jquery.json.min.js"></script>
</head>
<body>
<h2>Excel上传导出：poi方式</h2>

<%--<form action="/importPreviewExcel?token=7ed9890bb2e041d0a90ce9578d853472" enctype="multipart/form-data" method="post">
    <input type="file" name="myfile" id="myfile">
    <input type="submit" value="导入">
</form>--%>

<form id= "uploadForm">
    <p >上传文件： <input type="file" name="myfile"/></p>
    <input type="button" value="上传" onclick="doUpload()" />
</form>
<input type="hidden" id = "tokenId" value="${token}"/>
<h1>submitUserList(多用户提交)</h1>
<input type="hidden" id = "tokenId" value="${token}"/>
<input id="submit" type="button" value="Submit" onclick="submitUserList_3();" />
<script type="text/javascript" language="JavaScript">
    function submitUserList_3() {
       var token = document.getElementById("tokenId").value;
        alert("ok :"+token);
        var customerArray = new Array();
        customerArray.push({userNum: "1", name: "李四", position: "123"});
        customerArray.push({userNum: "2", name: "张三", position: "332"});
        $.ajax({
            url: "/importExcel?token="+token,
            type: "POST",
            contentType : 'application/json;charset=utf-8', //设置请求头信息
            dataType:"json",
            //data: JSON.stringify(customerArray),    //将Json对象序列化成Json字符串，JSON.stringify()原生态方法
            data: $.toJSON(customerArray),            //将Json对象序列化成Json字符串，toJSON()需要引用jquery.json.min.js
            success: function(data){
                alert(data.isSuc);
            },
            error: function(res){
                alert(res.responseText);
            }
        });
    }

</script>

<!-------------------------------------------------------------------------------------------------------------->
<br/><br/><br/><br/><br/><br/><br/>
<script language="JavaScript" type="text/JavaScript">
function doUpload() {
    var formData = new FormData($( "#uploadForm" )[0]);
    var token = document.getElementById("tokenId").value;
    $.ajax({
        url: 'http://localhost:8080/importPreviewExcel?token='+token ,
        type: 'POST',
        data: formData,
        async: false,
        cache: false,
        contentType: false,
        processData: false,
        success: function (returndata) {
            if (returndata.ifSuc == 1) {
            for (var i = 0; i < returndata.data.length; i++) {
                var data1 = returndata.data[i];
                var ttt = "编号 ：" + data1.userNum + " 职位 ：" + data1.position + " 部门 ：" + data1.depar + " 姓名 ：" + data1.name + " 性别 ：" + data1.sex + " 手机号 ：" + data1.phoneNum + " 邮箱 ：" + data1.email;
                var customerArray = new Array();
                alert(customerArray.push(ttt));
                console.log(customerArray[0]);
                alert(customerArray[0]);
            };
        }else {
                alert(returndata.msg);
            }
        },
        error: function (returndata) {
            alert(returndata);
        }
    });
}
</script>
<!-------------------------------------------------------------------------------------------------------------->
</body>
</html>
