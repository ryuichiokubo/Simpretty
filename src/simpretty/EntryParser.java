package simpretty;

import java.util.HashMap;
import java.util.logging.Logger;

import com.sun.syndication.feed.synd.SyndEntry;

public class EntryParser {
	private static final Logger log = Logger.getLogger(SimprettyServlet.class.getName());

	private SyndEntry entry;
	private HashMap<String, String> contents = new HashMap<String, String>();

	public EntryParser(SyndEntry entry) {
		this.entry = entry;
	}
	
	public void parse() {
		log.info("Started parsing.");
		
		contents.put("title", entry.getTitle());
		contents.put("hash", Integer.toString(entry.hashCode()));
		contents.put("time", Long.toString(entry.getPublishedDate().getTime()));
		contents.put("link", entry.getLink());
	}
	
	public HashMap<String, String> getContents() {
		return contents;
	}
}
