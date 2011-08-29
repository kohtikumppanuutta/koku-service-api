package fi.koku.services.entity.customer.v1;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

/**
 * Factory for customer service.
 * 
 * @author aspluma
 */
public class CustomerServiceFactory {
  private String uid;
  private String pwd;
  private String endpointBaseUrl;
  private String implVersion = "0.0.1-SNAPSHOT";
  private final URL wsdlLocation = getClass().getClassLoader().getResource("/wsdl/customerService.wsdl");

  public CustomerServiceFactory(String uid, String pwd, String endpointBaseUrl) {
    this.uid = uid;
    this.pwd = pwd;
    this.endpointBaseUrl = endpointBaseUrl;
  }

  public CustomerServicePortType getCustomerService() {
    CustomerService service = new CustomerService(wsdlLocation, new QName(
        "http://services.koku.fi/entity/customer/v1", "customerService"));
    CustomerServicePortType port = service.getCustomerServiceSoap11Port();
    String epAddr = endpointBaseUrl + "/customer-service-" + implVersion + "/CustomerServiceBean";

    ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epAddr);
    ((BindingProvider) port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, uid);
    ((BindingProvider) port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, pwd);
    return port;
  }

}
