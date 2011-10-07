package fi.koku.services.entity.authorizationinfo.util;

import java.util.List;
import java.util.Set;

import fi.koku.auth.KoKuRoleUtil;
import fi.koku.services.entity.authorizationinfo.v1.model.Role;

/**
 * Authorization related utility functions.
 * 
 * @author laukksa
 *
 */
public class AuthUtils {

  private AuthUtils() {
  }
  
  /**
   * Is the operation allowed for role(s).
   * 
   * @param operation
   * @param roles
   * @return allowed
   */
  public static boolean isOperationAllowed(String operation, List<Role> roles) {
    Set<String> allowedRoles = KoKuRoleUtil.getAllowedRoles(operation);
    for(Role r : roles) {
      if(allowedRoles.contains(r.getId())) {
        return true;
      }
    }
    return false;
  }
}
