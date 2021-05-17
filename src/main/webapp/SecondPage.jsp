<%@ page language="java" import="java.util.*"
	import="com.cn.DataShow,com.cn.Idea,com.cn.ResultShow,com.cn.BusinessModel"
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>SecondPage</title>


	<style>
		body {
			background-color: #F1FAFA;
			font-size: 25;
			width: 90%;
			margin: auto;
			border: 10px;
			padding: 12px;
		}

		div {
			background-color: lightgrey;
			width: 400px;
			border: 5px solid rgb(161, 117, 99);
			padding: 10px;
			margin: 10px;
		}

		h1 {
			color: black;
			font-size: 40px
		}
		
		h2 {
            color: black ; 
            font-size:30px;
        }

		p {
			font-size: 25px
		}
		
		form {
       		display: inline;
    	}

		input {
        	width: 100px; height: 30px; font-weight:bold;
   		}
		
		#des {
			margin-left:50px;
		}
   		
	</style>

</head>

<body>

	<h1> Business models </h1>

	<h2>
		We have found the following business models that suit your idea!
	</h2>
	
	<%
		List<BusinessModel> list_bm = (List<BusinessModel>)session.getAttribute("list_bm");
		BusinessModel bm1 = list_bm.get(0);
		BusinessModel bm2 = list_bm.get(1);
		BusinessModel bm3 = list_bm.get(2);
	%>

	<br>
	<br>
	<hr>

	<p>
		1 : The business model <%=bm1.getName()%> is <%=bm1.getSimilarity()%> % similar to your idea
	</p>

	<p id="des">
		Description : <%=bm1.getDescription()%>
	</p>

	<br>
	<hr>


	<p>
		2 : The business model <%=bm2.getName()%> is <%=bm2.getSimilarity()%> % similar to your idea
	</p>

	<p id="des">
		Description : <%=bm2.getDescription()%>
	</p>

	<br>
	<hr>
	

	<p>
		3 : The business model <%=bm3.getName()%> is <%=bm3.getSimilarity()%> % similar to your idea
	</p>

	<p id="des">
		Description : <%=bm3.getDescription()%>
	</p>


	<br>
	<hr>
	<br>

	<form>	
		<input type="button" value ="Evaluate" onclick = "window.location.href ='ThirdPage.jsp'">
	</form>


</body>

</html>