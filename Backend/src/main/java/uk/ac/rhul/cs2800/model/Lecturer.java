package uk.ac.rhul.cs2800.model;


import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Lecturer extends User {

  protected int id;

  @OneToMany(mappedBy = "user")
  private List<Registration> registered = new ArrayList<>();

  public Lecturer() {}

  public Lecturer(int id, String firstName, String lastName, String username, String email,
      String password) {
    super(id, firstName, lastName, username, email, password);
    this.id = id;
  }


}
