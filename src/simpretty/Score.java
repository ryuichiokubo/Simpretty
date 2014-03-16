package simpretty;

public class Score {
	public String keyword;
	public long count;
	
	public Score(String keyword, long count) {
		this.keyword = keyword;
		this.count = count;
	}
	
	@Override
	public String toString() {
		return "keyword: " + keyword + ", count: " + count;
	}
}
