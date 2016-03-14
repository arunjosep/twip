package com.arjose.twip.bolts;

import java.util.Map;
import java.util.regex.Pattern;

import com.arjose.twip.util.RedisUtils;
import com.lambdaworks.redis.api.sync.RedisCommands;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * Counts number of tweets that contain given keys. Stores result in redis as
 * "hash:key" where "key" is the word that was searched for.
 * 
 * @author arun
 *
 */
@SuppressWarnings("deprecation")
public class HashBolt extends BaseRichBolt {
	OutputCollector collector;
	String searchKeys = "";
	RedisCommands redis;
	Boolean connected = false;

	public HashBolt(String searchKeys) {
		if (searchKeys != null)
			this.searchKeys = searchKeys;
	}

	public HashBolt() {
	}

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		redis = RedisUtils.getRedis();
		if (redis != null) {
			connected = true;
		}
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
		Values values = null;

		// System.out.println("twipLog: Looking for keys : " + searchKeys + " in
		// tweet [" + tweet + "] |" + isRetweet + "|"
		// + favCount + "|" + retweetCount + "|" + name + "|" + replyTo + "|" +
		// place + "|" + country);

		if (searchKeys != null && !searchKeys.isEmpty()) {
			for (String key : searchKeys.split(Pattern.quote(","))) {
				if (tweet.toUpperCase().contains(key.toUpperCase())) {
					if (connected){
						redis.incr("hash:" + key.toLowerCase());
						redis.incr("hash:total_count");
						}
					values = new Values(tweet, isRetweet, favCount, retweetCount, name, replyTo, place, country, key);
					collector.emit(values);
				}
			}
		}
		collector.ack(tuple);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet", "isRetweet", "favCount", "retweetCount", "name", "replyTo", "place",
				"country", "foundKey"));
	}

}
