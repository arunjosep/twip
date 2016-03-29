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
    { backgroundColor: null,
      legend: {
               horizontalAlign: "center",
               verticalAlign: "bottom",
               fontFamily: "Montserrat",
               fontSize: 14,
	       fontColor: "#16a085"
      },
      title:{
        text: "All Tweets with Search Keys",
        fontFamily: "Montserrat",
        fontSize: 19,
        fontColor: "#16a085",
        fontWeight:"normal"
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
  
<?php
$redis=new Redis();
$redis->connect("localhost",6379);
$sentW = $redis->get("config:sentiment");
$sentW = ($sentW==="true");

if($sentW){
	$vneg = $redis->get("sentiment:0");
	$neg = $redis->get("sentiment:1");
	$neutral = $redis->get("sentiment:2");
	$pos = $redis->get("sentiment:3");
	$vpos = $redis->get("sentiment:4");


	$vneg = empty($vneg)?0.01:$vneg;
	$neg = empty($neg)?0.01:$neg;
	$neutral = empty($neutral)?0.01:$neutral;
	$pos = empty($pos)?0.01:$pos;
	$vpos = empty($vpos)?0.01:$vpos;
}
?>

<div id="sentimentpie" class ="subGraphInner"
<?php 
	if ($sentW){
		echo "style=\"height: 320px;\"";
	}
?>
>


<script>

var dps = [
<?php echo "{legendText: \"Very Negative\", y:".$vneg."},
{legendText: \"Negative\", y:".$neg."},
{legendText: \"Neutral\", y:".$neutral."},
{legendText: \"Positive\", y:".$pos."},
{legendText: \"Very Positive\", y:".$vpos."}"; ?>
]; 

<?php 
	if ($sentW){
		echo "showChart();";
	}
?>

</script> 

</div>

