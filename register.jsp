<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Registration Form</title>
</head>
<body>
<h1>User Registration Form</h1>

<% String errorMessage = (String) request.getAttribute("errorMessage"); %>
<% if (errorMessage != null) { %>
    <div style="color: red; font-weight: bold;">
        <%= errorMessage %>
    </div>
<% } %>

<form id="registration-form" action="register" method="post">
    <table>
        <tr>
            <td>User ID (ABHA Number):</td>
            <td><input type="text" name="userid" required pattern="[0-9]{14}" title="Please enter a valid 14-digit number"></td>
        </tr>
        <tr>
            <td colspan="2"><input type="submit" value="Register"></td>
        </tr>
    </table>
</form>

<div id="duplicate-message" style="display:none;color:red;font-weight:bold;">User ID already exists. Please enter a different User ID.</div>

<script>
    var form = document.getElementById("registration-form");
    var duplicateMessage = document.getElementById("duplicate-message");

    form.addEventListener("submit", function(event) {
        event.preventDefault();
        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'register', true);
        xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
        xhr.onreadystatechange = function() {
            if (xhr.readyState === 4) {
                if (xhr.status === 200) {
                    var response = xhr.responseText;
                    var duplicate = response.trim() === "true";
                    if (duplicate) {
                        duplicateMessage.style.display = "block";
                    } else {
                        form.submit();
                    }
                } else {
                    alert('Error: ' + xhr.statusText);
                }
            }
        };
        xhr.send('userid=' + encodeURIComponent(form.userid.value));
    });
</script>
</body>
</html>
