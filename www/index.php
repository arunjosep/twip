<!DOCTYPE html>
<html>
<head>
<title>Twip &copy;</title>

<style>

body{
    width: 80%;
    min-width:420px;
    max-width:840px;
    background-color:#FFFFF0;
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

.header {
    background-color: #181881;
    color:white;
    padding: 15px;    
    margin:  auto;
    text-align: center;
    font-family:"Arial";
    letter-spacing: 3px;
    border-radius: 5px; 
}
 
.sub{
    background-color:#181881;
    margin-top:7px;
    padding-top: 10px;
    padding-bottom:5px;
    padding-left:5px;
    padding-right:5px; 
    border-radius: 5px; 
}

.subHead{
    padding: 2px;
    font-weight: bold;
    color:#FFFFFF;
    font-size:large;
font-family:"Arial";
}
.total{
    color:#AAAABB;
    font-style: italic;
    font-weight: normal;
    float: right;
}

.dynRegion{    
    background-color:#EFEFEF;
    padding:10px;
    margin-top:3px; 
    border-radius: 2px;

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
    var timeout=750;
    $(document).ready(function(){
        setInterval(function() {
            $("#hashKeys").load("hashKeys.php");
        }, timeout);
    });
    $(document).ready(function(){
        setInterval(function() {
            $("#compareKeys").load("compareKeys.php");
        }, timeout);
    });
    $(document).ready(function(){
        setInterval(function() {
            $("#filterQuality").load("filterQuality.php");
        }, timeout);
    });
    $(document).ready(function(){
        setInterval(function() {
            $("#hashCount").load("hashTotal.php");
        }, timeout);
    });
    $(document).ready(function(){
        setInterval(function() {
            $("#compareCount").load("compareTotal.php");
        }, timeout);
    });
    $(document).ready(function(){
        setInterval(function() {
            $("#comparePerc").load("comparePerc.php");
        }, timeout);
    });

</script>
</head>

<body>

<div class="header"> <h1>TWIP &copy;<h1> </div>

<div class="sub">
 <div class="subHead" title="Keys that tweets are filtered with">Sources <span class="total" id="hashCount" title="Tweets that contain at least one filter key">0</span></div>
 <div id="hashKeys" class="dynRegion" title="Key being used as filter 
Number of tweets found with this key 
Percent of tweets with this key in all filtered tweets"></div>
</div>

<div class="sub">
 <div class="subHead" title="Keys that are being searched for, in all the filtered tweets">Search Keys <span class="total" id="compareCount" title="Filtered tweets that contain at least one search key">0</span></div>
 <div id="compareKeys" class="dynRegion" title="Key being searched for 
Number of tweets found with this key in all filtered tweets
Percent of tweets with this key in all tweets with at least one search key"></div>
</div>

<div class="sub">
 <div class="subHead" title="How effective the source filters are in finding search keys">Quality of filters <span class="total" id="comparePerc" title="Percent of filtered tweets with at least one search key (If this is low, improve filter keys)">0%</span></div>
 <div id="filterQuality" class="dynRegion" title="Key used as filter for input
Number of tweets filtered with this key that contains at least one search key
Percent of tweets that this filter brought in, in all tweets that contains at least one search key"></div>
</div>

<br>
</body>
</html>

