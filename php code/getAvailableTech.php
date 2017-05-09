<?php

error_reporting(E_ALL ^ E_NOTICE);

$con = mysqli_connect("146.148.28.194","root","wuyusheng","techSchedulerDB");


 $managerId =$_POST['managerId'];


$sql = "SELECT * FROM manageTechnician INNER JOIN technician ON manageTechnician.t_id=technician.t_id WHERE m_id='$managerId' ";
$sql2 = "SELECT * FROM manageTask INNER JOIN task ON manageTask.task_id=task.task_id WHERE m_id='$managerId'";

$result=mysqli_query($con,$sql);
$result2=mysqli_query($con,$sql2);

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

$i=0;
while ($row=mysqli_fetch_array($result)) {
	$i=$i+1;
	$response["techId".$i]=$row['t_id'];
	$response["techName".$i]=$row['firstName'];
	$response["skillLevel".$i]=$row['skill_level'];
	$response["workHour".$i]=$row['work_hour'];
	$response["surname".$i]=$row['surname'];

}
$response["techNum"]=$i;

$j=0;
while ($row=mysqli_fetch_array($result2)) {
	$j=$j+1;
	$response['taskId'.$j]=$row['task_id'];
	$response['taskSkill'.$j]=$row['skill_level'];
	$response['stationId'.$j]=$row['s_id'];
	$response['taskDuration'.$j]=$row['duration'];
	$response['taskStatus'.$j]=$row['finished'];
	$response['taskName'.$j]=$row['name'];
	$response['taskDescription'.$j]=$row['description'];

}
$response["taskNum"]=$j;

for($k=1;$k<$response['taskNum']+1;$k++){
	$stationId=$response['stationId'.$k];
	$resultPos=mysqli_query($con,"SELECT * FROM station WHERE s_id='$stationId'");
	while ($row=mysqli_fetch_array($resultPos)) {
		$response['stationLat'.$k]=$row['latitude'];
		$response['stationLong'.$k]=$row['longitude'];
		$response['stationName'.$k]=$row['stationName'];
	}
}

echo json_encode($response);
//以json的形式返回给客户端

mysqli_close($con);
?>
