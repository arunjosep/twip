package com.arjose.twip.spouts;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

public class TweetSpout extends BaseRichSpout {

	static final long DELAY_FOR_TWEET = 10;
	static final String DELIMITER = "~";

	private static ConfigurationBuilder config = null;
	LinkedBlockingQueue<String> queue = null;
	SpoutOutputCollector spoutOutputCollector;
	TwitterStream twitterStream;
	private boolean fire = false;
	private String key = "";

	public TweetSpout(String customerKey, String customerSecret, String accessToken, String accessSecret, String key,
			boolean fire) {
		this.fire = fire;
		this.key = key;
		if (!"".equals(customerKey) && !"".equals(customerSecret) && !"".equals(accessToken)
				&& !"".equals(accessSecret)) {
			config = new ConfigurationBuilder().setOAuthConsumerKey(customerKey).setOAuthConsumerSecret(customerSecret)
					.setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessSecret);
		} else {
			//This works only if the file twitter4j.properties is available with all four tokens.
			config = new ConfigurationBuilder();
		}
	}

	public TweetSpout(String customerKey, String customerSecret, String accessToken, String accessSecret, String key) {
		this(customerKey, customerSecret, accessToken, accessSecret, key, false);
	}

	public TweetSpout(String customerKey, String customerSecret, String accessToken, String accessSecret,
			boolean fire) {
		this(customerKey, customerSecret, accessToken, accessSecret, "", fire);
	}

	public TweetSpout(String customerKey, String customerSecret, String accessToken, String accessSecret) {
		this(customerKey, customerSecret, accessToken, accessSecret, "", false);
	}

	public TweetSpout(String key, Boolean openFire) {
		this("", "", "", "", key, false);
	}

	@SuppressWarnings("rawtypes")
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		TwitterStreamFactory fact = null;
		queue = new LinkedBlockingQueue<String>(1000);
		this.spoutOutputCollector = collector;

		try {
			fact = new TwitterStreamFactory(config.build());
		} catch (Exception ex) {
			System.out.println("twipLog: TwitterStreamFactory config failed.");
			return;
		}

		twitterStream = fact.getInstance();

		if (twitterStream == null) {
			System.out.println("twipLog: TwitterStreamFactory did not generate an instance for twitterStream");
			return;
		}
		
		twitterStream.addListener(new Tweeter());

		FilterQuery tweetFilterQuery = new FilterQuery();
		tweetFilterQuery.language(new String[] {"en"});
		if ("".equals(key))
			tweetFilterQuery.track(key.split(Pattern.quote(",")));				
		twitterStream.filter(tweetFilterQuery);
		

		System.out.println("twipLog: TweetSpout about to open " + (fire ? "FIRE" : "Sample") + " Hose ");
		if (!fire)
			twitterStream.sample();
		else
			twitterStream.firehose(1);
	}

	public void nextTuple() {
		String next = queue.poll();

		if (next == null) {
			Utils.sleep(DELAY_FOR_TWEET);
			return;
		}
		String[] tweetBits = next.split(Pattern.quote(DELIMITER), 8);
		String isRetweet = tweetBits[0];
		String favCount = tweetBits[1];
		String retweetCount = tweetBits[2];
		String name = tweetBits[3];
		String replyTo = tweetBits[4];
		String place = tweetBits[5];
		String country = tweetBits[6];
		String tweet = tweetBits[7];

		Values values = new Values(tweet, isRetweet, favCount, retweetCount, name, replyTo, place, country);
		spoutOutputCollector.emit(values);

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(
				new Fields("tweet", "isRetweet", "favCount", "retweetCount", "name", "replyTo", "place", "country"));
	}

	public void close() {
		twitterStream.shutdown();
	}

	/*
	 * Enqueues raw tweets as string in this format:
	 * Boolean_IS_RETWEET||Int_FAV_COUNT||Int_RETWEET_COUNT||
	 * SCREEN_NAME||IN_REPLY_TO||PLACE||COUNTRY||TWEET_TEXT
	 */
	private class Tweeter implements StatusListener {

		public void onStatus(Status status) {
			// Add the tweet into the queue buffer
			// queue.offer(status.getText());
			StringBuilder sb = new StringBuilder("");

			sb.append(status.isRetweet());
			sb.append(DELIMITER);
			sb.append(status.getFavoriteCount());
			sb.append(DELIMITER);
			sb.append(status.getRetweetCount());

			sb.append(DELIMITER);
			if (status.getUser() != null)
				sb.append(status.getUser().getScreenName());

			sb.append(DELIMITER);
			if (status.getInReplyToScreenName() != null)
				sb.append(status.getInReplyToScreenName());

			sb.append(DELIMITER);
			if (status.getPlace() != null)
				sb.append(status.getPlace().getName());

			sb.append(DELIMITER);
			if (status.getPlace() != null)
				sb.append(status.getPlace().getCountry());
			sb.append(DELIMITER);
			sb.append(status.getText());

			queue.offer(sb.toString());

		}

		public void onException(Exception arg0) {

		}

		public void onDeletionNotice(StatusDeletionNotice arg0) {

		}

		public void onScrubGeo(long arg0, long arg1) {

		}

		public void onStallWarning(StallWarning arg0) {

		}

		public void onTrackLimitationNotice(int arg0) {

		}
	}

}
