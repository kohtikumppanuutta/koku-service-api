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
