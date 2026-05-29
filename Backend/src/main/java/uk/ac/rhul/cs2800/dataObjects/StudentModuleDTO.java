package uk.ac.rhul.cs2800.dataObjects;

public class StudentModuleDTO {

  private String code;
  private String name;

  public StudentModuleDTO(String code, String name) {
    this.code = code;
    this.name = name;
  }

  public String getCode() {
    return code;
  }

  public String getName() {
    return name;
  }


}
