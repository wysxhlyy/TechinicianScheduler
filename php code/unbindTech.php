<?php

error_reporting(E_ALL ^ E_NOTICE);

$con = mysqli_connect("146.148.28.194","root","wuyusheng","techSchedulerDB");


 $id=$_POST['techId'];


$response = array();




$sql="DELETE FROM manageTechnician WHERE t_id='$id' ";

$deleteTask=mysqli_query($con,$sql);

if($deleteTask){
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
