package simpretty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class SimprettyServlet extends HttpServlet {

	private static final Logger log = Logger.getLogger(SimprettyServlet.class.getName());
	private static final String[] SOURCES = {
		"http://feeds.feedburner.com/teamtreehouse",
		"http://feeds2.feedburner.com/html5doctor",
		"http://feeds.feedburner.com/CssTricks",
		"http://www.sitepoint.com/feed/"
	};
	
	private void sendResponse(List<HashMap<String, String>>articles, HttpServletResponse resp) throws IOException {		
		Gson gson = new Gson();
		String json = gson.toJson(articles);
			    
		resp.setContentType("application/json");
		resp.setCharacterEncoding("utf-8");
		resp.getWriter().write(json);
	}
	
	private void sortArticles(List<HashMap<String, String>>articles) {
		Collections.sort(articles, new Comparator<HashMap<String, String>>() {
			public int compare(HashMap<String, String> article1, HashMap<String, String> article2) {
				int res = 0;
				if (article1.get("time") != null && article2.get("time") != null) {
					long time1 = Long.parseLong(article1.get("time"));
					long time2 = Long.parseLong(article2.get("time"));
					if (time1 < time2) {
						res = 1;
					} else if (time1 > time2) {
						res = -1;
					}
				}
				return res;
			}
		});
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		List<HashMap<String, String>> articles = new ArrayList<>();
		FeedManager manager = new FeedManager(SOURCES);

		articles = manager.asList();
		sortArticles(articles);
		sendResponse(articles, resp);
	}
}
