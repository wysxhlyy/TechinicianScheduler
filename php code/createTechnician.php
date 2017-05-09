<?php

error_reporting(E_ALL ^ E_NOTICE);

$con = mysqli_connect("146.148.28.194","root","wuyusheng","techSchedulerDB");



 $username = $_POST['username'];
 $password = $_POST['password'];
 $email=$_POST['email'];
 $phone=$_POST['phone'];
 $firstName=$_POST['firstName'];
 $surname=$_POST['surname'];

$sql = "INSERT INTO technician (username,password,email,phone,firstName,surname) VALUES ('$username','$password','$email','$phone','$firstName','$surname') ";
$sql2 = "SELECT * FROM technician WHERE username='$username' AND password='$password' ";

$result2=mysqli_query($con,$sql);
$result=mysqli_query($con,$sql2);

$num = mysqli_num_rows($result);

$response = array();

if($num > 0){
    $response["success"] = 1;
   }else{
    $response["success"] = 0;
}

if ($result===FALSE) {
    echo mysql_error();
}

while ($row=mysqli_fetch_array($result)) {
	$response["technicianId"]=$row['t_id'];
    $response["type"]=0;
}


echo json_encode($response);
//以json的形式返回给客户端

$con->close();
?>
