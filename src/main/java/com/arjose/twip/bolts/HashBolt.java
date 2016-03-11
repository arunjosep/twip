package com.arjose.twip.bolts;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisConnection;
import com.lambdaworks.redis.RedisURI;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

@SuppressWarnings("deprecation")
public class HashBolt extends BaseRichBolt {
	OutputCollector collector;
	StringBuilder result;
	String key = null;
	transient RedisConnection<String, String> redis;

	public HashBolt(String key) {
		this.key = key;
	}

	public HashBolt() {
	}

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		result = new StringBuilder();
		// RedisClient client = RedisClient.create(new RedisURI("localhost",
		// 6379, 1, TimeUnit.DAYS));
		// redis = (RedisConnection<String, String>) client.connect();
	}

	public void execute(Tuple tuple) {

		String tweet = tuple.getStringByField("tweet");
		String isRetweet = tuple.getStringByField("isRetweet");
		String favCount = tuple.getStringByField("favCount");
		String retweetCount = tuple.getStringByField("retweetCount");
		String name = tuple.getStringByField("name");
		String replyTo = tuple.getStringByField("replyTo");
		String place = tuple.getStringByField("place");
		String country = tuple.getStringByField("country");

		// System.out.println("twipLog: Looking for key " + key + " in tweet [" + tweet + "] |" + isRetweet + "|" + favCount + "|" + retweetCount + "|" + name + "|" + replyTo + "|" + place + "|" + country);
		if (key != null && !"".equals(key)) {
			for (String word : key.split(Pattern.quote(","))) {
				if (tweet.contains(word)) {
					System.out.println("twipLog: found word [" + word + "] in tweet [" + tweet + "]");
				}
			}
		}

		Values values = new Values(tweet, isRetweet, favCount, retweetCount, name, replyTo, place, country);
		collector.emit(values);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(
				new Fields("tweet", "isRetweet", "favCount", "retweetCount", "name", "replyTo", "place", "country"));
	}

}
