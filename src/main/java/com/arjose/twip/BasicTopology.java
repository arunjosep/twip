package com.arjose.twip;

import java.util.HashMap;

import com.arjose.twip.bolts.CompareBolt;
import com.arjose.twip.bolts.HashBolt;
import com.arjose.twip.spouts.TweetSpout;
import com.arjose.twip.util.CommandParser;
import com.arjose.twip.util.CommandlineArgs;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

public class BasicTopology {

	public static void main(String[] args) {

		TopologyBuilder builder = new TopologyBuilder();
		Config conf = new Config();

		/* *** Parse command line args *** */
		HashMap argsMap = CommandParser.parseList(args);

		String searchKeys = (String) ((argsMap.get(CommandlineArgs.D_HASH_TAG) != null)
				? argsMap.get(CommandlineArgs.D_HASH_TAG) : "");
		String candidates = (String) ((argsMap.get(CommandlineArgs.D_COMPARE_TAGS) != null)
				? argsMap.get(CommandlineArgs.D_COMPARE_TAGS) : "");
		Boolean openFire = (Boolean) ((argsMap.get(CommandlineArgs.S_OPEN_FIRE) != null)
				? argsMap.get(CommandlineArgs.S_OPEN_FIRE) : false);
		Boolean runProd = (Boolean) ((argsMap.get(CommandlineArgs.S_PROD_CLUSTER) != null)
				? argsMap.get(CommandlineArgs.S_PROD_CLUSTER) : false);
		Long ttl = (Long) ((argsMap.get(CommandlineArgs.D_TTL_SEC) != null) ? argsMap.get(CommandlineArgs.D_TTL_SEC)
				: 90L);

		/* *** Define topology *** */
		System.out.println("twipLog: BasicTopology starting up.");

		builder.setSpout("TweetDhaara", new TweetSpout(searchKeys, openFire));
		builder.setBolt("SortHashTags", new HashBolt(searchKeys), 3).shuffleGrouping("TweetDhaara");
		builder.setBolt("CountThroughKey", new CompareBolt(candidates), 4).shuffleGrouping("SortHashTags");
		builder.setBolt("Election", new CompareBolt(candidates), 6).shuffleGrouping("TweetDhaara");

		/* *** End of topology *** */

		// Run topology on local or production cluster

		if (!runProd) {
			// Run locally
			// conf.setDebug(true);
			conf.put(CommandlineArgs.D_HASH_TAG, searchKeys);
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
			conf.setNumWorkers(3);
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
