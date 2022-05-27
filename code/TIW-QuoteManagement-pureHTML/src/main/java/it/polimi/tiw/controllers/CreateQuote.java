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
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.OptionDAO;

import it.polimi.tiw.dao.QuoteDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class RegisterAccount
 */
@WebServlet("/CreateQuote")
public class CreateQuote extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CreateQuote() {
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

		HttpSession session = request.getSession(false);
		User currentUser = (User) session.getAttribute("currentUser");
		QuoteDAO quoteDAO = new QuoteDAO(connection);
		OptionDAO optionDAO = new OptionDAO(connection);
		String[] chosenOptions = null;
		int clientId = currentUser.getId();
		int productCode = Integer.parseInt(request.getParameter("productCode"));
		try {
		chosenOptions = request.getParameterValues("chosenOptions");
		if (chosenOptions == null || chosenOptions.length == 0 )
			throw new Exception();
		}
		catch(Exception e) {
			request.setAttribute("warning", "Please choose at least one option");
			RequestDispatcher dispatcher = request.getRequestDispatcher("/GotoClientHome");
			dispatcher.forward(request, response);
			return;
		}

		try {
			int quoteId = quoteDAO.insertQuote(clientId, productCode, chosenOptions);
			for (String s : chosenOptions) {
				optionDAO.insertOption(quoteId, Integer.parseInt(s));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		response.sendRedirect(getServletContext().getContextPath() + "/GotoClientHome");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
