<?php

$redis=new Redis();
$redis->connect("localhost",6379);

$vtotal = $redis->get("vote:total_count");
$htotal = $redis->get("hash:total_count");
$perc = number_format($vtotal*100/$htotal,2);
echo $perc."%";
?>
