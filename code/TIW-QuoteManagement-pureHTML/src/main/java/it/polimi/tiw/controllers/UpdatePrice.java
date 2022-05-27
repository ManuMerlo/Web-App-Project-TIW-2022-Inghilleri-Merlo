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
		// TODO Auto-generated constructor stub
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
		int quoteId = Integer.parseInt(request.getParameter("quoteId"));
		int price = -1;
		try {
			price = Integer.parseInt(request.getParameter("price"));
			if (price <= 0)
				throw new Exception();
		}catch (NumberFormatException e) {
			request.setAttribute("warning", "Invalid format! Please insert number");
			request.setAttribute("quoteId", quoteId);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/GotoQuoteDetails");
			dispatcher.forward(request, response);
			return;
		}catch (Exception e) {
			request.setAttribute("warning", "Please insert a correct price");
			request.setAttribute("quoteId", quoteId);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/GotoQuoteDetails");
			dispatcher.forward(request, response);
			return;
		} 
		

		try {
			quoteDAO.updateQuote(quoteId,currentUser.getId(),price);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		response.sendRedirect(getServletContext().getContextPath() + "/GotoWorkerHome");
	}

}
