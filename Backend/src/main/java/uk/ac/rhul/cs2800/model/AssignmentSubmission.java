package uk.ac.rhul.cs2800.model;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class AssignmentSubmission {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  // Student who submitted
  @ManyToOne
  @JoinColumn(name = "student_id", nullable = false)
  private Student student;

  // Assignment being submitted
  @ManyToOne
  @JoinColumn(name = "assignment_id", nullable = false)
  private Assignment assignment;

  private LocalDateTime submittedAt;

  private boolean marked;

  private Integer mark;

  // Store file paths (BEST PRACTICE)
  @ElementCollection
  @CollectionTable(
      name = "submission_files",
      joinColumns = @JoinColumn(name = "submission_id")
  )
  @Column(name = "file_path")
  private List<String> pdfFiles = new ArrayList<>();

  private int suggestedGrade;

  @Column(columnDefinition = "TEXT")
  private String feedbackForAssignment;

  @Column(columnDefinition = "TEXT")
  private String lecturerFeedback;

  /* =========================
     CONSTRUCTORS
  ========================= */

  public AssignmentSubmission() {}

  public AssignmentSubmission(Student student, Assignment assignment) {
    this.student = student;
    this.assignment = assignment;
    this.submittedAt = LocalDateTime.now();
    this.marked = false;
    this.mark = null;
  }

  public int getId() {
    return id;
  }

  public Student getStudent() {
    return student;
  }

  public int getSuggestedGrade() {
    return suggestedGrade;
  }

  public String getFeedbackForAssignment() {
    return feedbackForAssignment;
  }

  public void setSuggestedGrade(int suggestedGrade) {
    this.suggestedGrade = suggestedGrade;
  }

  public void setFeedbackForAssignment(String feedbackForAssignment) {
    this.feedbackForAssignment = feedbackForAssignment;
  }

  public void setStudent(Student student) {
    this.student = student;
  }

  public Assignment getAssignment() {
    return assignment;
  }

  public void setAssignment(Assignment assignment) {
    this.assignment = assignment;
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

  public Integer getMark() {
    return mark;
  }

  public void setMark(Integer mark) {
    this.mark = mark;
  }

  public List<String> getPdfFiles() {
    return pdfFiles;
  }

  public void setPdfFiles(List<String> pdfFiles) {
    this.pdfFiles = pdfFiles;
  }

  public String getLecturerFeedback() {
    return lecturerFeedback;
  }

  public void setLecturerFeedback(String lecturerFeedback) {
    this.lecturerFeedback = lecturerFeedback;
  }
}
