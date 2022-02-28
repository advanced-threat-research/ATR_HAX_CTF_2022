<?php
	setcookie("TEMPLATE", 'header.php');
?>

<html>
<title>Dr. Trav's Key Validator</title>
<body bgcolor="white">
<?php
	$template = 'header.php'; // Not technically needed now, but I guess a good default if the TEMPLATE cookie somehow gets removed
	if ( isset( $_COOKIE['TEMPLATE'] ) )
	{
		$template = $_COOKIE['TEMPLATE'];
	}
	include ( "templates/" . $template ); //directory traversal via concatenation
?>

<h3>
You've accessed my software validation page, where you can input your  license key, and unlock varying levels of my latest CAT scan medical software repository. 
<br><br>
<!--<form action="<?php echo( $self ); ?>" method = "post">-->

<?php
   if ( isset($_POST['submit']))
   {
	   $keyvalue=escapeshellcmd($_POST["validate"]);
	   //command injection probably possible with $keyvalue, have i mitigated this with escapeshellcmd?? Come back to check this
	   $key_checker_output = shell_exec("cd ../keygen && ./key_checker $keyvalue");
	   echo $key_checker_output;
   }
?>
<form action="" method="post">

Validate Key:
<input type="text" name="validate"><br><br>
<input type="submit" name="submit" value="submit">

<!--Note: Remember to log keys checked to log files in /var/log/skeletonkey.log-->

</form>


</h3>
</body>
</html>
