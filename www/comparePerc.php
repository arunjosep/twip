<style>
.inNonRT{
    float: left;
    background-color: #16a085;
    height:18px;
    overflow: hidden;

    box-shadow: 2px 3px 3px rgba(0, 0, 0, 0.35) inset;
}

.outRT{
    float: left;
    background-color: #bdc3c7;
    height:18px;
    overflow: hidden;
    box-shadow: 1px 2px 2px rgba(0, 0, 0, 0.30) inset;
    margin-right:15px;
    margin-top:2px;
    border-style: solid;
    border-width: 1px;
    border-color: #f8f8f8;
}
</style>

<?php

$redis=new Redis();
$redis->connect("localhost",6379);

$total = $redis->get("origin:in_count");
$original = $redis->get("origin:nonRT");
$nortperc = number_format($original*100/$total,2);
$isRTused = $redis->get("config:noRT");
$isRTused = ($isRTused==="true");
$title = (100-$nortperc)."% of all filtered tweets are retweets. ";
if ($isRTused){
    $title = $title."Only original tweets (non retweets) are used in this analysis.";
}

echo "<span style=\"float:left;\">RT&nbsp;</span><div class=\"outRT\" style=\"width:100px;\" title=\"".$title."\"><div class=\"inNonRT\" style=\"width:".$nortperc."%\"></div></div>";

$vtotal = $redis->get("vote:key_found");
$htotal = $redis->get("vote:in_count");
$perc = number_format($vtotal*100/$htotal,2);
echo "<div style=\"width:100px;float:right;text-align:right;\">".$perc."%</div>";
?>
