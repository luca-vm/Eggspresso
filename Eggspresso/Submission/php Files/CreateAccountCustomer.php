<?php
$username = "s2432389";
$password = "s2432389";
$database = "d2432389";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);
$CustUsername = $_REQUEST["CustUsername"];
$CustName = $_REQUEST["CustName"];
$CustPassword = $_REQUEST["CustPassword"];

if($r = mysqli_query($link,"INSERT INTO CUSTOMER VALUES('".$CustUsername."','".$CustName."','".$CustPassword."')")) {
    echo "Records added successfully.";
} else{
    echo "ERROR: Could not able to execute $sql. " . mysqli_error($link);
}
mysqli_close($link);
?>