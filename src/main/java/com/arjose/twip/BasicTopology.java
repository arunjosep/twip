package com.arjose.twip;

import java.util.HashMap;
import java.util.regex.Pattern;

import com.arjose.twip.bolts.CompareBolt;
import com.arjose.twip.bolts.HashBolt;
import com.arjose.twip.bolts.OriginBolt;
import com.arjose.twip.bolts.SentimentBolt;
import com.arjose.twip.bolts.TrendsBolt;
import com.arjose.twip.spouts.TweetSpout;
import com.arjose.twip.util.CommandParser;
import com.arjose.twip.util.CommandlineArgs;
import com.arjose.twip.util.KeyUtils;
import com.arjose.twip.util.RedisUtils;
import com.lambdaworks.redis.api.sync.RedisCommands;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

public class BasicTopology {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {

		TopologyBuilder builder = new TopologyBuilder();
		Config conf = new Config();
		RedisCommands redis;
		Boolean connected = false;
		String ProxyHost = "";
		Integer ProxyPort = 0;

		redis = RedisUtils.getRedis();
		if (redis != null) {
			connected = true;
		}

		/* *** Parse command line args *** */
		HashMap argsMap = CommandParser.parseList(args);

		String sourceKeys = (String) ((argsMap.get(CommandlineArgs.D_HASH_TAGS) != null)
				? argsMap.get(CommandlineArgs.D_HASH_TAGS) : "");
		String candidates = (String) ((argsMap.get(CommandlineArgs.D_COMPARE_TAGS) != null)
				? argsMap.get(CommandlineArgs.D_COMPARE_TAGS) : "");
		String proxy = (String) ((argsMap.get(CommandlineArgs.D_PROXY) != null) ? argsMap.get(CommandlineArgs.D_PROXY)
				: "");
		Boolean openFire = (argsMap.get(CommandlineArgs.S_OPEN_FIRE) != null);
		Boolean runTest = (argsMap.get(CommandlineArgs.S_TEST_CLUSTER) != null);
		Boolean runSent = (argsMap.get(CommandlineArgs.S_RUN_SENT) != null);
		Boolean noRT = (argsMap.get(CommandlineArgs.S_NO_RT) != null);
		Boolean addHash = (argsMap.get(CommandlineArgs.S_ADD_HASH) != null);
		Long ttl = (Long) ((argsMap.get(CommandlineArgs.D_TTL_SEC) != null) ? argsMap.get(CommandlineArgs.D_TTL_SEC)
				: 90L);

		try {
			ProxyHost = (String) proxy.split(Pattern.quote(":"))[0];
		} catch (Exception ex1) {
			ProxyHost = "";
		}
		try {
			ProxyPort = Integer.parseInt((String) proxy.split(Pattern.quote(":"))[1]);
		} catch (Exception ex2) {
			ProxyPort = 0;
		}

		if (connected) {
			redis.flushall();
			String[] skeys = addHash ? (KeyUtils.unHash(sourceKeys).split(Pattern.quote(",")))
					: sourceKeys.split(Pattern.quote(","));
			for (String key : skeys) {
				redis.sadd("keys:hash", key);
			}
			skeys = addHash ? (KeyUtils.unHash(candidates).split(Pattern.quote(",")))
					: candidates.split(Pattern.quote(","));
			for (String key : skeys) {
				redis.sadd("keys:compare", key);
			}
		}

		/* *** Define topology *** */
		System.out.println("twipLog: BasicTopology starting up.");

		builder.setSpout("TweetDhaara", new TweetSpout(sourceKeys, openFire, addHash, ProxyHost, ProxyPort));

		builder.setBolt("RemoveRTs", new OriginBolt(), 3).shuffleGrouping("TweetDhaara");

		if (noRT) {
			builder.setBolt("SortHashTags", new HashBolt(sourceKeys, addHash), 3).shuffleGrouping("RemoveRTs");
			builder.setBolt("Election", new CompareBolt(candidates, addHash), 3).shuffleGrouping("RemoveRTs");
			builder.setBolt("TrendsWithFilters", new TrendsBolt(addHash), 3).shuffleGrouping("RemoveRTs");

			if (connected) {
				redis.set("config:noRT", "true");
			}
		} else {
			builder.setBolt("SortHashTags", new HashBolt(sourceKeys, addHash), 3).shuffleGrouping("TweetDhaara");
			builder.setBolt("Election", new CompareBolt(candidates, addHash), 3).shuffleGrouping("TweetDhaara");
			builder.setBolt("TrendsWithFilters", new TrendsBolt(addHash), 3).shuffleGrouping("TweetDhaara");

			if (connected) {
				redis.set("config:noRT", "false");
			}
		}
		builder.setBolt("CountThroughKey", new CompareBolt(candidates, addHash), 3).shuffleGrouping("SortHashTags");
		builder.setBolt("TrendsWithCandidates", new TrendsBolt(addHash), 3).shuffleGrouping("Election");

		if (runSent) {
			builder.setBolt("SentimentsThroughKeys", new SentimentBolt(), 10).shuffleGrouping("Election");
			if (connected) {
				redis.set("config:sentiment", "true");
			}
		} else {
			if (connected) {
				redis.set("config:sentiment", "false");
			}
		}

		/* *** End of topology *** */

		// Run topology on local or production cluster

		if (runTest) {
			// Run locally
			// conf.setDebug(true);
			conf.put(CommandlineArgs.D_HASH_TAGS, sourceKeys);
			conf.put(CommandlineArgs.D_COMPARE_TAGS, candidates);

			conf.setMaxTaskParallelism(50);
			LocalCluster cluster = new LocalCluster();
			System.out.println("twipLog: Submitting BasicTopology on local cluster");
			try {

				cluster.submitTopology("basic-topology", conf, builder.createTopology());
			} catch (Exception e) {
				System.out.println("twipLog: Topology failed");
				e.printStackTrace();
			}
			try {
				Thread.sleep(ttl * 1000); // ttl is in seconds
			} catch (InterruptedException e) {
				System.out.println("twipLog: Topology was killed");
				e.printStackTrace();
			}
			System.out.println("twipLog: Topology shutting down. It's been a great run folks.");

			cluster.killTopology("basic-topology");
			cluster.shutdown();

		} else {
			// Run on production
			conf.setNumWorkers(4);
			conf.setMaxSpoutPending(4000);
			System.out.println("twipLog: Submitting BasicTopology on production cluster");
			try {
				StormSubmitter.submitTopology("basic-topology", conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				System.out.println("twipLog: Topology is already running. Could not submit");
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				System.out.println("twipLog: Topology is screwed up. Could not submit");
				e.printStackTrace();
			} catch (Exception e) {
				System.out.println("twipLog: Topology is seriously **** ");
				e.printStackTrace();
			}
		}
	}

}
