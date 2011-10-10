/**
 * Family service contains family related information and operations
 */
package fi.koku.services.entity.family.v1;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.koku.services.entity.community.v1.CommunityQueryCriteriaType;
import fi.koku.services.entity.community.v1.CommunityServiceFactory;
import fi.koku.services.entity.community.v1.CommunityServicePortType;
import fi.koku.services.entity.community.v1.CommunityType;
import fi.koku.services.entity.community.v1.MemberPicsType;
import fi.koku.services.entity.community.v1.MemberType;
import fi.koku.services.entity.community.v1.ServiceFault;
import fi.koku.services.entity.customer.v1.CustomerQueryCriteriaType;
import fi.koku.services.entity.customer.v1.CustomerServiceFactory;
import fi.koku.services.entity.customer.v1.CustomerServicePortType;
import fi.koku.services.entity.customer.v1.CustomerType;
import fi.koku.services.entity.customer.v1.CustomersType;
import fi.koku.services.entity.customer.v1.PicsType;
import fi.koku.services.entity.family.FamilyConstants;

/**
 * @author mikkope
 * 
 */
public class FamilyService {

  private static final Logger LOG = LoggerFactory.getLogger(FamilyService.class);

  private CustomerServicePortType customerService;
  private CommunityServicePortType communityService;

  public FamilyService() {
    CustomerServiceFactory customerServiceFactory = new CustomerServiceFactory(
        FamilyConstants.CUSTOMER_SERVICE_USER_ID, FamilyConstants.CUSTOMER_SERVICE_PASSWORD,
        FamilyConstants.CUSTOMER_SERVICE_ENDPOINT);
    customerService = customerServiceFactory.getCustomerService();

    CommunityServiceFactory communityServiceFactory = new CommunityServiceFactory(
        FamilyConstants.COMMUNITY_SERVICE_USER_ID, FamilyConstants.COMMUNITY_SERVICE_PASSWORD,
        FamilyConstants.COMMUNITY_SERVICE_ENDPOINT);
    communityService = communityServiceFactory.getCommunityService();
  }

  /**
   * Returns children of the person
   * 
   * @param pic, pic of person
   * @param auditUserId
   * @param auditComponentId
   * @return List of children (CustomerType)
   * @throws Exception
   */
  public List<CustomerType> getPersonsChildren(String pic, String auditUserId, String auditComponentId) throws Exception {

    List<CustomerType> children = new ArrayList<CustomerType>();
    List<CommunityType> communities = null;

    try {

      // 1) Query communities and persons
      communities = searchPersonsCommunitiesByPic(pic, auditUserId, auditComponentId);

      // ##TODO## The query may return more than one communities, how to
      // determine which is the correct one?
      // ##TODO## Assuming that the first list is the one. Check this!
      //
      if (communities.size()==1) {
        final int THE_ONLY_COMMUNITY = 0;
        List<MemberType> members = communities.get(THE_ONLY_COMMUNITY).getMembers().getMember();
        List<String> memberPics = filterMemberPicsWithRole(members, FamilyConstants.ROLE_DEPENDANT);

        // 2) Query persons (CustomerType) in community
        children = searchPersonsByPicList(memberPics, auditUserId, auditComponentId);

      }else if(communities.size()>1){  
        throw new Exception("Person has communities.size="+communities.size()+" (not required 1).");//#TODO# Evaluate proper operation for this
      }else{
        LOG.debug("No communities found with pic="+pic); 
      }
    
    
    } catch (fi.koku.services.entity.community.v1.ServiceFault communityFault) {
      LOG.error("Failed to get persons children (community members) from CommunityService", communityFault);
    }
    return children;
  }

  /**
   * Return parents of the person
   * 
   * @param pic
   * @param auditUserId
   * @param auditComponentId
   * @return List of parents (CustomerType)
   * @throws Exception
   */
  public List<CustomerType> getPersonsParents(String pic, String auditUserId, String auditComponentId) throws Exception {

    List<CustomerType> parents = new ArrayList<CustomerType>();
    List<CommunityType> communities = null;

    try {

      // 1) Query communities and persons
      communities = searchPersonsCommunitiesByPic(pic, auditUserId, auditComponentId);

      // ##TODO## The query may return more than one communities, how to
      // determine which is the correct one?
      // ##TODO## Assuming that the first list is the one. Check this!
      //
      if (communities.size()==1) {
        final int THE_ONLY_COMMUNITY = 0;
        List<MemberType> members = communities.get(THE_ONLY_COMMUNITY).getMembers().getMember();
        List<String> memberPics = filterMemberPicsWithRole(members, FamilyConstants.ROLE_GUARDIAN);

        parents = searchPersonsByPicList(memberPics, auditUserId, auditComponentId);

      }else if(communities.size()>1){  
        throw new Exception("Person has communities.size="+communities.size()+" (not required 1).");//#TODO# Evaluate proper operation for this
      }else{
        LOG.debug("No communities found with pic="+pic); 
      }
    
    
    } catch (fi.koku.services.entity.community.v1.ServiceFault communityFault) {
      LOG.error("Failed to get persons parents (community members) from CommunityService", communityFault);
    }
    return parents;
  }
  
  
  private List<CustomerType> searchPersonsByPicList(List<String> memberPics, String auditUserId, String auditComponentId)
      throws fi.koku.services.entity.customer.v1.ServiceFault {
    CustomerQueryCriteriaType query = new CustomerQueryCriteriaType();
    
    fi.koku.services.entity.customer.v1.AuditInfoType customerAuditInfoType = new fi.koku.services.entity.customer.v1.AuditInfoType();
    customerAuditInfoType.setComponent(auditComponentId);
    customerAuditInfoType.setUserId(auditUserId);

    //Add pics to criteria
    PicsType picsType = new PicsType();
    for (String memberPic : memberPics) {
      picsType.getPic().add(memberPic);  
    }
    query.setPics(picsType);
    CustomersType customersType = customerService.opQueryCustomers(query, customerAuditInfoType);
    return customersType.getCustomer();
  }


  
  
  private List<CommunityType> searchPersonsCommunitiesByPic(String pic, String auditUserId, String auditComponentId)
      throws ServiceFault {
    
    List<CommunityType> communities;
    // Set community query audit info
    fi.koku.services.entity.community.v1.AuditInfoType communityAuditInfoType = new fi.koku.services.entity.community.v1.AuditInfoType();
    communityAuditInfoType.setComponent(auditComponentId);
    communityAuditInfoType.setUserId(auditUserId);//##TODO## Check if this is correct?

    // Add community query parameters
    CommunityQueryCriteriaType criteria = new CommunityQueryCriteriaType();
    
    // Query with only one pic
    MemberPicsType picsType = new MemberPicsType();
    picsType.getMemberPic().add(pic);
    criteria.setMemberPics(picsType);
    
    criteria.setCommunityType(FamilyConstants.COMMUNITY_TYPE_GUARDIAN_COMMUNITY);

    communities = communityService.opQueryCommunities(criteria, communityAuditInfoType).getCommunity();
    LOG.debug("Tried to opQueryCommunities(criteria{memberPic=" + pic + " and communityType="
        + FamilyConstants.COMMUNITY_TYPE_GUARDIAN_COMMUNITY + "}," + "auditInfo{userId=" + communityAuditInfoType.getUserId()
        + " and componentType=" + communityAuditInfoType.getComponent() + "}) = list.size=" + communities.size());
    return communities;
  }

  /**
   * Extracts pics that have given role (ex. has role dependant/guardian) from MemberType-list
   * 
   * @param members, list of MemberTypes
   * @param roleId, role as string format
   * @return pics, list of Pics as Strings
   */
  private List<String> filterMemberPicsWithRole(List<MemberType> members, final String roleId) {
    List<String> pics = null;
    if (members != null && roleId!=null) {
      pics = new ArrayList<String>(members.size());
      
      for (MemberType m : members) {
        // Check if the person has given roleId
        if (roleId.equals(m.getRole())) {
          if (m.getPic() != null) {
            pics.add(m.getPic());
          }
        } else {
          LOG.debug("Member with pic=" + m.getPic() + " has role=" + m.getRole()
              + " and therefore is not added to list.");
        }
      }// ...FOR
    
    }// ..IF
    return pics;
  }

}
