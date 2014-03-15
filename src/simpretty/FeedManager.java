package simpretty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Logger;

import com.google.appengine.api.ThreadManager;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;

public class FeedManager {

	private static final Logger log = Logger.getLogger(FeedManager.class.getName());

	private final String[] urls;
	private final FeedDatabase db;

	private List<HashMap<String, String>> articles = new ArrayList<>();
	private List<SyndFeed> feeds = new ArrayList<>();

	public FeedManager(String[] urls) {
		this.urls = urls;
		this.db = new FeedDatabase();
	}

	public void save() {
		db.save(articles);
	}
	
	public List<HashMap<String, String>> asList() {
		 // XXX or fetch as another method to be called from caller
		if (feeds.isEmpty()) {
			fetchAll();
		}
		if (articles.isEmpty()) {
			parse();
		}
		save();
		
		return articles;
	}

	public synchronized void addFeed(SyndFeed feed) {
		feeds.add(feed);
	}
	
	public void fetchAll() {
		ThreadFactory threadFactory = ThreadManager.currentRequestThreadFactory();
		ArrayList<Thread> threads = new ArrayList<>();
		
		// Fetch feeds in multiple threads
		for (String url : urls) {
			FeedFetcher fetcher = new FeedFetcher(url, this);
			Thread thread = threadFactory.newThread(fetcher);
			threads.add(thread);
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
	}
	
	public void parse() {
		for (SyndFeed feed : feeds) {
			articles.addAll(parseFeed(feed));
		}
	}
	
	private List<HashMap<String, String>> parseFeed(SyndFeed feed) {
		List<HashMap<String, String>> contents 	= new ArrayList<HashMap<String, String>>();
		
		for (Object obj : feed.getEntries()) {
			SyndEntry entry = (SyndEntry) obj;
			EntryParser parser = new EntryParser(entry);
			parser.parse();
			contents.add(parser.getContents());
		}
		
		return contents;
	}	
}
