<?php
$username = "s2432389";
$password = "s2432389";
$database = "d2432389";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);
$StaffUsername = $_REQUEST["StaffUsername"];
$StaffPassword = $_REQUEST["StaffPassword"];
$AvgRating = $_REQUEST["AvgRating"];

if($r = mysqli_query($link,"INSERT INTO STAFF VALUES('".$StaffUsername."','".$StaffPassword."','".$AvgRating."')")) {
    echo "Records added successfully.";
} else{
    echo "ERROR: Could not execute query. " . mysqli_error($link);
}
mysqli_close($link);
?>