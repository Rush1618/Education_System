<!DOCTYPE html>
<html>
<head>
    <title>Admin Login</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            background-color: #eef2f7; /* Light blue-gray background */
            margin: 0;
            padding: 0;
            color: #333;
        }
        .login-container {
            background: white;
            padding: 2.5rem;
            border-radius: 10px; /* More rounded corners */
            box-shadow: 0 8px 20px rgba(0,0,0,0.1); /* Softer, larger shadow */
            width: 100%;
            max-width: 400px; /* Max width for the login form */
            box-sizing: border-box; /* Include padding in width */
        }
        h2 {
            text-align: center;
            color: #2c3e50; /* Dark blue heading */
            margin-bottom: 1.5rem;
            font-size: 1.8rem;
        }
        .input-group {
            margin-bottom: 1.2rem;
        }
        .input-group label {
            display: block;
            margin-bottom: 0.6rem;
            font-weight: 600;
            color: #555;
            font-size: 0.95rem;
        }
        .input-group input {
            width: calc(100% - 1.2rem); /* Adjust for padding */
            padding: 0.7rem;
            border: 1px solid #bdc3c7; /* Lighter border */
            border-radius: 6px; /* Slightly more rounded */
            font-size: 1rem;
            transition: all 0.3s ease;
        }
        .input-group input:focus {
            border-color: #3498db;
            box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
            outline: none;
        }
        .btn {
            width: 100%;
            padding: 0.8rem;
            border: none;
            border-radius: 6px;
            background-color: #3498db; /* Primary blue */
            color: white;
            font-size: 1.1rem;
            cursor: pointer;
            font-weight: 600;
            transition: background-color 0.3s ease, transform 0.2s ease;
            margin-top: 1rem;
        }
        .btn:hover {
            background-color: #2980b9; /* Darker blue on hover */
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .error {
            color: #e74c3c; /* Red for error messages */
            text-align: center;
            margin-top: 1.5rem;
            font-weight: 500;
        }</style>
</head>
<body>
    <div class="login-container">
        <h2>Teacher Login</h2>
        <form action="login" method="post">
            <div class="input-group">
                <label for="username">Username</label>
                <input type="text" id="username" name="username" required>
            </div>
            <div class="input-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
            </div>
            <button type="submit" class="btn">Login</button>
        </form>
        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
        %>
            <p class="error"><%= error %></p>
        <%
            }
        %>
    </div>
</body>
</html>
