package com.arjose.twip.bolts;

import java.util.Map;
import java.util.regex.Pattern;

import com.arjose.twip.util.KeyUtils;
import com.arjose.twip.util.RedisUtils;
import com.lambdaworks.redis.api.sync.RedisCommands;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

/**
 * Compares count of words in given input along with the source key that found
 * the tweet. Example: If a tweet was found by the key word "election" and the
 * candidates are "win,lose" counts are saved in redis with keys
 * "compare:election:win" and "compare:election:lose"
 * 
 * @author arun
 *
 */
public class CompareBolt implements IRichBolt {

	OutputCollector collector;
	static int bernie = 0;
	static int trump = 0;
	String candidates = "";
	Boolean addHash = false;
	RedisCommands redis;
	Boolean connected = false;

	public CompareBolt() {
	}

	public CompareBolt(String candidates, Boolean hash) {
		if (hash != null)
			addHash = hash;

		if (candidates != null) {
			if (addHash)
				this.candidates = KeyUtils.unHash(candidates);
			else
				this.candidates = candidates;
		}
	}

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;

		redis = RedisUtils.getRedis();
		if (redis != null) {
			connected = true;
			redis.incr("conn:CompareBolt");
		}
	}

	public void execute(Tuple tuple) {

		String tweet = tuple.getStringByField("tweet");
		String sourceKey;
		Values values = null;
		
		if (tuple.contains("foundKey"))
			sourceKey = tuple.getStringByField("foundKey");
		else
			sourceKey = "";

		if (!candidates.isEmpty()) {
			if (sourceKey != null) {
				// sourceKey is available only if a HashBolt sorted the tweets
				// based on how it found the tweet
				if (connected) {
					redis.incr("compare:in_count");
				}
				boolean found = false;
				for (String word : candidates.split(Pattern.quote(","))) {
					word = word.trim();
					if (tweet.toUpperCase().contains(word.toUpperCase())
							|| (addHash && tweet.toUpperCase().contains("#" + word.toUpperCase()))) {
						found = true;
						if (connected) {
							redis.incr("compare:" + sourceKey.toLowerCase() + ":" + word.toLowerCase());
							redis.incr("compare:total_count");
						}
						values = new Values(tweet, word, "false");
						collector.emit(values);
					}
				}
				if (connected && found) {
					redis.incr("compare:" + sourceKey.toLowerCase());
					values = new Values(tweet, "", "true");
				}
			} else {
				// If sourceKey is null, input is coming directly from source.
				// So count based on candidates only.
				if (connected) {
					redis.incr("vote:in_count");
				}
				boolean found = false;
				for (String word : candidates.split(Pattern.quote(","))) {
					word = word.trim();
					if (tweet.toUpperCase().contains(word.toUpperCase())
							|| (addHash && tweet.toUpperCase().contains("#" + word.toUpperCase()))) {
						found = true;
						if (connected) {
							redis.incr("vote:" + word.toLowerCase());
							redis.incr("vote:total_count");

						}
						values = new Values(tweet, word, "false");
						collector.emit(values);
					}
				}
				if (connected && found) {
					redis.incr("vote:key_found");
					values = new Values(tweet, "", "true");
					collector.emit(values);
				}
			}
		}

		collector.ack(tuple);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet", "foundKey", "unique"));

	}

	@Override
	public void cleanup() {
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
