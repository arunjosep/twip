package com.arjose.twip.bolts;

import java.util.Map;
import java.util.Properties;

import com.arjose.twip.util.RedisUtils;
import com.lambdaworks.redis.api.sync.RedisCommands;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations.SentimentAnnotatedTree;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.TypesafeMap;

public class SentimentBolt extends BaseRichBolt {
	OutputCollector collector;
	StanfordCoreNLP pipeline;
	RedisCommands redis;
	Boolean connected = false;

	public SentimentBolt() {
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,parse,sentiment");
		pipeline = new StanfordCoreNLP(props);

		redis = RedisUtils.getRedis();
		if (redis != null) {
			connected = true;
			redis.incr("conn:SentimentBolt");
		}
	}

	@Override
	public void execute(Tuple tuple) {
		String tweet = tuple.getStringByField("tweet");
		String sourceKey;
		try {
			sourceKey = tuple.getStringByField("foundKey");
		} catch (IllegalArgumentException ex) {
			sourceKey = null;
		}
		tweet = tweet.replaceAll("[^A-Za-z0-9 ]", "");

		int sentiment = findSentiment(tweet);
		sentiment = (sentiment >= 0 && sentiment <= 4) ? sentiment : 2;
		// System.out.println("twipLog: Sent: " + sentiment + " : " + tweet);
		if (sourceKey == null) {
			// If sourceKey is null, input is coming directly from source.
			// So count only total sentiments
			if (connected) {
				redis.incr("sentiment:total_count");
				redis.incr("sentiment:" + sentiment);
			}
		} else {
			// If sourceKey is present, source is sorted.
			// Save count based on candidate key.
			if (connected) {
				redis.incr("sentiment:" + sourceKey.toLowerCase() + ":" + sentiment);
				redis.incr("sentiment:" + sourceKey.toLowerCase() + ":" + "total_count");
			}
		}
		collector.ack(tuple);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

	private int findSentiment(String tweet) {
		// System.out.println("twipLog: Sent: " +tweet);
		int mainSentiment = 0;
		if (tweet != null && !tweet.isEmpty()) {
			int longest = 0;
			edu.stanford.nlp.pipeline.Annotation annotation = pipeline.process(tweet);
			for (CoreMap sentence : ((TypesafeMap) annotation).get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = sentence.get(SentimentAnnotatedTree.class);
				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
				String partText = sentence.toString();
				if (partText.length() > longest) {
					mainSentiment = sentiment;
					longest = partText.length();
				}

			}
		}
		return mainSentiment;
	}

}
