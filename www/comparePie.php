<script type="text/javascript" src="canvasjs.min.js"></script>
<script type="text/javascript">
    
    function showChart() {

    var chart = new CanvasJS.Chart("comparepie",
    { 
	  backgroundColor: null,
      data: [{
	       type: "pie",
	       dataPoints: dps,
               indexLabelFontFamily: "Montserrat",
               indexLabelFontSize: 16,
               indexLabelFontColor: "#16a085",
               indexLabelPlacement: "outside"
	     }
     ]});
     chart.render();
  }
  </script>
  

<div id="comparepie" class ="subGraphInner" style="height: 300px;">
<?php
$redis=new Redis();
$redis->connect("localhost",6379);

$keys = $redis->smembers("keys:compare");
?>
<script>
var dps = [{ y:0}
<?php
foreach ($keys as $key) {
    $count = $redis->get("vote:".$key);
    $count = empty($count)?0:$count;
    echo ",{indexLabel:\"".$key."\", y:".$count."}";
}
?>
]; 
showChart();
</script>
</div>

