package simpretty;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.gson.Gson;

public class ScoreServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(ScoreServlet.class.getName());
	private static final String[] SOURCES = {
		"http://feeds.feedburner.com/teamtreehouse",
		"http://feeds2.feedburner.com/html5doctor",
		"http://feeds.feedburner.com/CssTricks",
		"http://www.sitepoint.com/feed/"
	};
	
	private void sendResponse(Set<Score> score, HttpServletResponse resp) throws IOException {		
		Gson gson = new Gson();
		String json = gson.toJson(score);
			    
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		resp.getWriter().write(json);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		FeedManager manager = new FeedManager();
		manager.fetch();
		
		Set<Score> score = manager.getScore();
		log.info("@@@ score: " + score);
		
		sendResponse(score, resp);
	}
}
