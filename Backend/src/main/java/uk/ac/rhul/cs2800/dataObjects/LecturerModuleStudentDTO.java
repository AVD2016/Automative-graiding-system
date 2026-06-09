package uk.ac.rhul.cs2800.dataObjects;

public class LecturerModuleStudentDTO {

  private int studentId;
  private String fullName;
  private String email;

  private int missedDeadlines;
  private double averageGrade;

  public LecturerModuleStudentDTO(int studentId, String fullName, String email, int missedDeadlines,
      double averageGrade) {
    this.studentId = studentId;
    this.fullName = fullName;
    this.email = email;
    this.missedDeadlines = missedDeadlines;
    this.averageGrade = averageGrade;
  }

  public int getStudentId() {
    return studentId;
  }

  public String getFullName() {
    return fullName;
  }

  public String getEmail() {
    return email;
  }

  public int getMissedDeadlines() {
    return missedDeadlines;
  }

  public double getAverageGrade() {
    return averageGrade;
  }
}