/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.entity.customerservice.helper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.koku.services.entity.customerservice.model.CommunityRole;
import fi.koku.services.entity.customerservice.model.Message;
import fi.koku.services.entity.customerservice.model.Person;
import fi.koku.services.entity.community.v1.CommunityServiceConstants;
import fi.koku.services.entity.community.v1.CommunityServiceFactory;
import fi.koku.services.entity.community.v1.CommunityServicePortType;
import fi.koku.services.entity.community.v1.MembershipApprovalType;
import fi.koku.services.entity.community.v1.MembershipApprovalsType;
import fi.koku.services.entity.community.v1.MembershipRequestQueryCriteriaType;
import fi.koku.services.entity.community.v1.MembershipRequestType;
import fi.koku.services.entity.community.v1.MembershipRequestsType;
import fi.koku.services.entity.community.v1.ServiceFault;
import fi.koku.services.entity.customer.v1.CustomerQueryCriteriaType;
import fi.koku.services.entity.customer.v1.CustomerServiceFactory;
import fi.koku.services.entity.customer.v1.CustomerServicePortType;
import fi.koku.services.entity.customer.v1.CustomerType;
import fi.koku.services.entity.customer.v1.CustomersType;
import fi.koku.services.entity.customer.v1.PicsType;

/**
 * Message related helper class.
 * 
 * @author hurulmi
 */
public class MessageHelper {

  private static Logger logger = LoggerFactory.getLogger(MessageHelper.class);
  
  private CustomerServicePortType customerService;
  
  private CommunityServicePortType communityService;
  
  private String componentName;

  public MessageHelper(CustomerServicePortType customerService, CommunityServicePortType communityService,
      String componentName) {
    super();
    this.customerService = customerService;
    this.communityService = communityService;
    this.componentName = componentName;
  }

  /**
   * Get membership requests which user has received (that is all requests user needs to approve or reject).
   * 
   * @throws ServiceFault 
   * @throws fi.koku.services.entity.customer.v1.ServiceFault 
   * 
   */
  public List<Message> getMessagesFor(Person user, boolean userFamilyHasTwoParents, String newReqMessageText, String newReqMessageTextTwoParents) throws ServiceFault, fi.koku.services.entity.customer.v1.ServiceFault {
    
    List<Message> requestMessages = new ArrayList<Message>();
    
    if (user == null) {
      return requestMessages;
    }
    
    logger.debug("calling getMessagesFor() with pic = " + user.getPic());
    
    MembershipRequestQueryCriteriaType membershipRequestQueryCriteria = new MembershipRequestQueryCriteriaType();
    membershipRequestQueryCriteria.setApproverPic(user.getPic());
    
    MembershipRequestsType membershipRequestsType = communityService.opQueryMembershipRequests(membershipRequestQueryCriteria, CommunityServiceFactory.createAuditInfoType(componentName, user.getPic()));
    
    if (membershipRequestsType != null) {
      List<String> memberToAddPics = new ArrayList<String>();
      List<String> messageIds = new ArrayList<String>();
      List<String> senderPics = new ArrayList<String>();
      List<String> userApprovalStatusList = new ArrayList<String>();
      List<String> addedMemberRoles = new ArrayList<String>();
      
      List<MembershipRequestType> membershipRequests = membershipRequestsType.getMembershipRequest();
      Iterator<MembershipRequestType> mrti = membershipRequests.iterator();
      while (mrti.hasNext()) {
        MembershipRequestType membershipRequest = mrti.next();
        memberToAddPics.add(membershipRequest.getMemberPic());
        messageIds.add(membershipRequest.getId());
        senderPics.add(membershipRequest.getRequesterPic());
        addedMemberRoles.add(membershipRequest.getMemberRole());
        MembershipApprovalsType membershipApprovalsType = membershipRequest.getApprovals();
        List<MembershipApprovalType> approvals = membershipApprovalsType.getApproval();
        Iterator<MembershipApprovalType> ait = approvals.iterator();
        while (ait.hasNext()) {
          MembershipApprovalType approval = ait.next();
          String approverPic = approval.getApproverPic();
          
          // there should be match because MembershipRequestQueryCriteriaType.approverPic was set to user's pic
          if (approverPic.equals(user.getPic())) {
            userApprovalStatusList.add(approval.getStatus());
            break;
          }
        }
      }
      
      // reason for two loops is that the WS services are called only once (for better performance)
      
      List<Person> requestSenders = getPersons(senderPics, user.getPic());
      List<Person> targetPersons = getPersons(memberToAddPics, user.getPic());
      Person requestSender = null;
      Iterator<Person> targetPersonIterator = targetPersons.iterator();
      
      Iterator<String> messageIdIterator = messageIds.iterator();
      Iterator<String> memberToAddIterator = memberToAddPics.iterator();
      Iterator<String> messageSenderIterator = senderPics.iterator();
      Iterator<String> approvalStatusIterator = userApprovalStatusList.iterator();
      Iterator<String> addedMemberRoleIterator = addedMemberRoles.iterator();
      
      while (messageIdIterator.hasNext()) {
        String messageId = messageIdIterator.next();
        String memberToAddPic = memberToAddIterator.next();
        String senderPic = messageSenderIterator.next();
        String userApprovalStatus = approvalStatusIterator.next();
        String addedMemberRole = addedMemberRoleIterator.next();
        
        if (CommunityServiceConstants.MEMBERSHIP_REQUEST_STATUS_NEW.equals(userApprovalStatus)) {
          
          Iterator<Person> ri = requestSenders.iterator();
          while (ri.hasNext()) {
            Person sender = ri.next();
            if (sender.getPic().equals(senderPic)) {
              requestSender = sender;
            }
          }
          
          Person targetPerson = targetPersonIterator.next();
          String senderName = "";
          String targetName = "";
          if (addedMemberRole == null) {
            addedMemberRole = "";
          }
          
          if (requestSender != null) {
            senderName = requestSender.getFullName();
          }
          
          if (targetPerson != null) {
            targetName = targetPerson.getFullName();
          }
          
          boolean twoParentsInFamily = false;
          if (memberToAddPic.equals(user.getPic())) {
            // twoParentsInFamily is set if there are two (or more; this is theoretical, 
            // shouldn't be possible) parents in the family
            twoParentsInFamily = userFamilyHasTwoParents;
          }
          
          String messageText = "";
          if (twoParentsInFamily) {
            messageText = newReqMessageTextTwoParents.replace("@SENDER_NAME@", senderName);
          }
          else {
            messageText = newReqMessageText.replace("@SENDER_NAME@", senderName).replace("@TARGET_NAME@", targetName);
          }
          
          Message message = new Message(messageId, senderPic, memberToAddPic, addedMemberRole, messageText, twoParentsInFamily);
          requestMessages.add(message);
        }
      }
    }
    return requestMessages;
  }
  
  /**
   * Get membership requests which the user has sent.
   * 
   * @throws ServiceFault 
   * @throws fi.koku.services.entity.customer.v1.ServiceFault 
   * 
   */
  public List<Message> getSentMessages(Person user, String sentReqMessageText) throws ServiceFault, fi.koku.services.entity.customer.v1.ServiceFault {
    
    List<Message> requestMessages = new ArrayList<Message>();
    
    if (user == null) {
      return requestMessages;
    }
    
    logger.debug("calling getSentMessages() with pic = " + user.getPic());
    
    MembershipRequestQueryCriteriaType membershipRequestQueryCriteria = new MembershipRequestQueryCriteriaType();
    membershipRequestQueryCriteria.setRequesterPic(user.getPic());
    MembershipRequestsType membershipRequestsType = null;
    
    membershipRequestsType = communityService.opQueryMembershipRequests(membershipRequestQueryCriteria, CommunityServiceFactory.createAuditInfoType(componentName, user.getPic()));
    
    if (membershipRequestsType != null) {
      List<String> memberToAddPics = new ArrayList<String>();
      List<String> messageIds = new ArrayList<String>();
      List<String> senderPics = new ArrayList<String>();
      List<String> memberRoles = new ArrayList<String>();
      
      List<MembershipRequestType> membershipRequests = membershipRequestsType.getMembershipRequest();
      Iterator<MembershipRequestType> mrti = membershipRequests.iterator();
      MembershipRequestType membershipRequest = null;
      while (mrti.hasNext()) {
        membershipRequest = mrti.next();
        memberToAddPics.add(membershipRequest.getMemberPic());
        messageIds.add(membershipRequest.getId());
        senderPics.add(membershipRequest.getRequesterPic());
        memberRoles.add(membershipRequest.getMemberRole());
      }
      
      List<Person> membersToAdd = getPersons(memberToAddPics, user.getPic());
      Iterator<Person> pi = membersToAdd.iterator();
      Iterator<String> messageIdIt = messageIds.iterator();
      Iterator<String> senderPicIt = senderPics.iterator();
      Iterator<String> memberRoleIt = memberRoles.iterator();
      
      String targetPersonName = "";
      String messageId = "";
      String senderPic = "";
      String memberRole = "";
      while (pi.hasNext()) {
        Person targetPerson = pi.next();
        targetPersonName = targetPerson.getFullName();
        messageId = messageIdIt.next();
        senderPic = senderPicIt.next();
        memberRole = memberRoleIt.next();
        
        String messageText = sentReqMessageText.replace("@TARGET_NAME@", targetPersonName);
        Message message = new Message(messageId, senderPic, "" /*memberToAddPic*/, memberRole, messageText, false);
        requestMessages.add(message);
      }
    }
    
    return requestMessages;
  }
  
  /**
   * Uses customer service to return list of Persons with list of pics.
   * 
   * @param pics
   * @param currentUserPic
   * @return list of persons
   * @throws fi.koku.services.entity.customer.v1.ServiceFault
   */
  private List<Person> getPersons(List<String> pics, String currentUserPic) throws fi.koku.services.entity.customer.v1.ServiceFault {
    ArrayList<Person> persons = new ArrayList<Person>();
    
    if (pics == null || pics.size() == 0) {
      return persons;
    }
    
    PicsType picsType = new PicsType();
    picsType.getPic().addAll(pics);
    CustomerQueryCriteriaType customerQueryCriteria = new CustomerQueryCriteriaType();
    customerQueryCriteria.setPics(picsType);
    CustomersType customersType = customerService.opQueryCustomers(customerQueryCriteria, CustomerServiceFactory.createAuditInfoType(componentName, currentUserPic));
    
    if (customersType != null) {
      List<CustomerType> customers = customersType.getCustomer();
      Iterator<CustomerType> ci = customers.iterator();
      while (ci.hasNext()) {
        persons.add(new Person(ci.next()));
      }
    }
    
    return persons;
  } 
  
  /**
   * Sends a new membership request for adding a parent into a family. 
   * 
   * @throws ServiceFault 
   * 
   */
  public void sendParentAdditionMessage(String communityId, String memberToAddPic, String requesterPic, CommunityRole role) throws ServiceFault {
    logger.debug("calling sendParentAdditionMessage()");
    logger.debug("communityId: " + communityId);
    logger.debug("memberToAddPic: " + memberToAddPic);
    logger.debug("requesterPic: " + requesterPic);
    logger.debug("role: " + role.getRoleID());
    
    fi.koku.services.entity.community.v1.AuditInfoType communityAuditInfoType = CommunityServiceFactory.createAuditInfoType(componentName, requesterPic);
    
    MembershipRequestQueryCriteriaType membershipRequestQueryCriteria = new MembershipRequestQueryCriteriaType();
    membershipRequestQueryCriteria.setRequesterPic(requesterPic);
    
    MembershipRequestsType membershipRequestsType = communityService.opQueryMembershipRequests(membershipRequestQueryCriteria, communityAuditInfoType);
    List<MembershipRequestType> membershipRequests = membershipRequestsType.getMembershipRequest();
    
    Iterator<MembershipRequestType> mri = membershipRequests.iterator();
    while (mri.hasNext()) {
      MembershipRequestType request = mri.next();
      
      CommunityRole requestRole = CommunityRole.createFromRoleID(request.getMemberRole());
      if (CommunityRole.FATHER.equals(requestRole) || CommunityRole.MOTHER.equals(requestRole) || CommunityRole.PARENT.equals(requestRole)) {
        // if a membership request with the role father/mother/parent exists it means that the user has already sent a request
        // to add other parent into the family; max. number of parents per family is two so return and do nothing
        return;
      }
    }
    
    MembershipApprovalType membershipApproval = new MembershipApprovalType();
    membershipApproval.setApproverPic(memberToAddPic);
    membershipApproval.setStatus(CommunityServiceConstants.MEMBERSHIP_REQUEST_STATUS_NEW);
    
    MembershipApprovalsType membershipApprovalsType = new MembershipApprovalsType();
    membershipApprovalsType.getApproval().add(membershipApproval);
    
    if (logger.isDebugEnabled()) {
      logger.debug("listing approvals:");
      Iterator<MembershipApprovalType> mi = membershipApprovalsType.getApproval().iterator();
      while (mi.hasNext()) {
        MembershipApprovalType approval = mi.next();
        logger.debug("approval: " + approval.getApproverPic() + ", " + approval.getStatus());
      }
    }
    
    MembershipRequestType membershipRequest = new MembershipRequestType();
    membershipRequest.setCommunityId(communityId);
    membershipRequest.setMemberRole(role.getRoleID());
    membershipRequest.setMemberPic(memberToAddPic);
    membershipRequest.setRequesterPic(requesterPic);
    membershipRequest.setApprovals(membershipApprovalsType);
    
    communityService.opAddMembershipRequest(membershipRequest, communityAuditInfoType);

  }
  
  /**
   * Sends a new membership request for adding a member (not parent) into a family.
   * 
   * @throws ServiceFault 
   */
  public void sendFamilyAdditionMessage(String communityId, List<String> recipients, String requesterPic, String memberToAddPic, CommunityRole role) throws ServiceFault {
    if (logger.isDebugEnabled()) {
      logger.debug("calling sendFamilyAdditionMessage()");
      logger.debug("communityId: " + communityId);
      logger.debug("recipients:");
      Iterator<String> ri = recipients.iterator();
      while (ri.hasNext()) {
        String recipientPic = ri.next();
        logger.debug("recipient pic: " + recipientPic);
      }
      logger.debug("requesterPic: " + requesterPic);
      logger.debug("memberToAddPic: " + memberToAddPic);
      logger.debug("role: " + role.getRoleID());
    }
    
    fi.koku.services.entity.community.v1.AuditInfoType communityAuditInfoType = CommunityServiceFactory.createAuditInfoType(componentName, requesterPic);
    
    MembershipRequestQueryCriteriaType membershipRequestQueryCriteria = new MembershipRequestQueryCriteriaType();
    membershipRequestQueryCriteria.setRequesterPic(requesterPic);
    
    MembershipRequestsType membershipRequestsType = communityService.opQueryMembershipRequests(membershipRequestQueryCriteria, communityAuditInfoType);
    List<MembershipRequestType> membershipRequests = membershipRequestsType.getMembershipRequest();
    Iterator<MembershipRequestType> mri = membershipRequests.iterator();
    
    // check if the person is already added into the family (= request has already been sent)
    while (mri.hasNext()) {
      MembershipRequestType request = mri.next();
      
      if (request.getMemberPic().equals(memberToAddPic)) {
        return;
      }
    }
    
    MembershipApprovalsType membershipApprovalsType = new MembershipApprovalsType();
    
    Iterator<String> recipientsIterator = recipients.iterator();
    while (recipientsIterator.hasNext()) {
      String approverPic = recipientsIterator.next();
      
      MembershipApprovalType membershipApproval = new MembershipApprovalType();
      membershipApproval.setApproverPic(approverPic);
      membershipApproval.setStatus("new");
      
      membershipApprovalsType.getApproval().add(membershipApproval);
    }
    
    if (logger.isDebugEnabled()) {
      logger.debug("listing approvals:");
      Iterator<MembershipApprovalType> mi = membershipApprovalsType.getApproval().iterator();
      while (mi.hasNext()) {
        MembershipApprovalType approval = mi.next();
        logger.debug("approval: " + approval.getApproverPic() + ", " + approval.getStatus());
      }
    }
    
    MembershipRequestType membershipRequest = new MembershipRequestType();
    membershipRequest.setCommunityId(communityId);
    membershipRequest.setMemberRole(role.getRoleID());
    membershipRequest.setMemberPic(memberToAddPic);
    membershipRequest.setRequesterPic(requesterPic);
    membershipRequest.setApprovals(membershipApprovalsType);
    
    communityService.opAddMembershipRequest(membershipRequest, communityAuditInfoType);
  }  
}
