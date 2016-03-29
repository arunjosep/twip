<script type="text/javascript" src="canvasjs.min.js"></script>
<script type="text/javascript">
    
  function showChart(divId,header) {

    CanvasJS.addColorSet("sentimentColors",
                [
                "#ff2020",
                "#ff8000",
                "#ffff00",
                "#80ff00",
                "#00d000"                
                ]);

    var chart = new CanvasJS.Chart(divId,
    {  
      legend: {
               horizontalAlign: "center",
               verticalAlign: "bottom",
               fontFamily: "Montserrat",
               fontSize: 13,
       	       fontColor: "#16a085"       		   
      },
      backgroundColor: null,
      title:{
        text: header,
        fontFamily: "Montserrat",
        fontSize: 16,
        fontColor: "#16a085",
        fontWeight:"normal"
      },
      colorSet: "sentimentColors",
      data: [{
	       type: "doughnut",
	       dataPoints: dps,
               showInLegend: false,
               startAngle: -90
	     }
     ]});
     chart.render();
  }
</script>


  
<?php
$redis=new Redis();
$redis->connect("localhost",6379);
$sentI = $redis->get("config:sentiment");
$sentI = ($sentI==="true");
$keys = $redis->smembers("keys:compare");

$keyCount =0;
if($sentI){
	foreach ($keys as $key) {	
		$vneg = $redis->get("sentiment:".$key.":0");
		$neg = $redis->get("sentiment:".$key.":1");
		$neutral = $redis->get("sentiment:".$key.":2");
		$pos = $redis->get("sentiment:".$key.":3");
		$vpos = $redis->get("sentiment:".$key.":4");
		$vneg = empty($vneg)?0.01:$vneg;
		$neg = empty($neg)?0.01:$neg;
		$neutral = empty($neutral)?0.01:$neutral;
		$pos = empty($pos)?0.01:$pos;
		$vpos = empty($vpos)?0.01:$vpos;
		echo "<div id=\"sentimentspie".$keyCount."\"class =\"sentimentCandGraph\" style=\"\"></div>";
		echo "<script> var dps = ["."{legendText: \"Very Negative\", y:".$vneg."},
				{legendText: \"Negative\", y:".$neg."},
				{legendText: \"Neutral\", y:".$neutral."},
				{legendText: \"Positive\", y:".$pos."},
				{legendText: \"Very Positive\", y:".$vpos."}];
				showChart(\"sentimentspie".$keyCount."\",\"".$key."\");</script>";
		$keyCount++;
	}
} else {
	echo "<span style=\"font-size:80%;\">Nothing to see here!<br>Enable sentiment analysis for search keys to see mind-blowing analytics.</span>";
}
?>


