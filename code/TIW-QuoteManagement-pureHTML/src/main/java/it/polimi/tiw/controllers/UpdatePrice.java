package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Quote;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.QuoteDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class UpdatePrice
 */
@WebServlet("/UpdatePrice")
public class UpdatePrice extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdatePrice() {
		super();
	}

	@Override
	public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	@Override
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		User currentUser = (User) session.getAttribute("currentUser");
		QuoteDAO quoteDAO = new QuoteDAO(connection);
		Quote quote = null;
		String path = null;
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
		}catch (Exception e) {
			path = "/GotoWorkerHome";
			warning(request, response, "Incorrect quote id", path);
			return;
		}
		
		if (quote.getWorkerId() != 0) {
			path = "/GotoQuoteDetails";
			warning(request, response, "This quote is already priced", path);
			return;
		}

		try {
			path = "/GotoQuoteDetails";
			price = Integer.parseInt(request.getParameter("price"));
			if (price <= 0)
				throw new Exception();
		} catch (NumberFormatException e) {
			warning(request, response, "Invalid format! Please insert number", path);
			return;
		} catch (Exception e) {
			warning(request, response, "Please insert a correct price", path);
			return;
		}

		try {
			quoteDAO.updateQuote(quoteId, currentUser.getId(), price);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		response.sendRedirect(getServletContext().getContextPath() + "/GotoWorkerHome");
	}

	private void warning(HttpServletRequest request, HttpServletResponse response, String warning, String path)
			throws ServletException, IOException {
		request.setAttribute("warning", warning);
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(path);
		dispatcher.forward(request, response);
	}

}
