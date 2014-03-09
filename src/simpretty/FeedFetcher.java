package simpretty;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class FeedFetcher implements Runnable {

	private static final Logger log = Logger.getLogger(FeedFetcher.class.getName());
	private static final int CON_TIMEOUT = 10 * 1000;
	
	private final String url;
	private final FeedManager manager;
	
	public FeedFetcher(String url, FeedManager manager) {
		this.url = url;
		this.manager = manager;
	}

	@Override
	public void run() {
		SyndFeed feed = fetch();
		if (feed != null) {
			log.info("Loaded: " + feed.getTitle());
			manager.addFeed(feed);
		}
	}

	private SyndFeed fetch() {
		SyndFeed feed = null;

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
		
		return feed;
	}

}
