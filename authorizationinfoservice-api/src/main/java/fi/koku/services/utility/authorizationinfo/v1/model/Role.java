/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
/**
 * Simple class that represents Role 
 */
package fi.koku.services.utility.authorizationinfo.v1.model;

/**
 * @author mikkope
 *
 */
public class Role {
  private String id;
  private String name;
  
  public Role(String id) {
    this.id = id;
  }
  
  public Role(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  
 public boolean equals(String o){ 
 
   return true;
   
 }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Role other = (Role) obj;
    if (id == null) {
      if (other.id != null)
        return false;
    } else if (!id.equals(other.id))
      return false;
    return true;
  }

 
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public String toString() {
    return "Role [id=" + id + ", name=" + name + "]";
  }

  
}
