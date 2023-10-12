<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Create Event</title>
    <link rel="stylesheet" type="text/css" href="css/calendar.css">
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
    <div class="this">
    <h1>Calendar Events</h1>
        <div id="events">
        </div>
    <div class="createevents">
    <div id="overlay"></div>
    <div id="popup">
        <button id="closePopup">X</button>
        <h1>Create Event</h1>
            <form action="createeventservlet" method="post">
                <label for="summary">Summary:</label>
                <input type="text" id="summary" name="summary" required><br>

                <label for="description">Description:</label>
                <input type="text" id="description" name="description"><br>

                <label for="startDateTime">Start Date and Time:</label>
                <input type="datetime-local" id="startDateTime" name="startDateTime" required><br>

                <label for="endDateTime">End Date and Time:</label>
                <input type="datetime-local" id="endDateTime" name="endDateTime" required><br>

                <label for="participants">Participants (comma-separated email addresses):</label>
                <input type="text" id="participants" name="participants"><br>

                <input type="submit" value="Create Event">
            </form>
    </div>
    </div>
    <c:if test="${sessionScope.userRole == 'admin'}">
        <button id="createEventButton">Create Event</button>
    </c:if>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="scripts/Gevent.js"></script>
    </div>
</body>
</html>