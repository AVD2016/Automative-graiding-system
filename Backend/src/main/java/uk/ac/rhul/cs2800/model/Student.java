package uk.ac.rhul.cs2800.model;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

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
    this.id = id;
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

