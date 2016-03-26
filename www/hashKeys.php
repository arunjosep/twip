<style>
.inHk{
    float: left;
    background-color: #e68a00;
    height:14px;
    overflow: hidden;
}
.outHk{
    background-color: #ffe0b3;
    height:14px;
    overflow: hidden;
    margin-bottom:7px;
    box-shadow: 2px 2px 2px rgba(0, 0, 0, 0.3);
}
</style>

<?php

$redis=new Redis();
$redis->connect("localhost",6379);

$keys = $redis->smembers("keys:hash");
$total = $redis->get("hash:total_count");
foreach ($keys as $key) {
    $count = $redis->get("hash:".$key);
    $perc = number_format($count*100/$total,2);
    echo "<div class=\"keyLeft\">".$key."</div><div class=\"keyRight\"><span class=\"count\">".$count."</span><span class=\"perc\">".$perc."%</span></div><br>";
    echo "<div class=\"outHk\" style=\"width:99%\"><div class=\"inHk\" style=\"width:".$perc."%\"></div></div>";
} 

?>
