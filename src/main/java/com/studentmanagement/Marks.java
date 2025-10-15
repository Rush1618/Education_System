package com.studentmanagement;

public class Marks {
    private int id;
    private int studentId;
    private int subjectId;
    private int internals;
    private int iat1;
    private int iat2;
    private int semMarks;
    private String subject;
    private int internalMarks;
    private int termMarks;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public int getInternals() {
        return internals;
    }

    public void setInternals(int internals) {
        this.internals = internals;
    }

    public int getIat1() {
        return iat1;
    }

    public void setIat1(int iat1) {
        this.iat1 = iat1;
    }

    public int getIat2() {
        return iat2;
    }

    public void setIat2(int iat2) {
        this.iat2 = iat2;
    }

    public int getSemMarks() {
        return semMarks;
    }

    public void setSemMarks(int semMarks) {
        this.semMarks = semMarks;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getInternalMarks() {
        return internalMarks;
    }

    public void setInternalMarks(int internalMarks) {
        this.internalMarks = internalMarks;
    }

    public int getTermMarks() {
        return termMarks;
    }

    public void setTermMarks(int termMarks) {
        this.termMarks = termMarks;
    }
}