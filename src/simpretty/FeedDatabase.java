package simpretty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
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
import com.google.gson.Gson;

public class FeedDatabase {

	private static final Logger log = Logger.getLogger(FeedDatabase.class.getName());

	private static final String name = "articles";
	
	private Index index;
	
	public FeedDatabase() {
		IndexSpec indexSpec = IndexSpec.newBuilder().setName(name).build();
		index = SearchServiceFactory.getSearchService().getIndex(indexSpec);		
	}
	
	public void save(List<Map<String, String>> articles) {

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
	
	public Set<Score> search(String[] keywords) {
		Set<Score> score = new TreeSet<Score>(new Comparator<Score>() {
			@Override
			public int compare(Score s1, Score s2) {
				return (int) (s2.count - s1.count);
			}			
		});
		
		for (int i = 0; i < keywords.length; i++) {
			String queryString = "title: " + keywords[i];
			Results<ScoredDocument> results = index.search(queryString);
			long found = results.getNumberFound();
			
			// XXX debug
			for (ScoredDocument document : results) {
				log.info("@@@@ document: " + document.toString());
			}
			
			score.add(new Score(keywords[i], found));
		}
		
		return score;
	}

}
