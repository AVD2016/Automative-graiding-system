package uk.ac.rhul.cs2800.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class Student {

  /** The unique identifier for the student. */
  @Id
  private int id;

  /** The student's first name. */
  private String firstName;

  /** The student's last name. */
  private String lastName;

  /** The student's system username. */
  private String username;

  /** The student's email address. */
  private String email;

  private String password;
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
  @OneToMany(mappedBy = "student")
  private List<Registration> registered = new ArrayList<>();

  /**
   * Default constructor with empty parameters assigned.
   */
  public Student() {
    this.id = 0;
    this.firstName = "";
    this.lastName = "";
    this.username = "";
    this.email = "";
  }

  /**
   * Main constructor to create an instance of the class.
   *
   * @param id the unique ID number of the student
   * @param firstName the student's first name
   * @param lastName the student's last name
   * @param username the course name the student is enrolled in
   * @param email the student's email address
   */
  public Student(int id, String firstName, String lastName, String username, String email) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.username = username;
    this.email = email;
  }

  public Student(int id, String firstName, String lastName, String username, String email, String password) {
    this.id = id;
    this.firstName = firstName;
    this.lastName = lastName;
    this.username = username;
    this.email = email;
    this.password = password;
  }

  /**
   * Sets student id.
   *
   * @param id of the student
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * returns student id.
   *
   * @return student id
   */
  public int getId() {
    return this.id;
  }

  /**
   * sets student first name.
   *
   * @param firstName of the student
   */
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  /**
   * returns student first name.
   *
   * @return first name of the student
   */
  public String getFirstName() {
    return this.firstName;
  }

  /**
   * sets student last name.
   *
   * @param lastName of the student
   */
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  /**
   * returns student last name.
   *
   * @return last name of the student
   */
  public String getLastName() {
    return this.lastName;
  }

  /**
   * sets student username.
   *
   * @param username of the student
   */
  public void setUsername(String username) {
    this.username = username;
  }

  /**
   * returns Student username.
   *
   * @return username of the student
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * sets email of the student.
   *
   * @param email of the student
   */
  public void setEmail(String email) {
    this.email = email;
  }

  /**
   * returns student email.
   *
   * @return email of the student
   */
  public String getEmail() {
    return this.email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPassword() {
    return this.password;
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
    Registration registration = new Registration(module);
    this.registered.add(registration);
  }

  public List<Registration> getRegistered() {
    return new ArrayList<>(registered);
  }
}

