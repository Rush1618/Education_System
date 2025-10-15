<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%
    // Check if user is logged in
    if (session.getAttribute("user") == null) {
        response.sendRedirect(request.getContextPath() + "/login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Student Dashboard</title>
    <style>
        body { font-family: sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; }
        .header { background-color: #333; color: white; padding: 1rem; display: flex; justify-content: space-between; align-items: center; }
        .header h1 { margin: 0; }
        .header a { color: white; text-decoration: none; }
        .container { padding: 2rem; }
        .controls { background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); margin-bottom: 2rem; }
        .form-group { display: inline-block; margin-right: 1rem; }
        .form-group label { margin-right: 0.5rem; }
        .form-group select, .btn { padding: 0.5rem 1rem; border-radius: 4px; border: 1px solid #ccc; }
        .btn { background-color: #007bff; color: white; cursor: pointer; text-decoration: none; }
        .btn-pdf { background-color: #dc3545; }
        .student-table { background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
        h2 { border-bottom: 2px solid #eee; padding-bottom: 0.5rem; }
        table { width: 100%; border-collapse: collapse; margin-top: 1rem; }
        th, td { padding: 0.75rem; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #f2f2f2; }
    </style>
</head>
<body>

    <div class="header">
        <h1>Student Management Dashboard</h1>
        <a href="${pageContext.request.contextPath}/logout">Logout</a>
    </div>

    <div class="container">
        <div class="controls">
            <h2>Select View</h2>
            <form action="${pageContext.request.contextPath}/students" method="get">
                <div class="form-group">
                    <label for="branch">Branch:</label>
                    <select id="branch" name="branch" onchange="this.form.submit()">
                        <option value="">-- Select Branch --</option>
                        <c:forEach var="b" items="${branches}">
                            <option value="${b}" ${param.branch == b ? 'selected' : ''}>${b}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-group">
                    <label for="division">Division:</label>
                    <select id="division" name="division">
                        <option value="">-- Select Division --</option>
                        <c:forEach var="d" items="${divisions}">
                            <option value="${d}" ${param.division == d ? 'selected' : ''}>${d}</option>
                        </c:forEach>
                    </select>
                </div>
                <button type="submit" class="btn">Show Students</button>

            </form>
        </div>

        <div class="student-table">
            <h2>Student Data</h2>
            <c:choose>
                <c:when test="${not empty studentDetails}">
                    <table>
                        <thead>
                            <tr>
                                <th>Roll Number</th>
                                <th>Name</th>
                                <th>Marks (%)</th>
                                <th>Attendance (%)</th>
                                <th>Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="detail" items="${studentDetails}">
                                <tr>
                                    <td><c:out value="${detail.student.rollNumber}" /></td>
                                    <td><a href="${pageContext.request.contextPath}/student-details?rollNumber=${detail.student.rollNumber}"><c:out value="${detail.student.name}" /></a></td>
                                    <td><fmt:formatNumber value="${detail.marksPercentage}" maxFractionDigits="2" />%</td>
                                    <td><fmt:formatNumber value="${detail.attendancePercentage}" maxFractionDigits="2" />%</td>
                                    <td><a href="download?rollNumber=${detail.student.rollNumber}" class="btn btn-pdf">Download Profile</a></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p>Please select a branch and division to see student data.</p>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

</body>
</html>
