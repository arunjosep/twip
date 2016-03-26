<script type="text/javascript" src="canvasjs.min.js"></script>
<script type="text/javascript">
    
    function showChart() {

    var chart = new CanvasJS.Chart("hashpie",
    { 
      data: [{
	       type: "pie",
	       dataPoints: dps,
               indexLabelFontFamily: "Book Antiqua",
               indexLabelFontSize: 16,
               indexLabelFontColor: "#888888",
               indexLabelPlacement: "outside"
	     }
     ]});
     chart.render();
  }
  </script>
  

<div id="hashpie" style="height: 300px; width: 100%;">
<?php
$redis=new Redis();
$redis->connect("localhost",6379);

$keys = $redis->smembers("keys:hash");
?>
<script>
var dps = [{ y:0}
<?php
foreach ($keys as $key) {
    $count = $redis->get("hash:".$key);
    echo ",{indexLabel:\"".$key."\", y:".$count."}";
}
?>
]; 
showChart();
</script>
</div>

