package com.studentmanagement;

import java.util.List;

public class StudentDetails {
    private Student student;
    private double marksPercentage;
    private double attendancePercentage;
    private List<Marks> marks;
    private List<Attendance> attendance;

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public double getMarksPercentage() {
        return marksPercentage;
    }

    public void setMarksPercentage(double marksPercentage) {
        this.marksPercentage = marksPercentage;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public List<Marks> getMarks() {
        return marks;
    }

    public void setMarks(List<Marks> marks) {
        this.marks = marks;
    }

    public List<Attendance> getAttendance() {
        return attendance;
    }

    public void setAttendance(List<Attendance> attendance) {
        this.attendance = attendance;
    }
}