<style>
.trueEff{
    float: left;
    background-color:#5c5c8a;
    height:10px;
    overflow: hidden;
    margin-bottom:4px;
}
.falseEff{
    float: left;
    background-color:#4da6ff;
    height:10px;
    overflow: hidden;
    margin-bottom:4px;
}
.extraEff{
    float: left;
    background-color:#DDDDFF;
    height:10px;
    overflow: hidden;
    margin-bottom:4px;
}
</style>



<?php

$redis=new Redis();
$redis->connect("localhost",6379);

$hkeys = $redis->smembers("keys:hash");
$ckeys = $redis->smembers("keys:compare");
$total = $redis->get("compare:total_count");

foreach ($hkeys as $hkey) {
    $bool = true;
    $tcount = $redis->get("compare:".$hkey);
    $tperc = number_format($tcount*100/$total,2);
    echo "<div class=\"keyLeft\">".$hkey."</div><div class=\"keyRight\"><span class=\"count\">".$tcount."</span><span class=\"perc\">".$tperc."%</span></div><br>";
    foreach ($ckeys as $ckey) {
        $count = $redis->get("compare:".$hkey.":".$ckey);
        $perc = number_format($count*100/$total,2);
        
        if ($bool){
            echo "<div class=\"trueEff\" style=\"width:".$perc."%\"></div>";
        }else{
            echo "<div class=\"falseEff\" style=\"width:".$perc."%\"></div>";
        }
	
        $bool=!$bool;
    }
    echo "<div class=\"extraEff\" style=\"width:".(99.99-$tperc)."%\"></div><br>";
}
?>
