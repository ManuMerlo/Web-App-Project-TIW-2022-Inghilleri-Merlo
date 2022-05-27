package it.polimi.tiw.filters;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.QuoteDAO;

/**
 * Servlet Filter implementation class AdminChecker
 */

public class QuoteChecker implements Filter {

	private Connection connection;

	/**
	 * Default constructor.
	 */
	public QuoteChecker() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		System.out.print("Quote filter executing ..\n");
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		HttpSession session = req.getSession();
		QuoteDAO quoteDAO = new QuoteDAO(connection);
		User user = null;
		String loginpath = null;
		user = (User) session.getAttribute("currentUser");
		if (user.getRole().equals("client")) {
			loginpath = req.getServletContext().getContextPath() + "/GotoClientHome";
		} else if (user.getRole().equals("worker")) {
			loginpath = req.getServletContext().getContextPath() + "/GotoWorkerHome";
		}
		try {
			if ((user.getRole().equals("client") && user.getId() != quoteDAO
					.findQuoteById(Integer.parseInt(req.getParameter("quoteId"))).getClientId())
					|| (user.getRole().equals("worker") && user.getId() != quoteDAO
							.findQuoteById(Integer.parseInt(req.getParameter("quoteId"))).getWorkerId())) {
				res.sendRedirect(loginpath);
				return;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
