package com.studentmanagement;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class StudentDetailServlet extends HttpServlet {
    private StudentDAO studentDAO;

    @Override
    public void init() throws ServletException {
        studentDAO = new StudentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getSession(false) == null || request.getSession(false).getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String rollNumber = request.getParameter("rollNumber");

        if (rollNumber == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing rollNumber parameter.");
            return;
        }

        StudentDetails studentDetails = studentDAO.getStudentByRollNumber(rollNumber);

        if (studentDetails.getStudent() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found.");
            return;
        }

        // Get marks
        List<Marks> marksList = studentDAO.getStudentMarks(studentDetails.getStudent().getId());
        studentDetails.setMarks(marksList);

        // Calculate marks percentage
        double totalMarksObtained = 0;
        double totalMaxMarks = 0;
        for (Marks marks : marksList) {
            totalMarksObtained += marks.getIat1() + marks.getIat2() + marks.getInternals() + marks.getSemMarks();
            totalMaxMarks += 200; // Assuming IAT1(50) + IAT2(50) + Internals(50) + SemMarks(50) = 200
        }
        if (totalMaxMarks > 0) {
            studentDetails.setMarksPercentage((totalMarksObtained / totalMaxMarks) * 100);
        }

        // Get attendance
        List<Attendance> attendanceList = studentDAO.getStudentAttendance(studentDetails.getStudent().getId());
        studentDetails.setAttendance(attendanceList);

        // Calculate attendance percentage
        long presentDays = attendanceList.stream().filter(a -> "PRESENT".equals(a.getStatus())).count();
        long totalAttendanceDays = attendanceList.size();
        if (totalAttendanceDays > 0) {
            studentDetails.setAttendancePercentage(((double) presentDays / totalAttendanceDays) * 100);
        }

        // Get holidays
        List<Holiday> holidays = studentDAO.getAllHolidays();
        request.setAttribute("holidays", holidays);

        request.setAttribute("student", studentDetails);
        request.getRequestDispatcher("studentProfile.jsp").forward(request, response);
    }
}
