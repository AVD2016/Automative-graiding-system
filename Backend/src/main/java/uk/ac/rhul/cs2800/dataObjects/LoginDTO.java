package uk.ac.rhul.cs2800.dataObjects;

public class LoginDTO {
  private int id;
  private String firstName;
  private String lastName;
  private String role;

  public LoginDTO(int id, String firstName, String lastName, String role) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.role = role;
  }

  public int getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getRole() {
    return role;
  }
}

