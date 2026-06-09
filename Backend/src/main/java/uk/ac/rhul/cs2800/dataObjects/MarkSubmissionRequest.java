package uk.ac.rhul.cs2800.dataObjects;

public class MarkSubmissionRequest {

  private String lecturerFeedback;
  private Integer finalMark;

  public String getLecturerFeedback() {
    return lecturerFeedback;
  }

  public void setLecturerFeedback(String lecturerFeedback) {
    this.lecturerFeedback = lecturerFeedback;
  }

  public Integer getFinalMark() {
    return finalMark;
  }

  public void setFinalMark(Integer finalMark) {
    this.finalMark = finalMark;
  }
}

