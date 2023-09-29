<!DOCTYPE html>
<html>
<head>
    <title>Item Information Form</title>
</head>
<body>
    <h2>Enter Item Information</h2>
    <form action="addlistitemservlet" method="post">
        <label for="itemName">Item Name:</label>
        <input type="text" id="itemName" name="itemName" required><br><br>

        <label for="quantity">Quantity:</label>
        <input type="number" id="quantity" name="quantity" required><br><br>

        <label for="unitOfMeasurement">Unit of Measurement:</label>
        <input type="text" id="unit" name="unit" required><br><br>

        <input type="submit" value="Submit">
    </form>
    <a href="index.jsp">Back to Home Page</a>
</body>
</html>