package uk.ac.rhul.cs2800.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import uk.ac.rhul.cs2800.exception.NoGradeAvailableException;
import uk.ac.rhul.cs2800.exception.NoRegistrationException;

/**
 * The Student class represents a student with basic personal and academic information.
 * 
 * <p>
 * This class is part of the student grade management system.
 * </p>
 *
 * @author Vladyslav Abramov
 * @version 1.1
 * @since 2025-10-23
 */
@Entity
public class Student extends User {

  /**
   * A mapping between modules and the student's grades for those modules. Each key represents a
   * {@link Module}, and the corresponding value represents the student's {@link Grade} for that
   * module.
   */
  @OneToMany(mappedBy = "student")
  private List<Grade> grades = new ArrayList<>();

  /**
   * A list of modules that the student is currently registered for.
   * 
   * <p>
   * This list stores all {@link Module} objects associated with the student. It is initialized as
   * an empty {@link ArrayList} and can be modified as the student registers for or withdraws from
   * modules.
   * </p>
   */
  @OneToMany(mappedBy = "user")
  private List<Registration> registered = new ArrayList<>();

  /**
   * Default constructor with empty parameters assigned.
   */
  public Student() {
    super();
  }


  public Student(int id, String firstName, String lastName, String username, String email, String password) {
    super(id, firstName, lastName, username, email, password);
  }


  /**
   * Creates new instance of Grade class. Maps Grades to Modules.
   *
   * @param score used to initialize instance of class grade
   * @param module used to map grades to modules
   */
  public void addGrade(int score, Module module) throws NoRegistrationException {
    for (Registration r : registered) {
      if (r.getModule().getCode().equals(module.getCode())) {
        Grade grade = new Grade(score, module);
        grades.add(grade);
        return;
      }
    }
    throw new NoRegistrationException(
        "Student isn`t registered for this module: " + module.getCode());
  }

  /**
   * Retrieves the grade for the specified module.
   *
   * @param module the module for which to get the grade
   * @return the {@link Grade} associated with the specified module
   * @throws NoGradeAvailableException if the student has no grade recorded for the module
   */
  public Grade getGrade(Module module) throws NoGradeAvailableException {
    for (Grade g : grades) {
      if (module.getCode().equals(g.getModule().getCode())) {
        return g;
      }
    }
    throw new NoGradeAvailableException("No grade found for specified module: " + module.getCode());
  }

  /**
   * Computes the average score of all grades associated with this student.
   *
   * <p>
   * This method iterates over all {@link Grade} objects stored in the {@code grades} map and
   * calculates the arithmetic mean of their scores.
   * </p>
   *
   * <p>
   * If there are no grades recorded, this method will return {@code Float.NaN} to indicate that the
   * average cannot be computed.
   * </p>
   *
   * @return the average score as a {@code float}, or {@code Float.NaN} if no grades exist
   */
  public float computeAverage() {
    float average = 0;
    int count = 0;

    for (Grade g : grades) {
      count++;
      average += g.getScore();
    }
    return average / count;
  }

  /**
   * Registers the student for the specified module by creating a new {@link Registration} and
   * adding it to the student's list of registered modules.
   *
   * @param module the module to register the student for
   */
  public void registerModule(Module module) {
    Registration registration = new Registration(module, this);
    this.registered.add(registration);
  }

  public List<Registration> getRegistered() {
    return new ArrayList<>(registered);
  }
}

