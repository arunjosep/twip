<style>
.inEff{
    float: left;
    background-color:#5c5c8a;
    height:10px;
    overflow: hidden;
    margin-bottom:4px;
}
.outEff{
    float: left;
    background-color:#4da6ff;
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
    echo "<div class=\"keyLeft\">".$hkey."</div><div class=\"keyRight\"></div><br>";
    foreach ($ckeys as $ckey) {
        $count = $redis->get("compare:".$hkey.":".$ckey);
        $perc = number_format($count*100/$total,2);
        
        if ($bool){
            echo "<div class=\"inEff\" style=\"width:".$perc."%\"></div>";
        }else{
            echo "<div class=\"outEff\" style=\"width:".$perc."%\"></div>";
        }
        $bool=!$bool;
    }
    echo "<br>";
}
?>
