package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
		if (request.getParameter("warning") != null) {
			ctx.setVariable("warning", "Please choose at least one Option");
		}

		int quoteId = Integer.parseInt(request.getParameter("quoteId"));
		OptionDAO optionDAO = new OptionDAO(connection);
		ProductDAO productDAO = new ProductDAO(connection);
		QuoteDAO quoteDAO = new QuoteDAO(connection);
		List<Option> options = new ArrayList<>();
		Product product;
		Quote quote;

		try {
			quote = quoteDAO.findQuoteById(quoteId);
			product = productDAO.findProductByCode(quote.getProductCode());
			options = optionDAO.findOptionsByQuoteId(quoteId);

		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		User user = (User) session.getAttribute("currentUser");
		if (user.getRole().equalsIgnoreCase("worker")) {
			UserDAO userDAO = new UserDAO(connection);
			try {
				User client = userDAO.findClientById(quote.getClientId());
				// request.setAttribute("client",client);
				ctx.setVariable("client", client);
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			}
		}
		// request.setAttribute("product", product);
		// request.setAttribute("options",options);
		// request.setAttribute("quote",quote);
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
		// TODO Auto-generated method stub
		doPost(request, response);
	}
}
