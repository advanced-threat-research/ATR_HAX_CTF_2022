<?php

/**
  * Function to query information based on
  * a parameter: in this case, last name.
  *
  */

if (isset($_POST['submit'])) {
  try {
    require "../dbconfig.php";
    require "../common.php";

    $connection = new PDO($dsn, $username, $password, $options);
    $params = [];
    $setStr = "";
    foreach ($_POST as $key => $value)
	{
	    if ($key != "submit")
	    {
	       $whereStr .= $key." = :".$key.","; 
	       $params[$key] = $value; 
            }

    }
    $whereStr = rtrim($whereStr, ",");    
    $sql = "SELECT *
    FROM employee
    WHERE $whereStr";
    $statement = $connection->prepare($sql);
    $statement->execute($params);
 
    $result = $statement->fetchAll();
  } catch(PDOException $error) {
       echo $sql . "<br>" . $error->getMessage();
       echo "Sql Error";
  }
}
?>
<?php require "templates/header.php"; ?>

<?php
if (isset($_POST['submit'])) {
  if ($result && $statement->rowCount() > 0) { ?>
    <h2>Results from employee table</h2>

    <table>
      <thead>
<tr>
  <th>First Name</th>
  <th>Last Name</th>
  <th>Location ID</th>
  <th>Age</th>
  <th>Date</th>
</tr>
      </thead>
      <tbody>
  <?php foreach ($result as $row) { ?>
      <tr>
<td><?php echo escape($row["firstname"]); ?></td>
<td><?php echo escape($row["lastname"]); ?></td>
<td><?php echo escape($row["idlocation"]); ?></td>
<td><?php echo escape($row["age"]); ?></td>
<td><?php echo escape($row["date"]); ?></td>
      </tr>
    <?php } ?>
      </tbody>
  </table>
  <?php } else { ?>
    > No results found for <?php echo escape($_POST['location']); ?>.
  <?php }
} ?>

<h2>Find employee based on last name</h2>

<form method="post">
  <label for="lastname">Last Name</label>
  <input type="text" id="lastname" name="lastname">
  <input type="submit" name="submit" value="View Results">
</form>

<a href="index.php">Back to home</a>

<?php require "templates/footer.php"; ?>
