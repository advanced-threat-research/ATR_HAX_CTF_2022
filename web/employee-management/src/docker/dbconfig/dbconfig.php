<?php
    $host = 'localhost';
    $dbname = 'employeeInfo';
    $username = 'root';
    $password = 'rootpassword';
    $dsn = "mysql:host=$host;dbname=$dbname"; // will use later
    $options    = array(
                PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION
              );
