<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
<body>
<script type="text/javascript">
    window.onload=function () {
        window.location.href="/system/loginpage";
    }
</script>
<h2>sendmail proj</h2>
<a href="test">测试接口</a>
</body>

<form action="/upload/fileUpload" method="post" enctype="multipart/form-data">
    <input type="file" name="fileName">

    <input type="submit" value="提交">
</form>

    <a href="http://10.10.11.56/yunmeeting/M00/00/00/CgoLOFklJ_OAYSgvAAxghgyhUmc557.jpg?attname=mmexport1495181274426.jpg">下载图片</a>

    <a href="/upload/selectTenementByFile">查看</a>

    <a href="/upload/deleteFileUpload">删除</a>
    <div style="width: 300px; height: 300px;"><img src="http://10.10.11.56/yunmeeting/M00/00/00/CgoLOFkvw_2AJkErAABx8VQAgbI828.jpg"/></div>
    <a href="/dictionary/deleteSysDictionary">字典测试</a>

<br/><br/><br/><br/><br/><br/><br/><br/>
<form action="/dictionary/insertSysDictionary" method="post" >
    <input type="text" name="parentId" value="上一级ID">
    <input type="text" name="dictSort" value="字典分类">
    <input type="text" name="tenantId" value="租户ID">
    <input type="text" name="dictCode" value="字典编码">
    <input type="text" name="dictName" value="字典名称">
    <input type="text" name="dictValue" value="字典值">
    <input type="text" name="descript" value="备注">
    <input type="text" name="createId" value="创建人">
    <input type="text" name="platformId" value="1">

    <input type="submit" value="创建提交">
</form>

<%--<form action="/dictionary/updateSysDictionary" method="post" >
    <input type="text" name="parentId" value="上一级ID">
    <input type="text" name="dictSort" value="字典分类">
    <input type="text" name="tenantId" value="租户ID">
    <input type="text" name="dictCode" value="字典编码">
    <input type="text" name="dictName" value="字典名称">
    <input type="text" name="dictValue" value="字典值">
    <input type="text" name="descript" value="备注">
    <input type="text" name="createId" value="创建人">
    <input type="text" name="platformId" value="平台ID">

    <input type="submit" value="字典提交">
</form>--%>
<br/><br/><br/><br/><br/><br/>
<a href="/dictionary/selectSysDictionary">字典测试</a>

<br/><br/><br/><br/><br/><br/>
<a href="/upload/test1">字典测试</a>

<br/><br/><br/><br/><br/><br/>
<a href="/upload/test2">字典测试</a>
</html>
