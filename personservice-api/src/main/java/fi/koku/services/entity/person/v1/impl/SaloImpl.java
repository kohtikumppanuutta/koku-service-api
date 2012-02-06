package fi.koku.services.entity.person.v1.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.koku.services.entity.customer.v1.AuditInfoType;
import fi.koku.services.entity.customer.v1.CustomerQueryCriteriaType;
import fi.koku.services.entity.customer.v1.CustomerServiceFactory;
import fi.koku.services.entity.customer.v1.CustomerServicePortType;
import fi.koku.services.entity.customer.v1.CustomerType;
import fi.koku.services.entity.customer.v1.CustomersType;
import fi.koku.services.entity.customer.v1.PicsType;
import fi.koku.services.entity.customer.v1.ServiceFault;
import fi.koku.services.entity.person.v1.Person;
import fi.koku.services.entity.person.v1.PersonConstants;
import fi.koku.services.entity.person.v1.PersonInfoProvider;
import fi.koku.services.utility.user.v1.UserIdsQueryParamType;
import fi.koku.services.utility.user.v1.UserInfoServicePortType;
import fi.koku.services.utility.user.v1.UserPicsQueryParamType;
import fi.koku.services.utility.user.v1.UserType;
import fi.koku.services.utility.user.v1.UsersType;
import fi.koku.services.utility.userinfo.v1.UserInfoServiceConstants;
import fi.koku.services.utility.userinfo.v1.UserInfoServiceFactory;

/**
 * Implementation class for PersonInfoProvider for Salo environment.
 * 
 * @author hanhian
 */
public class SaloImpl implements PersonInfoProvider {

  private static final Logger LOG = LoggerFactory.getLogger(SaloImpl.class);

  private CustomerServicePortType customerService;
  private UserInfoServicePortType userService;

  public SaloImpl() {
    // Initialize customerservice
    CustomerServiceFactory customerServiceFactory = new CustomerServiceFactory(
        PersonConstants.CUSTOMER_SERVICE_USER_ID, PersonConstants.CUSTOMER_SERVICE_PASSWORD,
        PersonConstants.CUSTOMER_SERVICE_ENDPOINT);
    customerService = customerServiceFactory.getCustomerService();

    UserInfoServiceFactory factory = new UserInfoServiceFactory(PersonConstants.USER_INFO_SERVICE_USER_ID,
        PersonConstants.USER_INFO_SERVICE_PASSWORD, PersonConstants.USER_INFO_SERVICE_ENDPOINT);
    userService = factory.getUserInfoService();
  }

  @Override
  public List<Person> getPersonsFromCustomerDomainWithUidList(List<String> uids, String auditUserId,
      String auditComponentId) {

    List<Person> personList = new ArrayList<Person>();
    UserIdsQueryParamType uidsQueryType = new UserIdsQueryParamType();
    uidsQueryType.setDomain(UserInfoServiceConstants.USER_INFO_SERVICE_DOMAIN_CUSTOMER);
    uidsQueryType.getId().addAll(uids);
    try {
      UsersType usersType = userService.opGetUsersByIds(uidsQueryType);
      for (UserType userType : usersType.getUser()) {
        personList.add(new Person(userType.getPic(), userType.getFirstname(), userType.getLastname()));
      }
    } catch (Exception e) {
      LOG.error("Failed to get persons from UserInfoService.", e);
    }

    // NOTICE: You could also ask Person information from CustomerService
    // using hetu
    // personList = getPersonsFromCustomerDomainWithPicList(pics,
    // auditUserId, auditComponentId);
    return personList;
  }

  @Override
  public List<Person> getPersonsFromOfficerDomainWithUidList(List<String> uids, String auditUserId,
      String auditComponentId) {

    UserIdsQueryParamType uidsQueryType = new UserIdsQueryParamType();
    uidsQueryType.setDomain(UserInfoServiceConstants.USER_INFO_SERVICE_DOMAIN_OFFICER);
    uidsQueryType.getId().addAll(uids);
    UsersType users = null;

    try {
      users = userService.opGetUsersByIds(uidsQueryType);
    } catch (Exception e) {
      LOG.error("Failed to get User data from user info service", e);
    }

    List<Person> personList = new ArrayList<Person>(uids.size());
    if (users != null) {
      for (UserType emp : users.getUser()) {
        personList.add(new Person(emp.getPic(), emp.getFirstname(), emp.getLastname()));
      }
    }

    return personList;
  }

  @Override
  public List<Person> getPersonsFromCustomerDomainWithPicList(List<String> pics, String auditUserId,
      String auditComponentId) {

    List<Person> personList = new ArrayList<Person>(pics.size());
    AuditInfoType customerAuditInfo = new AuditInfoType();
    customerAuditInfo.setComponent(auditComponentId);
    customerAuditInfo.setUserId(auditUserId);

    CustomerQueryCriteriaType query = new CustomerQueryCriteriaType();

    // Add pics to criteria
    PicsType picsType = new PicsType();
    for (String pic : pics) {
      picsType.getPic().add(pic);
    }
    query.setPics(picsType);
    try {
      CustomersType customersType = customerService.opQueryCustomers(query, customerAuditInfo);

      // Traverse data from WS and put pic(hetu), firstname and lastname to
      // personList -array
      for (CustomerType c : customersType.getCustomer()) {
        personList.add(new Person(c.getHenkiloTunnus(), c.getEtuNimi(), c.getSukuNimi()));
      }

    } catch (ServiceFault e) {
      LOG.error("Failed to query users from customerService. pics.size=" + pics.size() + ", auditUserId=" + auditUserId
          + ", auditComponentId=" + auditComponentId, e);
      personList = null;
    }
    return personList;
  }

// Tällä metodilla kutsu saadaan tehtyä LDAP:piin
//  @Override
//  public List<Person> getPersonsFromCustomerDomainWithPicList(List<String> pics, String auditUserId,
//      String auditComponentId) {
//    
//    UserPicsQueryParamType picsQueryType = new UserPicsQueryParamType();
//    picsQueryType.setDomain(UserInfoServiceConstants.USER_INFO_SERVICE_DOMAIN_CUSTOMER);
//    picsQueryType.getPic().addAll(pics);
//    UsersType users = null;
//    
//    try {
//      users = userService.opGetUsersByPics(picsQueryType);
//    } catch (Exception e) {
//      LOG.error("Failed to get User data from user info service", e);
//    }
//
//    List<Person> personList = new ArrayList<Person>(pics.size());
//    if (users != null) {
//      for (UserType emp : users.getUser()) {
//        personList.add(new Person(emp.getPic(), emp.getFirstname(), emp.getLastname()));
//      }
//    }
//
//    return personList;    
//  }

  @Override
  public List<Person> getPersonsFromOfficerDomainWithPicList(List<String> pics, String auditUserId,
      String auditComponentId) {

    UserPicsQueryParamType picsQueryType = new UserPicsQueryParamType();
    picsQueryType.setDomain(UserInfoServiceConstants.USER_INFO_SERVICE_DOMAIN_OFFICER);
    picsQueryType.getPic().addAll(pics);
    UsersType users = null;

    try {
      users = userService.opGetUsersByPics(picsQueryType);
    } catch (Exception e) {
      LOG.error("Failed to get User data from user info service", e);
    }

    List<Person> personList = new ArrayList<Person>(pics.size());
    if (users != null) {
      for (UserType emp : users.getUser()) {
        personList.add(new Person(emp.getPic(), emp.getFirstname(), emp.getLastname()));
      }
    }

    return personList;
  }
}