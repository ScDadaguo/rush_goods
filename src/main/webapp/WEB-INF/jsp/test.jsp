<%--
  Created by IntelliJ IDEA.
  User: A
  Date: 2018/12/29
  Time: 17:47
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script type="text/javascript" src="https://code.jquery.com/jquery-3.2.1.min.js">

    </script>
    <script type="text/javascript">
        console.log("guohao")
        for (var i=1;i=300;i++){
        var  params={
            userId:1,
            productId:1,
            quantity:1
        };
        // $.post("http://localhost:8080/purchase",params,function (result) {
        //     alert(result.message);
        //
        // })

        $.ajax({
            type: "POST",
            url: "http://localhost:8080/purchase",
            contentType: "application/json", //必须有
            dataType: "json", //表示返回值类型，不必须
            data: JSON.stringify(params) ,//相当于 //data: "{'str1':'foovalue', 'str2':'barvalue'}",
            async:false,
        });

        }
    </script>
</head>
<body>

</body>
</html>
