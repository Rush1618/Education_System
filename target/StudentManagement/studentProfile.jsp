<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Student Profile</title>
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
            background: white;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        }
        .profile-header {
            display: flex;
            align-items: center;
            margin-bottom: 2rem;
            padding-bottom: 1.5rem;
            border-bottom: 1px solid #eee;
        }
        .student-image {
            width: 120px;
            height: 120px;
            border-radius: 50%;
            object-fit: cover;
            margin-right: 2rem;
            border: 3px solid #3498db;
            box-shadow: 0 2px 6px rgba(0,0,0,0.1);
        }
        .student-info {
            flex-grow: 1;
        }
        .student-info h2 {
            color: #2c3e50;
            margin-top: 0;
            margin-bottom: 0.5rem;
            font-size: 2rem;
        }
        .student-info p {
            margin: 0.3rem 0;
            color: #555;
            font-size: 1.1rem;
        }
        h3 {
            color: #2c3e50;
            border-bottom: 2px solid #3498db;
            padding-bottom: 0.5rem;
            margin-top: 2rem;
            margin-bottom: 1.5rem;
            font-size: 1.6rem;
        }
        .calendar-table, .marks-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 1rem;
            box-shadow: 0 2px 8px rgba(0,0,0,0.05);
            border-radius: 8px;
            overflow: hidden; /* Ensures rounded corners apply to table */
        }
        .calendar-table th, .calendar-table td,
        .marks-table th, .marks-table td {
            border: 1px solid #e0e6ed; /* Lighter border */
            padding: 12px 15px;
            text-align: center;
            font-size: 0.95rem;
        }
        .calendar-table th, .marks-table th {
            background-color: #f8f9fa;
            font-weight: 600;
            color: #444;
        }
        .calendar-table td {
            height: 40px; /* Fixed height for calendar cells */
            width: 14.28%; /* 100/7 */
        }
        .calendar-table .present {
            background-color: #d4edda; /* Light green */
            color: #155724;
        }
        .calendar-table .absent {
            background-color: #f8d7da; /* Light red */
            color: #721c24;
        }
        .calendar-table .holiday {
            background-color: #e2e3e5; /* Light gray */
            color: #6c757d;
        }
        .marks-table tbody tr:nth-child(even) {
            background-color: #fcfcfc;
        }
        .marks-table tbody tr:hover {
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
            margin-top: 2rem;
            display: inline-block;
            text-decoration: none;
        }
        .btn:hover {
            background-color: #2980b9;
            transform: translateY(-2px);
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <img class="student-image" src="images/${student.student.rollNumber}.jpg" alt="Student Image">
            <div class="student-info">
                <h2>${student.student.name}</h2>
                <p>Roll No: ${student.student.rollNumber}</p>
                <p>Branch: ${student.student.branch}</p>
                <p>Division: ${student.student.division}</p>
                <p>Attendance: ${String.format('%.2f', student.attendancePercentage)}%</p>
            </div>
        </div>

        <div class="calendar">
            <h3>Attendance Calendar</h3>
            <table class="calendar-table">
                <thead>
                    <tr>
                        <th>Sun</th>
                        <th>Mon</th>
                        <th>Tue</th>
                        <th>Wed</th>
                        <th>Thu</th>
                        <th>Fri</th>
                        <th>Sat</th>
                    </tr>
                </thead>
                <tbody>
                    <jsp:useBean id="currentDate" class="java.util.Date" />
                    <jsp:useBean id="calendar" class="java.util.GregorianCalendar" />
                    <% calendar.setTime(currentDate); %>
                    <% int month = calendar.get(java.util.Calendar.MONTH); %>
                    <% int year = calendar.get(java.util.Calendar.YEAR); %>
                    <% calendar.set(java.util.Calendar.DAY_OF_MONTH, 1); %>
                    <% int firstDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK); %>
                    <% int daysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH); %>

                    <tr>
                        <% for (int i = 1; i < firstDayOfWeek; i++) { %>
                            <td></td>
                        <% } %>
                        <% for (int day = 1; day <= daysInMonth; day++) { %>
                            <% calendar.set(java.util.Calendar.DAY_OF_MONTH, day); %>
                            <% String dayStatus = ""; %>
                            <% boolean isHoliday = false; %>
                            <c:forEach var="holiday" items="${holidays}">
                                <c:if test="${holiday.holidayDate.dayOfMonth == day && holiday.holidayDate.monthValue -1 == month && holiday.holidayDate.year == year}">
                                    <% isHoliday = true; %>
                                </c:if>
                            </c:forEach>
                            <c:forEach var="attendance" items="${student.attendance}">
                                <c:if test="${attendance.attendanceDate.dayOfMonth == day && attendance.attendanceDate.monthValue -1 == month && attendance.attendanceDate.year == year}">
                                    <c:choose>
                                        <c:when test="${attendance.status == 'PRESENT'}">
                                            <% dayStatus = "present"; %>
                                        </c:when>
                                        <c:otherwise>
                                            <% dayStatus = "absent"; %>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>
                            </c:forEach>
                            <td class="<%= isHoliday ? "holiday" : dayStatus %>"><%= day %></td>
                            <% if ((firstDayOfWeek + day - 1) % 7 == 0) { %>
                                </tr><tr>
                            <% } %>
                        <% } %>
                        <% while ((firstDayOfWeek + daysInMonth -1) % 7 != 0) { %>
                            <td></td>
                            <% daysInMonth++; %>
                        <% } %>
                    </tr>
                </tbody>
            </table>
        </div>

        <div class="marks-table">
            <h3>Marks</h3>
            <table class="marks-table">
                <thead>
                    <tr>
                        <th>Subject</th>
                        <th>IAT 1 Marks</th>
                        <th>IAT 2 Marks</th>
                        <th>Total IAT Marks</th>
                        <th>Internal Marks</th>
                        <th>Sem Marks</th>
                        <th>Total Marks</th>
                        <th>Percentage</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="marks" items="${student.marks}">
                        <tr>
                            <td>${marks.subjectName}</td>
                            <td>${marks.iat1}</td>
                            <td>${marks.iat2}</td>
                            <td>${marks.iat1 + marks.iat2}</td>
                            <td>${marks.internals}</td>
                            <td>${marks.semMarks}</td>
                            <td>${marks.iat1 + marks.iat2 + marks.internals + marks.semMarks}</td>
                            <td>${String.format('%.2f', ((marks.iat1 + marks.iat2 + marks.internals + marks.semMarks) / 200.0) * 100)}%</td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>

        <button onclick="window.location.href='download?rollNumber=${student.student.rollNumber}'">Download Profile</button>
    </div>

<%
    // Debugging: Print student.rollNumber to server console
    System.out.println("studentProfile.jsp: student.rollNumber = " + (request.getAttribute("student") != null ? ((com.studentmanagement.StudentDetails)request.getAttribute("student")).getStudent().getRollNumber() : "null"));
%>
</body>
</html>

<%
    // Debugging: Print student.rollNumber to server console
    System.out.println("studentProfile.jsp: student.rollNumber = " + (request.getAttribute("student") != null ? ((com.studentmanagement.StudentDetails)request.getAttribute("student")).getStudent().getRollNumber() : "null"));
%>