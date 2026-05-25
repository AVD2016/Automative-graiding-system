package uk.ac.rhul.cs2800.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/**
 * Represents a module in the student grade management system.
 *
 * <p>
 * Each module has a unique code, a name, and a flag indicating whether it is mandatory or not
 * (MNC).
 * </p>
 *
 * @author Vladyslav Abramov
 * @version 1.0
 * @since 2025-10-23
 */
@Entity
public class Module {

  @Id
  private String code;

  private String name;
  private boolean mnc;

  /**
   * Constructs an empty {@code Module} with default values.
   *
   * <p>
   * The code and name are initialized as empty strings, and the MNC flag is set to {@code false}.
   * </p>
   */
  public Module() {
    this.code = "";
    this.name = "";
    this.mnc = false;
  }

  /**
   * Constructs a {@code Module} with the specified code, name, and MNC flag.
   *
   * @param code the unique code of the module
   * @param name the name of the module
   * @param mnc {@code true} if the module is mandatory, {@code false} otherwise
   */
  public Module(String code, String name, boolean mnc) {
    this.code = code;
    this.name = name;
    this.mnc = mnc;
  }

  /**
   * Sets the code of the module.
   *
   * @param code the new module code
   */
  public void setCode(String code) {
    this.code = code;
  }

  /**
   * Returns the code of the module.
   *
   * @return the module code
   */
  public String getCode() {
    return this.code;
  }

  /**
   * Sets the name of the module.
   *
   * @param name the new module name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the name of the module.
   *
   * @return the module name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets whether the module is mandatory.
   *
   * @param mnc {@code true} if the module is mandatory, {@code false} otherwise
   */
  public void setMnc(boolean mnc) {
    this.mnc = mnc;
  }

  /**
   * Returns whether the module is mandatory.
   *
   * @return {@code true} if the module is mandatory, {@code false} otherwise
   */
  public boolean getMnc() {
    return this.mnc;
  }

}
