/**
 * This class contains constants related to UserInformationService
 */
package fi.koku.services.entity.userinformation;

import fi.koku.settings.KoKuPropertiesUtil;

/**
 * @author mikkope
 *
 */
public class UserInformationConstants {

  final public static String USER_INFORMATION_SERVICE_FULL_URL = KoKuPropertiesUtil.get("userinformation.service.endpointaddress.full.url");
  
}
