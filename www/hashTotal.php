<?php

$redis=new Redis();
$redis->connect("localhost",6379);

$total = $redis->get("hash:in_count");
echo $total
?>
