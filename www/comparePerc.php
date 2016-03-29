<?php

$redis=new Redis();
$redis->connect("localhost",6379);

$vtotal = $redis->get("vote:key_found");
$htotal = $redis->get("vote:in_count");
$perc = number_format($vtotal*100/$htotal,2);
echo $perc."%";
?>
