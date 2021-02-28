package com.cn;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Main extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		resp.setCharacterEncoding("utf-8");

		// get idea from the Page 1
		String idea = req.getParameter("idea");
		
		// insert idea and keywords into table ideas.
		DataInsert.insertMySQl(idea);
		
		// get triples from the keywords of the latest idea and insert to table triplesidea
		TriplesIdea.getAndSetTriplesIdea();
		
		// compare one idea with all the business models and get similarity
		List<Float> similarity = Compare.getSimilarity();
		
		// get suitable business models according to similarity and index
		List<BusinessModel> list_bm = ResultShow.showBusinessModel(similarity);
		
		// Put list into jsp
		HttpSession session = req.getSession();
		session.setAttribute("list_bm", list_bm);
		
		// go to Page 2
		 resp.sendRedirect("/knowledge_web_maven/SecondPage.jsp");
		
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
}
