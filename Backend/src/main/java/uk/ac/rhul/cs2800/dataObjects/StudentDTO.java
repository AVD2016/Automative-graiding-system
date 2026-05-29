package uk.ac.rhul.cs2800.dataObjects;

import java.util.List;

public class StudentDTO {

  private int id;
  private String firstName;
  private String lastName;
  private String username;
  private String email;

  private List<StudentModuleDTO> modules;

  public StudentDTO(int id, String firstName, String lastName, String username, String email,
      List<StudentModuleDTO> modules) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.username = username;
    this.email = email;
    this.modules = modules;
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

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public List<StudentModuleDTO> getModules() {
    return modules;
  }

}
