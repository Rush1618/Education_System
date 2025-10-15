<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Check if user is logged in
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return; // Stop processing the page
    }
    String userRole = (String) session.getAttribute("role");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #eef2f7; /* Light blue-gray background */
            margin: 0;
            padding: 0;
            color: #333;
        }
        .header {
            background-color: #2c3e50; /* Dark blue header */
            color: white;
            padding: 1.2rem 2rem;
            text-align: center;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 4px rgba(0,0,0,0.1);
        }
        .header h1 {
            margin: 0;
            font-size: 1.8rem;
        }
        .header a {
            color: #ecf0f1; /* Light gray for links */
            text-decoration: none;
            margin-left: 1.5rem;
            font-weight: 500;
            transition: color 0.3s ease;
        }
        .header a:hover {
            color: #3498db; /* Blue on hover */
        }
        .container {
            padding: 2rem;
            max-width: 1200px;
            margin: 2rem auto;
        }
        .card {
            background: white;
            padding: 1.5rem;
            border-radius: 10px; /* More rounded corners */
            box-shadow: 0 4px 12px rgba(0,0,0,0.08); /* Softer, larger shadow */
            margin-bottom: 2rem;
            transition: transform 0.3s ease;
        }
        .card:hover {
            transform: translateY(-5px); /* Slight lift on hover */
        }
        h2 {
            color: #2c3e50;
            border-bottom: 2px solid #3498db; /* Blue underline */
            padding-bottom: 0.8rem;
            margin-top: 0;
            font-size: 1.5rem;
        }
        .form-group {
            display: inline-block;
            margin-right: 1.5rem;
            margin-bottom: 1rem;
            vertical-align: middle;
        }
        .form-group label {
            margin-right: 0.8rem;
            font-weight: 600;
            color: #555;
        }
        .form-group select,
        .form-group input[type="text"],
        .form-group input[type="password"],
        .btn {
            padding: 0.6rem 1.2rem;
            border-radius: 6px; /* Slightly more rounded */
            border: 1px solid #bdc3c7; /* Lighter border */
            font-size: 1rem;
            transition: all 0.3s ease;
        }
        .form-group select:focus,
        .form-group input[type="text"]:focus,
        .form-group input[type="password"]:focus {
            border-color: #3498db;
            box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
            outline: none;
        }
        .btn {
            background-color: #3498db; /* Primary blue */
            color: white;
            cursor: pointer;
            border: none;
            font-weight: 600;
            padding: 0.7rem 1.5rem;
        }
        .btn:hover {
            background-color: #2980b9; /* Darker blue on hover */
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .btn-danger {
            background-color: #e74c3c; /* Red for danger actions */
        }
        .btn-danger:hover {
            background-color: #c0392b;
        }
        .student-table table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
        }
        .student-table th, .student-table td {
            border: 1px solid #ecf0f1; /* Light border for table cells */
            padding: 12px 15px;
            text-align: left;
        }
        .student-table th {
            background-color: #f8f9fa; /* Light header background */
            font-weight: 600;
            color: #555;
        }
        .student-table tbody tr:nth-child(even) {
            background-color: #fcfcfc; /* Zebra striping */
        }
        .student-table tbody tr:hover {
            background-color: #f0f8ff; /* Light blue on row hover */
        }</style>
</head>
<body>

    <div class="header">
        <h1>Student Management Dashboard</h1>
        <% if ("SUPERADMIN".equals(userRole)) { %>
            <a href="userManagement" style="float: left; margin-right: 1rem;">User Management</a>
        <% } %>
        <a href="logout">Logout</a>
    </div>

    <div class="container">
        <div class="controls">
            <h2>Select View</h2>
            <form action="students" method="get">
                <div class="form-group">
                    <label for="branch">Branch:</label>
                    <select id="branch" name="branch">
                        <!-- Options will be loaded dynamically later -->
                        <option value="cse">Computer Science</option>
                        <option value="mech">Mechanical</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="division">Division:</label>
                    <select id="division" name="division">
                        <!-- Options will be loaded dynamically later -->
                        <option value="a">A</option>
                        <option value="b">B</option>
                    </select>
                </div>
                <button type="submit" class="btn">Show Students</button>
            </form>
        </div>

        <div class="student-table">
            <h2>Student Data</h2>
            <!-- Student data table will be displayed here by the StudentServlet -->
            <p>Please select a branch and division to see student data.</p>
        </div>
    </div>

</body>
</html>
