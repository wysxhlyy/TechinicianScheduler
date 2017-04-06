<?php

error_reporting(E_ALL ^ E_NOTICE);

$con = mysqli_connect("146.148.28.194","root","wuyusheng","techSchedulerDB");


 $name = $_POST['taskName'];
 $skill = $_POST['taskSkillReq'];
 $station= $_POST['taskStation'];
 $duration=$_POST['taskDuration'];
 $description=$_POST['taskDescription'];
 $finished="false";
 $managerId=$_POST['managerId'];



$sql1 = "SELECT * FROM station WHERE stationName= '$station'";

$result=mysqli_query($con,$sql1);

while ($row=mysqli_fetch_array($result)) {
	$station_id=$row['s_id'];
}

$sql2 = "INSERT INTO task(name,skill_level,s_Id,duration,description,finished) VALUES ('$name','$skill','$station_id','$duration','$description','$finished')";

$addResult=mysqli_query($con,$sql2);

$sql3 ="SELECT @@IDENTITY";
$findId=mysqli_query($con,$sql3);

while ($row=mysqli_fetch_array($findId)) {
	$lastInsert=$row[0];
}


$sql4 = "INSERT INTO manageTask(m_id,task_id) VALUES ('$managerId','$lastInsert')";
$addTaskManage=mysqli_query($con,$sql4);

$response = array();
if($addTaskManage){
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
