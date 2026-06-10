package uk.ac.rhul.cs2800.dataObjects;

public class LecturerEmailDTO {

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  private String firstName;
  private String lastName;
  private String email;

  // getters + setters
}