<?php

$redis=new Redis();
$redis->connect("localhost",6379);

$total = $redis->get("hash:total_count");
echo $total
?>
