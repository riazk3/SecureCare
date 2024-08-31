<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>
</head>
<body>
    <h1>Login</h1>

    <form id="login-form" action="user" method="post">
        <table>
            <tr>
                <td><label for="bio1">Bio1:</label></td>
                <td><input type="text" id="bio1" name="bio1" required></td>
            </tr>
            <tr>
                <td><label for="bio2">Bio2:</label></td>
                <td><input type="text" id="bio2" name="bio2" required></td>
            </tr>
            <tr>
                <td colspan="2"><input type="submit" value="Login"></td>
            </tr>
        </table>
    </form>

</body>
</html>
