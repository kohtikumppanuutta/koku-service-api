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
 * Centralized place for PersonService related constants
 */
package fi.koku.services.entity.person.v1;

import fi.koku.settings.KoKuPropertiesUtil;

/**
 * @author mikkope
 *
 */
public class PersonConstants {

  //Customer service related information
  final public static String CUSTOMER_SERVICE_ENDPOINT = KoKuPropertiesUtil.get("customer.service.endpointaddress");
  final public static String CUSTOMER_SERVICE_USER_ID = "marko";
  final public static String CUSTOMER_SERVICE_PASSWORD = "marko";
  
  //Kahva/LDAPservice related information #TODO# Consider harmonizing endpointaddress with others
  final public static String KAHVA_SERVICE_FULL_URL = KoKuPropertiesUtil.get("kahva.service.endpointaddress.full.url");
  
  final public static String USER_INFORMATION_SERVICE_FULL_URL = KoKuPropertiesUtil.get("userinformation.service.endpointaddress");
  final public static String USER_INFORMATION_SERVICE_USER_ID = "TODO";
  final public static String USER_INFORMATION_SERVICE_PASSWORD = "TODO";
  
  
  final public static String PERSON_SERVICE_DOMAIN_CUSTOMER = "person_service_domain_customer";
  final public static String PERSON_SERVICE_DOMAIN_OFFICER = "person_service_domain_officer";
  
  final public static String USER_INFO_SERVICE_ENDPOINT = KoKuPropertiesUtil
      .get("user.info.service.endpointaddress");
  final public static String USER_INFO_SERVICE_USER_ID = KoKuPropertiesUtil
      .get("person.service.user.info.service.user.id");
  final public static String USER_INFO_SERVICE_PASSWORD = KoKuPropertiesUtil
      .get("person.service.user.info.service.password");
  
  
  final public static String PERSON_PROVIDER_IMPL_CLASS_NAME = KoKuPropertiesUtil
      .get("person.service.person.info.provider.class.name");
}
