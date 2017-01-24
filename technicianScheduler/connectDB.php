<?php

error_reporting(E_ALL ^ E_NOTICE);

$con = mysqli_connect("localhost","root","","technicianTry");


 $username = $_POST['username'];
 $password = $_POST['password'];

$sql = "SELECT * from table1 WHERE username='$username' AND password='$password'";

$result=mysqli_query($con,$sql);

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
	$response["name"]=$row['username'];
	$response["role"]=$row['role'];
}

echo json_encode($response);
//以json的形式返回给客户端

mysqli_close($con);