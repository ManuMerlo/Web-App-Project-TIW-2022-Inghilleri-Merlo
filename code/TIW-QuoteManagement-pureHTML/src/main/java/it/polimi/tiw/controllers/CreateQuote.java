package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.dao.ProductDAO;
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
		ProductDAO productDAO = new ProductDAO(connection);
		List<Integer> chosenOptions = new ArrayList<>();
		int clientId = currentUser.getId();
		Integer productCode = null;
		try {
			/*if (request.getParameter("productCode") == null || request.getParameter("productCode").equals(""))
				throw new Exception();*/
			productCode = Integer.parseInt(request.getParameter("productCode"));
			if (productCode==null || productDAO.findProductByCode(productCode) == null) {
				throw new Exception();
			}
		} catch (Exception e) {
			warning(request,response,"Invalid product");
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
			warning(request,response,"Invalid options");
			return;
		}
		try {
			int quoteId = quoteDAO.insertQuote(clientId, productCode);
			for (Integer optionCode : chosenOptions) {
				optionDAO.insertOption(quoteId, optionCode);
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
	
	private void warning(HttpServletRequest request, HttpServletResponse response, String warning) throws ServletException, IOException {
		request.setAttribute("warning", warning);
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/GotoClientHome");
		dispatcher.forward(request, response);
		/*ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("warning", "Incorrect username or password");
		response.sendRedirect(getServletContext().getContextPath() + "/GotoClientHome");*/
		
	}
}
