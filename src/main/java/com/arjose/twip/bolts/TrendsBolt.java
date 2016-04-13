package com.arjose.twip.bolts;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import com.arjose.twip.util.RedisUtils;
import com.lambdaworks.redis.api.sync.RedisCommands;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class TrendsBolt extends BaseRichBolt {
	OutputCollector collector;
	RedisCommands redis;
	Boolean connected = false;
	Boolean addHash = false;
	String url = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

	public TrendsBolt(Boolean hash) {
		if (hash != null)
			addHash = hash;
	}

	public TrendsBolt() {
	}

	@Override
	public void execute(Tuple tuple) {
		String tweet = tuple.getStringByField("tweet");
		Boolean fromSource = false;

		// Remove urls from the tweet
		tweet = tweet.replaceAll(url, "");

		fromSource = !tuple.contains("unique");
		for (String word : tweet.split("[^A-Za-z0-9#]")) {
			word = word.trim().toLowerCase();
			if (!ignoreWord(word)) {
				if (fromSource) {
					redis.zaddincr("trends:source", 1, word);
				} else if ("true".equalsIgnoreCase(tuple.getStringByField("unique"))) {
					redis.zaddincr("trends:candidate", 1, word);
				}
			}
		}
	}

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this.collector = collector;
		redis = RedisUtils.getRedis();
		if (redis != null) {
			connected = true;
			redis.incr("conn:TrendsBolt");
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer arg0) {

	}

	private boolean ignoreWord(String word) {
		HashSet<String> ignoreSet = new HashSet<String>(Arrays.asList("a", "about", "above", "above", "across", "after",
				"afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also", "although",
				"always", "am", "among", "amongst", "amoungst", "amount", "an", "and", "another", "any", "anyhow",
				"anyone", "anything", "anyway", "anywhere", "are", "around", "as", "at", "back", "be", "became",
				"because", "become", "becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below",
				"beside", "besides", "between", "beyond", "bill", "both", "bottom", "but", "by", "call", "can",
				"cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done",
				"down", "due", "during", "each", "eg", "eight", "either", "eleven", "else", "elsewhere", "empty",
				"enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few",
				"fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty",
				"found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have",
				"having", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself",
				"him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest",
				"into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd",
				"made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly",
				"move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next",
				"nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off",
				"often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours",
				"ourselves", "out", "over", "own", "part", "per", "perhaps", "please", "put", "rather", "re", "rt",
				"same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show",
				"side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime",
				"sometimes", "somewhere", "still", "such", "system", "t", "take", "ten", "than", "that", "the", "their",
				"them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein",
				"thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through",
				"throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty",
				"two", "un", "under", "until", "up", "upon", "us", "very", "via", "vs", "was", "we", "well", "were",
				"what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby",
				"wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole",
				"whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours",
				"yourself", "yourselves"));

		return (word.isEmpty() || word.length() == 1 || ignoreSet.contains(word));
	}
}
