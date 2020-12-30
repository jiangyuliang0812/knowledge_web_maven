<%@ page language="java" import="java.util.*"
	import="com.cn.DataShow,com.cn.Idea"
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>FirstPage</title>


<style>

body {
    background-color: #F1FAFA;
	font-size: 16;
	width: 85%;
	margin: auto;
}

div {
	background-color: lightgrey;
	width: 400px;
	border: 5px solid rgb(161, 117, 99);
	padding: 10px;
	margin: 10px;
}
</style>

</head>
</head>
<h1  style="color: black ; font-size:45px"> Challenge </h1>
    
	<p style="font-size:25px">
		Welcome user ! <br> We are searching for innovative solutions for
		the security of city building.
	</p>

	<hr>
	
	<h1  style="color: black ; font-size:35px"> Submit a new idea </h1>
    <p style="font-size:25px">
        Please describe your ideas as follows
    </p>


	<form action="/knowledge_web_maven/DataInsert" method="post">

			<div>
            <label style="font-size:25px" > What is the purpose: </label>
			<textarea style="min-width:390px;min-height:100px; font-size:25px" name="purpose" ></textarea>
		</div>

		<div>
			<label style="font-size:25px" > What is the mechanism: </label>
			<textarea style="min-width:390px;min-height:100px; font-size:25px" name="mechanism"></textarea>
		</div>

		<br>
		
        <input type="submit" name="submit" value="Submit"
        style="width: 100px; height: 30px; font-weight:bold" />
    

	</form>


	<form action="/knowledge_web_maven/DataReset" method="post">

	<input type="submit" name="reset" value="Reset"
			style="width: 100px; height: 30px; font-weight:bold" />

	</form>
	
	
	<form action="/knowledge_web_maven/sparqlQuery" method="post">

	<input type="submit" name="getTriples" value="getTriples"
			style="width: 100px; height: 30px; font-weight:bold" />

	</form>


	<br>
	<hr>
	<br>
	

	<table border="1">
		<tr>
			<td>Idea_id</td>
			<td>Purpose</td>
			<td>Mechanism</td>
			<td>CreateTime</td>
			<td>keyword_purpose</td>
			<td>keyword_mechanism</td>
			
		</tr>
		<%
			DataShow show = new DataShow();
			List<Idea> list = show.showMySQl();
			for (Idea idea : list) {
		%>
		<tr>
			<td><%=idea.getIdea_id()%></td>
			<td><%=idea.getPurpose()%></td>
			<td><%=idea.getMechanism()%></td>
			<td><%=idea.getCreateTime()%></td>
			<td><%=idea.getKeyword_purpose()%></td>
			<td><%=idea.getKeyword_mechanism()%></td>
		</tr>
		<%
			}
		%>
	</table>
	



	<br>
	<br>
	<hr>
	<br>
	

</body>
</html>