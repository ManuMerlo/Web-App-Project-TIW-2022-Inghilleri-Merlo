package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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
import it.polimi.tiw.packets.QuoteDetails;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/UpdatePrice")
@MultipartConfig
public class UpdatePrice extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public UpdatePrice() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User currentUser = (User) session.getAttribute("currentUser");
		QuoteDAO quoteDAO = new QuoteDAO(connection);
		Quote quote = null;
		
		int quoteId = -1;
		int price = -1;
		try {
			quoteId = Integer.parseInt(request.getParameter("quoteId"));
			quote = quoteDAO.findQuoteById(quoteId);
			if(quote==null) {
				throw new Exception();
			}
		} catch(SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover the quote");
			return;
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid Quote Id");
			return;
		}
		
		if (quote.getWorkerId() != 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("This quote is already priced");
			return;
		}

		try {
			price = Integer.parseInt(request.getParameter("price"));
			if (price <= 0)
				throw new Exception();
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid format! Please insert number");
			return;
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Please insert a correct price");
			return;
		}

		try {
			quoteDAO.updateQuote(quoteId, currentUser.getId(), price);
			quote=quoteDAO.findQuoteById(quoteId);
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to update the quote's price");
			return;
		}
		String json = new Gson().toJson(quote);

		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);

	}

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
