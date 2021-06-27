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
        font-size: 20;
        width: 90%;
        margin: auto;
        border: 10px;
        padding: 12px;
    }
	
    #container{
        width: 1000px;
        height: 450px;
        border: 5px;
        padding: 5px;
        margin: 5px;
    }
	
    #outside{
        height: 400px;
        width: 500px;
        float: left;
    }

    #inside1{
        background-color: lightgrey;
        width: 400px;
        border: 6px solid rgb(161, 117, 99);
        padding: 12px;
        margin: 6px;
    }

    form{
        display: inline;
    }
    
    p{
        font-size: 25px;
    }
    
    h1{
        color: black ; font-size:40px;
    }
    
    h2{
        color: black ; font-size:30px;
    }
    
    label{
        font-size:20px;
    }
    
    #pm{
       min-width:385px;min-height:250px; font-size:20px;
    }

    #question{
        min-width:400px;min-height:30px; font-size:20px;
    }
    
    input{
        width: 100px; height: 30px; font-weight:bold;
    }
    
    </style>
    
    </head>
    
    <body>
    
    <h1> Challenge </h1>
        
        <p>
            Welcome user ! 
            <br> 
            We are searching for innovative ideas with commercial value.
        </p>
    
        <hr>
        
        <div id="container">

        <div id="outside">
       
        <h2> Submit a new idea </h2>
    
        
        <form action="/knowledge_web_maven/Main" method="post">
    
            <div id="inside1">
                <label> Please describe your Idea : </label>
                <textarea id="pm" name="idea" ></textarea>
                <br>
            </div>
    
            <br>
            
            <input type="submit" name="submit" value="Submit"/>
    
        
        
        </div>

		</div>
        
		<br>
	  	<hr>
	  	<br>
	  	
        <table border="1">
            <tr>
                <td>Idea_id</td>
                <td>Idea</td>     
                <td>CreateTime</td>
                <td>keyword_idea</td>
               
            </tr>
            <%
                DataShow show = new DataShow();
                List<Idea> list = show.showMySQl();
                for (Idea idea : list) {
            %>
            <tr>
                <td><%=idea.getIdea_id()%></td>
                <td><%=idea.getIdea()%></td>
                <td><%=idea.getCreateTime()%></td>
                <td><%=idea.getKeyword_idea()%></td>
            </tr>
            <%
                }
            %>
        </table>
        
    
    
      <br>
      <hr>
    
    </body>
</html>