<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>List menu</title>
    <link rel="stylesheet" type="text/css" href="css/displaylist.css">
</head>
<body>
    <div class="navbar">
        <ul>
            <li><a href="displaylist.jsp">To-Do List</a></li>
            <li><a href="Gevent.jsp">Calendar Events</a></li>
            <li><span class="session-attribute">User: <%= session.getAttribute("username") %> (<%= session.getAttribute("userRole") %>)</span></li>
            <li><a href="logout">Logout</a></li>
        </ul>
    </div>
    <div class="container">
        <div id="list">
        </div>
        <div id="completedlist">
        </div>
        <div class="inputitem">
            <div id="overlay"></div>
            <div id="popup">
                <button id="closePopup">X</button>
                <h1>Enter Item Information</h1>
                    <form action="addlistitemservlet" method="post">
                        <label for="itemName">Item Name:</label>
                        <input type="text" id="itemName" name="itemName" required><br><br>

                        <label for="quantity">Quantity:</label>
                        <input type="number" id="quantity" name="quantity" required><br><br>

                        <label for="unitOfMeasurement">Unit of Measurement:</label>
                        <input type="text" id="unit" name="unit" required><br><br>

                        <input type="submit" value="Submit">
                    </form>
            </div>
            </div>
        <c:if test="${sessionScope.userRole == 'admin'}">
            <button id="inputItemButton">Input Item</button>
        </c:if>
    </div>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="scripts/displaylist.js"></script>
</body>
</html>