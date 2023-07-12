<?php
$username = "s2432389";
$password = "s2432389";
$database = "d2432389";
$link = mysqli_connect("127.0.0.1", $username, $password, $database);
$OrderNo = $_REQUEST["OrderNo"];
$Rating = $_REQUEST["Rating"];
$output=array();
/* Select queries return a resultset */
if ($r = mysqli_query($link, "UPDATE ORDERS SET Rating='".$Rating."'  WHERE OrderNo='".$OrderNo."'")) {
echo "Rating updated successfully.";
} else{
    echo "ERROR: Could not execute query. " . mysqli_error($link);
}
mysqli_close($link);
?>