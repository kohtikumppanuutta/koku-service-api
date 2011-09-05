package fi.koku.services.common.kahva;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import fi.arcusys.tampere.hrsoa.ws.ldap.LdapService;
import fi.arcusys.tampere.hrsoa.ws.ldap.LdapService_Service;


/**
 * Factory for Kahva LDAP service
 * 
 * @author mikkope
 *
 */
public class LdapServiceFactory {

  private String epAddr;
  private final URL wsdlLocation = getClass().getClassLoader().getResource("/wsdl/ldapService.wsdl");

  public LdapServiceFactory(String endpointAddr) {
    this.epAddr = endpointAddr;
  }

  
  public LdapService getOrganizationService() {
    LdapService_Service service = new LdapService_Service(wsdlLocation, new QName(
      "http://www.arcusys.fi/tampere/hrsoa/ws/ldap/", "ldapService"));

    LdapService port = service.getLdapServiceSOAP();
    ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epAddr);
    return port;
  }

  
}
