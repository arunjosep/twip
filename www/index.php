<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;charset=ISO-8859-1">
<meta http-equiv='cache-control' content='no-cache'>
<meta http-equiv='expires' content='0'>
<meta http-equiv='pragma' content='no-cache'>
<title>TwIP &copy;</title>
<link href='https://fonts.googleapis.com/css?family=Montserrat:400,700' rel='stylesheet' type='text/css'>
<style>

body{
    width: 80%;
    min-width:960px;
    background-color:#16a085;
    position: absolute;
    top:0;
    bottom: 0;
    left: 0;
    right: 0;
    margin-left: auto;
    margin-right: auto;
    font-family: 'Montserrat', sans-serif;
}

sup{
    font-size: 60%;
}

.header {
    background-color: #ecf0f1;
    padding:5px;  
    margin-top: 15px;
    text-align: center;
    border-radius: 4px; 
    box-shadow: 3px 3px 4px rgba(0, 0, 0, 0.4);
    font-weight: bold;
    font-size: 140%;
    color: rgba(22, 160, 133, 0.75);
    text-shadow: 2px 2px 2px #ecf0f1, 0 0 0 #000, 2px 2px 2px #ecf0f1;
}
 
.sub{
    background-color:#ecf0f1;
    border-color: #0c0c41;
    border-width: 1px;
    overflow:hidden;
    margin-top:12px;
    padding-top: 10px;
    padding-bottom:10px;
    padding-left:15px;
    padding-right:5px;
    border-radius: 4px; 
    box-shadow: 3px 3px 4px rgba(0, 0, 0, 0.4);
}

.subHead{
    padding: 2px;
    padding-bottom: 5px;
    padding-left: 0px;
    font-weight: bold;
    color:#16a085;
    color: rgba(22, 160, 133, 0.75);
    text-shadow: 1px 2px 2px #ecf0f1, 0 0 0 #000, 2px 1px 2px #ecf0f1;
    font-size:120%;
}

.subContent{
    background-color:#ecf0f1;
    overflow:hidden;
    margin-top:3px; 
    border-radius: 4px;
    width:100%;
    display: table;
}

.subGraphWrapper{
    padding:5px;
    color:#999999;
    text-align: center;
    overflow: hidden;
    width:36%;
    display: table-cell;
    vertical-align: middle;
}

.subGraphInner{
    vertical-align: middle;
    border-radius: 4px; 
    background-color:#FFFFFF;
    box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.4) ;
}

.sentimentCandGraph{
    vertical-align: middle;
    border-radius: 4px; 
    background-color:#FFFFFF;
    box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.4) ;
    height: 150px; 
    width: 150px; 
    margin:5px; 
    float:left;
}

.subData{
    padding:10px;
    padding-left:15px;
    overflow: hidden;
    display: table-cell;
    
}

#sentiments{
    color:#999999;
    text-align: center;
    vertical-align: top;
    padding:5px;
}

.total{
    color:#16a085;
    font-weight: normal;
    float: right;
    padding-right: 12px;
}

.keyLeft{
    color: #27ae60;
    float: left;
    overflow: hidden;
}

.keyRight{
    font-style: normal;
    float: right;
    overflow: hidden;
    padding-right: 7px;
}

.perc{
    color: #3498db;
    width: 70px;
    float: right;
    text-align: right;
}

.count{
    font-style: italic;
    color: #27ae60;
}

#creditAnchor{
    margin:10px;
    padding:10px;
    text-align: center;
    font-size:80%;
    color: #ecf0f1;
    font-weight:thin;
    display: inline-block;
    background-color:#1abc9c;
    border-radius:4px;
}

</style>

<script src="jquery-latest.js"></script>
<script>
    var quickTimeout=1000;
    var slowTimeout=4200;
    var showall=true;

    $(document).ready(function(){

	$(".subContent").hide(250);
	$("#subSearchContent").toggle(250);
        $("#fullcredits").hide();

        setInterval(function quickreload() {
            $("#hashKeys").load("hashKeys.php");
            $("#compareKeys").load("compareKeys.php");
            $("#filterQuality").load("filterQuality.php");
            $("#hashCount").load("hashTotal.php");
            $("#compareCount").load("compareTotal.php");
            $("#comparePerc").load("comparePerc.php");
        }, quickTimeout);

        setInterval(function slowreload() {
            $("#hashPie").load("hashPie.php");
            $("#comparePie").load("comparePie.php");
            $("#qualityPie").load("qualityPie.php");
            $("#sentimentPie").load("sentimentPie.php");
            $("#sentiments").load("sentimentCandidatesPie.php");
        }, slowTimeout);

        $("#header").click(function(){
            showall=!showall;
            if(showall){
                $(".subContent").show(250);
            } else {
                $(".subContent").hide(250);
            }
        });

        $("#subHash").click(function(){
            $("#subHashContent").toggle(250);
        });

        $("#subSearch").click(function(){
            $("#subSearchContent").toggle(250);
        });

        $("#subQuality").click(function(){
            $("#subQualityContent").toggle(250);
        });

        $("#subSent").click(function(){
            $("#subSentContent").toggle(250);
        });

    });


	function hideCredits(){ $("#fullcredits").hide(250); }

	function showCredits(){ $("#fullcredits").show(250); }

</script>

</head>

<body>

<div class="header" id="header" title="Click to show/hide all regions"><h1>Twitter Intelligence Processing<sup>&copy;</sup></h1></div>

<?php function showGraphText() {echo"[ One awesome graph is being drawn just for you ]";}?>
<?php function showGraphTitle() {echo"title=\"This graph is refreshed less frequently than text data to save your bandwidth. For real-time accuracy, refer the frequently updated text data\"";}?>

<div class="sub">
 <div class="subHead" id="subHash" title="Keys that tweets are filtered with">Sources<span class="total" id="hashCount" title="Tweets that contain at least one filter key">0</span></div>
 <div class="subContent" id="subHashContent">
  <div class="subGraphWrapper" id="hashPie" <?php showGraphTitle(); ?>><?php showGraphText(); ?></div>
  <div id="hashKeys" class="subData" title="Key being used as filter 
Number of tweets found with this key 
Percent of tweets with this key in all filtered tweets (Sum of these percentages may be higher than 100 if more than one filter key is found in each tweet)"></div>
  </div>
</div>

<div class="sub">
 <div class="subHead" id="subSearch" title="Keys that are being searched for, in all the filtered tweets">Search Keys<span class="total" id="compareCount" title="Filtered tweets that contain at least one search key">0</span></div>
 <div class="subContent" id="subSearchContent">
  <div class="subGraphWrapper" id="comparePie" <?php showGraphTitle(); ?>><?php showGraphText(); ?></div>
  <div id="compareKeys" class="subData" title="Key being searched for 
Occurrences of this key in all filtered tweets
Occurrence of this key per occurrence of all keys counting once per tweet"></div>
 </div>
</div>

<div class="sub">
 <div class="subHead" id="subSent" title="Average sentiment of tweets that contain search keys">Sentiment</div>
 <div class="subContent" id="subSentContent" title="Sentiment analysis is restricted to processing grammatically correct english language sentences and may not perfectly reflect the actual context and intent of tweets. Use this only as an indicator.">
  <div class="subGraphWrapper" id="sentimentPie" <?php showGraphTitle(); ?>></div>
  <div id="sentiments" class="subData"></div>
 </div>
</div>

<div class="sub">
 <div class="subHead" id="subQuality" title="How effective the source filters are in finding search keys">Source Performance<span class="total" id="comparePerc" title="Percent of filtered tweets with at least one search key (If this is low, improve filter keys)">0%</span></div>
 <div class="subContent" id="subQualityContent">
  <div class="subGraphWrapper" id="qualityPie" <?php showGraphTitle(); ?>><?php showGraphText(); ?></div>
  <div id="filterQuality" class="subData" title="Key used as filter for input
Number of tweets this filter key brought in, that had a search key in it
Percent of tweets this filter key brought in, that had a search key in it"></div>
 </div>
</div>

<div id="creditWrapper" style="text-align: center">  
<div id="creditAnchor" onmouseleave="hideCredits()" onmouseenter="showCredits()">&nbsp;Created by Arun Jose&nbsp;
 <div id = "fullcredits"  onmouseleave="hideCredits()">
Created in partial fulfilment of dissertation requirement for M.Tech at BITS<br>
Cluster managed by <a href="http://storm.apache.org/" target="_blank">Apache Storm</a><br>
Data provided by <a href="https://twitter.com/" target="_blank">Twitter</a><br>
Twitter-Java interfacing through <a href="http://twitter4j.org" target="_blank">Twitter4j</a><br>
Sentiment analysis libraries from <a href="http://stanfordnlp.github.io/CoreNLP/" target="_blank">stanford-corenlp</a><br>
High availability key storage on <a href="http://redis.io/" target="_blank">Redis</a><br>
Redis-Java integration through <a href="http://redis.paluch.biz/" target="_blank">com.lambdaworks.redis</a><br>
Redis-PHP integration through <a href="https://github.com/phpredis/phpredis" target="_blank">phpredis</a><br>
Web app served by <a href="https://www.lighttpd.net/" target="_blank">lighttpd</a><br>
Pies and Doughnuts from <a href="http://canvasjs.com/" target="_blank">CanvasJS</a><br>
 </div>
</div>
</div>

</body>
</html>

