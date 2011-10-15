package fi.koku.services.entity.userinformation.v1;

/**
 * Factory for UserInformation service
 * 
 * 
 * @author mikkope
 *
 */

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import fi.tampere.contract.municipalityportal.uis.UserInformationService;
import fi.tampere.contract.municipalityportal.uis.UserInformationServicePortType;

public class UserInformationServiceFactory {
  private String uid;
  private String pwd;
  private String endpointUrl;
  private final URL wsdlLocation = getClass().getClassLoader().getResource("wsdl/customerService.wsdl");

  public UserInformationServiceFactory(String uid, String pwd, String endpointUrl) {
    this.uid = uid;
    this.pwd = pwd;
    this.endpointUrl = endpointUrl;
  }

  public UserInformationServicePortType getUserInformationService() {
      UserInformationService service = new UserInformationService(wsdlLocation, new QName(
        "http://tampere.fi/contract/municipalityportal/uis", "UserInformationService"));
      UserInformationServicePortType port = service.getUserInformationServicePort();
    String epAddr = endpointUrl;
  
    ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epAddr);
    ((BindingProvider) port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, uid);
    ((BindingProvider) port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, pwd);
    
    return port;
    
  }
  
  
  
  
}
