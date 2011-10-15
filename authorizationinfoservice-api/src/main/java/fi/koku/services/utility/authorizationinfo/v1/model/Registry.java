/**
 * Simple class that represents Registry (rekisteri / potilastietorekisteri)
 */
package fi.koku.services.utility.authorizationinfo.v1.model;

/**
 * @author mikkope
 *
 */
public class Registry {
    private String id;
    private String name;
    
    public Registry(String id) {
      this.id = id;
    }
    
    public Registry(String id, String name) {
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
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      return result;
    }
    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Registry other = (Registry) obj;
      if (id == null) {
        if (other.id != null)
          return false;
      } else if (!id.equals(other.id))
        return false;
      return true;
    }
    
    
    
}
