package com.arjose.twip;

import com.arjose.twip.bolts.AnchorBolt;
import com.arjose.twip.spouts.PanchayathSpout;
import com.esotericsoftware.minlog.Log;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;

public class BasicTopology {

	public static void main(String[] args) {
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("panchayath", new PanchayathSpout(), 1);
		builder.setBolt("anchor1", new AnchorBolt(), 2).shuffleGrouping("panchayath");

		Config conf = new Config();
		conf.setDebug(true);

		if (args != null && args.length > 0) {
			try {
				StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				Log.error("Seems to be running already");
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				Log.error("Screwed up topology");
				e.printStackTrace();
			}

		} else {
			conf.setMaxTaskParallelism(3);

			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("default-input", conf, builder.createTopology());

			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				Log.error("No input passed");
				e.printStackTrace();
			}
			cluster.shutdown();
		}
	}

}
