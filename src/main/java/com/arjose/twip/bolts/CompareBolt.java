package com.arjose.twip.bolts;

import java.util.Map;
import java.util.regex.Pattern;

import com.arjose.twip.util.RedisUtils;
import com.lambdaworks.redis.api.sync.RedisCommands;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

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
		}
	}

	public void execute(Tuple tuple) {

		String tweet = tuple.getStringByField("tweet");
		String sourceKey = tuple.getStringByField("foundKey");

		if (candidates != null && !candidates.isEmpty()) {
			for (String word : candidates.split(Pattern.quote(","))) {
				if (tweet.toUpperCase().contains(word.toUpperCase())) {
					if (connected) {
						redis.incr("compare:" + sourceKey.toLowerCase() + ":" + word.toLowerCase());
						redis.incr("compare:total_count");
					}
				}
			}
		}

		collector.ack(tuple);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

	@Override
	public void cleanup() {
	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

}
