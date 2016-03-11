package com.arjose.twip.bolts;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class ElectionBolt extends BaseRichBolt {

	public ElectionBolt() {
		// TODO Auto-generated constructor stub
	}

	public ElectionBolt(String key) {
		// TODO Auto-generated constructor stub
	}

	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		// TODO Auto-generated method stub

	}

	public void execute(Tuple input) {
		// TODO Auto-generated method stub

	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub

	}

}
