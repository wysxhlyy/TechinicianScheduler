<?php

error_reporting(E_ALL ^ E_NOTICE);

$con = mysqli_connect("146.148.28.194","root","wuyusheng","techSchedulerDB");


 $technicianId = $_POST['technicianId'];

$sql = "SELECT * FROM station INNER JOIN task ON station.s_id=task.s_id WHERE task_id in (SELECT task_id FROM taskSchedule WHERE t_id='$technicianId')";

$result=mysqli_query($con,$sql);

$num = mysqli_num_rows($result);

$response = array();


if($num >= 0){
    $response["success"] = 1;
   }else{
    $response["success"] = 0;
}

if ($result===FALSE) {
    echo mysql_error();
}

$i=1;
while ($row=mysqli_fetch_array($result)) {
	$response["taskName".$i]=$row['name'];
	$response["skill_level".$i]=$row['skill_level'];
	$response["stationId".$i]=$row['s_id'];
	$response["duration".$i]=$row['duration'];
	$response["description".$i]=$row['description'];
	$response["stationName".$i]=$row['stationName'];
	$response["latitude".$i]=$row['latitude'];
	$response["longitude".$i]=$row['longitude'];
	$i=$i+1;
}

$response["taskSize"]=$i;

echo json_encode($response);
//以json的形式返回给客户端

mysqli_close($con);
?>
