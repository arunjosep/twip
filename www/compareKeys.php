<style>
.inCk{
    float: left;
    background-color: #1f7a1f;
    height:14px;
    overflow: hidden;
}
.outCk{
    background-color: #c2f0c2;
    height:14px;
    overflow: hidden;
    margin-bottom:7px;
    box-shadow: 2px 2px 2px rgba(0, 0, 0, 0.3);
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
    echo "<div class=\"outCk\" style=\"width:99%\"><div class=\"inCk\" style=\"width:".$perc."%\"></div></div>";
}

?>
