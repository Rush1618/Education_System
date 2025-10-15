package com.studentmanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class StudentDAO {

    public List<String> getBranches() {
        List<String> branches = new ArrayList<>();
        String sql = "SELECT DISTINCT branch FROM Student ORDER BY branch";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                branches.add(rs.getString("branch"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return branches;
    }

    public List<String> getDivisionsForBranch(String branch) {
        List<String> divisions = new ArrayList<>();
        String sql = "SELECT DISTINCT division FROM Student WHERE branch = ? ORDER BY division";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, branch);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    divisions.add(rs.getString("division"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return divisions;
    }

    // Updated to calculate attendance from the new daily attendance table
    public List<StudentDetails> getStudentsByBranchAndDivision(String branch, String division) {
        List<StudentDetails> studentDetails = new ArrayList<>();
        String sql = "SELECT s.id, s.name, s.rollNumber, s.branch, s.division, " +
                     " (SELECT AVG((m.internals + m.iat1 + m.iat2 + m.semMarks) / 4.0) FROM Marks m WHERE m.studentId = s.id) as avg_marks, " +
                     " (SELECT CAST(COUNT(CASE WHEN a.status = 'PRESENT' THEN 1 END) AS DOUBLE) * 100 / COUNT(a.id) FROM Attendance a WHERE a.studentId = s.id) as avg_attendance " +
                     "FROM Student s " +
                     "WHERE s.branch = ? AND s.division = ? " +
                     "GROUP BY s.id, s.name, s.rollNumber, s.branch, s.division " +
                     "ORDER BY s.rollNumber";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, branch);
            stmt.setString(2, division);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Student student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setName(rs.getString("name"));
                    student.setRollNumber(rs.getString("rollNumber"));
                    student.setBranch(rs.getString("branch"));
                    student.setDivision(rs.getString("division"));

                    double avgMarks = rs.getDouble("avg_marks");
                    double avgAttendance = rs.getDouble("avg_attendance");

                    StudentDetails sd = new StudentDetails();
                    sd.setStudent(student);
                    sd.setMarksPercentage(avgMarks);
                    sd.setAttendancePercentage(avgAttendance);
                    studentDetails.add(sd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentDetails;
    }

    public StudentDetails getStudentByRollNumber(String rollNumber) {
        StudentDetails studentDetails = new StudentDetails();
        Student student = null;
        String query = "SELECT id, name, rollNumber, branch, division FROM Student WHERE rollNumber = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, rollNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setName(rs.getString("name"));
                    student.setRollNumber(rs.getString("rollNumber"));
                    student.setBranch(rs.getString("branch"));
                    student.setDivision(rs.getString("division"));
                    studentDetails.setStudent(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentDetails;
    }

    public List<Marks> getStudentMarks(int studentId) {
        List<Marks> marksList = new ArrayList<>();
        String query = "SELECT m.id, m.studentId, m.subjectId, m.internals, m.iat1, m.iat2, m.semMarks, s.name as subjectName FROM Marks m JOIN Subject s ON m.subjectId = s.id WHERE m.studentId = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Marks marks = new Marks();
                    marks.setId(rs.getInt("id"));
                    marks.setStudentId(rs.getInt("studentId"));
                    marks.setSubjectId(rs.getInt("subjectId"));
                    marks.setInternals(rs.getInt("internals"));
                    marks.setIat1(rs.getInt("iat1"));
                    marks.setIat2(rs.getInt("iat2"));
                    marks.setSemMarks(rs.getInt("semMarks"));
                    marks.setSubject(rs.getString("subjectName"));
                    marksList.add(marks);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marksList;
    }

    public List<Attendance> getStudentAttendance(int studentId) {
        List<Attendance> attendanceList = new ArrayList<>();
        String query = "SELECT id, studentId, attendanceDate, status FROM Attendance WHERE studentId = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Attendance attendance = new Attendance();
                    attendance.setId(rs.getInt("id"));
                    attendance.setStudentId(rs.getInt("studentId"));
                    attendance.setDate(rs.getDate("attendanceDate").toLocalDate());
                    attendance.setStatus(rs.getString("status"));
                    attendanceList.add(attendance);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendanceList;
    }

    public Student getStudentById(int studentId) {
        Student student = null;
        String sql = "SELECT * FROM Student WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    student = new Student();
                    student.setId(rs.getInt("id"));
                    student.setName(rs.getString("name"));
                    student.setRollNumber(rs.getString("rollNumber"));
                    student.setBranch(rs.getString("branch"));
                    student.setDivision(rs.getString("division"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public List<MarksDetails> getMarksDetailsForStudent(int studentId) {
        List<MarksDetails> marksDetails = new ArrayList<>();
        String sql = "SELECT * FROM Subject s LEFT JOIN Marks m ON s.id = m.subjectId AND m.studentId = ? WHERE s.branch = (SELECT branch FROM Student WHERE id = ?)";

        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, studentId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Subject subject = new Subject();
                    subject.setId(rs.getInt("id"));
                    subject.setName(rs.getString("name"));
                    subject.setBranch(rs.getString("branch"));

                    Marks marks = null;
                    if (rs.getObject("m.id") != null) {
                        marks = new Marks();
                        marks.setId(rs.getInt("m.id"));
                        marks.setStudentId(rs.getInt("m.studentId"));
                        marks.setSubjectId(rs.getInt("m.subjectId"));
                        marks.setInternals(rs.getInt("m.internals"));
                        marks.setIat1(rs.getInt("m.iat1"));
                        marks.setIat2(rs.getInt("m.iat2"));
                        marks.setSemMarks(rs.getInt("m.semMarks"));
                    }
                    marksDetails.add(new MarksDetails(subject, marks));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return marksDetails;
    }

    public Map<LocalDate, String> getDailyAttendanceForStudent(int studentId, int year, int month) {
        Map<LocalDate, String> attendanceMap = new HashMap<>();
        String sql = "SELECT attendanceDate, status FROM Attendance WHERE studentId = ? AND YEAR(attendanceDate) = ? AND MONTH(attendanceDate) = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, studentId);
            stmt.setInt(2, year);
            stmt.setInt(3, month);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    attendanceMap.put(rs.getDate("attendanceDate").toLocalDate(), rs.getString("status"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return attendanceMap;
    }

    public Map<LocalDate, String> getHolidaysForMonth(int year, int month) {
        Map<LocalDate, String> holidayMap = new HashMap<>();
        String sql = "SELECT holidayDate, description FROM Holidays WHERE YEAR(holidayDate) = ? AND MONTH(holidayDate) = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    holidayMap.put(rs.getDate("holidayDate").toLocalDate(), rs.getString("description"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holidayMap;
    }
    public List<Holiday> getAllHolidays() {
        List<Holiday> holidays = new ArrayList<>();
        String query = "SELECT id, holidayDate, description FROM Holidays";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Holiday holiday = new Holiday();
                    holiday.setId(rs.getInt("id"));
                    holiday.setHolidayDate(rs.getDate("holidayDate").toLocalDate());
                    holiday.setDescription(rs.getString("description"));
                    holidays.add(holiday);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return holidays;
    }
    public User authenticateUser(String username, String password) {
        User user = null;
        String query = "SELECT id, username, password, role FROM Users WHERE username = ? AND password = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(rs.getString("password"));
                    user.setRole(rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

}
