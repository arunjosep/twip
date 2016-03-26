<script type="text/javascript" src="canvasjs.min.js"></script>
<script type="text/javascript">
    
  function showChart() {

    CanvasJS.addColorSet("sentimentColors",
                [
                "#ff2020",
                "#ff8000",
                "#ffff00",
                "#80ff00",
                "#00d000"                
                ]);

    var chart = new CanvasJS.Chart("sentimentpie",
    {  
      legend: {
               horizontalAlign: "center",
               verticalAlign: "bottom",
               fontFamily: "Book Antiqua",
               fontSize: 15
      },
      title:{
        text: "All Filtered Tweets",
        fontFamily: "Arial",
        fontSize: 18,
        fontColor: "#555599",
      },
      legend:{
        fontFamily: "Arial",
        fontSize: 13,
        fontColor: "#555599",
        horizontalAlign: "center"
      },
      colorSet: "sentimentColors",
      data: [{
	       type: "doughnut",
	       dataPoints: dps,
               showInLegend: true,
               startAngle: -90
	     }
     ]});
     chart.render();
  }
</script>
  

<div id="sentimentpie" style="height: 300px; width: 100%;">
<?php
$redis=new Redis();
$redis->connect("localhost",6379);

$vneg = $redis->get("sentiment:0");
$neg = $redis->get("sentiment:1");
$neutral = $redis->get("sentiment:2");
$pos = $redis->get("sentiment:3");
$vpos = $redis->get("sentiment:4");


$vneg = empty($vneg)?0:$vneg;
$neg = empty($neg)?0:$neg;
$neutral = empty($neutral)?0:$neutral;
$pos = empty($pos)?0:$pos;
$vpos = empty($vpos)?0:$vpos;

?>

<script>

var dps = [
<?php echo "{legendText: \"Very Negative\", y:".$vneg."},
{legendText: \"Negative\", y:".$neg."},
{legendText: \"Neutral\", y:".$neutral."},
{legendText: \"Positive\", y:".$pos."},
{legendText: \"Very Positive\", y:".$vpos."}"; ?>
]; 
showChart();

</script> 
</div>

