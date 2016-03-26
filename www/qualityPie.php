<script type="text/javascript" src="canvasjs.min.js"></script>
<script type="text/javascript">
    
    function showChart() {

    var chart = new CanvasJS.Chart("qualitypie",
    {  
      legend: {
               horizontalAlign: "center",
               verticalAlign: "bottom",
               fontFamily: "Book Antiqua",
               fontSize: 15
      },
      data: [{
	       type: "doughnut",
	       dataPoints: dps,
               showInLegend: true,
               startAngle:  -90,
               indexLabelFontFamily: "Book Antiqua",
               indexLabelFontSize: 16,
               indexLabelFontColor: "#888888",
               indexLabelPlacement: "inside"
	     }
     ]});
     chart.render();
  }
  </script>
  

<div id="qualitypie" style="height: 300px; width: 100%;">
<?php
$redis=new Redis();
$redis->connect("localhost",6379);

$vtotal = $redis->get("vote:total_count");
$htotal = $redis->get("hash:total_count");
?>
<script>
var dps = [
<?php echo "{legendText: \"Tweets that contain a search key\", y:".$vtotal."},{legendText: \"Tweets with a source key but not a filter key\", y:".($htotal-$vtotal)."}"; ?>
]; 
showChart();
</script>
</div>

