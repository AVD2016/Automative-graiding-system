package uk.ac.rhul.cs2800.model;


import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;

@Entity
public class Lecturer extends User {

  @OneToMany(mappedBy = "user")
  private List<Registration> registered = new ArrayList<>();

  public Lecturer() {}

  public Lecturer(int id, String firstName, String lastName, String username, String email,
      String password) {
    super(id, firstName, lastName, username, email, password);
    this.id = id;
  }

  @OneToMany(mappedBy = "user")
  public List<Registration> getRegistered() {
    return new ArrayList<>(registered);
  }

  public void setRegistered(List<Registration> registered) {
    this.registered = registered;
  }

}
