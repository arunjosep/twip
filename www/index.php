<!DOCTYPE html>
<html>
<head>
<title>TwIP &copy;</title>

<style>

body{
    width: 80%;
    min-width:960px;
    background-color:#e9e9a0;
    position: absolute;
    top:0;
    bottom: 0;
    left: 0;
    right: 0;
    margin-left: auto;
    margin-right: auto;
    padding: 20px;
    font-family:"Book Antiqua", Palatino, serif;
}

sup{
    font-size: 60%;
}

.header {
    background-color: #2020ac;
    border-color: #0c0c41;
    border-width: 1px;
    border-style: solid;
    color:white;
    padding: 15px;    
    margin:  auto;
    text-align: center;
    font-family:"Arial";
    letter-spacing: 3px;
    border-radius: 4px; 
    box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.4);
}
 
.sub{
    background-color:#2020ac;
    border-color: #0c0c41;
    border-width: 1px;
    border-style: solid;
    overflow:hidden;
    margin-top:12px;
    padding-top: 10px;
    padding-bottom:5px;
    padding-left:5px;
    padding-right:5px;
    border-radius: 4px; 
    box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.4);
}

.subHead{
    padding: 2px;
    padding-left: 10px;
    font-weight: bold;
    color:#FFFFFF;
    font-size:large;
    font-family:"Arial";
}

.subContent{
    background-color:#F8F8FF;
    overflow:hidden;
    margin-top:3px; 
    border-radius: 2px;
    width:100%;
    display: table;
}

.subGraph{
    background-color:#FFFFFF;
    padding:2px;
    padding-right:25px;
    color:#999999;
    text-align: center;
    overflow: hidden;
    box-shadow: 0px 5px 3px rgba(0, 0, 0, 0.6);
    display: inline-block;
    width:35%;
    display: table-cell;
    vertical-align: middle;
}

.subData{
    padding:10px;
    overflow: hidden;
    display: table-cell;
    horizontal-align: left;
    vertical-align: top;
}

.total{
    color:#DDDDFF;
    font-family:"Book Antiqua", Palatino, serif;
    font-weight: normal;
    float: right;
    padding-right: 12px;
}

.keyLeft{
    color: #0000cc;
    float: left;
    overflow: hidden;
}

.keyRight{
    color: #0000cc;
    font-style: normal;
    float: right;
    overflow: hidden;
    padding-right: 5px;
}

.perc{
    width: 70px;
    float: right;
    text-align: right;
}

.count{
    font-style: italic;
    color: #b3b3ff
}

</style>

<script src="jquery-latest.js"></script>
<script>
    var quickTimeout=700;
    var slowTimeout=4200;
    var showall=true;

    $(document).ready(function(){

        setInterval(function() {
            $("#hashKeys").load("hashKeys.php");
        }, quickTimeout);

        setInterval(function() {
            $("#compareKeys").load("compareKeys.php");
        }, quickTimeout);

        setInterval(function() {
            $("#filterQuality").load("filterQuality.php");
        }, quickTimeout);

        setInterval(function() {
            $("#hashCount").load("hashTotal.php");
        }, quickTimeout);

        setInterval(function() {
            $("#compareCount").load("compareTotal.php");
        }, quickTimeout);

        setInterval(function() {
            $("#comparePerc").load("comparePerc.php");
        }, quickTimeout);

        setInterval(function() {
            $("#hashPie").load("hashPie.php");
        }, slowTimeout);

        setInterval(function() {
            $("#comparePie").load("comparePie.php");
        }, slowTimeout);

        setInterval(function() {
            $("#qualityPie").load("qualityPie.php");
        }, slowTimeout);

        setInterval(function() {
            $("#sentimentPie").load("sentimentPie.php");
        }, slowTimeout);
		
        $("#header").click(function(){
            showall=!showall;
            if(showall){
                $(".subContent").show(250);
            }else{
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

</script>

</head>

<body>

<div class="header" id="header" title="Click to show/hide all regions"> <h1 title="Twitter Intelligent Processing">TwIP<sup>&copy;</sup> </h1> </div>

<?php function showGraphText() {echo"[ One awesome graph is being drawn just for you ]";}?>
<?php function showGraphTitle() {echo"title=\"This graph is refreshed less frequently than text data to save your bandwidth. For real-time accuracy, refer the frequently updated text data\"";}?>

<div class="sub">
 <div class="subHead" id="subHash" title="Keys that tweets are filtered with">Sources<span class="total" id="hashCount" title="Tweets that contain at least one filter key">0</span></div>
 <div class="subContent" id="subHashContent">
  <div class="subGraph" id="hashPie" <?php showGraphTitle(); ?>><?php showGraphText(); ?></div>
  <div id="hashKeys" class="subData" title="Key being used as filter 
Number of tweets found with this key 
Percent of tweets with this key in all filtered tweets"></div>
  </div>
</div>

<div class="sub">
 <div class="subHead" id="subSearch" title="Keys that are being searched for, in all the filtered tweets">Search Keys<span class="total" id="compareCount" title="Filtered tweets that contain at least one search key">0</span></div>
 <div class="subContent" id="subSearchContent">
  <div class="subGraph" id="comparePie" <?php showGraphTitle(); ?>><?php showGraphText(); ?></div>
  <div id="compareKeys" class="subData" title="Key being searched for 
Number of tweets found with this key in all filtered tweets
Percent of tweets with this key in all tweets with at least one search key"></div>
 </div>
</div>

<div class="sub">
 <div class="subHead" id="subQuality" title="How effective the source filters are in finding search keys">Source Performance<span class="total" id="comparePerc" title="Percent of filtered tweets with at least one search key (If this is low, improve filter keys)">0%</span></div>
 <div class="subContent" id="subQualityContent">
  <div class="subGraph" id="qualityPie" <?php showGraphTitle(); ?>><?php showGraphText(); ?></div>
  <div id="filterQuality" class="subData" title="Key used as filter for input
Number of tweets filtered with this key that contains at least one search key
Percent of tweets that this filter brought in, in all filtered tweets"></div>
 </div>
</div>

<div class="sub">
 <div class="subHead" id="subSent" title="Average sentiment of tweets">Sentiment</div>
 <div class="subContent" id="subSentContent">
  <div class="subGraph" id="sentimentPie" <?php showGraphTitle(); ?>><?php showGraphText(); ?></div>
  <div id="sentiments" class="subData" title="Sentiments"></div>
 </div>
</div>

<br>
</body>
</html>

