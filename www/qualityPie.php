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
      title:{
        text: "All Tweets with Source Keys",
        fontFamily: "Montserrat",
        fontSize: 19,
        fontColor: "#16a085",
        fontWeight:"normal"
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

$good = $redis->get("vote:key_found");
$all = $redis->get("vote:in_count");
?>
<script>
var dps = [
<?php echo "{legendText: \"Search key present\", y:".$good."},{legendText: \"Search key not present\", y:".($all-$good)."}"; ?>
]; 
showChart();
</script>
</div>

