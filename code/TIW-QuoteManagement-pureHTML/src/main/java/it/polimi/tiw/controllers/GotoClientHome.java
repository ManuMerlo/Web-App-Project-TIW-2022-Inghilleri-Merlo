package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.beans.Quote;
import it.polimi.tiw.dao.QuoteDAO;
import it.polimi.tiw.beans.Option;
import it.polimi.tiw.beans.Product;
import it.polimi.tiw.dao.OptionDAO;
import it.polimi.tiw.dao.ProductDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class ToRegisterPage
 */
@WebServlet("/GotoClientHome")
public class GotoClientHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GotoClientHome() {
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
		int productCode = -1;
		List<Product> products;
		String selectedProductName= null;
		ProductDAO productDAO = new ProductDAO(connection);
		QuoteDAO quoteDAO = new QuoteDAO(connection);
		List<Quote> quotes;
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		if (request.getAttribute("warning") == null && request.getParameter("productCode") != null) {
			OptionDAO optionDAO = new OptionDAO(connection);
			List<Option> options = null;
			try {
				productCode = Integer.parseInt(request.getParameter("productCode"));
				if (productDAO.findProductByCode(productCode) == null) {
					throw new Exception();
				}
				selectedProductName = productDAO.findProductByCode(productCode).getName();
				options = optionDAO.findOptionsByProductCode(productCode);
				ctx.setVariable("selectedProductName", selectedProductName);
				ctx.setVariable("selectedProductCode", productCode);
				ctx.setVariable("visibilityOptions", true);
				ctx.setVariable("options", options);
			} catch (SQLException e) {
				e.printStackTrace();
				return;
			} catch (Exception e) {
				ctx.setVariable("warning", "Invalid Product");
				ctx.setVariable("visibilityOptions", false);
			}
		}
		try {
			quotes = quoteDAO.findQuotesByUserId(currentUser.getId(), currentUser.getRole());
			for (Quote q : quotes) {
				q.setProduct(productDAO.findProductByCode(q.getProductCode()));
			}
			products = productDAO.findAllProducts();
		} catch (SQLException e) {
			e.printStackTrace();
			return;
		}
		ctx.setVariable("products", products);
		ctx.setVariable("quotes", quotes);
		templateEngine.process("/WEB-INF/ClientHome.html", ctx, response.getWriter());
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
