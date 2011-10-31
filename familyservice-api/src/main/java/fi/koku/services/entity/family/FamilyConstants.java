/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
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

  //Community service related
  final public static String COMMUNITY_SERVICE_ENDPOINT = KoKuPropertiesUtil.get("community.service.endpointaddress");
  final public static String COMMUNITY_TYPE_GUARDIAN_COMMUNITY = "guardian_community";
  final public static String COMMUNITY_TYPE_FAMILY = "family_community";
  final public static String ROLE_DEPENDANT = "dependant";
  final public static String ROLE_GUARDIAN = "guardian";
  
}
