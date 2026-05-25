package uk.ac.rhul.cs2800.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Represents a grade awarded for a specific module.
 *
 * <p>
 * Each grade stores the numeric score achieved and the associated {@link Module}.
 * </p>
 *
 * @author Vladyslav Abramov
 * @version 1.0
 * @since 2025-10-24
 */
@Entity
public class Grade {

  /** The unique identifier for this grade. */
  @Id
  @GeneratedValue
  private long id;

  /** The numeric score awarded for the module. */
  private int score;

  /** The student associated with this grade. */
  @ManyToOne
  @JoinColumn(name = "student_id")
  private Student student;

  /** The module associated with this grade. */
  @ManyToOne
  @JoinColumn(name = "module_code")
  private Module module;

  /**
   * Constructs a {@code Grade} object with default values.
   *
   * <p>
   * The score is initialized to {@code 0}, and the module is initialized as a new {@link Module}
   * instance.
   * </p>
   */
  public Grade() {
    this.score = 0;
    this.module = new Module();
  }

  /**
   * Constructs a {@code Grade} object with the specified score and module.
   *
   * @param score the numeric score achieved
   * @param module the module associated with this grade
   */
  public Grade(int score, Module module) {
    this.score = score;
    this.module = module;
  }

  /**
   * Returns the unique identifier of this grade.
   *
   * @return the ID of this grade
   */
  public long getId() {
    return id;
  }

  /**
   * Sets the unique identifier for the grade.
   *
   * <p>
   * This should generally only be used by JPA.
   * </p>
   *
   * @param id the ID to assign
   */
  public void setId(long id) {
    this.id = id;
  }


  /**
   * Sets the score for this grade.
   *
   * @param score the numeric score to set
   */
  public void setScore(int score) {
    this.score = score;
  }

  /**
   * Returns the score for this grade.
   *
   * @return the numeric score
   */
  public int getScore() {
    return this.score;
  }

  /**
   * Returns the student associated with this grade.
   *
   * @return the {@link Student} who received this grade
   */
  public Student getStudent() {
    return student;
  }

  /**
   * Sets the student associated with this grade.
   *
   * @param student the student to assign to this grade
   */
  public void setStudent(Student student) {
    this.student = student;
  }

  /**
   * Sets the module associated with this grade.
   *
   * @param module to associate with this grade
   */
  public void setModule(Module module) {
    this.module = module;
  }

  /**
   * Returns instance of the Module the grade is for.
   *
   * @return module which is an instance of class Module associated with this grade
   */
  public Module getModule() {
    return this.module;
  }
}
