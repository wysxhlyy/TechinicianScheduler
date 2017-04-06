<?php

error_reporting(E_ALL ^ E_NOTICE);

$conn =new mysqli("146.148.28.194","root","wuyusheng","techSchedulerDB");

if($conn->connect_error){
	die("Connection failed ".$conn->connect_error);
}

$managerId=$_POST['managerId'];
$techUsername=$_POST['username'];


$sql1="SELECT * FROM technician WHERE username='$techUsername'";
$result=mysqli_query($conn,$sql1);
$num = mysqli_num_rows($result);

while ($row=mysqli_fetch_array($result)) {
	$technicianId=$row['t_id'];
	}

	$response = array();

if($num > 0){
    $response["success"] = 1;
	$sql2 = "INSERT INTO manageTechnician (m_id,t_id) VALUES ('$managerId','$technicianId') ";

	if($conn->query($sql2)===TRUE){
	    $response["addSuccess"] = 1;
	}else{
	    $response["addSuccess"] = 0;
	}

   }else{
    $response["success"] = 0;
}

echo json_encode($response);
//以json的形式返回给客户端

$conn->close();
?>
