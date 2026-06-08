package uk.ac.rhul.cs2800.dataObjects;

import java.time.LocalDateTime;
import java.util.List;
import uk.ac.rhul.cs2800.model.Assignment;
import uk.ac.rhul.cs2800.model.AssignmentSubmission;

public class AssignmentLecturerDTO {

  private int id;

  private String title;

  private String moduleCode;

  private String moduleName;

  private LocalDateTime deadline;

  private int numberOfSubmissions;

  private int numberOfUnmarkedSubmissions;

  public AssignmentLecturerDTO() {}

  public AssignmentLecturerDTO(Assignment assignment, List<AssignmentSubmission> submissions) {

    this.id = assignment.getId();

    this.title = assignment.getTitle();

    this.moduleCode = assignment.getModule().getCode();

    this.moduleName = assignment.getModule().getName();

    this.deadline = assignment.getDeadline();

    this.numberOfSubmissions = submissions.size();

    this.numberOfUnmarkedSubmissions =
        (int) submissions.stream()
            .filter(s -> !s.isMarked())
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

  public LocalDateTime getDeadline() {
    return deadline;
  }

  public void setDeadline(LocalDateTime deadline) {
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