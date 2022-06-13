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

import it.polimi.tiw.beans.Quote;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.QuoteDAO;
import it.polimi.tiw.dao.ProductDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class GotoWorkerHome
 */
@WebServlet("/GotoWorkerHome")
public class GotoWorkerHome extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	private Connection connection;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GotoWorkerHome() {
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
		ProductDAO productDAO =new ProductDAO(connection);
		List<Quote> managedQuotes;
		List<Quote> unmanagedQuotes;
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		try {
			managedQuotes = quoteDAO.findQuotesByUserId(currentUser.getId(),currentUser.getRole());
			for(Quote q:managedQuotes) {
				q.setProduct(productDAO.findProductByCode(q.getProductCode()));
			}
			unmanagedQuotes = quoteDAO.findUnmanagedQuotes();
			for(Quote q:unmanagedQuotes) {
				q.setProduct(productDAO.findProductByCode(q.getProductCode()));
			}
		} catch (SQLException e) {
			//e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not Possible to load the Worker page");
			return;
		}
		ctx.setVariable("managedQuotes", managedQuotes);
		ctx.setVariable("unmanagedQuotes", unmanagedQuotes);
		
		templateEngine.process("/WEB-INF/WorkerHome.html", ctx, response.getWriter());
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
