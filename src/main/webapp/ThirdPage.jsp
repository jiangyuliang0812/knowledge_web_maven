<%@ page language="java" import="java.util.*"
	import="com.cn.DataShow,com.cn.Idea,com.cn.Evaluation"
	contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>

<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>ThirdPage</title>

    <style>
        body {
            background-color: #F1FAFA;
            font-size: 16;
            width: 90%;
            margin: auto;
            border: 10px;
            padding: 12px;
        }

        p {
            font-size: 25px;
        }

        h1 {
            color: black;
            font-size: 40px;
        }

        input{
        width: 100px; height: 30px; font-weight:bold;
        }

    </style>

</head>

<body>

    <h1> Evaluation </h1>

    <p>
        Dear user! 
        <br>
        Please evaluate the recommended business model.
    </p>

    <br>
    <hr>
    <br>

    <form name=form1 action="/knowledge_web_maven/Evaluation" method="post">
        <p>
            
            Question 1:
            <br>
            Is the business model suitable for the idea?
            <label><input type="radio" name="evaluation" value="Yes">Yes</label>
            <label><input type="radio" name="evaluation" value="No">No</label>

        </p>
    </form>
    <form name=form2 action="/knowledge_web_maven/Evaluation" method="post">
        <p>
            <label> 
                Question 2:
                <br>
                Is the business model suitable for the idea?
            </label>
            <label><input type="radio" name="evaluation" value="Yes">Yes</label>
            <label><input type="radio" name="evaluation" value="No">No</label>
        </p>
    </form>
    <form name=form3 action="/knowledge_web_maven/Evaluation" method="post">
        <p>
            <label> 
                Question 3:
                <br>
                Is the business model suitable for the idea?
            </label>
            <label><input type="radio" name="evaluation" value="Yes">Yes</label>
            <label><input type="radio" name="evaluation" value="No">No</label>
        </p>
    </form>

    <p>
        <input type="button" value="Submit" onclick="javascript:form1.submit();form2.submit();form3.submit();" id=button name=button/>
    </p>
   

    <br>
    <hr>
    <br>

</body>

</html>