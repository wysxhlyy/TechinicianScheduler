<?php

error_reporting(E_ALL ^ E_NOTICE);

$con = mysqli_connect("146.148.28.194","root","wuyusheng","techSchedulerDB");


 $id=$_POST['taskId'];
 $name = $_POST['taskName'];
 $skill = $_POST['taskSkillReq'];
 $station= $_POST['taskStation'];
 $duration=$_POST['taskDuration'];
 $description=$_POST['taskDescription'];
 $status=$_POST['taskStatus'];

$response = array();


 $sql1 = "SELECT * FROM station WHERE stationName='$station'";

$result=mysqli_query($con,$sql1);

while ($row=mysqli_fetch_array($result)) {
	$station_id=$row['s_id'];
}
$num = mysqli_num_rows($result);


if($num>0){
	$sql="UPDATE task SET name='$name',skill_level='$skill',s_id='$station_id',duration='$duration',description='$description',finished='$status' WHERE task_id='$id' ";

	$editTask=mysqli_query($con,$sql);

	if($editTask){
	    $response["success"] = 1;
	}else{
	    $response["success"] = 0;
	}
}



if ($result===FALSE) {
    echo mysql_error();
}

echo json_encode($response);
//以json的形式返回给客户端

mysqli_close($con);
?>
