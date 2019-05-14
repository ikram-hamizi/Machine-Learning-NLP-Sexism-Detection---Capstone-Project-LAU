package instClasses;

import java.util.List;

public class WindowTweet {
	
	private String original_tweet;
	private List<String> processed_tweet_qw;
	
	public WindowTweet(String original_tweet, List<String> processed_tweet_qw)
	{
		this.original_tweet = original_tweet;
		this.processed_tweet_qw = processed_tweet_qw;
	}
	
	public void setOriginal_tweet(String original_tweet) {
		this.original_tweet = original_tweet;
	}
	
	public String getOriginal_tweet() {
		return original_tweet;
	}
	
	public void setProcessed_tweet_qw(List<String> processed_tweet_qw) {
		this.processed_tweet_qw = processed_tweet_qw;
	}
	
	public List<String> getProcessed_tweet_qw() {
		return processed_tweet_qw;
	}
	
	public boolean isInitialized()
	{
		if(original_tweet != null && processed_tweet_qw != null)
			return true;
		return false;
	}
}
