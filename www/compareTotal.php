<?php

$redis=new Redis();
$redis->connect("localhost",6379);

$vtotal = $redis->get("vote:total_count");
echo $vtotal;
?>
