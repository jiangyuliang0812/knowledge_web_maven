<%@ page language="java" import="java.util.*" import="com.cn.DataShow,com.cn.Idea,com.cn.Evaluation"
    contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>

    <!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

    <html>

    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
        <title>ThirdPage</title>

        <style>
            body {
                background-color: #F1FAFA;
                font-size: 25;
                width: 90%;
                margin: auto;
                border: 10px;
                padding: 12px;
            }

            p {
                font-size: 24px;
            }

            h1 {
                color: black;
                font-size: 40px;
            }

            h2 {
                color: black;
                font-size: 30px;
            }
           
            h3 {
                color: black;
                font-size: 28px;
            }


            input {
                width: 100px;
                height: 30px;
                font-weight: bold;
            }

            form {
                margin-left: 50px;
            }

            #container {
                width: 1000px;
                height: 180px;
                border: 2px;
                padding: 2px;
                margin: 2px;
            }

            #entername {
                width: 200px;
                height: 35px;
            }
        </style>

    </head>

    <body>

        <h1>Evaluation</h1>

        <h2>
            Dear user! <br> Please evaluate the recommended business model.
        </h2>

        <br>
        <hr>
        <br>

        <form name=form0 action="/knowledge_web_maven/Evaluation" method="post">

            <h3>
                <label> Please enter your name : </label>
                <input id="entername" type="text" name="entername">
            </h3>

            <h3>The 1st recommended business model:</h3>
			
            <p>
                Can help the idea to better solve problems?
                <label><input type="radio" name="evaluate1" value="Yes">Yes</label> 
                <label><input type="radio" name="evaluate1" value="No">No</label>
            </p>

            <p>
                Can help the idea to increase revenue?
                <label><input type="radio" name="evaluate2" value="Yes">Yes</label>
                <label><input type="radio" name="evaluate2" value="No">No</label>
            </p>

            <h3>The 2nd recommended business model:</h3>

            <p>
                Can help the idea to better solve problems?
                <label><input type="radio" name="evaluate3" value="Yes">Yes</label> 
                <label><input type="radio" name="evaluate3" value="No">No</label>
            </p>
            <p>
                Can help the idea to increase revenue?
                <label><input type="radio" name="evaluate4" value="Yes">Yes</label> 
                <label><input type="radio" name="evaluate4" value="No">No</label>
            </p>

            <h3>The 3rd recommended business model:</h3>

            <p>
                Can help the idea to better solve problems?
                <label><input type="radio" name="evaluate5" value="Yes">Yes</label> 
                <label><input type="radio" name="evaluate5" value="No">No</label>
            </p>
            <p>
                Can help the idea to increase revenue?
                <label><input type="radio" name="evaluate6" value="Yes">Yes</label> 
                <label><input type="radio" name="evaluate6" value="No">No</label>
            </p>

        </form>


        <p>
            <input type="button" value="Submit" onclick="javascript:form0.submit();" id=button name=button />
        </p>



        <br>
        <hr>
        <br>

    </body>

    </html>