package simpretty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.jdom.Element;

import com.sun.syndication.feed.synd.SyndEntry;

public class EntryParser {

	private static final Logger log = Logger.getLogger(SimprettyServlet.class.getName());

	private SyndEntry entry;
	private Map<String, String> contents = new HashMap<String, String>();

	public EntryParser(SyndEntry entry) {
		this.entry = entry;
	}
	
	/* 
	 * contents:
	 * 	- title
	 * 	- uri
	 * 	- time
	 * 	- link
	 * 	- comment
	 */
	public void parse() {
		log.info("Started parsing.");
		//log.info(entry.toString());
		
		contents.put("title", entry.getTitle());
		contents.put("uri", entry.getUri());
		if (entry.getPublishedDate() != null) {
			contents.put("time", Long.toString(entry.getPublishedDate().getTime()));
		}
		contents.put("link", entry.getLink());

		// Get number of comments (XXX particular to teamtreehouse or feedburner?)
		@SuppressWarnings("unchecked")
		List<Element> foreignMarkups = (List<Element>) entry.getForeignMarkup();
		for (Element foreignMarkup : foreignMarkups) {
			//log.info("name:" + foreignMarkup.getName());
			//log.info("namespace:" + foreignMarkup.getNamespace());
			//log.info("text:" + foreignMarkup.getText());

			if (foreignMarkup.getName() == "comments") {
				contents.put("comment", foreignMarkup.getText());
			}
		}
	}
	
	public Map<String, String> getContents() {
		return contents;
	}
}
