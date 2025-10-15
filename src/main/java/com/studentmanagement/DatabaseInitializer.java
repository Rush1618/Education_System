package com.studentmanagement;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@WebListener
public class DatabaseInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Class.forName("org.h2.Driver");
            Connection conn = DriverManager.getConnection("jdbc:h2:~/studentdb", "sa", "");
            Statement stmt = conn.createStatement();

            // --- CREATE TABLES (IF NOT EXISTS) ---
            stmt.execute("CREATE TABLE IF NOT EXISTS Users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(50) NOT NULL)"); // e.g., ADMIN, TEACHER, STUDENT

            stmt.execute("CREATE TABLE IF NOT EXISTS Student (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255), " +
                    "rollNumber VARCHAR(50) UNIQUE, " +
                    "branch VARCHAR(100), " +
                    "division VARCHAR(10))");

            stmt.execute("CREATE TABLE IF NOT EXISTS Subject (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) UNIQUE, " +
                    "branch VARCHAR(100))");

            stmt.execute("CREATE TABLE IF NOT EXISTS Marks (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "studentId INT, " +
                    "subjectId INT, " +
                    "internals INT, " +
                    "iat1 INT, " +
                    "iat2 INT, " + // New column
                    "semMarks INT, " +
                    "FOREIGN KEY (studentId) REFERENCES Student(id) ON DELETE CASCADE, " +
                    "FOREIGN KEY (subjectId) REFERENCES Subject(id) ON DELETE CASCADE, " +
                    "UNIQUE(studentId, subjectId))"); // Prevent duplicate marks for same student/subject

            stmt.execute("CREATE TABLE IF NOT EXISTS Attendance (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "studentId INT, " +
                    "attendanceDate DATE, " +
                    "status VARCHAR(10), " + // PRESENT, ABSENT
                    "FOREIGN KEY (studentId) REFERENCES Student(id) ON DELETE CASCADE, " +
                    "UNIQUE(studentId, attendanceDate))"); // Prevent duplicate attendance for same student/date
            
            stmt.execute("CREATE TABLE IF NOT EXISTS Holidays (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "holidayDate DATE UNIQUE, " +
                    "description VARCHAR(255))");


            // --- INSERT SAMPLE DATA (IF NOT EXISTS) ---

            // Superadmin User
            if (!userExists(conn, "superadmin")) {
                stmt.execute("INSERT INTO Users (username, password, role) VALUES ('superadmin', 'admin123', 'SUPERADMIN')");
            }

            // Students
            List<Map<String, Object>> students = new ArrayList<>();
            String[] studentBranches = {"Computer Science", "CO", "ESE", "AIML", "IT", "Mechanical"};
            int studentIdCounter = 1;

            for (String branch : studentBranches) {
                for (int i = 1; i <= 10; i++) {
                    String rollNumber = branch.substring(0, Math.min(branch.length(), 3)).toUpperCase() + String.format("%02d", i);
                    if (!studentExists(conn, rollNumber)) {
                        String studentName = "Student_" + branch.replace(" ", "_") + String.format("%02d", i);
                        String division = (i <= 5) ? "A" : "B";
                        stmt.execute("INSERT INTO Student (id, name, rollNumber, branch, division) VALUES (" + studentIdCounter + ", '" + studentName + "', '" + rollNumber + "', '" + branch + "', '" + division + "')");
                    }
                    
                    Map<String, Object> student = new HashMap<>();
                    student.put("id", getStudentId(conn, rollNumber)); // Get actual ID after potential insert
                    student.put("branch", branch);
                    students.add(student);
                    studentIdCounter++;
                }
            }

            // Subjects
            List<Map<String, Object>> subjects = new ArrayList<>();
            int subjectIdCounter = 1;
            Map<String, String[]> branchSubjectNames = new HashMap<>();
            branchSubjectNames.put("Computer Science", new String[]{"Data Structures", "Algorithms", "Operating Systems", "Database Systems", "Computer Networks"});
            branchSubjectNames.put("CO", new String[]{"CO Subject 1", "CO Subject 2", "CO Subject 3", "CO Subject 4", "CO Subject 5"});
            branchSubjectNames.put("ESE", new String[]{"ESE Subject 1", "ESE Subject 2", "ESE Subject 3", "ESE Subject 4", "ESE Subject 5"});
            branchSubjectNames.put("AIML", new String[]{"AIML Subject 1", "AIML Subject 2", "AIML Subject 3", "AIML Subject 4", "AIML Subject 5"});
            branchSubjectNames.put("IT", new String[]{"IT Subject 1", "IT Subject 2", "IT Subject 3", "IT Subject 4", "IT Subject 5"});
            branchSubjectNames.put("Mechanical", new String[]{"Thermodynamics", "Fluid Mechanics", "Machine Design", "Manufacturing Processes", "Heat Transfer"});

            for (Map.Entry<String, String[]> entry : branchSubjectNames.entrySet()) {
                String branch = entry.getKey();
                for (String subjectName : entry.getValue()) {
                    if (!subjectExists(conn, subjectName)) {
                        stmt.execute("INSERT INTO Subject (id, name, branch) VALUES (" + subjectIdCounter + ", '" + subjectName + "', '" + branch + "')");
                    }
                    Map<String, Object> subject = new HashMap<>();
                    subject.put("id", getSubjectId(conn, subjectName)); // Get actual ID after potential insert
                    subject.put("branch", branch);
                    subjects.add(subject);
                    subjectIdCounter++;
                }
            }

            // Generate Marks for all students and their respective subjects
            Random random = new Random();
            String insertMarksSQL = "INSERT INTO Marks (studentId, subjectId, internals, iat1, iat2, semMarks) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertMarksSQL)) {
                for (Map<String, Object> student : students) {
                    int studentId = (int) student.get("id");
                    String studentBranch = (String) student.get("branch");

                    for (Map<String, Object> subject : subjects) {
                        int subjectId = (int) subject.get("id");
                        String subjectBranch = (String) subject.get("branch");

                        if (studentBranch.equals(subjectBranch) && !marksExists(conn, studentId, subjectId)) {
                            ps.setInt(1, studentId);
                            ps.setInt(2, subjectId);
                            ps.setInt(3, 15 + random.nextInt(6)); // Internals 15-20
                            ps.setInt(4, 30 + random.nextInt(21)); // IAT1 30-50
                            ps.setInt(5, 30 + random.nextInt(21)); // IAT2 30-50
                            ps.setInt(6, 60 + random.nextInt(31)); // SemMarks 60-90
                            ps.executeUpdate();
                        }
                    }
                }
            }

            // Generate Attendance for all students for the last 15 days
            String insertAttendanceSQL = "INSERT INTO Attendance (studentId, attendanceDate, status) VALUES (?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertAttendanceSQL)) {
                LocalDate today = LocalDate.now();
                for (Map<String, Object> student : students) {
                    int studentId = (int) student.get("id");
                    for (int i = 0; i < 15; i++) {
                        LocalDate attendanceDate = today.minusDays(i);
                        // Skip weekends for attendance
                        if (attendanceDate.getDayOfWeek().getValue() < 6 && !attendanceExists(conn, studentId, attendanceDate)) { // Monday=1, Sunday=7
                            ps.setInt(1, studentId);
                            ps.setDate(2, java.sql.Date.valueOf(attendanceDate));
                            ps.setString(3, random.nextBoolean() ? "PRESENT" : "ABSENT");
                            ps.executeUpdate();
                        }
                    }
                }
            }

            // Holidays
            LocalDate upcomingHolidayDate = LocalDate.now().plusDays(7);
            if (!holidayExists(conn, upcomingHolidayDate)) {
                stmt.execute("INSERT INTO Holidays (holidayDate, description) VALUES ('" + upcomingHolidayDate + "', 'Upcoming Holiday')");
            }
            LocalDate pastHolidayDate = LocalDate.now().minusDays(20);
            if (!holidayExists(conn, pastHolidayDate)) {
                stmt.execute("INSERT INTO Holidays (holidayDate, description) VALUES ('" + pastHolidayDate + "', 'Past Holiday')");
            }


            stmt.close();
            conn.close();
            System.out.println("Database re-initialized successfully with new schema and data.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean userExists(Connection conn, String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean studentExists(Connection conn, String rollNumber) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Student WHERE rollNumber = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private int getStudentId(Connection conn, String rollNumber) throws SQLException {
        String sql = "SELECT id FROM Student WHERE rollNumber = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNumber);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1; // Should not happen if studentExists is true
    }

    private boolean subjectExists(Connection conn, String subjectName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Subject WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subjectName);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private int getSubjectId(Connection conn, String subjectName) throws SQLException {
        String sql = "SELECT id FROM Subject WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, subjectName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        }
        return -1; // Should not happen if subjectExists is true
    }

    private boolean marksExists(Connection conn, int studentId, int subjectId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Marks WHERE studentId = ? AND subjectId = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean attendanceExists(Connection conn, int studentId, LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Attendance WHERE studentId = ? AND attendanceDate = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    private boolean holidayExists(Connection conn, LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Holidays WHERE holidayDate = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
}
