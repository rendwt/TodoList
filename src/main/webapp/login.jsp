<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
    <h2>User Login</h2>
    <form action="login" method="post">
        <label for="username">Username:</label>
        <input type="text" id="username" name="username" required><br><br>

        <label for="password">Password:</label>
        <input type="password" id="password" name="password" required><br><br>

        <input type="submit" value="Login">
    </form>
    <% String loginError = (String) session.getAttribute("loginError"); %>
        <% if (loginError != null) { %>
            <p style="color: red;"><%= loginError %></p>
        <% } %>
     <p>Not registered? <a href="register.jsp">Register</a></p>
</body>
</html>
