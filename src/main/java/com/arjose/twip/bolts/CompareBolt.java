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

		if (candidates != null && !candidates.isEmpty()) {
			for (String word : candidates.split(Pattern.quote(","))) {
				System.out.println("twipLog: Looking for word:" + word);
				if (tweet.toUpperCase().contains(word.toUpperCase())) {
					if (connected)
						redis.incr("compare:" + word.toLowerCase());
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
