package fi.koku.services.entity.customercommunication.v1;

/**
 * Factory for CustomerCommunication service
 * 
 * 
 * @author dkudinov
 *
 */

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.tampere.contract.municipalityportal.ccs.CustomerCommunicationService;
import fi.tampere.contract.municipalityportal.ccs.CustomerCommunicationServicePortType;

public class CustomerCommunicationServiceFactory {
  private String uid;
  private String pwd;
  private String endpointUrl;
  private final URL wsdlLocation = getClass().getClassLoader().getResource("wsdl/CustomerCommunicationService.wsdl");
  private static Logger log = LoggerFactory.getLogger(CustomerCommunicationServiceFactory.class);

  public CustomerCommunicationServiceFactory(String uid, String pwd, String endpointUrl) {
    this.uid = uid;
    this.pwd = pwd;
    this.endpointUrl = endpointUrl;
  }

  public CustomerCommunicationServicePortType getCustomerCommunicationService() {
    if(wsdlLocation == null)
      log.error("wsdllocation=null");
    CustomerCommunicationService service = new CustomerCommunicationService(wsdlLocation, new QName(
        "http://tampere.fi/contract/municipalityportal/ccs", "CustomerCommunicationService"));
    CustomerCommunicationServicePortType port = service.getCustomerCommunicationServicePort();
    String epAddr = endpointUrl;
    log.debug("ep addr: "+epAddr);
  
    ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epAddr);
    ((BindingProvider) port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, uid);
    ((BindingProvider) port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, pwd);
    
    return port;
    
  }
  
  
  
  
}
