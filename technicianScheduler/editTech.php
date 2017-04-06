<?php

error_reporting(E_ALL ^ E_NOTICE);

$con = mysqli_connect("146.148.28.194","root","wuyusheng","techSchedulerDB");


 $id=$_POST['techId'];
 $skill = $_POST['techSkill'];
 $workHour=$_POST['techWorkHour'];



$response = array();



$sql="UPDATE technician SET skill_level='$skill',work_hour='$workHour' WHERE t_id='$id' ";

$editTech=mysqli_query($con,$sql);

if($editTech){
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
