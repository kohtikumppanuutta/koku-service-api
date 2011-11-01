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
import fi.koku.services.entity.community.v1.CommunityServicePortType;
import fi.koku.services.entity.community.v1.CommunityType;
import fi.koku.services.entity.community.v1.MemberPicsType;
import fi.koku.services.entity.community.v1.MemberType;
import fi.koku.services.entity.community.v1.MembersType;
import fi.koku.services.entity.customer.v1.CustomerQueryCriteriaType;
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

  private static Logger log = LoggerFactory.getLogger(FamilyHelper.class);
  
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
   */
  public DependantsAndFamily getDependantsAndFamily(String userPic) {
    List<Dependant> dependants = new ArrayList<Dependant>();
    CommunityQueryCriteriaType communityQueryCriteria = new CommunityQueryCriteriaType();
    communityQueryCriteria.setCommunityType(CommunityServiceConstants.COMMUNITY_TYPE_GUARDIAN_COMMUNITY);
    
    MemberPicsType memberPics = new MemberPicsType();
    memberPics.getMemberPic().add(userPic);
    communityQueryCriteria.setMemberPics(memberPics);
    
    CommunitiesType communitiesType = null;
    
    fi.koku.services.entity.community.v1.AuditInfoType communityAuditInfoType = new fi.koku.services.entity.community.v1.AuditInfoType();
    communityAuditInfoType.setComponent(componentName);
    communityAuditInfoType.setUserId(userPic);
    
    fi.koku.services.entity.customer.v1.AuditInfoType customerAuditInfoType = new fi.koku.services.entity.customer.v1.AuditInfoType();
    customerAuditInfoType.setComponent(componentName);
    customerAuditInfoType.setUserId(userPic);
    
    try {
      communitiesType = communityService.opQueryCommunities(communityQueryCriteria, communityAuditInfoType);
    } catch (fi.koku.services.entity.community.v1.ServiceFault fault) {
      log.error("PyhDemoService.getDependantsAndFamily: opQueryCommunities raised a ServiceFault", fault);
    }
    
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
    try {
      customersType = customerService.opQueryCustomers(customerQueryCriteriaType, customerAuditInfoType);
    } catch (fi.koku.services.entity.customer.v1.ServiceFault fault) {
      log.error("PyhDemoService.getDependantsAndFamily: opGetCustomer raised an ServiceFault", fault);
    }
    if (customersType != null) {
      List<CustomerType> customers = customersType.getCustomer();
      Iterator<CustomerType> ci = customers.iterator();
      while (ci.hasNext()) {
        CustomerType customer = ci.next();
        dependants.add(new Dependant(customer));
      }
    }
    
    // next check if dependant is member of user's family
    
    Family userFamily;
    try {
      userFamily = getFamily(userPic);
    } catch (FamilyNotFoundException fnfe) {
      userFamily = null;
      log.error("getDependantsAndFamily(): caught FamilyNotFoundException: cannot set Dependant.memberOfUserFamily because userFamily is null!");
      log.error(fnfe.getMessage());
    } catch (TooManyFamiliesException tmfe) {
      userFamily = null;
      log.error("getDependantsAndFamily(): caught TooManyFamiliesException: cannot set Dependant.memberOfUserFamily because userFamily is null!", tmfe);
      log.error(tmfe.getMessage());
    }
    
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
    
    if (log.isDebugEnabled()) {
      Iterator<Dependant> it = dependants.iterator();
      log.debug("getDependantsAndFamily(), returning dependants:");
      while (it.hasNext()) {
        log.debug("dep pic: " + it.next().getPic());
      }
      log.debug("--");
    }
    
    return dependantsAndFamily;
  }
    
  
  /**
   * Returns all other members of the user's family except dependants.
   */
  public FamilyIdAndFamilyMembers getOtherFamilyMembers(String userPic) {
    List<Dependant> dependants = getDependantsAndFamily(userPic).getDependants();
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
    
    CommunitiesType communitiesType = null;
    
    fi.koku.services.entity.community.v1.AuditInfoType communityAuditInfoType = new fi.koku.services.entity.community.v1.AuditInfoType();
    communityAuditInfoType.setComponent(componentName);
    communityAuditInfoType.setUserId(userPic);
    
    fi.koku.services.entity.customer.v1.AuditInfoType customerAuditInfoType = new fi.koku.services.entity.customer.v1.AuditInfoType();
    customerAuditInfoType.setComponent(componentName);
    customerAuditInfoType.setUserId(userPic);
    
    try {
      communitiesType = communityService.opQueryCommunities(communityQueryCriteria, communityAuditInfoType);
    } catch (fi.koku.services.entity.community.v1.ServiceFault fault) {
      log.error("PyhDemoService.getOtherFamilyMembers: opQueryCommunities raised a ServiceFault", fault);
    }
    
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
        try {
          CustomerQueryCriteriaType customerCriteria = new CustomerQueryCriteriaType();
          PicsType picsType = new PicsType();
          picsType.getPic().addAll(otherFamilyMemberPics);
          customerCriteria.setPics(picsType);
          customerCriteria.setSelection("basic");
          customersType = customerService.opQueryCustomers(customerCriteria, customerAuditInfoType);
        } catch (fi.koku.services.entity.customer.v1.ServiceFault fault) {
          log.error("PyhDemoService.getOtherFamilyMembers: opQueryCustomers raised a ServiceFault", fault);
        }
        
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
    
    if (log.isDebugEnabled()) {
      Iterator<FamilyMember> it = otherFamilyMembers.iterator();
      log.debug("getOtherFamilyMembers(), returning members:");
      while (it.hasNext()) {
        log.debug("member pic: " + it.next().getPic());
      }
      log.debug("--");
    }
    
    FamilyIdAndFamilyMembers fidm = new FamilyIdAndFamilyMembers();
    fidm.setFamilyMembers(otherFamilyMembers);
    fidm.setFamilyId(familyId);
    return fidm;
  }
  
  /**
   * Returns user's dependants' PICs.
   */
  private Set<String> getDependantPics(String userPic) {
    Set<String> dependantPics = new HashSet<String>();
    List<Dependant> dependants = getDependantsAndFamily(userPic).getDependants();
    Iterator<Dependant> di = dependants.iterator();
    while (di.hasNext()) {
      dependantPics.add(di.next().getPic());
    }
    return dependantPics;
  }
  
  /**
   * Returns user's family members' PICs.
   */
  private Set<String> getFamilyMemberPics(String userPic) {
    Set<String> familyMemberPics = new HashSet<String>();
    List<FamilyMember> familyMembers = getOtherFamilyMembers(userPic).getFamilyMembers();
    Iterator<FamilyMember> fmi = familyMembers.iterator();
    while (fmi.hasNext()) {
      familyMemberPics.add(fmi.next().getPic());
    }
    return familyMemberPics;
  }
  
  /**
   * Query persons by name, PIC and customer ID and returns results in a List<Person> list.
   */
  public List<Person> searchUsers(String surname, String customerPic, /*String customerID,*/ String currentUserPic) {
    
    // this search can return only one result because search criteria includes PIC
    
    CustomerQueryCriteriaType customerCriteria = new CustomerQueryCriteriaType();
    
    PicsType pics = new PicsType();
    pics.getPic().add(customerPic);
    customerCriteria.setPics(pics);
    CustomersType customersType = null;
    
    fi.koku.services.entity.customer.v1.AuditInfoType customerAuditInfoType = new fi.koku.services.entity.customer.v1.AuditInfoType();
    customerAuditInfoType.setComponent(componentName);
    customerAuditInfoType.setUserId(currentUserPic);
    
    try {
      customersType = customerService.opQueryCustomers(customerCriteria, customerAuditInfoType);
    } catch (fi.koku.services.entity.customer.v1.ServiceFault fault) {
      log.error("PyhDemoService.searchUsers: opQueryCustomers raised a ServiceFault", fault);
    }
    
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
    
    if (log.isDebugEnabled()) {
      log.debug("searchUsers(): searchedUsers contains:");
      Iterator<Person> pi = searchedUsers.iterator();
      while (pi.hasNext()) {
        Person p = pi.next();
        log.debug("person pic: " + p.getPic());
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
   */
  private Family getFamily(String pic) throws TooManyFamiliesException, FamilyNotFoundException {
    List<Family> families = new ArrayList<Family>();
    CommunityQueryCriteriaType communityCriteria = new CommunityQueryCriteriaType();
    communityCriteria.setCommunityType(CommunityServiceConstants.COMMUNITY_TYPE_FAMILY);
    
    MemberPicsType memberPics = new MemberPicsType();
    memberPics.getMemberPic().add(pic);
    communityCriteria.setMemberPics(memberPics);
    
    CommunitiesType communitiesType = null;
    
    fi.koku.services.entity.community.v1.AuditInfoType communityAuditInfoType = new fi.koku.services.entity.community.v1.AuditInfoType();
    communityAuditInfoType.setComponent(componentName);
    communityAuditInfoType.setUserId(pic);
    
    try {
      communitiesType = communityService.opQueryCommunities(communityCriteria, communityAuditInfoType);
    } catch (fi.koku.services.entity.community.v1.ServiceFault fault) {
      log.error("PyhDemoService.getFamily: opQueryCommunities raised a ServiceFault", fault);
    }
    
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
        
        log.debug("getFamily(): returning family with community ID " + family.getCommunityId());
        
        return family;
      }
    }
    
    log.debug("getFamily(): returning null!");
    
    return null;
  }
  
  /**
   * Checks if the user's family has max. number of parents.
   */
  public boolean isParentsSet(String userPic) {
    Family family = null;
    
    try {
      family = getFamily(userPic);
    } catch (TooManyFamiliesException tme) {
      log.error("PyhDemoService.isParentsSet(): getFamily(userPic) threw a TooManyFamiliesException!", tme);
      log.error(tme.getMessage());
    } catch (FamilyNotFoundException fnfe) {
      log.error("PyhDemoService.isParentsSet(): getFamily(userPic) threw a FamilyNotFoundException!");
      log.error(fnfe.getMessage());
    }
    
    if (family != null) {
      log.debug("isParentsSet(): returning " + family.isParentsSet());
      
      return family.isParentsSet();
    }
    
    log.debug("isParentsSet(): family == null, returning false");
    
    return false;
  } 
  
}
