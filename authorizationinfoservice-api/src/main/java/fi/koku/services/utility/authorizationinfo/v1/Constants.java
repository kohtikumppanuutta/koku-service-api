/*
 * Copyright 2012 Ixonos Plc, Finland. All rights reserved.
 * 
 * This file is part of Kohti kumppanuutta.
 *
 * This file is licensed under GNU LGPL version 3.
 * Please see the 'license.txt' file in the root directory of the package you received.
 * If you did not receive a license, please contact the copyright holder
 * (http://www.ixonos.com/).
 *
 */
/**
 * This class contains constants that should be used in AuthorizationInfoService
 */
package fi.koku.services.utility.authorizationinfo.v1;

import fi.koku.services.utility.authorizationinfo.v1.model.Registry;
import fi.koku.services.utility.authorizationinfo.v1.model.Role;

/**
 * @author mikkope
 *
 */
public class Constants {
  
  
  /**
   * Domain constants
   * 
   * Domain constants can be used in the AuthorizationInfoService-implementation for example
   * to decide to what sub-system the query will be directed.
   * 
   * In case of using DOMAIN_DAYCARE as a domain, the implementation could limit the
   * getUsersRoles()-query to only DayCare-related subsystems instead of asking this 
   * information from all systems.
   */
  public static String DOMAIN_DAYCARE = "DAYCARE";
  public static String DOMAIN_LOG = "LOG";
  public static String DOMAIN_CHILD_WELFARE_CLINIC;//Neuvola in Finnish
  
  //REGISTRIES (Potilastieto, päivähoidon asiakastieto)
  public static Registry REGISTRY_PATIENT_INFORMATION = new Registry("PATIENT_INFORMATION");
  public static Registry REGISTRY_DAYCARE_CUSTOMER_INFORMATION = new Registry("DAYCARE_CUSTOMER_INFORMATION");

  //ROLES
  public static Role ROLE_LOK_ADMIN = new Role("ROLE_LOK_ADMIN");
  public static Role ROLE_LOK_LOG_ADMIN = new Role("ROLE_LOK_LOG_ADMIN");
  

}
