package com.studentmanagement;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PdfDownloadServlet extends HttpServlet {
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

        if (rollNumber == null || rollNumber.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing rollNumber parameter.");
            return;
        }

        StudentDetails studentDetails = studentDAO.getStudentByRollNumber(rollNumber);
        Student student = studentDetails.getStudent();

        if (student == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student with roll number " + rollNumber + " not found.");
            return;
        }

        List<Marks> marksList = studentDAO.getStudentMarks(student.getId());
        List<Attendance> attendanceList = studentDAO.getStudentAttendance(student.getId());

        // Calculate attendance percentage
        double attendancePercentage = 0.0;
        if (!attendanceList.isEmpty()) {
            long presentDays = attendanceList.stream().filter(a -> "PRESENT".equals(a.getStatus())).count();
            attendancePercentage = (double) presentDays / attendanceList.size() * 100;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"student_profile_" + rollNumber + ".pdf\"");

        try (PdfWriter writer = new PdfWriter(response.getOutputStream());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {


            // Add student image (commented out for now to prevent errors if image is missing)
            // try {
            //     Image image = new Image(ImageDataFactory.create(getServletContext().getRealPath("/images/" + rollNumber + ".jpg")));
            //     document.add(image.setAutoScale(true));
            // } catch (Exception e) {
            //     System.err.println("Could not load image for student " + rollNumber + ": " + e.getMessage());
            // }

            // Add student details
            PdfFont bold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            document.add(new Paragraph("Student Profile").setFont(bold).setFontSize(18));
            document.add(new Paragraph("Name: " + student.getName()));
            document.add(new Paragraph("Roll No: " + student.getRollNumber()));
            document.add(new Paragraph("Branch: " + student.getBranch()));
            document.add(new Paragraph("Division: " + student.getDivision()));
            document.add(new Paragraph(String.format("Attendance: %.2f%%", attendancePercentage)));

            // Add attendance table
            document.add(new Paragraph("Attendance Records").setFont(bold).setFontSize(14));
            Table attendanceTable = new Table(UnitValue.createPercentArray(new float[]{1, 1, 1}));
            attendanceTable.setWidth(UnitValue.createPercentValue(100));
            attendanceTable.setMarginTop(10);

            attendanceTable.addHeaderCell("Date");
            attendanceTable.addHeaderCell("Status");
            attendanceTable.addHeaderCell("Student ID");

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Attendance att : attendanceList) {
                attendanceTable.addCell(att.getDate().format(dateFormatter));
                attendanceTable.addCell(att.getStatus());
                attendanceTable.addCell(String.valueOf(att.getStudentId()));
            }
            document.add(attendanceTable);

            // Add marks table
            document.add(new Paragraph("Marks").setFont(bold).setFontSize(14));
            Table marksTable = new Table(UnitValue.createPercentArray(new float[]{2, 1, 1, 1, 1, 1})); // Subject, Internals, IAT1, IAT2, SemMarks, Total
            marksTable.setWidth(UnitValue.createPercentValue(100));
            marksTable.setMarginTop(10);

            marksTable.addHeaderCell("Subject");
            marksTable.addHeaderCell("Internals");
            marksTable.addHeaderCell("IAT 1");
            marksTable.addHeaderCell("IAT 2");
            marksTable.addHeaderCell("Sem Marks");
            marksTable.addHeaderCell("Total");

            for (Marks marks : marksList) {
                marksTable.addCell(marks.getSubject());
                marksTable.addCell(String.valueOf(marks.getInternals()));
                marksTable.addCell(String.valueOf(marks.getIat1()));
                marksTable.addCell(String.valueOf(marks.getIat2()));
                marksTable.addCell(String.valueOf(marks.getSemMarks()));
                int totalMarks = marks.getInternals() + marks.getIat1() + marks.getIat2() + marks.getSemMarks();
                marksTable.addCell(String.valueOf(totalMarks));
            }
            document.add(marksTable);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
