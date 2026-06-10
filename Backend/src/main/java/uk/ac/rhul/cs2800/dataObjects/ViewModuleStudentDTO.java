package uk.ac.rhul.cs2800.dataObjects;

import java.util.List;

public class ViewModuleStudentDTO {

  private String code;
  private String name;
  private int credits;

  private int assignmentCount;
  private double averageGrade;

  private List<LecturerDTO> lecturers;

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public int getCredits() {
    return credits;
  }

  public int getAssignmentCount() {
    return assignmentCount;
  }

  public double getAverageGrade() {
    return averageGrade;
  }

  public List<LecturerDTO> getLecturers() {
    return lecturers;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setCredits(int credits) {
    this.credits = credits;
  }

  public void setAssignmentCount(int assignmentCount) {
    this.assignmentCount = assignmentCount;
  }

  public void setAverageGrade(double averageGrade) {
    this.averageGrade = averageGrade;
  }

  public void setLecturers(List<LecturerDTO> lecturers) {
    this.lecturers = lecturers;
  }

  // getters + setters
}