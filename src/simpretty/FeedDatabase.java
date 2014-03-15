package simpretty;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.Field;
import com.google.appengine.api.search.Index;
import com.google.appengine.api.search.IndexSpec;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.appengine.api.search.SearchServiceFactory;

public class FeedDatabase {

	private static final Logger log = Logger.getLogger(FeedDatabase.class.getName());

	public void save(List<HashMap<String, String>> articles) {
		// TODO use articles
		
		Document doc = Document.newBuilder()
				.addField(Field.newBuilder().setName("testContent").setText("the snow in finland"))
				.build();
		
		IndexSpec indexSpec = IndexSpec.newBuilder().setName("test").build();
		Index index = SearchServiceFactory.getSearchService().getIndex(indexSpec);
		index.put(doc);
		
		String queryString = "testContent: snow";
		Results<ScoredDocument> results = index.search(queryString);
		for (ScoredDocument document : results) {
			log.info(document.toString());
		}
	}

}
