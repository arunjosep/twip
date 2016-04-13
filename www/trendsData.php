<style>
.trendList{
    width:50%;
    float:left;
}
.trendHeader{
    color:#27ae60;
    text-align:center;
    padding-bottom:5px;
    font-size: 118%;
}
.trend{
    color:#2980b9;
    text-align:right;
}
.score{
    padding-left:30px;
    width:50%;
    float:right;
    text-align:left;
    color:#95a5a6;
}
</style>


<?php
$redis=new Redis();
$redis->connect("localhost",6379);

$sourceTrends = $redis->zrevrange("trends:source", 0 ,11, "withscores");

echo "<div class =\"trendList\"><div class=\"trendHeader\">Top trends in source tweets</div>";
foreach ($sourceTrends as $word => $score) {
   echo "<div class=\"trend\" title=\"Click on trending word to search for it in twitter\"><a href=\"https://twitter.com/search?q=%22".$word."%22\" target=\"_blank\">".$word."</a><span class=\"score\" title=\"Number of occurrences of this key\"> ".$score."</span></div>";
}
echo"</div>";

$candidateTrends = $redis->zrevrange("trends:candidate", 0 ,11, "withscores");
echo "<div class =\"trendList\"><div class=\"trendHeader\">Top trends in tweets with a search key</div>";
foreach ($candidateTrends as $word => $score) {
   echo "<div class=\"trend\" title=\"Click on trending word to search for it in twitter\"><a href=\"https://twitter.com/search?q=%22".$word."%22\" target=\"_blank\">".$word."</a><span class=\"score\" title=\"Number of occurrences of this key\"> ".$score."</span></div>";
}
echo"</div>";
?>
