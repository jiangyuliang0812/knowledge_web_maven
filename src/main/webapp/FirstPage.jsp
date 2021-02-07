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
        width: 90%;
        margin: auto;
        border: 10px;
        padding: 12px;
    }

    #container{
        width: 1000px;
        height: 700px;
        border: 5px;
        padding: 5px;
        margin: 5px;
    }

    #outside{
        height: 500px;
        width: 450px;
        float: left;
    }


    #inside1{
        background-color: lightgrey;
        width: 300px;
        border: 6px solid rgb(161, 117, 99);
        padding: 12px;
        margin: 6px;
    }

    #inside2{
        background-color: lightgrey;
        width: 450px;
        border: 6px solid rgb(161, 117, 99);
        padding: 12px;
        margin: 12px;
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
       min-width:300px;min-height:200px; font-size:20px;
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
            We are searching for innovative solutions for the security of city building.
        </p>
    
        <hr>
        
        <div id="container">

        <div id="outside">
       
        <h2> Submit a new idea </h2>
            
        <p>
            Please describe your ideas as follows
        </p>
    
        
        <form action="/knowledge_web_maven/DataInsert" method="post">
    
            <div id="inside1">
                <label> Please describe your Idea : </label>
                <textarea id="pm" name="idea" ></textarea>
                <br>
            </div>
    
            <br>
            
            <input type="submit" name="submit" value="Submit"/>
    
        </form>
    
    
        <form action="/knowledge_web_maven/DataReset" method="post">
    
        <input type="submit" name="reset" value="Reset"/>
        
        </form>
        
        <form action="/knowledge_web_maven/TriplesIdea" method="post">
    
        <input type="submit" name="getTriples" value="getTriples"/>
        </form>
        
        </div>


        <div id="outside">
            
            <h2>
                Please describe your ideas as follows
            </h2>
        
            
            <form action="/knowledge_web_maven/DataInsert" method="post">
        
                <div id="inside2">
                    
                    <label> Advantages of your idea? </label>
                    <textarea id="question"></textarea>
                    <br>
                    <label> How to use your ideas to make money? </label>
                    <textarea id="question"></textarea>
                    <br>
                    <label> What is the risk of your idea? how to reduce that? </label>
                    <textarea id="question"></textarea>
                    <br>
                    <label> What is your idea focus on? </label>
                    <textarea id="question"></textarea>
                </div>
        
                <br>
                
        
            </form>
       
        </div>

		</div>
        
		<br>
	  	<hr>
	  
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