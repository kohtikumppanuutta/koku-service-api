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
import fi.koku.services.entity.community.v1.MemberType;
import fi.koku.services.entity.customer.v1.CustomerServiceFactory;
import fi.koku.services.entity.customer.v1.CustomerServiceHelper;
import fi.koku.services.entity.customer.v1.CustomerServicePortType;
import fi.koku.services.entity.customer.v1.CustomerType;
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

  public List<CustomerType> getUsersChildren(String pic) throws Exception {

    List<CustomerType> children = new ArrayList<CustomerType>();
    List<CommunityType> communities = null;

    try {

      // 1) Query communities and users

      // Set community query audit info
      fi.koku.services.entity.community.v1.AuditInfoType communityAuditInfoType = new fi.koku.services.entity.community.v1.AuditInfoType();
      communityAuditInfoType.setComponent(FamilyConstants.COMPONENT_FAMILY_SERVICE);
      communityAuditInfoType.setUserId(pic);

      // Add community query parameters
      CommunityQueryCriteriaType criteria = new CommunityQueryCriteriaType();
      criteria.setMemberPic(pic);
      // criteria.setCommunityType(FamilyConstants.COMMUNITY_TYPE_FAMILY);
      criteria.setCommunityType(FamilyConstants.COMMUNITY_TYPE_GUARDIAN_COMMUNITY);

      communities = communityService.opQueryCommunities(criteria, communityAuditInfoType).getCommunity();
      LOG.debug("Tried to opQueryCommunities(criteria{memberPic=" + pic + " and communityType="
          + FamilyConstants.COMMUNITY_TYPE_FAMILY + "}," + "auditInfo{userId=" + communityAuditInfoType.getUserId()
          + " and componentType=" + communityAuditInfoType.getComponent() + "}) = list.size=" + communities.size());

      // ##TODO## The query may return more than one communities, how to
      // determine which is the correct one?
      // ##TODO## Assuming that the first list is the one. Check this!
      //
      if (communities.size()==1) {
        final int THE_ONLY_COMMUNITY = 0;
        List<MemberType> members = communities.get(THE_ONLY_COMMUNITY).getMembers().getMember();
        List<String> memberPics = getChildrenPics(members);

        // 2) Query users (CustomerType) in community
        fi.koku.services.entity.customer.v1.AuditInfoType customerAuditInfoType = new fi.koku.services.entity.customer.v1.AuditInfoType();
        customerAuditInfoType.setComponent(FamilyConstants.COMPONENT_FAMILY_SERVICE);
        customerAuditInfoType.setUserId(pic);

        CustomerServiceHelper srvHelper = new CustomerServiceHelper(customerService, customerAuditInfoType);
        children = srvHelper.getCustomers(memberPics);

      }else if(communities.size()>1){  
        throw new Exception("User has communities.size="+communities.size()+" (not required 1).");//#TODO# Evaluate proper operation for this
      }else{
        LOG.debug("No communities found with pic="+pic); 
      }
    
    
    } catch (fi.koku.services.entity.community.v1.ServiceFault communityFault) {
      LOG.error("Failed to get users children (community members) from CommunityService", communityFault);
    }
    return children;
  }

  /**
   * Extracts pics of children (=has role dependant) from MemberType-list
   * 
   * @param members, list of MemberTypes
   * @return, list of Pics as Strings
   */
  private List<String> getChildrenPics(List<MemberType> members) {
    List<String> pics = null;
    if (members != null) {
      pics = new ArrayList<String>(members.size());
      
      for (MemberType m : members) {
        // Check if the user has role DEPENDANT
        if (FamilyConstants.ROLE_DEPENDANT.equals(m.getRole())) {
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
