package uk.ac.rhul.cs2800.dataObjects;

import java.util.List;

public class LecturerModuleExpandedDTO {

  private LecturerViewModulesDTO module;
  private List<LecturerModuleStudentDTO> students;

  public LecturerModuleExpandedDTO(LecturerViewModulesDTO module,
      List<LecturerModuleStudentDTO> students) {
    this.module = module;
    this.students = students;
  }

  public LecturerViewModulesDTO getModule() {
    return module;
  }

  public List<LecturerModuleStudentDTO> getStudents() {
    return students;
  }
}
