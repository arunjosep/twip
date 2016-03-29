<style>
.keysWrapper{
    float: center; 
    text-align: center;
    background-color:#FFFFFF;
    border-radius: 2px;
    display:inline-block;
    padding: 5px 4px 5px 4px; 
    margin-bottom: 2px;  
}

.searchKey{
    color:#FFFFFF;
    font-weight: bold;
    border-radius: 2px;
    padding: 2px;
}

.eff{
    float: left;
    height:12px;
    overflow: hidden;
    margin-top:3px;
    margin-bottom:3px;
    box-shadow: 0px 2px 2px rgba(0, 0, 0, 0.3) inset;
}

.outEff{
    background-color: #ffffff;
    height:18px;
    overflow: hidden;
    margin-bottom:7px;
    padding-left:3px;
    box-shadow: 2px 2px 2px rgba(0, 0, 0, 0.3);
}
</style>

<?php

$redis=new Redis();
$redis->connect("localhost",6379);

$colors=array("#d966ff","#ccbf22","#ff8000","#bfff00","#00bfff","#ff00ff","#ff0040","#ffff00","#66ff66","#66b3ff");

$hkeys = $redis->smembers("keys:hash");
$ckeys = $redis->smembers("keys:compare");

$colorPattern = 0;
echo "<div class=\"keysWrapper\"title =\"Search keys in their colors used in usage graphs below\">";
foreach ($ckeys as $key) {
    if($colorPattern>0)
        echo "&nbsp;";
    echo "<span class= \"searchKey\" style=\"background-color:".$colors[$colorPattern++%sizeof($colors)].";\">&nbsp;".$key."&nbsp;</span>";
    }
echo "</div><br>";

$whiteBar="How much of each search key, this source key brought in. All colored pieces from all the bars in this panel will make up one full bar of all found search keys.";

$total = $redis->get("compare:total_count");
$itotal = $redis->get("compare:in_count");
foreach ($hkeys as $hkey) {
    $tcount = $redis->get("compare:".$hkey);
    $tperc = number_format($tcount*100/$itotal,2);
    echo "<div class=\"keyLeft\">".$hkey."</div><div class=\"keyRight\"><span class=\"count\">".$tcount."</span><span class=\"perc\">".$tperc."%</span></div><div class=\"outEff\" style=\"width:99%\" title=\"".$whiteBar."\">";
    $colorPattern = 0;
    foreach ($ckeys as $ckey) {
        $count = $redis->get("compare:".$hkey.":".$ckey);
        $perc = number_format($count*100/$total,2);
        echo "<div class=\"eff\" style=\"background-color:".$colors[$colorPattern++%sizeof($colors)].";width:".$perc."%;\" title=\"".$whiteBar."\"></div>";
    }
    echo "</div>";
}
?>
