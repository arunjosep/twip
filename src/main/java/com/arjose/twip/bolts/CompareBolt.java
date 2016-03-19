package com.arjose.twip.bolts;

import java.util.Map;
import java.util.regex.Pattern;

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
	RedisCommands redis;
	Boolean connected = false;

	public CompareBolt() {
	}

	public CompareBolt(String candidates) {
		if (candidates != null)
			this.candidates = candidates;
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

		try {
			sourceKey = tuple.getStringByField("foundKey");
		} catch (IllegalArgumentException ex) {
			sourceKey = null;
		}

		if (candidates != null && !candidates.isEmpty()) {
			if (sourceKey != null) {
				// sourceKey is available only if a HashBolt sorted the tweets
				// based on how it found the tweet
				for (String word : candidates.split(Pattern.quote(","))) {
					if (tweet.toUpperCase().contains(word.toUpperCase())) {
						if (connected) {
							redis.incr("compare:" + sourceKey.toLowerCase() + ":" + word.toLowerCase());
							redis.incr("compare:total_count");
						}
						values = new Values(tweet, word);
						collector.emit(values);
					}
				}
			} else {
				// If sourceKey is null, input is coming directly from source.
				// So count based on candidates only.
				for (String word : candidates.split(Pattern.quote(","))) {
					if (tweet.toUpperCase().contains(word.toUpperCase())) {
						if (connected) {
							Long vote = redis.incr("voteCount:" + word.toLowerCase());
							Long total = redis.incr("vote:total_count");
							double perc = (vote * 100.0) / total;
							redis.set("votePercent:" + word.toLowerCase(), String.format("%.2f", perc));
						}
						values = new Values(tweet, word);
						collector.emit(values);
					}
				}
			}
		}

		collector.ack(tuple);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet", "foundKey"));

	}

	@Override
	public void cleanup() {
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
