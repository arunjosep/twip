<style>
.inCk{
    float: left;
    background-color: #1f7a1f;
    height:10px;
    overflow: hidden;
    margin-bottom:4px;
}
.outCk{
    background-color: #c2f0c2;
    height:10px;
    overflow: hidden;
    margin-bottom:4px;
}

</style>

<?php

$redis=new Redis();
$redis->connect("localhost",6379);

$keys = $redis->smembers("keys:compare");
$total = $redis->get("vote:total_count");
foreach ($keys as $key) {
    $count = $redis->get("vote:".$key);
    $perc = number_format($count*100/$total,2);
    echo "<div class=\"keyLeft\">".$key."</div><div class=\"keyRight\"><span class=\"count\">".$count."</span><span class=\"perc\">".$perc."%</span></div><br>";
    echo "<div class=\"inCk\" style=\"width:".$perc."%\"></div>";
    echo "<div class=\"outCk\" style=\"width:".(100-$perc)."%\"></div>";
}

?>
