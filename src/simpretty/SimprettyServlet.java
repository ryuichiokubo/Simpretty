package simpretty;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

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
	private static final int CON_TIMEOUT = 30 * 1000;
	
	private SyndFeed getFeedFromUrl(String urlStr) throws IOException {
		log.info("Loading: " + urlStr);
		
		URL url = new URL(urlStr);
		URLConnection conn = url.openConnection();
		conn.setConnectTimeout(CON_TIMEOUT);
		
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = null;
		try {
			feed = input.build(new XmlReader(conn));
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FeedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return feed;
	}
	
	private List<HashMap<String, String>> parseFeed(SyndFeed feed) {
		List<HashMap<String, String>> articles 	= new ArrayList<HashMap<String, String>>();
		List<EntryParser> parsers 				= new ArrayList<EntryParser>();
		List<Thread> threads 					= new ArrayList<Thread>();
		ThreadFactory threadFactory 			= ThreadManager.currentRequestThreadFactory();
		
		for (Object obj : feed.getEntries()) {
			SyndEntry entry = (SyndEntry) obj;
			EntryParser parser = new EntryParser(entry);
			Thread thread = threadFactory.newThread(parser);
			thread.start();
			threads.add(thread);
			parsers.add(parser);
		}
		for (Thread thread: threads) {
			try {
				thread.join(); // XXX replace with notify or something to wait for threads execution smartly
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (EntryParser parser: parsers) {
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
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException { // XXX exception handling
		
		SyndFeed feed = getFeedFromUrl("http://rss.dailynews.yahoo.co.jp/fc/rss.xml");
		List<HashMap<String, String>> articles = parseFeed(feed);	
		sendResponse(articles, resp);
	}
}
