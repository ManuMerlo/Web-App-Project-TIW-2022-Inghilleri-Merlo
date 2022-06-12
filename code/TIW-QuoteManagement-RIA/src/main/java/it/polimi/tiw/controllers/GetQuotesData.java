package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.beans.Quote;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.QuoteDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/GetQuotesData")
@MultipartConfig
public class GetQuotesData extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetQuotesData() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("currentUser");
		QuoteDAO QuoteDAO = new QuoteDAO(connection);
		List<Quote> quotes = new ArrayList<Quote>();
		int workerId=-1;
		try {
			if(request.getParameter("workerId")!=null && request.getParameter("workerId")!="")
				workerId=Integer.parseInt(request.getParameter("workerId"));
			if(workerId==0 && user.getRole().equalsIgnoreCase("worker")) {
				quotes = QuoteDAO.findUnmanagedQuotes();
			} else quotes = QuoteDAO.findQuotesByUserId(user.getId(),user.getRole());
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover Quotes");
			return;
		}
	
		String json = new Gson().toJson(quotes);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
