<?php
$username = "s2432389";
$password = "s2432389";
$database = "d2432389";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);
$CustUsername = $_REQUEST["CustUsername"];
$output=array();
/* Select queries return a resultset */
if ($r = mysqli_query($link, "SELECT CustPassword from CUSTOMER where CustUsername='".$CustUsername."'")) {
while ($row=$r->fetch_assoc()){
$output[]=$row;
}
}
mysqli_close($link);
echo json_encode($output);
?>