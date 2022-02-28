<?php

/**
  * Use an HTML form to create a new entry in the
  * employee and location table.
  *
  */

function insert(){
  require "../dbconfig.php";
  require "../common.php";
	try {
		$connection = new PDO($dsn, $username, $password, $options);

		$new_user = array(
		  "firstname" => $_POST['firstname'],
		  "lastname"  => $_POST['lastname'],
		  "age"  => $_POST['age']
		);

		$new_location= array(
			"city" => $_POST['city'],
			"state" => $_POST['state'],
			"country" => $_POST['country']
		);

		$sql = sprintf(
	"INSERT INTO %s (%s) values (%s)",
	"employee",
	implode(", ", array_keys($new_user)),
	":" . implode(", :", array_keys($new_user))
		);

		$statement = $connection->prepare($sql);
		$statement->execute($new_user);
		$sql = sprintf(
	"INSERT INTO %s (%s) values (%s)",
	"location",
	implode(", ", array_keys($new_location)),
	":" . implode(", :", array_keys($new_location))
		);

		$statement = $connection->prepare($sql);
		$statement->execute($new_location);
		if (isset($_POST['submit']) && $statement) 
			echo $_POST['firstname'] . " successfully added to location and employee tables.";
	  } catch(PDOException $error) {
		  #echo $sql . "<br>" . $error->getMessage();
		  echo "SQL Error";
	  }
}
if (isset($_POST['submit'])) {

  if(!is_numeric($_POST['age'])) 
  {
	  echo "Age must be a number";
  }
  elseif (strlen($_POST['state']) > 2)
  {
	echo "State must be only 2 characters";
  }
  else
	  insert();

}
?>

<?php require "templates/header.php"; ?>

<h2>Add a Employee</h2>

<form method="post">
  <label for="firstname">First Name</label>
  <input type="text" name="firstname" id="firstname">
  <label for="lastname">Last Name</label>
  <input type="text" name="lastname" id="lastname">
  <label for="location">Age</label>
  <input type="text" name="age" id="age">
  <label for="city">City</label>
  <input type="text" name="city" id="city">
   <label for="state">State</label>
  <input type="text" name="state" id="state">
  <label for="country">Country</label>
  <input type="text" name="country" id="country">
  <br>
  <input type="submit" name="submit" value="Submit">
</form>

<a href="index.php">Back to home</a>

<?php require "templates/footer.php"; ?>
