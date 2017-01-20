<?php 
	$con=mysqli_connect("localhost","root","root");
	$sql="CREATE DATABASE user_info";
	if(mysqli_connect_errno($con)){
		echo "Failed to connect to Mysql: ".mysqli_connect_error();
	}

	$username=$_POST['username'];
	$password=$_POST['password'];
	$result=mysqli_query($con,"SELECT Role FROM table1 where Username='$username' and Password='$password'");
	$row=mysqli_fetch_array($result);
	$data=$row[0];

	if($data){
		echo $data;
	}

	mysqli_close($con);
?>