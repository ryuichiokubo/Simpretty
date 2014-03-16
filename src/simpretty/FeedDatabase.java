package simpretty;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.PutResponse;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;

public class FeedDatabase {

	private static final Logger log = Logger.getLogger(FeedDatabase.class.getName());

	private static final String name = "articles";
	
	private Index index;
	
	public void save(List<Map<String, String>> articles) {

		IndexSpec indexSpec = IndexSpec.newBuilder().setName(name).build();
		index = SearchServiceFactory.getSearchService().getIndex(indexSpec);

		ArrayList<Document> docs = new ArrayList<Document>();
		for (Map<String, String> article : articles) {
			String uri = article.get("uri"); // XXX or create hash or use link
			String title = article.get("title");
			String link = article.get("link");
			Date date = new Date();
			date.setTime(Long.valueOf(article.get("time")));
			
			Document doc = Document.newBuilder()
					.setId(uri)
					.addField(Field.newBuilder().setName("title").setText(title))
					.addField(Field.newBuilder().setName("link").setText(link))
					.addField(Field.newBuilder().setName("date").setDate(date))
					.build();
			docs.add(doc);
		}
		
		index.put(docs); // XXX putAsync
	}
	
	public Map<String, Long> search(String[] keywords) {
		Map<String, Long> score = new HashMap<String, Long>();

		for (int i = 0; i < keywords.length; i++) {
			String queryString = "title: " + keywords[i];
			Results<ScoredDocument> results = index.search(queryString);
			long found = results.getNumberFound();
			
			log.info("@@@ num: " + found);
			
			// XXX debug
			for (ScoredDocument document : results) {
				log.info("@@@@ document: " + document.toString());
			}
			
			score.put(keywords[i], found);
		}
		
		return score;
	}

}
