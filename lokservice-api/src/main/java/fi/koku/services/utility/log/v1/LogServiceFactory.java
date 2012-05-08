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
/**
 * Service factory class for LogService
 */
package fi.koku.services.utility.log.v1;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Simple factory class to provide access to Log service.
 * 
 * @author mikkope
 */
public class LogServiceFactory {

  private String uid;
  private String pwd;
  private String endpointBaseUrl;
  private final URL wsdlLocation = getClass().getClassLoader().getResource("wsdl/logService.wsdl");

  private static Logger log = LoggerFactory.getLogger(LogServiceFactory.class);

  public LogServiceFactory(String uid, String pwd, String endpointBaseUrl) {
    this.uid = uid;
    this.pwd = pwd;
    this.endpointBaseUrl = endpointBaseUrl;
  }

  public LogServicePortType getLogService() {
    if (wsdlLocation == null)
      log.error("wsdllocation=null");
    LogService service = new LogService(wsdlLocation, new QName("http://services.koku.fi/utility/log/v1", "logService"));
    LogServicePortType port = service.getLogServiceSoap11Port();
    String epAddr = endpointBaseUrl + "/LogServiceEndpointBean";
    log.debug("ep addr: " + epAddr);

    ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epAddr);
    ((BindingProvider) port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, uid);
    ((BindingProvider) port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, pwd);
    log.debug("Created logServiceClient with epAddr=" + epAddr + ", uid=" + uid);

    return port;
  }
}