package simpretty;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;
import java.util.concurrent.ThreadFactory;

import javax.servlet.ServletException;
import javax.servlet.http.*;

import com.google.appengine.api.ThreadManager;
import com.google.gson.Gson;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

@SuppressWarnings("serial")
public class SimprettyServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(SimprettyServlet.class.getName());
	private static final int CON_TIMEOUT = 10 * 1000;
	private static final String[] SOURCES = {
		"http://feeds.feedburner.com/teamtreehouse",
		"http://feeds2.feedburner.com/html5doctor",
		"http://feeds.feedburner.com/CssTricks",
		"http://www.sitepoint.com/feed/"
	};
	
	private final class FeedGetter implements Runnable {
		
		private final String url;
		private SyndFeed feed;
		
		public FeedGetter(String url) {
			this.url = url;
		}
		
		public void run() {
			log.info("Loading: " + url);
			
			try {
				
				URL resource = new URL(url);
				URLConnection conn = resource.openConnection();
				conn.setConnectTimeout(CON_TIMEOUT);
				SyndFeedInput input = new SyndFeedInput();
				feed = input.build(new XmlReader(conn));
				
			} catch (FeedException e) {
				// TODO Auto-generated catch block
				log.warning(e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.warning(e.toString());
				e.printStackTrace();
			}	
		}

		public SyndFeed getFeed() {
			return feed;
		}
	}
	
	private List<HashMap<String, String>> parseFeed(SyndFeed feed) {
		List<HashMap<String, String>> articles 	= new ArrayList<HashMap<String, String>>();
		
		for (Object obj : feed.getEntries()) {
			SyndEntry entry = (SyndEntry) obj;
			EntryParser parser = new EntryParser(entry);
			parser.parse();
			articles.add(parser.getContents());
		}
		
		return articles;
	}
	
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
		ThreadFactory threadFactory = ThreadManager.currentRequestThreadFactory();
		ArrayList<Thread> threads = new ArrayList<>();
		ArrayList<FeedGetter> getters = new ArrayList<>();
		
		// Fetch feeds in multiple threads
		for (String url : SOURCES) {
			FeedGetter getter = new FeedGetter(url);
			Thread thread = threadFactory.newThread(getter);
			threads.add(thread);
			getters.add(getter);
			thread.start();
		}

		// Wait for all threads to complete
		for (Thread thread : threads) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Parse and concatenate all feeds
		for (FeedGetter getter : getters) {
			SyndFeed feed = getter.getFeed();
			articles.addAll(parseFeed(feed));
		}
		
		sortArticles(articles);
		sendResponse(articles, resp);
	}
}
