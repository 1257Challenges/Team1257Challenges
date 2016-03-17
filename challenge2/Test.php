<!DOCTYPE HTML> 
<html>
<head>
<link href='https://fonts.googleapis.com/css?family=Cantarell|Quicksand:400,300,700' rel='stylesheet' type='text/css'>
<link rel="stylesheet" href="main.css" type="text/css" />

</head>
<body> 

<?php
// define variables and set to empty values
$nameErr = $numberErr = "";
$name = $number = "";

if ($_SERVER["REQUEST_METHOD"] == "POST") {
   if (empty($_POST["name"])) {
     $nameErr = "Competition is required";
   } else {
     $name = test_input($_POST["name"]);
     // check if name only contains letters and whitespace
     if (!preg_match("/^[a-zA-Z ]*$/",$name)) {
       $nameErr = "Only letters and white space allowed"; 
     }
   }
   
   if (empty($_POST["number"])) {
     $numberErr = "Number is required";
   } else {
     $number = test_input($_POST["number"]);
     // check if name only contains letters and whitespace
   }
  

   if (empty($_POST["comment"])) {
     $comment = "";
   } else {
     $comment = test_input($_POST["comment"]);
   }

 
}

function test_input($data) {
   $data = trim($data);
   $data = stripslashes($data);
   $data = htmlspecialchars($data);
   return $data;
}
?>

<h2>Team 1257 Challenge App</h2>
<form method="post" action="<?php echo htmlspecialchars($_SERVER["PHP_SELF"]);?>">
   Competition: <input type="text" name="competition" value="<?php echo $name;?>">
   <span id="competition-clarify" class="error clarify">*<?php echo $nameErr;?></span>
   <br><br>
   Number: <input type="text" name="number" value="<?php echo $number;?>">
   <span id="num-clarify" class="error clarify">*<?php echo $numberErr;?></span>
   <br><br>
   <input type="submit" name="submit" value="Submit"> 
</form>

<?php
echo "<h2>Your Output:</h2>";
//echo "$name $number";
//echo "<br>";

echo exec('./teaminfo.sh $name $number');
?>

</body>
