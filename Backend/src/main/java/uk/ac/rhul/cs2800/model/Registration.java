package uk.ac.rhul.cs2800.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Represents a registration record linking a {@link Student} to a {@link Module}.
 *
 * <p>
 * A {@code Registration} instance stores references to a student and a module. The module may be
 * {@code null} if it has not yet been assigned.
 * </p>
 *
 * <p>
 * This entity is used to track which students are enrolled in which modules.
 * </p>
 *
 * @author Vladyslav Abramov
 * @version 1.0
 * @since 2025-10-24
 */
@Entity
public class Registration {

  /** Unique identifier for this registration. */
  @Id
  @GeneratedValue
  private long id;

  /** The module associated with this registration. */
  @ManyToOne
  @JoinColumn(name = "module_code")
  private Module module;

  /** The student associated with this registration. */
  @ManyToOne
  @JoinColumn(name = "student_id")
  @JsonIgnore
  private Student student;

  /**
   * Creates a new {@code Registration} with no associated module.
   *
   * <p>
   * The {@code module} field is initialized to {@code null}.
   * </p>
   */
  public Registration() {
    this.module = null;
  }

  /**
   * Creates a new {@code Registration} with the specified module.
   *
   * @param module the module to associate with this registration
   */
  public Registration(Module module) {
    this.module = module;
  }

  /**
   * Returns the unique identifier for this registration.
   *
   * @return the registration ID
   */
  public long getId() {
    return id;
  }

  /**
   * Sets the unique identifier for this registration.
   *
   * <p>
   * This is typically only used by JPA.
   * </p>
   *
   * @param id the ID to assign to this registration
   */
  public void setId(long id) {
    this.id = id;
  }

  /**
   * Sets the module associated with this registration.
   *
   * @param module the module to assign, or {@code null} to clear it
   */
  public void setModule(Module module) {
    this.module = module;
  }

  /**
   * Returns the module associated with this registration.
   *
   * @return the associated module, or {@code null} if none has been assigned
   */
  public Module getModule() {
    return this.module;
  }

  /**
   * Returns the student associated with this registration.
   *
   * @return the student linked to this registration
   */
  public Student getStudent() {
    return student;
  }

  /**
   * Sets the student associated with this registration.
   *
   * @param student the student to assign to this registration
   */
  public void setStudent(Student student) {
    this.student = student;
  }
}
