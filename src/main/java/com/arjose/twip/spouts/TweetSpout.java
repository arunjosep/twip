package com.arjose.twip.spouts;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;

import com.arjose.twip.util.RedisUtils;
import com.lambdaworks.redis.api.sync.RedisCommands;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TweetSpout extends BaseRichSpout {

	static final long DELAY_FOR_TWEET = 10;
	static final String DELIMITER = "~";

	private static ConfigurationBuilder config = null;
	LinkedBlockingQueue<String> queue = null;
	SpoutOutputCollector spoutOutputCollector;
	TwitterStream twitterStream;
	private boolean fire = false;
	private String keyString = "";
	RedisCommands redis;
	Boolean connected = false;
	String customerKey, customerSecret, accessToken, accessSecret;
	String ProxyHost;
	Integer ProxyPort;

	public TweetSpout(String customerKey, String customerSecret, String accessToken, String accessSecret,
			String keyString, boolean fire) {
		this.customerKey = customerKey;
		this.customerSecret = customerSecret;
		this.accessToken = accessToken;
		this.accessSecret = accessSecret;
		this.fire = fire;
		this.keyString = keyString;
	}

	public TweetSpout(String searchKeys, Boolean openFire, String ProxyHost, Integer ProxyPort) {
		this.fire = openFire;
		this.keyString = searchKeys;

		this.ProxyHost = ProxyHost;
		this.ProxyPort = ProxyPort;

		this.customerKey = "";
		this.customerSecret = "";
		this.accessToken = "";
		this.accessSecret = "";
	}

	@SuppressWarnings("rawtypes")
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		TwitterStreamFactory fact = null;
		queue = new LinkedBlockingQueue<String>(10000);
		this.spoutOutputCollector = collector;

		System.out.println("twipLog: TweetSpout using keyString : " + keyString);

		// ----------------Add keys and secrets here------------------------//


		config = new ConfigurationBuilder().setOAuthConsumerKey(customerKey).setOAuthConsumerSecret(customerSecret)
				.setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessSecret);

		redis = RedisUtils.getRedis();
		if (redis != null) {
			connected = true;
			redis.incr("conn:TweetSpout");
		}
		if (ProxyHost != null && !ProxyHost.isEmpty() && ProxyPort != 0) {
			config.setHttpProxyHost(ProxyHost);
			config.setHttpProxyPort(ProxyPort);
			System.out.println("twipLog: Proxy set to [" + ProxyHost + ":" + ProxyPort + "]");
		}
		String[] keys = keyString.split(Pattern.quote(","));

		try

		{
			fact = new TwitterStreamFactory(config.build());
		} catch (

		Exception ex)

		{
			System.out.println("twipLog: TwitterStreamFactory config failed.");
			ex.printStackTrace();
			return;
		}

		twitterStream = fact.getInstance();

		if (twitterStream == null)

		{
			System.out.println("twipLog: TwitterStreamFactory did not generate an instance for twitterStream");
			return;
		}

		twitterStream.addListener(new Tweeter());

		FilterQuery tweetFilterQuery = new FilterQuery();
		tweetFilterQuery.language(new String[] { "en" });
		if (keys.length != 0)

		{
			// System.out.println("twipLog: Tracking: " + keyString);
			tweetFilterQuery.track(keys);

		}

		System.out.println("twipLog: TweetSpout about to open " + (fire ? "FIRE" : "Filtered") + " Hose ");
		if (!fire)

		{
			twitterStream.filter(tweetFilterQuery);
			// Using sample() clears the filters and opens twitter's
			// SampleStream.
			// twitterStream.sample();
		} else

		{
			twitterStream.firehose(1);
		}

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
		if (connected)
			redis.incr("dhaara:total_count");

		spoutOutputCollector.emit(values);

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(
				new Fields("tweet", "isRetweet", "favCount", "retweetCount", "name", "replyTo", "place", "country"));
	}

	public void close() {
		twitterStream.shutdown();
		System.out.println("twipLog: twitterStream closed.");
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
