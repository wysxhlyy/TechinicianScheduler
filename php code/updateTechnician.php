<?php

error_reporting(E_ALL ^ E_NOTICE);

$con = mysqli_connect("146.148.28.194","root","wuyusheng","techSchedulerDB");


 $password = $_POST['password'];
 $email = $_POST['email'];
 $phone = $_POST['phone'];
 $techId=$_POST['techId'];

$sql = "UPDATE technician SET password='$password',email='$email',phone='$phone' WHERE t_id='$techId' ";

$result=mysqli_query($con,$sql);


$response = array();


if($result){
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
