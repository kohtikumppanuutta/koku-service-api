/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.utility.authorizationinfo.util;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.koku.KoKuException;
import fi.koku.KoKuNotAuthorizedException;
import fi.koku.auth.KoKuRoleUtil;
import fi.koku.services.utility.authorizationinfo.v1.model.Role;

/**
 * Authorization related utility functions.
 * 
 * @author laukksa
 *
 */
public class AuthUtils {

  private static Logger logger = LoggerFactory.getLogger(AuthUtils.class);
  
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
  
  /**
   * Throw KoKuNotAuthorizedException if not authorized.
   * 
   * @param operation
   * @param roles
   */
  public static void requirePermission(String operation, String userId, List<Role> roles) {
    if (!isOperationAllowed(operation, roles)) {
      logger.info("Access denied: {}, {}, {}", new Object[] {operation, userId, roles});
      throw new KoKuNotAuthorizedException(KoKuException.NOT_AUTHORIZED_ERROR_CODE, KoKuException.NOT_AUTHORIZED_ERROR_MESSAGE);
    }
  }
}
