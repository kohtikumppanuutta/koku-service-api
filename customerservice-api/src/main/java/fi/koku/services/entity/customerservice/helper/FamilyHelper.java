/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.entity.customerservice.helper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.koku.services.entity.customerservice.exception.FamilyNotFoundException;
import fi.koku.services.entity.customerservice.exception.TooManyFamiliesException;
import fi.koku.services.entity.customerservice.model.DependantsAndFamily;
import fi.koku.services.entity.customerservice.model.Family;
import fi.koku.services.entity.customerservice.model.Person;
import fi.koku.services.entity.customerservice.model.Dependant;
import fi.koku.services.entity.customerservice.model.FamilyIdAndFamilyMembers;
import fi.koku.services.entity.customerservice.model.FamilyMember;
import fi.koku.services.entity.customerservice.model.CommunityRole;
import fi.koku.services.entity.community.v1.CommunitiesType;
import fi.koku.services.entity.community.v1.CommunityQueryCriteriaType;
import fi.koku.services.entity.community.v1.CommunityServiceConstants;
import fi.koku.services.entity.community.v1.CommunityServiceFactory;
import fi.koku.services.entity.community.v1.CommunityServicePortType;
import fi.koku.services.entity.community.v1.CommunityType;
import fi.koku.services.entity.community.v1.MemberPicsType;
import fi.koku.services.entity.community.v1.MemberType;
import fi.koku.services.entity.community.v1.MembersType;
import fi.koku.services.entity.community.v1.ServiceFault;
import fi.koku.services.entity.customer.v1.CustomerQueryCriteriaType;
import fi.koku.services.entity.customer.v1.CustomerServiceConstants;
import fi.koku.services.entity.customer.v1.CustomerServiceFactory;
import fi.koku.services.entity.customer.v1.CustomerServicePortType;
import fi.koku.services.entity.customer.v1.CustomerType;
import fi.koku.services.entity.customer.v1.CustomersType;
import fi.koku.services.entity.customer.v1.PicsType;

/**
 * Family related helper class.
 * 
 * @author hurulmi
 *
 */
public class FamilyHelper {

  private static Logger logger = LoggerFactory.getLogger(FamilyHelper.class);
  
  private CustomerServicePortType customerService;
  
  private CommunityServicePortType communityService;
  
  private String componentName;
  
  public FamilyHelper(CustomerServicePortType customerService, CommunityServicePortType communityService,
      String componentName) {
    super();
    this.customerService = customerService;
    this.communityService = communityService;
    this.componentName = componentName;
  }  
  
  /**
   * Returns user's dependants.
   * 
   * @throws ServiceFault 
   * @throws fi.koku.services.entity.customer.v1.ServiceFault 
   */
  public DependantsAndFamily getDependantsAndFamily(String userPic, Family userFamily) throws ServiceFault, fi.koku.services.entity.customer.v1.ServiceFault {
    List<Dependant> dependants = new ArrayList<Dependant>();
    CommunityQueryCriteriaType communityQueryCriteria = new CommunityQueryCriteriaType();
    communityQueryCriteria.setCommunityType(CommunityServiceConstants.COMMUNITY_TYPE_GUARDIAN_COMMUNITY);
    
    MemberPicsType memberPics = new MemberPicsType();
    memberPics.getMemberPic().add(userPic);
    communityQueryCriteria.setMemberPics(memberPics);
    
    CommunitiesType communitiesType = communityService.opQueryCommunities(communityQueryCriteria, CommunityServiceFactory.createAuditInfoType(componentName, userPic));

    ArrayList<String> depPics = new ArrayList<String>();
    
    if (communitiesType != null) {
      List<CommunityType> communities = communitiesType.getCommunity();
      Iterator<CommunityType> ci = communities.iterator();
      
      while (ci.hasNext()) {
        CommunityType community = ci.next();
        MembersType membersType = community.getMembers();
        List<MemberType> members = membersType.getMember();
        Iterator<MemberType> mi = members.iterator();
        while (mi.hasNext()) {
          MemberType member = mi.next();
          if (member.getRole().equals(CommunityServiceConstants.COMMUNITY_ROLE_DEPENDANT)) {
            depPics.add(member.getPic());
          }
        }
      }
    }
    
    PicsType picsType = new PicsType();
    picsType.getPic().addAll(depPics);
    CustomerQueryCriteriaType customerQueryCriteriaType = new CustomerQueryCriteriaType();
    customerQueryCriteriaType.setPics(picsType);
    CustomersType customersType = null;
    
    customersType = customerService.opQueryCustomers(customerQueryCriteriaType, CustomerServiceFactory.createAuditInfoType(componentName, userPic));

    if (customersType != null) {
      List<CustomerType> customers = customersType.getCustomer();
      Iterator<CustomerType> ci = customers.iterator();
      while (ci.hasNext()) {
        CustomerType customer = ci.next();
        dependants.add(new Dependant(customer));
      }
    }
    
    // next check if dependant is member of user's family
    
    DependantsAndFamily dependantsAndFamily = new DependantsAndFamily();
    
    if (userFamily != null) {
      Iterator<Dependant> di = dependants.iterator();
      while (di.hasNext()) {
        Dependant d = di.next();
        
        List<MemberType> members = userFamily.getAllMembers();
        Iterator<MemberType> mi = members.iterator();
        while (mi.hasNext()) {
          MemberType member = mi.next();
          // if dependant belongs to user's family then set Dependant.memberOfUserFamily
          if (d.getPic().equals(member.getPic())) {
            d.setMemberOfUserFamily(true);
          }
        }
      }
      dependantsAndFamily.setFamily(userFamily);
    }
    
    dependantsAndFamily.setDependants(dependants);
    
    if (logger.isDebugEnabled()) {
      Iterator<Dependant> it = dependants.iterator();
      logger.debug("getDependantsAndFamily(), returning dependants:");
      while (it.hasNext()) {
        logger.debug("dep pic: " + it.next().getPic());
      }
      logger.debug("--");
    }
    
    return dependantsAndFamily;
  }
    
  
  /**
   * Returns all other members of the user's family except dependants.
   * 
   * @throws ServiceFault 
   * @throws fi.koku.services.entity.customer.v1.ServiceFault 
   */
  public FamilyIdAndFamilyMembers getOtherFamilyMembers(String userPic, Family family) throws ServiceFault, fi.koku.services.entity.customer.v1.ServiceFault {
    
    List<Dependant> dependants = getDependantsAndFamily(userPic, family).getDependants();
    Set<String> dependantPics = new HashSet<String>();
    Iterator<Dependant> di = dependants.iterator();
    while (di.hasNext()) {
      dependantPics.add(di.next().getPic());
    }
    
    List<FamilyMember> otherFamilyMembers = new ArrayList<FamilyMember>();
    CommunityQueryCriteriaType communityQueryCriteria = new CommunityQueryCriteriaType();
    communityQueryCriteria.setCommunityType(CommunityServiceConstants.COMMUNITY_TYPE_FAMILY);
    
    MemberPicsType memberPics = new MemberPicsType();
    memberPics.getMemberPic().add(userPic);
    communityQueryCriteria.setMemberPics(memberPics);
    
    CommunitiesType communitiesType = communityService.opQueryCommunities(communityQueryCriteria, CommunityServiceFactory.createAuditInfoType(componentName, userPic));
    
    String familyId = "";
    
    if (communitiesType != null) {
      List<CommunityType> communities = communitiesType.getCommunity();
      Iterator<CommunityType> ci = communities.iterator();
      
      List<String> otherFamilyMemberPics = new ArrayList<String>();
      List<String> otherFamilyMemberRoles = new ArrayList<String>();
      
      while (ci.hasNext()) {
        CommunityType community = ci.next();
        familyId = community.getId(); // communityService.opQueryCommunities should return only one community
        MembersType membersType = community.getMembers();
        List<MemberType> members = membersType.getMember();
        Iterator<MemberType> mi = members.iterator();
        while (mi.hasNext()) {
          MemberType member = mi.next();
          if (!dependantPics.contains(member.getPic()) && !userPic.equals(member.getPic())) {
            
            otherFamilyMemberPics.add(member.getPic());
            otherFamilyMemberRoles.add(member.getRole());
          }
        }
      }
      
      if (otherFamilyMemberPics.size() > 0) {
        CustomersType customersType = null;
        CustomerQueryCriteriaType customerCriteria = new CustomerQueryCriteriaType();
        PicsType picsType = new PicsType();
        picsType.getPic().addAll(otherFamilyMemberPics);
        customerCriteria.setPics(picsType);
        customerCriteria.setSelection(CustomerServiceConstants.QUERY_SELECTION_BASIC);
        
        customersType = customerService.opQueryCustomers(customerCriteria, CustomerServiceFactory.createAuditInfoType(componentName, userPic));

        
        if (customersType != null) {
          Iterator<CustomerType> customerIterator = customersType.getCustomer().iterator();
          Iterator<String> roleIterator = otherFamilyMemberRoles.iterator();
          while (customerIterator.hasNext()) {
            CustomerType customer = customerIterator.next();
            String role = roleIterator.next();
            otherFamilyMembers.add(new FamilyMember(customer, CommunityRole.createFromRoleID(role)));
          }
        }
      }
    }
    
    if (logger.isDebugEnabled()) {
      Iterator<FamilyMember> it = otherFamilyMembers.iterator();
      logger.debug("getOtherFamilyMembers(), returning members:");
      while (it.hasNext()) {
        logger.debug("member pic: " + it.next().getPic());
      }
    }
    
    FamilyIdAndFamilyMembers fidm = new FamilyIdAndFamilyMembers();
    fidm.setFamilyMembers(otherFamilyMembers);
    fidm.setFamilyId(familyId);
    return fidm;
  }
  
  /**
   * Returns user's dependants' PICs.
   * 
   * @throws ServiceFault 
   * @throws fi.koku.services.entity.customer.v1.ServiceFault 
   */
  private Set<String> getDependantPics(String userPic) throws ServiceFault, fi.koku.services.entity.customer.v1.ServiceFault {
    
    Set<String> dependantPics = new HashSet<String>();
    List<Dependant> dependants = getDependantsAndFamily(userPic, null).getDependants();
    Iterator<Dependant> di = dependants.iterator();
    while (di.hasNext()) {
      dependantPics.add(di.next().getPic());
    }
    return dependantPics;
  }
  
  /**
   * Returns user's family members' PICs.
   * 
   * @throws ServiceFault 
   * @throws fi.koku.services.entity.customer.v1.ServiceFault 
   */
  private Set<String> getFamilyMemberPics(String userPic) throws ServiceFault, fi.koku.services.entity.customer.v1.ServiceFault {
    Set<String> familyMemberPics = new HashSet<String>();
    List<FamilyMember> familyMembers = getOtherFamilyMembers(userPic, null).getFamilyMembers();
    Iterator<FamilyMember> fmi = familyMembers.iterator();
    while (fmi.hasNext()) {
      familyMemberPics.add(fmi.next().getPic());
    }
    return familyMemberPics;
  }
  
  /**
   * Query persons by name, PIC and customer ID and returns results in a List<Person> list.
   * 
   * @throws ServiceFault 
   * @throws fi.koku.services.entity.customer.v1.ServiceFault 
   */
  public List<Person> searchUsers(String surname, String customerPic, /*String customerID,*/ String currentUserPic) throws ServiceFault, fi.koku.services.entity.customer.v1.ServiceFault {
    
    // this search can return only one result because search criteria includes PIC
    
    CustomerQueryCriteriaType customerCriteria = new CustomerQueryCriteriaType();
    
    PicsType pics = new PicsType();
    pics.getPic().add(customerPic);
    customerCriteria.setPics(pics);
    
    CustomersType customersType = customerService.opQueryCustomers(customerCriteria, CustomerServiceFactory.createAuditInfoType(componentName, currentUserPic));
    
    Set<String> depPics = getDependantPics(currentUserPic);
    Set<String> familyMemberPics = getFamilyMemberPics(currentUserPic);
    
    List<Person> searchedUsers = new ArrayList<Person>();
    
    if (customersType != null) {
      List<CustomerType> customers = customersType.getCustomer();
      Iterator<CustomerType> ci = customers.iterator();
      while (ci.hasNext()) {
        CustomerType customer = ci.next();
        // filter out user and his/hers dependants from search results
        // surname given as search parameter must match the customer's surname (searching by PIC alone is forbidden)
        if (!depPics.contains(customer.getHenkiloTunnus()) && !familyMemberPics.contains(customer.getHenkiloTunnus()) &&
            !currentUserPic.equals(customer.getHenkiloTunnus()) && surname.equalsIgnoreCase(customer.getSukuNimi())) {
          searchedUsers.add(new Person(customer));
        }
      }
    }
    
    if (logger.isDebugEnabled()) {
      logger.debug("searchUsers(): searchedUsers contains:");
      Iterator<Person> pi = searchedUsers.iterator();
      while (pi.hasNext()) {
        Person p = pi.next();
        logger.debug("person pic: " + p.getPic());
      }
    }
    
    return searchedUsers;
  }
  
  /**
   * Returns a family by person's PIC.
   *
   * If the query returns more than one community (family) then TooManyFamiliesException is thrown.
   * A user should belong to one family only. Dependants are an exception because they can be members 
   * of one or more families. NOTE! The method parameter 'pic' should be guardian's or parent's pic.
   * 
   * @throws ServiceFault 
   * 
   */
  public Family getFamily(String pic) throws TooManyFamiliesException, FamilyNotFoundException, ServiceFault {
    List<Family> families = new ArrayList<Family>();
    CommunityQueryCriteriaType communityCriteria = new CommunityQueryCriteriaType();
    communityCriteria.setCommunityType(CommunityServiceConstants.COMMUNITY_TYPE_FAMILY);
    
    MemberPicsType memberPics = new MemberPicsType();
    memberPics.getMemberPic().add(pic);
    communityCriteria.setMemberPics(memberPics);
    
    CommunitiesType communitiesType = communityService.opQueryCommunities(communityCriteria, CommunityServiceFactory.createAuditInfoType(componentName, pic));
    
    if (communitiesType != null) {
      List<CommunityType> communities = communitiesType.getCommunity();
      Iterator<CommunityType> ci = communities.iterator();
      
      if (!ci.hasNext()) {
        throw new FamilyNotFoundException("FamilyNotFoundException: family containing a member PIC " + pic + " not found!");
      }
      
      while (ci.hasNext()) { 
        CommunityType community = ci.next();
        families.add(new Family(community));
      }
      
      if (families.size() > 1) {
        throw new TooManyFamiliesException("opQueryCommunities with parameter 'pic=" + pic + "' returned more than one family!");
      } else if (families.size() > 0) {
        Family family = families.get(0);   
        logger.debug("getFamily(): returning family with community ID " + family.getCommunityId());
        return family;
      }
    }
    
    logger.debug("getFamily(): returning null!");
    
    return null;
  }
  
  /**
   * Checks if the user's family has max. number of parents.
   */
  public boolean isParentsSet(String userPic, Family family) {
    if (family != null) {
      logger.debug("isParentsSet(): returning " + family.isParentsSet());
      return family.isParentsSet();
    }
    logger.debug("isParentsSet(): family == null, returning false");
    return false;
  } 
  
}
