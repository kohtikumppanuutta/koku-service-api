package fi.koku.services.entity.kks.v1;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Helper class for creating KKS WS endpoints.
 * 
 * @author Ixonos / tuomape
 */
public class KksServiceFactory {
  private static Logger log = LoggerFactory.getLogger(KksServiceFactory.class);
  public static String SERVICE_AREA_DAYCARE = "kk.servicearea.daycare";
  public static String SERVICE_AREA_BASIC_EDUCATION = "kk.servicearea.basicEducation";
  public static String SERVICE_AREA_CHILD_HEALTH = "kk.servicearea.childHealth";

  private String uid;
  private String pwd;
  private String endpointBaseUrl;

  private final URL KKS_WSDL_LOCATION = getClass().getClassLoader().getResource("/wsdl/kksService.wsdl");

  public KksServiceFactory(String uid, String pwd, String endpointBaseUrl) {
    this.uid = uid;
    this.pwd = pwd;
    this.endpointBaseUrl = endpointBaseUrl;
  }

  public KksServicePortType getKksService() {
    if(KKS_WSDL_LOCATION == null)
      log.error("wsdllocation=null");
    KksService service = new KksService(KKS_WSDL_LOCATION, new QName("http://services.koku.fi/entity/kks/v1",
        "kksService"));
    KksServicePortType kksServicePort = service.getKksServiceSoap11Port();
    String epAddr = endpointBaseUrl + "/KksServiceEndpointBean";
    log.debug("ep addr: "+epAddr);

    ((BindingProvider) kksServicePort).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epAddr);
    ((BindingProvider) kksServicePort).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, uid);
    ((BindingProvider) kksServicePort).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, pwd);
    return kksServicePort;
  }

}
