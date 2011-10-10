/**
 * This class contains Family service related constants
 */
package fi.koku.services.entity.family;

import fi.koku.settings.KoKuPropertiesUtil;

/**
 * @author mikkope
 *
 */
public class FamilyConstants {

  //Customer service related 
  final public static String CUSTOMER_SERVICE_ENDPOINT = KoKuPropertiesUtil.get("customer.service.endpointaddress");
  final public static String CUSTOMER_SERVICE_USER_ID = "marko";
  final public static String CUSTOMER_SERVICE_PASSWORD = "marko";
  
  //Community service related
  final public static String COMMUNITY_SERVICE_ENDPOINT = KoKuPropertiesUtil.get("community.service.endpointaddress");
  final public static String COMMUNITY_SERVICE_USER_ID = "marko";
  final public static String COMMUNITY_SERVICE_PASSWORD = "marko";
  final public static String COMMUNITY_TYPE_GUARDIAN_COMMUNITY = "guardian_community";
  final public static String COMMUNITY_TYPE_FAMILY = "family_community";
  final public static String ROLE_DEPENDANT = "dependant";
  final public static String ROLE_GUARDIAN = "guardian";
  
}
