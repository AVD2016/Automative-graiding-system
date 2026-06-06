package uk.ac.rhul.cs2800.dataObjects;

public class LecturerViewModulesDTO {

  private String code;
  private String name;
  private int credits;

  private int assignedCredits;
  private int courseworkCount;
  private int studentsEnrolled;

  public LecturerViewModulesDTO(String code, String name, int credits, int assignedCredits,
      int courseworkCount, int studentsEnrolled) {
    this.code = code;
    this.name = name;
    this.credits = credits;
    this.assignedCredits = assignedCredits;
    this.courseworkCount = courseworkCount;
    this.studentsEnrolled = studentsEnrolled;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }

  public int getCredits() {
    return credits;
  }

  public int getAssignedCredits() {
    return assignedCredits;
  }

  public int getCourseworkCount() {
    return courseworkCount;
  }

  public int getStudentsEnrolled() {
    return studentsEnrolled;
  }
}
