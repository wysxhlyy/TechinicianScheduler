<?php

error_reporting(E_ALL ^ E_NOTICE);

$con = mysqli_connect("146.148.28.194","root","wuyusheng","techSchedulerDB");


 $username = $_POST['username'];
 $password = $_POST['password'];

$sql = "SELECT * FROM manager WHERE username='$username' AND password='$password' ";

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
	$count++;
	$response["username"]=$row['username'];
	$response["email"]=$row['email'];
	$response["phone"]=$row['phone'];
	$response["firstName"]=$row['firstName'];
	$response["surname"]=$row['surname'];
	$response["password"]=$row['password'];
	$response["m_id"]=$row['m_id'];
}

echo json_encode($response);
//以json的形式返回给客户端

mysqli_close($con);
?>
