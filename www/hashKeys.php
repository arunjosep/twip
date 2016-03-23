<style>
.inHk{
    float: left;
    background-color:#e68a00;
    height:10px;
    overflow: hidden;
    margin-bottom:4px;
}
.outHk{
    background-color:#ffe0b3;
    height:10px;
    overflow: hidden;
    margin-bottom:4px;
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
    echo "<div class=\"inHk\" style=\"width:".$perc."%\"></div>";
    echo "<div class=\"outHk\" style=\"width:".(100-$perc)."%\"></div>";
} 

?>
