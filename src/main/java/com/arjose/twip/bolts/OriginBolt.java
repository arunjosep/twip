package com.arjose.twip.bolts;

import java.util.Map;

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
 * Filters out retweets from original content
 * 
 * @author arun
 *
 */
public class OriginBolt extends BaseRichBolt {
	OutputCollector collector;
	RedisCommands redis;
	Boolean connected = false;

	public OriginBolt() {

	}

	public void execute(Tuple tuple) {
		Values values = null;
		String tweet = tuple.getStringByField("tweet");
		String isRetweet = tuple.getStringByField("isRetweet");
		Boolean isRT = ("true".equalsIgnoreCase(isRetweet)) ? true : false;

		if (connected) {
			redis.incr("origin:in_count");
		}

		if (!isRT) {
			if (connected) {
				redis.incr("origin:nonRT");
			}
			values = new Values(tweet);
			collector.emit(values);
		} else {
			if (connected) {
				redis.incr("origin:RT");
			}
		}
		collector.ack(tuple);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet"));
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		redis = RedisUtils.getRedis();
		if (redis != null) {
			connected = true;
			redis.incr("conn:OriginBolt");
		}
	}

}
