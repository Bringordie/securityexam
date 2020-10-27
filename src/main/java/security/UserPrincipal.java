package security;

import entities.Role;
import entities.User;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserPrincipal implements Principal {

  private String username;
  private String role;
  private int usernameID;

  /* Create a UserPrincipal, given the Entity class User*/
  public UserPrincipal(User user) {
    this.username = user.getUserName();
    this.role = user.getRole().toString();
    this.usernameID = user.getId();
  }

  public UserPrincipal(String username, int usernameID, String role) {
    super();
    this.username = username;
    this.role = role;
    this.usernameID = usernameID;
  }

  @Override
  public String getName() {
    return username;
  }
  
  public int getNameID() {
    return usernameID;
  }


  public boolean isUserInRole(String role) {
    return this.role.matches(role);
  }
}
