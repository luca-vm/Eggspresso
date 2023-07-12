<?php
$username = "s2432389";
$password = "s2432389";
$database = "d2432389";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);
$StaffUsername = $_REQUEST["StaffUsername"];
$CustUsername = $_REQUEST["CustUsername"];
$Status = $_REQUEST["Status"];
$OrderTime = $_REQUEST["OrderTime"];
$Restaurant = $_REQUEST["Restaurant"];
$Rating = $_REQUEST["Rating"];

$output=array();
/* Select queries return a resultset */
if ($r = mysqli_query($link, "INSERT INTO ORDERS VALUES('".$CustUsername."','".$StaffUsername."','".$Status."','".$OrderTime."','".$Restaurant."','".$Rating."')")) {
 echo "Records added successfully.";
} else{
    echo "ERROR: Could not execute query. " . mysqli_error($link);
}
mysqli_close($link);
echo json_encode($output);
?>