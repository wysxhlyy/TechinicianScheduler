<?php

error_reporting(E_ALL ^ E_NOTICE);

$con = mysqli_connect("146.148.28.194","root","wuyusheng","techSchedulerDB");


 $id=$_POST['taskId'];


$response = array();




$sql="DELETE FROM task WHERE task_id='$id' ";
$sql2="DELETE FROM manageTask WHERE task_id='$id'";

$deleteTask=mysqli_query($con,$sql);
$deleteTaskManage=mysqli_query($con,$sql2);

if($deleteTask&&$deleteTaskManage){
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
