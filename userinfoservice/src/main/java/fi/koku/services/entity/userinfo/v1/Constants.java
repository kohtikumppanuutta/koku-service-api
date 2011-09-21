/**
 * This class contains constants that should be used in UserInfoService
 */
package fi.koku.services.entity.userinfo.v1;

import fi.koku.services.entity.userinfo.v1.model.Registry;
import fi.koku.services.entity.userinfo.v1.model.Role;

/**
 * @author mikkope
 *
 */
public class Constants {
  
  
  /**
   * Domain constants
   * 
   * Domain constants can be used in the UserInfoService-implementation for example
   * to decide to what sub-system the query will be directed.
   * 
   * In case of using DOMAIN_DAYCARE as a domain, the implementation could limit the
   * getUsersRoles()-query to only DayCare-related subsystems instead of asking this 
   * information from all systems.
   */
  public static String DOMAIN_DAYCARE = "DAYCARE";
  public static String DOMAIN_LOG = "LOG";
  
  //REGISTRIES (Potilastieto, päivähoidon asiakastieto)
  public static Registry REGISTRY_PATIENT_INFORMATION = new Registry("PATIENT_INFORMATION");
  public static Registry REGISTRY_DAYCARE_CUSTOMER_INFORMATION = new Registry("DAYCARE_CUSTOMER_INFORMATION");

  //ROLES
  public static Role ROLE_LOG_VIEWER = new Role("ROLE_LOG_VIEWER");
  public static Role ROLE_LOG_ADMIN = new Role("ROLE_LOG_ADMIN");
  

}
