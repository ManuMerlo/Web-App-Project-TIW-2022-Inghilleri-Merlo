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
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Option;
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.beans.Product;
import it.polimi.tiw.dao.ProductDAO;
import it.polimi.tiw.beans.Quote;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.dao.QuoteDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class ToRegisterPage
 */
@WebServlet("/GotoQuoteDetails")
public class GotoQuoteDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GotoQuoteDetails() {
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String loginpath = getServletContext().getContextPath() + "/Login.html";
		HttpSession session = request.getSession();
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		if (session.isNew() || session.getAttribute("currentUser") == null) {
			response.sendRedirect(loginpath);
			return;
		}

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
			if(quote==null) throw new Exception();
			product = productDAO.findProductByCode(quote.getProductCode());
			options = optionDAO.findOptionsByQuoteId(quoteId);
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			warning(request, response, user.getRole(), "Invalid quote id");
			return;
		}
		try {
			if ((user.getRole().equalsIgnoreCase("client") && user.getId() != quote.getClientId())
					|| (user.getRole().equalsIgnoreCase("worker") && quote.getWorkerId()!=0 && user.getId() != quote.getWorkerId())) {
				throw new Exception();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		} catch (Exception e) {
			warning(request, response, user.getRole(), "You cannot see this quote");
			return;
		}

		if (user.getRole().equalsIgnoreCase("worker")) {
			UserDAO userDAO = new UserDAO(connection);
			try {
				User client = userDAO.findClientById(quote.getClientId());
				ctx.setVariable("client", client);
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
		}
		ctx.setVariable("product", product);
		ctx.setVariable("options", options);
		ctx.setVariable("quote", quote);

		templateEngine.process("/WEB-INF/QuoteDetails.html", ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	private void warning(HttpServletRequest request, HttpServletResponse response, String role, String warning)
			throws ServletException, IOException {
		String path = null;
		request.setAttribute("warning", warning);
		if (role.equals("client")) {
			path = "/GotoClientHome";
		} else if (role.equals("worker")) {
			path = "/GotoWorkerHome";
		}
		RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(path);
		dispatcher.forward(request, response);

	}
}
