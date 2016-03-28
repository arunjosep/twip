<script type="text/javascript" src="canvasjs.min.js"></script>
<script type="text/javascript">
    
    function showChart() {

    var chart = new CanvasJS.Chart("qualitypie",
    { backgroundColor: null,
      legend: {
               horizontalAlign: "center",
               verticalAlign: "bottom",
               fontFamily: "Montserrat",
               fontSize: 15,
               fontColor: "#16a085" 
      },
      data: [{
	       type: "doughnut",
	       dataPoints: dps,
               showInLegend: true,
               startAngle:  -90,
               indexLabelFontFamily: "Montserrat",
               indexLabelFontSize: 16,
               indexLabelFontColor: "#16a085",
               indexLabelPlacement: "inside"
	     }
     ]});
     chart.render();
  }
  </script>
  

<div id="qualitypie" class ="subGraphInner" style="height: 300px;">
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

