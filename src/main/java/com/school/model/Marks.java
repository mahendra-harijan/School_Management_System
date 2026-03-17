package com.school.model;

import java.sql.Date;
import java.sql.Timestamp;

public class Marks {
    private int id;
    private int studentId;
    private String studentName;
    private String rollNumber;
    private int subjectId;
    private String subjectName;
    private String examType;
    private double marksObtained;
    private int maxMarks;
    private Date examDate;
    private String remarks;
    private int enteredBy;
    private Timestamp enteredAt;

    public Marks() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(double marksObtained) { this.marksObtained = marksObtained; }

    public int getMaxMarks() { return maxMarks; }
    public void setMaxMarks(int maxMarks) { this.maxMarks = maxMarks; }

    public Date getExamDate() { return examDate; }
    public void setExamDate(Date examDate) { this.examDate = examDate; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public int getEnteredBy() { return enteredBy; }
    public void setEnteredBy(int enteredBy) { this.enteredBy = enteredBy; }

    public Timestamp getEnteredAt() { return enteredAt; }
    public void setEnteredAt(Timestamp enteredAt) { this.enteredAt = enteredAt; }

    public double getPercentage() {
        if (maxMarks == 0) return 0;
        return (marksObtained / maxMarks) * 100;
    }

    public String getGrade() {
        double pct = getPercentage();
        if (pct >= 90) return "A+";
        if (pct >= 80) return "A";
        if (pct >= 70) return "B+";
        if (pct >= 60) return "B";
        if (pct >= 50) return "C";
        if (pct >= 40) return "D";
        return "F";
    }
}
