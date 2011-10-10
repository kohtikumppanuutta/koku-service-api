/**
 * Centralized place for PersonService related constants
 */
package fi.koku.services.entity.person.v1;

/**
 * @author mikkope
 *
 */
public class PersonConstants {

  final public static String CUSTOMER_SERVICE_ENDPOINT = "http://localhost:8180/customer-service-0.0.1-SNAPSHOT/CustomerServiceEndpointBean?wsdl";
  
  final public static String CUSTOMER_SERVICE_USER_ID = "marko";
  final public static String CUSTOMER_SERVICE_PASSWORD = "marko";
  
  final public static String COMPONENT_PERSON_SERVICE = "person_service";
  
  final public static String PERSON_SERVICE_DOMAIN_CUSTOMER = "person_service_domain_customer";
  final public static String PERSON_SERVICE_DOMAIN_OFFICER = "person_service_domain_officer";
}
