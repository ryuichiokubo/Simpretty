package simpretty;

import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HomeServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(HomeServlet.class.getName());
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		log.info("Serving home");

		resp.setContentType("text/html");
	    resp.setCharacterEncoding("utf-8");
	}
}