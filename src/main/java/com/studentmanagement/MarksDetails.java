package com.studentmanagement;

public class MarksDetails {
    private Subject subject;
    private Marks marks;

    public MarksDetails(Subject subject, Marks marks) {
        this.subject = subject;
        this.marks = marks;
    }

    public Subject getSubject() {
        return subject;
    }

    public Marks getMarks() {
        return marks;
    }

    // Calculated fields for the view
    public int getTotalIatMarks() {
        return (marks != null) ? marks.getIat1() + marks.getIat2() : 0;
    }

    public int getTotalMarks() {
        // Assuming Total Marks = IATs + Internals + Sem Marks
        return (marks != null) ? getTotalIatMarks() + marks.getInternals() + marks.getSemMarks() : 0;
    }
}
