package com.arjose.twip.spouts;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

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
	static final boolean USE_FIRE = false;
	static final String DELIMITER = "~|";

	transient private ConfigurationBuilder config = null;
	LinkedBlockingQueue<String> queue = null;
	SpoutOutputCollector spoutOutputCollector;
	TwitterStream twitterStream;
	private boolean fire = USE_FIRE;

	public TweetSpout(String customerKey, String customerSecret, String accessToken, String accessSecret,
			boolean fire) {
		this.fire = fire;
		if (customerKey != null && customerSecret != null && accessToken != null && accessSecret != null) {
			config = new ConfigurationBuilder().setOAuthConsumerKey(customerKey).setOAuthConsumerSecret(customerSecret)
					.setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessSecret);
		}
	}

	public TweetSpout(String customerKey, String customerSecret, String accessToken, String accessSecret) {
		this(customerKey, customerSecret, accessToken, accessSecret, false);
	}

	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		queue = new LinkedBlockingQueue<String>(1000);
		this.spoutOutputCollector = collector;
		
		TwitterStreamFactory fact = new TwitterStreamFactory(config.build());
		
		twitterStream = fact.getInstance();
		
		if (twitterStream == null){
			System.out.println("twipLog: TwitterStreamFactory did not generate an instance for twitterStream");
			return;
		}
			
		FilterQuery tweetFilterQuery = new FilterQuery();
		tweetFilterQuery.language(new String[] { "en" });
		twitterStream.addListener(new Tweeter());
		twitterStream.filter(tweetFilterQuery);

		if (!fire)
			twitterStream.sample();
		else
			twitterStream.firehose(1);
	}

	@Override
	public void nextTuple() {
		String next = queue.poll();

		if (next == null) {
			Utils.sleep(DELAY_FOR_TWEET);
			return;
		}
		String tweet = next.split(DELIMITER)[0];
		String isRetweet = next.split(DELIMITER)[1];
		String favCount = next.split(DELIMITER)[2];
		String retweetCount =  next.split(DELIMITER)[3];
		String name = next.split(DELIMITER)[4];
		String replyTo = next.split(DELIMITER)[5];
		String place = next.split(DELIMITER)[6];
		String country = next.split(DELIMITER)[7];
		
		Values values = new Values(tweet, isRetweet, favCount, retweetCount, name, replyTo, place, country);
		spoutOutputCollector.emit(values);

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet", "isRetweet", "favCount", "retweetCount", "name", "replyTo", "place", "country"));
	}

	public void close() {
		twitterStream.shutdown();
	}

	/*
	 * Enqueues tweets as string in this format:
	 * TWEET_TEXT||Boolean_IS_RETWEET||Int_FAV_COUNT||Int_RETWEET_COUNT||
	 * SCREEN_NAME||IN_REPLY_TO||PLACE||COUNTRY
	 */
	private class Tweeter implements StatusListener {

		@Override
		public void onStatus(Status status) {
			// Add the tweet into the queue buffer
			// queue.offer(status.getText());
			StringBuilder sb = new StringBuilder();
			sb.append(status.getText());

			sb.append(DELIMITER);
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

			queue.offer(sb.toString());

		}

		@Override
		public void onException(Exception arg0) {

		}

		@Override
		public void onDeletionNotice(StatusDeletionNotice arg0) {

		}

		@Override
		public void onScrubGeo(long arg0, long arg1) {

		}

		@Override
		public void onStallWarning(StallWarning arg0) {

		}

		@Override
		public void onTrackLimitationNotice(int arg0) {

		}
	}

}
