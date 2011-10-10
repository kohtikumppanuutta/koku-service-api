/**
 * Service that consists of Person related operations
 */
package fi.koku.services.entity.person.v1;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.arcusys.tampere.hrsoa.entity.User;
import fi.arcusys.tampere.hrsoa.ws.ldap.LdapService;
import fi.koku.services.common.kahva.LdapServiceFactory;
import fi.koku.services.entity.customer.v1.AuditInfoType;
import fi.koku.services.entity.customer.v1.CustomerQueryCriteriaType;
import fi.koku.services.entity.customer.v1.CustomerServiceFactory;
import fi.koku.services.entity.customer.v1.CustomerServicePortType;
import fi.koku.services.entity.customer.v1.CustomerType;
import fi.koku.services.entity.customer.v1.CustomersType;
import fi.koku.services.entity.customer.v1.PicsType;
import fi.koku.services.entity.customer.v1.ServiceFault;

/**
 * @author mikkope
 *
 */
public class PersonService {

  private static final Logger LOG = LoggerFactory.getLogger(PersonService.class);
  private static String endpoint;
  
  private CustomerServicePortType customerService;
  private LdapService ldapService;
  
  public PersonService(){

    //Initialize customerservice
    CustomerServiceFactory customerServiceFactory = new CustomerServiceFactory(
        PersonConstants.CUSTOMER_SERVICE_USER_ID, PersonConstants.CUSTOMER_SERVICE_PASSWORD,
        PersonConstants.CUSTOMER_SERVICE_ENDPOINT);
    customerService = customerServiceFactory.getCustomerService();
    
    //Initialize LdapService (aka KahvaService)
    endpoint = "http://localhost:8280/kahvaservice-mock-0.0.1-SNAPSHOT/KahvaServiceEndpointBean";
    LdapServiceFactory f = new LdapServiceFactory(endpoint);
    ldapService = f.getOrganizationService();
    
        
  }
  
  /**
   * Returns list of persons with corresponding pics (hetus)
  * 
 * @param pics, string array of person Ids
 * @param domain, string constants that allows to add new implementations for different domains or backends
 * @param auditUserId, id of service user
 * @param auditComponentId, id of component that is using the service
 * @return list of Person -objects
   */
  public List<Person> getPersonsByPics(List<String> pics, final String domain, final String auditUserId, final String auditComponentId) {
    
    List<Person> personList = null;
    
    if(pics!=null && !pics.isEmpty()){
      
      //Check input parameters
      if(isNotNullOrEmpty(auditUserId) & isNotNullOrEmpty(auditComponentId)){
    
        if(PersonConstants.PERSON_SERVICE_DOMAIN_CUSTOMER.equals(domain)){
          //Search using CustomerService 
          personList = getPersonsFromCustomerDomainWithPicList(pics, auditUserId, auditComponentId);

        }else if(PersonConstants.PERSON_SERVICE_DOMAIN_OFFICER.equals(domain)){
          //Search using LdapService
          personList = getPersonsFromOfficerDomainWithPicList(pics);
          
        }else{
          LOG.error("No valid domain was set. Search cannot be done");
        }
        
      }else{
        LOG.debug("Input parameters were null or empty. domain="+domain+", auditUserId="+auditUserId+", auditComponentId="+auditComponentId);
      }
      
    }else{
      LOG.debug("List of pics is null or empty. Do nothing.");
    }
    
    return personList;
  }
  
 /**
  * Returns list of persons with corresponding uids (currect case Loora portal uids)
  * 
 * @param uids, string array of personIds
 * @param domain, string constants that allows to add new implementations for different domains or backends
 * @param auditUserId, id of service user
 * @param auditComponentId, id of component that is using the service
 * @return list of Person -objects
 */
public List<Person> getPersonsByUids(List<String> uids, final String domain, final String auditUserId, final String auditComponentId) {
    
    List<Person> personList = null;
    
    if(uids!=null && !uids.isEmpty()){
      
      //Check input parameters
      if(isNotNullOrEmpty(auditUserId) & isNotNullOrEmpty(auditComponentId)){
    
        if(PersonConstants.PERSON_SERVICE_DOMAIN_OFFICER.equals(domain)){
          //Search using LdapService
          personList = getPersonsFromOfficerDomainWithUidList(uids);
          
        }else{
          LOG.error("No valid domain was set. Search cannot be done");
        }
        
      }else{
        LOG.debug("Input parameters were null or empty. domain="+domain+", auditUserId="+auditUserId+", auditComponentId="+auditComponentId);
      }
      
    }else{
      LOG.debug("List of uids is null or empty. Do nothing.");
    }
    
    return personList;
  }
  
  private List<Person> getPersonsFromOfficerDomainWithUidList(final List<String> uids) {
    
    //Init array
    List<Person> personList = new ArrayList<Person>(uids.size());
    
    try {
        
        List<User> users = ldapService.getUsersByIds(uids);

        for (User userFromWS : users) {
          personList.add( new Person( userFromWS.getSsn(),userFromWS.getFirstName() ,userFromWS.getLastName()));  
        }
      
    } catch (Exception e) {
       LOG.error("Failed to get Person data from external source: WS.endpoint="+endpoint);
       personList = null;
    }
    return personList;
}

  private List<Person> getPersonsFromCustomerDomainWithPicList(List<String> pics, String auditUserId,
      String auditComponentId) {
        
    List<Person> personList = new ArrayList<Person>(pics.size());
    
    AuditInfoType customerAuditInfo = new AuditInfoType();
    customerAuditInfo.setComponent(auditComponentId);
    customerAuditInfo.setUserId(auditUserId);
    
    CustomerQueryCriteriaType query = new CustomerQueryCriteriaType();
    
    //Add pics to criteria
    PicsType picsType = new PicsType();
    for (String pic : pics) {
      picsType.getPic().add(pic);  
    }
    query.setPics(picsType);
    try {
      CustomersType customersType = customerService.opQueryCustomers(query, customerAuditInfo);
      
      //Traverse data from WS and put pic(hetu), firstname and lastname to personList -array
      for (CustomerType c : customersType.getCustomer()) {
        personList.add( new Person(c.getHenkiloTunnus(), c.getEtuNimi(), c.getStatus()) );
      }
      
    } catch (ServiceFault e) {
      LOG.error("Failed to query users from customerService. pics.size="+pics.size()+", auditUserId="+auditUserId+", auditComponentId="+auditComponentId,e);
      personList=null;
    }
        
    return personList;
  }

  
  private List<Person> getPersonsFromOfficerDomainWithPicList(List<String> pics) {

    //Initialize array
    List<Person> personList = new ArrayList<Person>(pics.size());
    
    try {
      
      //Loop the pic array
      //#TODO# This could be a place to improve
      //Current external LdapService interface is limited to query one user at time, which isn't efficient.
      //Usual usage is still only one user
      for (String pic : pics) {
        User userFromWS = ldapService.getUserBySSN(pic);
        personList.add( new Person( userFromWS.getSsn(),userFromWS.getFirstName() ,userFromWS.getLastName()));
      }
      
    } catch (Exception e) {
       LOG.error("Failed to get User data from external source: WS.endpoint="+endpoint);
       personList = null;
    }
    return personList;
  }

 
  private boolean isNotNullOrEmpty(String s){
    if(s!=null && !s.isEmpty()){
      return true;
    }else{
      return false;  
    }
  }
  
}