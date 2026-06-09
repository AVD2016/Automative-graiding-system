package uk.ac.rhul.cs2800.dataObjects;

import java.time.LocalDateTime;
import uk.ac.rhul.cs2800.model.AssignmentSubmission;

public class AssignmentSubmissionLecturerDTO {

  private int id;

  private String studentName;

  private LocalDateTime submittedAt;

  private boolean marked;

  private String mark;

  public AssignmentSubmissionLecturerDTO() {}

  public AssignmentSubmissionLecturerDTO(AssignmentSubmission submission) {

    if (submission == null) {
      throw new IllegalArgumentException("Submission cannot be null");
    }

    this.id = submission.getId();

    // =========================
    // STUDENT NAME
    // =========================
    if (submission.getStudent() != null) {

      String first = submission.getStudent().getFirstName();
      String last = submission.getStudent().getLastName();

      this.studentName = ((first != null) ? first : "") + " " + ((last != null) ? last : "");

      this.studentName = this.studentName.trim();

      if (this.studentName.isEmpty()) {
        this.studentName = "Unknown Student";
      }

    } else {
      this.studentName = "Unknown Student";
    }
    this.submittedAt = submission.getSubmittedAt();
    this.marked = submission.isMarked();

    // IMPORTANT: always return STRING
    if (submission.isMarked() && submission.getMark() != null) {
      this.mark = String.valueOf(submission.getMark());
    } else {
      this.mark = "Not marked";
    }
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getStudentName() {
    return studentName;
  }

  public void setStudentName(String studentName) {
    this.studentName = studentName;
  }

  public LocalDateTime getSubmittedAt() {
    return submittedAt;
  }

  public void setSubmittedAt(LocalDateTime submittedAt) {
    this.submittedAt = submittedAt;
  }

  public boolean isMarked() {
    return marked;
  }

  public void setMarked(boolean marked) {
    this.marked = marked;
  }

  public String getMark() {
    return mark;
  }

  public void setMark(String mark) {
    this.mark = mark;
  }
}