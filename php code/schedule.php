<?php

error_reporting(E_ALL ^ E_NOTICE);

$con = mysqli_connect("146.148.28.194","root","wuyusheng","techSchedulerDB");

$judge=0;

$scheduleSize=$_POST['scheduleSize'];

for($i=0;$i<$scheduleSize;$i++){
	$taskId=$_POST['taskId'.$i];
	$techId=$_POST['techId'.$i];
	$sql = "INSERT INTO taskSchedule (m_id,task_id,t_id) VALUES('$taskId','$techId') ";
	$result=mysqli_query($con,$sql);
	if(!$result){
		$judge=1;
	}
}

$response = array();


if($$judge==0){
    $response["success"] = 1;
   }else{
    $response["success"] = 0;
}

if ($result===FALSE) {
    echo mysql_error();
}


echo json_encode($response);
//以json的形式返回给客户端

mysqli_close($con);
?>
