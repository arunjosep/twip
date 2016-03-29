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
public class HashBolt extends BaseRichBolt {
	OutputCollector collector;
	String sourceKeys = "";
	RedisCommands redis;
	Boolean connected = false;

	public HashBolt(String sourceKeys) {
		if (sourceKeys != null)
			this.sourceKeys = sourceKeys;
	}

	public HashBolt() {
	}

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		redis = RedisUtils.getRedis();
		if (redis != null) {
			connected = true;
			redis.incr("conn:HashBolt");
		}
	}

	public void execute(Tuple tuple) {
		String tweet = tuple.getStringByField("tweet");
		Values values = null;
		boolean found = false;
		if (sourceKeys != null && !sourceKeys.isEmpty()) {
			if (connected) {
				redis.incr("hash:in_count");
			}
			for (String key : sourceKeys.split(Pattern.quote(","))) {
				if (tweet.toUpperCase().contains(key.toUpperCase())) {
					found = true;
					if (connected) {
						redis.incr("hash:" + key.toLowerCase());
						redis.incr("hash:out_count");
					}
					values = new Values(tweet, key);
					collector.emit(values);
				}
			}
			if (connected && found) {
				redis.incr("hash:key_found");
			}
		}
		collector.ack(tuple);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet", "foundKey"));
	}

}
