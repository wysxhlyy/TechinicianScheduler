<?php

error_reporting(E_ALL ^ E_NOTICE);

$con = mysqli_connect("146.148.28.194","root","wuyusheng","techSchedulerDB");


 $id=$_POST['taskId'];
 $status=$_POST['status'];


$response = array();

$sql = "UPDATE task SET setFinished='$status' WHERE task_id='$id' ";

$editTask=mysqli_query($con,$sql);

if($editTask){
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
