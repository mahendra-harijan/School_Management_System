package com.school.model;

import java.sql.Date;

public class Attendance {
    private int id;
    private int studentId;
    private String studentName;
    private String rollNumber;
    private int classId;
    private String className;
    private Date attendanceDate;
    private String status;
    private int subjectId;
    private String subjectName;
    private int markedBy;
    private String markedByName;
    private String remarks;

    public Attendance() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public int getClassId() { return classId; }
    public void setClassId(int classId) { this.classId = classId; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Date getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(Date attendanceDate) { this.attendanceDate = attendanceDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getMarkedBy() { return markedBy; }
    public void setMarkedBy(int markedBy) { this.markedBy = markedBy; }

    public String getMarkedByName() { return markedByName; }
    public void setMarkedByName(String markedByName) { this.markedByName = markedByName; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}
