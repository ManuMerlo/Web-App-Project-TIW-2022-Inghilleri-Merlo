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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.dao.ProductDAO;
import it.polimi.tiw.dao.QuoteDAO;
import it.polimi.tiw.utils.ConnectionHandler;

@WebServlet("/CreateQuote")
@MultipartConfig
public class CreateQuote extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

	public CreateQuote() {
		super();
	}

	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User currentUser = (User) session.getAttribute("currentUser");
		QuoteDAO quoteDAO = new QuoteDAO(connection);
		OptionDAO optionDAO = new OptionDAO(connection);
		ProductDAO productDAO = new ProductDAO(connection);
		List<Integer> chosenOptions = new ArrayList<>();
		int clientId = currentUser.getId();
		Integer productCode = null;

		try {
			productCode = Integer.parseInt(request.getParameter("productCode"));
			if (productCode == null || productDAO.findProductByCode(productCode) == null) {
				throw new Exception();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to recover Quotes");
			return;
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid Quote Id");
			return;
		}
		try {
			if (request.getParameter("chosenOptions") == null || request.getParameter("chosenOptions").equals(""))
				throw new Exception();
			for (String s : request.getParameterValues("chosenOptions")) {
				if (s == null || s.equals("") || !optionDAO.hasOptionByCode(productCode, Integer.parseInt(s)))
					throw new Exception();
				chosenOptions.add(Integer.parseInt(s));
			}
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid Options");
			return;
		}
		try {
			int quoteId = quoteDAO.insertQuote(clientId, productCode);
			for (Integer optionCode : chosenOptions) {
				optionDAO.insertOption(quoteId, optionCode);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Not possible to insert Quote");
		}
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
