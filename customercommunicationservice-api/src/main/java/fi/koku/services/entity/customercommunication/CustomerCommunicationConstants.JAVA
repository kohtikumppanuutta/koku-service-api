/**
 * This class contains constants related to CustomerCommunicationConstants
 */
package fi.koku.services.entity.userinformation;

import fi.koku.settings.KoKuPropertiesUtil;

/**
 * @author dkudinov
 *
 */
public class CustomerCommunicationConstants {

  final public static String CUSTOMER_COMMUNICATION_SERVICE_FULL_URL = KoKuPropertiesUtil.get("customercommunication.service.endpointaddress.full.url");
  
}
