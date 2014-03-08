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
	private List<HashMap<String, String>> articles = new ArrayList<>();
	private List<FeedFetcher> fetchers = new ArrayList<>();

	public FeedManager(String[] urls) {
		this.urls = urls;
		fetchAll();
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
	
	private void fetchAll() {
		ThreadFactory threadFactory = ThreadManager.currentRequestThreadFactory();
		ArrayList<Thread> threads = new ArrayList<>();
		
		// Fetch feeds in multiple threads
		for (String url : urls) {
			FeedFetcher fetcher = new FeedFetcher(url);
			Thread thread = threadFactory.newThread(fetcher);
			threads.add(thread);
			fetchers.add(fetcher);
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
	
	public List<HashMap<String, String>> asList() {
		for (FeedFetcher fetcher : fetchers) {
			SyndFeed feed = fetcher.getFeed();
			articles.addAll(parseFeed(feed));
		}

		return articles;
	}
}
