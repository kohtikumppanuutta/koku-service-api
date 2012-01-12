/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.utility.emplyeeinfo.v1;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.koku.services.utility.employee.v1.EmployeeInfoService;
import fi.koku.services.utility.employee.v1.EmployeeInfoServicePortType;

/**
 * Simple factory class to provide access to Employee Info service.
 * 
 * @author hanhian
 */
public class EmployeeInfoServiceFactory {

  private String uid;
  private String pwd;
  private String endpointBaseUrl;
  private final URL wsdlLocation = getClass().getClassLoader().getResource("wsdl/employeeInfoService.wsdl");

  private static Logger log = LoggerFactory.getLogger(EmployeeInfoServiceFactory.class);

  public EmployeeInfoServiceFactory(String uid, String pwd, String endpointBaseUrl) {
    this.uid = uid;
    this.pwd = pwd;
    this.endpointBaseUrl = endpointBaseUrl;
  }

  public EmployeeInfoServicePortType getEmployeeInfoService() {
    if (wsdlLocation == null)
      log.error("wsdllocation=null");

    EmployeeInfoService service = new EmployeeInfoService(wsdlLocation, new QName(
        "http://services.koku.fi/utility/employee/v1", "employeeInfoService"));
    EmployeeInfoServicePortType port = service.getEmployeeInfoServiceSoap11Port();
    String epAddr = endpointBaseUrl + "/EmployeeInfoServiceEndpointBean";
    log.debug("ep addr: " + epAddr);

    ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epAddr);
    ((BindingProvider) port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, uid);
    ((BindingProvider) port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, pwd);
    log.debug("Created employeeInfoServiceClient with epAddr=" + epAddr + ", uid=" + uid);

    return port;
  }
}