package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.lang.String;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.beans.Option;
import it.polimi.tiw.beans.Product;
import it.polimi.tiw.beans.Quote;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.dao.ProductDAO;
import it.polimi.tiw.dao.QuoteDAO;
import it.polimi.tiw.packets.QuoteDetails;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/GetQuoteDetails")
public class GetQuoteDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public GetQuoteDetails() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession();
		int quoteId;
		OptionDAO optionDAO = new OptionDAO(connection);
		ProductDAO productDAO = new ProductDAO(connection);
		QuoteDAO quoteDAO = new QuoteDAO(connection);
		List<Option> options = new ArrayList<>();
		Product product = null;
		Quote quote = null;
		User user = (User) session.getAttribute("currentUser");
		
		try {
			quoteId = Integer.parseInt(request.getParameter("quoteId"));
			quote = quoteDAO.findQuoteById(quoteId);
			if (quote == null || (user.getRole().equalsIgnoreCase("client") && user.getId() != quote.getClientId())
					|| (user.getRole().equalsIgnoreCase("worker") && quote.getWorkerId() != 0
					&& user.getId() != quote.getWorkerId()))
				throw new Exception();
			product = productDAO.findProductByCode(quote.getProductCode());
			options = optionDAO.findOptionsByQuoteId(quoteId);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid quote Id");
			return;
		}

		String json = new Gson().toJson(new QuoteDetails(quote,product,options));
		
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
