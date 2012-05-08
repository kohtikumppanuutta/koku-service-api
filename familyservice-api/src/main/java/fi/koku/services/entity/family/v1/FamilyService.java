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
 * Family service contains family related information and operations
 */
package fi.koku.services.entity.family.v1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

  public FamilyService(String customerServiceUserId, String customerServicePassword, String communityServiceUserId, String communityServicePassword ) {
    CustomerServiceFactory customerServiceFactory = new CustomerServiceFactory(
        customerServiceUserId, customerServicePassword,
        FamilyConstants.CUSTOMER_SERVICE_ENDPOINT);
    customerService = customerServiceFactory.getCustomerService();

    CommunityServiceFactory communityServiceFactory = new CommunityServiceFactory(
        communityServiceUserId, communityServicePassword,
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
    Set<String> memberPics = new HashSet<String>();
    try {

      // 1) Query communities and persons
      communities = searchPersonsCommunitiesByPic(pic, auditUserId, auditComponentId);

      //Check if found one or more communities
      if(communities!=null && communities.size()>0){
        
        //Iterate communities. For example child can belong to many communities and
        // therefore we have to get all guardians from those communities 
        for (CommunityType community : communities) {
          List<MemberType> members = community.getMembers().getMember();
          memberPics.addAll( filterMemberPicsWithRole(members, FamilyConstants.ROLE_DEPENDANT) );
        }

        // 2) Query persons (CustomerType) in communities
        children = searchPersonsByPicList(memberPics, auditUserId, auditComponentId);
      
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
    Set<String> memberPics = new HashSet<String>();
    try {

      // 1) Query communities and persons
      communities = searchPersonsCommunitiesByPic(pic, auditUserId, auditComponentId);

      //Check if found one or more communities
      if(communities!=null && communities.size()>0){
        
        //Iterate communities. For example child can belong to many communities and
        // therefore we have to get all guardians from those communities 
        for (CommunityType community : communities) {
          
          List<MemberType> members = community.getMembers().getMember();
          memberPics.addAll( filterMemberPicsWithRole(members, FamilyConstants.ROLE_GUARDIAN) );
  
          //remove request pic (might be parent)          
          memberPics.remove(pic);
          
          parents = searchPersonsByPicList(memberPics, auditUserId, auditComponentId);
        }
        
      }else{
        LOG.debug("No communities found with pic="+pic); 
      }
    
    
    } catch (fi.koku.services.entity.community.v1.ServiceFault communityFault) {
      LOG.error("Failed to get community parents (community members) from CommunityService", communityFault);
    }
    return parents;
  }
  
  
  private List<CustomerType> searchPersonsByPicList(Set<String> memberPics, String auditUserId, String auditComponentId)
      throws fi.koku.services.entity.customer.v1.ServiceFault {
    
    if ( memberPics == null || memberPics.isEmpty() ) {
      return new ArrayList<CustomerType>();
    }
    
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
   * Set-type is used to remove duplicates
   * 
   * @param members, list of MemberTypes
   * @param roleId, role as string format
   * @return pics, set of Pics as Strings
   */
  private Set<String> filterMemberPicsWithRole(List<MemberType> members, final String roleId) {
    Set<String> pics = null;
    if (members != null && roleId!=null) {
      pics = new HashSet<String>(members.size());
      
      for (MemberType m : members) {
        // Check if the person has given roleId
        if (roleId.equals(m.getRole())) {
          if (m.getPic() != null) {
            pics.add(m.getPic());
          }
        } else {
          LOG.debug("Member with pic=" + m.getPic() + " has role=" + m.getRole()
              + " and therefore is not added to set.");
        }
      }// ...FOR
    
    }// ..IF
    return pics;
  }

}
