<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>User Management</title>
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
        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            border-radius: 8px;
            overflow: hidden;
        }
        th, td {
            border: 1px solid #e0e6ed;
            padding: 12px 15px;
            text-align: left;
            font-size: 0.95rem;
        }
        th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #444;
        }
        tbody tr:nth-child(even) {
            background-color: #fcfcfc;
        }
        tbody tr:hover {
            background-color: #f0f8ff;
        }
        .btn {
            padding: 0.7rem 1.5rem;
            border-radius: 6px;
            border: none;
            background-color: #3498db;
            color: white;
            cursor: pointer;
            font-size: 1rem;
            font-weight: 600;
            transition: background-color 0.3s ease, transform 0.2s ease;
            text-decoration: none;
            display: inline-block;
            margin-right: 0.5rem;
        }
        .btn:hover {
            background-color: #2980b9;
            transform: translateY(-2px);
        }
        .btn-danger {
            background-color: #e74c3c;
        }
        .btn-danger:hover {
            background-color: #c0392b;
        }
        .form-group {
            margin-bottom: 1rem;
        }
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            font-weight: 600;
            color: #555;
        }
        .form-group input[type="text"],
        .form-group input[type="password"],
        .form-group select {
            width: calc(100% - 1.2rem); /* Adjust for padding */
            padding: 0.6rem;
            border: 1px solid #bdc3c7;
            border-radius: 6px;
            font-size: 1rem;
            transition: all 0.3s ease;
        }
        .form-group input[type="text"]:focus,
        .form-group input[type="password"]:focus,
        .form-group select:focus {
            border-color: #3498db;
            box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
            outline: none;
        }
        .form-actions {
            margin-top: 1.5rem;
            display: flex;
            gap: 0.5rem;
        }</style>
</head>
<body>

    <div class="header">
        <h1>User Management</h1>
        <a href="dashboard.jsp">Dashboard</a>
        <a href="logout">Logout</a>
    </div>

    <div class="container">
        <div class="card">
            <h2>Add New User</h2>
            <form action="userManagement" method="post">
                <input type="hidden" name="action" value="add">
                <div class="form-group">
                    <label for="username">Username:</label>
                    <input type="text" id="username" name="username" required>
                </div>
                <div class="form-group">
                    <label for="password">Password:</label>
                    <input type="password" id="password" name="password" required>
                </div>
                <div class="form-group">
                    <label for="role">Role:</label>
                    <select id="role" name="role">
                        <option value="STUDENT">STUDENT</option>
                        <option value="TEACHER">TEACHER</option>
                        <option value="ADMIN">ADMIN</option>
                        <option value="SUPERADMIN">SUPERADMIN</option>
                    </select>
                </div>
                <div class="form-actions">
                    <button type="submit" class="btn">Add User</button>
                </div>
            </form>
        </div>

        <div class="card">
            <h2>Existing Users</h2>
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Role</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${users}">
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.username}</td>
                            <td>${user.role}</td>
                            <td>
                                <a href="userManagement?action=edit&id=${user.id}" class="btn">Edit</a>
                                <a href="userManagement?action=delete&id=${user.id}" class="btn btn-danger" onclick="return confirm('Are you sure you want to delete this user?');">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <c:if test="${user != null && user.id != 0}">
            <div class="card">
                <h2>Edit User: ${user.username}</h2>
                <form action="userManagement" method="post">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" value="${user.id}">
                    <div class="form-group">
                        <label for="editUsername">Username:</label>
                        <input type="text" id="editUsername" name="username" value="${user.username}" required>
                    </div>
                    <div class="form-group">
                        <label for="editPassword">Password (leave blank to keep current):</label>
                        <input type="password" id="editPassword" name="password" value="${user.password}"> <%-- Pre-filling password is bad practice, but for demo purposes --%>
                    </div>
                    <div class="form-group">
                        <label for="editRole">Role:</label>
                        <select id="editRole" name="role">
                            <option value="STUDENT" <c:if test="${user.role == 'STUDENT'}">selected</c:if>>STUDENT</option>
                            <option value="TEACHER" <c:if test="${user.role == 'TEACHER'}">selected</c:if>>TEACHER</option>
                            <option value="ADMIN" <c:if test="${user.role == 'ADMIN'}">selected</c:if>>ADMIN</option>
                            <option value="SUPERADMIN" <c:if test="${user.role == 'SUPERADMIN'}">selected</c:if>>SUPERADMIN</option>
                        </select>
                    </div>
                    <div class="form-actions">
                        <button type="submit" class="btn">Update User</button>
                        <a href="userManagement" class="btn">Cancel</a>
                    </div>
                </form>
            </div>
        </c:if>
    </div>

</body>
</html>
