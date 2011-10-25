package fi.koku.services.utility.authorizationinfo.v1;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.koku.services.utility.authorization.v1.AuthorizationInfoService;
import fi.koku.services.utility.authorization.v1.AuthorizationInfoServicePortType;
import fi.koku.services.utility.authorizationinfo.v1.impl.AuthorizationInfoServiceDummyImpl;
import fi.koku.services.utility.authorizationinfo.v1.impl.AuthorizationInfoServiceWSImpl;

/**
 * Factory for authorization service.
 * 
 * @author aspluma
 */
public class AuthorizationInfoServiceFactory {
  private String uid;
  private String pwd;
  private String endpointPathUrl;
  private final URL wsdlLocation = getClass().getClassLoader().getResource("wsdl/authorizationInfoService.wsdl");
  
  private static Logger log = LoggerFactory.getLogger(AuthorizationInfoServiceFactory.class);

  public AuthorizationInfoServiceFactory(String uid, String pwd, String endpointPathUrl) {
    this.uid = uid;
    this.pwd = pwd;
    this.endpointPathUrl = endpointPathUrl;
  }

  public fi.koku.services.utility.authorizationinfo.v1.AuthorizationInfoService getAuthorizationInfoService() {
    return new AuthorizationInfoServiceWSImpl(getAuthorizationInfoServicePortType());
  }

  public fi.koku.services.utility.authorizationinfo.v1.AuthorizationInfoService getAuthorizationInfoService(String implType) {
    if("mock".equals(implType))
        return new AuthorizationInfoServiceDummyImpl();
    return getAuthorizationInfoService();
  }
  
  private AuthorizationInfoServicePortType getAuthorizationInfoServicePortType() {
    if(wsdlLocation == null)
      log.error("wsdllocation=null");
    AuthorizationInfoService service = new AuthorizationInfoService(wsdlLocation, new QName(
        "http://services.koku.fi/utility/authorization/v1", "authorizationInfoService"));
    AuthorizationInfoServicePortType port = service.getAuthorizationInfoServiceSoap11Port();
    
    String epAddr = endpointPathUrl + "/AuthorizationInfoServiceEndpointBean";
    log.debug("ep addr: "+epAddr);

    ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epAddr);
    ((BindingProvider) port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, uid);
    ((BindingProvider) port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, pwd);
    return port;
  }

}
