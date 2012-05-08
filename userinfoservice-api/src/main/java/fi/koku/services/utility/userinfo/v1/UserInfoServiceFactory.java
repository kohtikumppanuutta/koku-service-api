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
package fi.koku.services.utility.userinfo.v1;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.koku.services.utility.user.v1.UserInfoService;
import fi.koku.services.utility.user.v1.UserInfoServicePortType;

/**
 * Simple factory class to provide access to User Info service.
 * 
 * @author hanhian
 */
public class UserInfoServiceFactory {

  private String uid;
  private String pwd;
  private String endpointBaseUrl;
  private final URL wsdlLocation = getClass().getClassLoader().getResource("wsdl/userInfoService.wsdl");

  private static Logger log = LoggerFactory.getLogger(UserInfoServiceFactory.class);

  public UserInfoServiceFactory(String uid, String pwd, String endpointBaseUrl) {
    this.uid = uid;
    this.pwd = pwd;
    this.endpointBaseUrl = endpointBaseUrl;
  }

  public UserInfoServicePortType getUserInfoService() {
    if (wsdlLocation == null)
      log.error("wsdllocation=null");

    UserInfoService service = new UserInfoService(wsdlLocation, new QName(
        "http://services.koku.fi/utility/user/v1", "userInfoService"));
    UserInfoServicePortType port = service.getUserInfoServiceSoap11Port();
    String epAddr = endpointBaseUrl + "/UserInfoServiceEndpointBean";
    log.debug("ep addr: " + epAddr);

    ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epAddr);
    ((BindingProvider) port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, uid);
    ((BindingProvider) port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, pwd);
    log.debug("Created userInfoServiceClient with epAddr=" + epAddr + ", uid=" + uid);

    return port;
  }
}