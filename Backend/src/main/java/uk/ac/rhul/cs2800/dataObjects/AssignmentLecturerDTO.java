package uk.ac.rhul.cs2800.dataObjects;

import java.util.ArrayList;
import java.util.List;
import uk.ac.rhul.cs2800.model.Assignment;
import uk.ac.rhul.cs2800.model.AssignmentSubmission;

public class AssignmentLecturerDTO {

  private int id;

  private String title;

  private String moduleCode;

  private String moduleName;

  private String deadline;

  private int numberOfSubmissions;

  private int numberOfUnmarkedSubmissions;

  public AssignmentLecturerDTO() {}

  public AssignmentLecturerDTO(Assignment assignment, List<AssignmentSubmission> submissions) {

    if (assignment == null) {
      throw new IllegalArgumentException("Assignment cannot be null");
    }

    this.id = assignment.getId();

    this.title = assignment.getTitle() != null ? assignment.getTitle() : "Untitled";

    this.moduleCode = (assignment.getModule() != null && assignment.getModule().getCode() != null)
        ? assignment.getModule().getCode()
        : "N/A";

    this.moduleName = (assignment.getModule() != null && assignment.getModule().getName() != null)
        ? assignment.getModule().getName()
        : "N/A";

    this.deadline = assignment.getDeadline() != null ? assignment.getDeadline().toString() : null;

    if (submissions == null) {
      submissions = new ArrayList<>();
    }

    this.numberOfSubmissions = submissions.size();

    this.numberOfUnmarkedSubmissions =
        (int) submissions.stream()
            .filter(s -> s != null && !s.isMarked())
            .count();
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getModuleCode() {
    return moduleCode;
  }

  public void setModuleCode(String moduleCode) {
    this.moduleCode = moduleCode;
  }

  public String getModuleName() {
    return moduleName;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public String getDeadline() {
    return deadline;
  }

  public void setDeadline(String deadline) {
    this.deadline = deadline;
  }

  public int getNumberOfSubmissions() {
    return numberOfSubmissions;
  }

  public void setNumberOfSubmissions(int numberOfSubmissions) {
    this.numberOfSubmissions = numberOfSubmissions;
  }

  public int getNumberOfUnmarkedSubmissions() {
    return numberOfUnmarkedSubmissions;
  }

  public void setNumberOfUnmarkedSubmissions(int numberOfUnmarkedSubmissions) {
    this.numberOfUnmarkedSubmissions = numberOfUnmarkedSubmissions;
  }
}