/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.common.kahva;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
  private final URL wsdlLocation = getClass().getClassLoader().getResource("wsdl/ldapService.wsdl");
  private static Logger log = LoggerFactory.getLogger(LdapServiceFactory.class);

  public LdapServiceFactory(String endpointAddr) {
    this.epAddr = endpointAddr;
  }

  
  public LdapService getOrganizationService() {
    if(wsdlLocation == null)
      log.error("wsdllocation=null");
    LdapService_Service service = new LdapService_Service(wsdlLocation, new QName(
      "http://www.arcusys.fi/tampere/hrsoa/ws/ldap/", "ldapService"));
    log.debug("ep addr: "+epAddr);

    LdapService port = service.getLdapServiceSOAP();
    ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epAddr);
    return port;
  }

  
}
