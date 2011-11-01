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
import fi.koku.services.entity.community.v1.CommunityServicePortType;
import fi.koku.services.entity.community.v1.MembershipApprovalType;
import fi.koku.services.entity.community.v1.MembershipApprovalsType;
import fi.koku.services.entity.community.v1.MembershipRequestQueryCriteriaType;
import fi.koku.services.entity.community.v1.MembershipRequestType;
import fi.koku.services.entity.community.v1.MembershipRequestsType;
import fi.koku.services.entity.customer.v1.CustomerQueryCriteriaType;
import fi.koku.services.entity.customer.v1.CustomerServicePortType;
import fi.koku.services.entity.customer.v1.CustomerType;
import fi.koku.services.entity.customer.v1.CustomersType;
import fi.koku.services.entity.customer.v1.PicsType;

/**
 * Message related helper class.
 * 
 * @author hurulmi
 *
 */
public class MessageHelper {

  private static Logger log = LoggerFactory.getLogger(MessageHelper.class);
  
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
   */
  public List<Message> getMessagesFor(Person user, boolean userFamilyHasTwoParents) {
    
    List<Message> requestMessages = new ArrayList<Message>();
    
    if (user == null) {
      return requestMessages;
    }
    
    log.debug("calling getMessagesFor() with pic = " + user.getPic());
    
    fi.koku.services.entity.community.v1.AuditInfoType communityAuditInfoType = new fi.koku.services.entity.community.v1.AuditInfoType();
    communityAuditInfoType.setComponent(componentName);
    communityAuditInfoType.setUserId(user.getPic());
    
    MembershipRequestQueryCriteriaType membershipRequestQueryCriteria = new MembershipRequestQueryCriteriaType();
    membershipRequestQueryCriteria.setApproverPic(user.getPic());
    MembershipRequestsType membershipRequestsType = null;
    
    try {
      membershipRequestsType = communityService.opQueryMembershipRequests(membershipRequestQueryCriteria, communityAuditInfoType);
    } catch (fi.koku.services.entity.community.v1.ServiceFault fault) {
      log.error("PyhDemoService.getMessagesFor: opQueryMembershipRequests raised a ServiceFault", fault);
    }
    
    if (membershipRequestsType != null) {
      List<String> memberToAddPics = new ArrayList<String>();
      List<String> messageIds = new ArrayList<String>();
      List<String> senderPics = new ArrayList<String>();
      List<String> userApprovalStatusList = new ArrayList<String>();
      
      List<MembershipRequestType> membershipRequests = membershipRequestsType.getMembershipRequest();
      Iterator<MembershipRequestType> mrti = membershipRequests.iterator();
      // iterate through membership requests
      while (mrti.hasNext()) {
        MembershipRequestType membershipRequest = mrti.next();
        memberToAddPics.add(membershipRequest.getMemberPic());
        messageIds.add(membershipRequest.getId());
        senderPics.add(membershipRequest.getRequesterPic());
        
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
      Iterator<Person> requestSenderIterator = requestSenders.iterator();
      Iterator<Person> targetPersonIterator = targetPersons.iterator();
      
      Iterator<String> messageIdIterator = messageIds.iterator();
      Iterator<String> memberToAddIterator = memberToAddPics.iterator();
      Iterator<String> messageSenderIterator = senderPics.iterator();
      Iterator<String> approvalStatusIterator = userApprovalStatusList.iterator();
      
      while (messageIdIterator.hasNext()) {
        String messageId = messageIdIterator.next();
        String memberToAddPic = memberToAddIterator.next();
        String senderPic = messageSenderIterator.next();
        String userApprovalStatus = approvalStatusIterator.next();
        
        if (CommunityServiceConstants.MEMBERSHIP_REQUEST_STATUS_NEW.equals(userApprovalStatus)) {
          
          Person requestSender = requestSenderIterator.next();
          Person targetPerson = targetPersonIterator.next();
          String senderName = "";
          String targetName = "";
          
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
            messageText = "Uusi perheyhteyspyyntö: " + senderName + 
            " on lisäämässä sinua perheyhteisönsä jäseneksi, mutta et voi hyväksyä pyyntöä, koska perheessänne on jo kaksi vanhempaa. " +
            "Voit hylätä pyynnön tai poistaa toisen vanhemman perheyhteisöstäsi, minkä jälkeen voit hyväksyä pyynnön.";
          }
          else {
            messageText = "Saapunut perheyhteyspyyntö: " + senderName + " haluaa lisätä perheyhteisön jäseneksi: " + targetName + ".\n" +
              "Pyynnön hyväksymällä tietojen lisääminen tapahtuu automaattisesti tämän verkkopalvelun tietoihin.";
          }
          
          Message message = new Message(messageId, senderPic, memberToAddPic, messageText, twoParentsInFamily);
          requestMessages.add(message);
        }
      }
    }
    return requestMessages;
  }
  
  /**
   * Get membership requests which the user has sent.
   * 
   */
  public List<Message> getSentMessages(Person user) {
    
    List<Message> requestMessages = new ArrayList<Message>();
    
    if (user == null) {
      return requestMessages;
    }
    
    log.debug("calling getSentMessages() with pic = " + user.getPic());
    
    fi.koku.services.entity.community.v1.AuditInfoType communityAuditInfoType = new fi.koku.services.entity.community.v1.AuditInfoType();
    communityAuditInfoType.setComponent(componentName);
    communityAuditInfoType.setUserId(user.getPic());
    
    MembershipRequestQueryCriteriaType membershipRequestQueryCriteria = new MembershipRequestQueryCriteriaType();
    membershipRequestQueryCriteria.setRequesterPic(user.getPic());
    MembershipRequestsType membershipRequestsType = null;
    
    try {
      membershipRequestsType = communityService.opQueryMembershipRequests(membershipRequestQueryCriteria, communityAuditInfoType);
    } catch (fi.koku.services.entity.community.v1.ServiceFault fault) {
      log.error("PyhDemoService.getSentMessages: opQueryMembershipRequests raised a ServiceFault", fault);
    }
    
    if (membershipRequestsType != null) {
      ArrayList<String> memberToAddPics = new ArrayList<String>();
      ArrayList<String> messageIds = new ArrayList<String>();
      ArrayList<String> senderPics = new ArrayList<String>();
      
      List<MembershipRequestType> membershipRequests = membershipRequestsType.getMembershipRequest();
      Iterator<MembershipRequestType> mrti = membershipRequests.iterator();
      MembershipRequestType membershipRequest = null;
      while (mrti.hasNext()) {
        membershipRequest = mrti.next();
        memberToAddPics.add(membershipRequest.getMemberPic());
        messageIds.add(membershipRequest.getId());
        senderPics.add(membershipRequest.getRequesterPic());
      }
      
      List<Person> membersToAdd = getPersons(memberToAddPics, user.getPic());
      Iterator<Person> pi = membersToAdd.iterator();
      Iterator<String> messageIdIt = messageIds.iterator();
      Iterator<String> senderPicIt = senderPics.iterator();
      
      String targetPersonName = "";
      String messageId = "";
      String senderPic = "";
      while (pi.hasNext()) {
        Person targetPerson = pi.next();
        targetPersonName = targetPerson.getFullName();
        messageId = messageIdIt.next();
        senderPic = senderPicIt.next();
        
        String messageText = "Lisäys perheyhteystietoihisi: " + targetPersonName + " (odottaa vastaanottajan hyväksyntää)";
        Message message = new Message(messageId, senderPic, "" /*memberToAddPic*/, messageText, false);
        requestMessages.add(message);
      }
    }
    
    return requestMessages;
  }
  
  private List<Person> getPersons(List<String> pics, String currentUserPic) {
    ArrayList<Person> persons = new ArrayList<Person>();
    
    if (pics == null || pics.size() == 0) {
      return persons;
    }
    
    fi.koku.services.entity.customer.v1.AuditInfoType customerAuditInfoType = new fi.koku.services.entity.customer.v1.AuditInfoType();
    customerAuditInfoType.setComponent(componentName);
    customerAuditInfoType.setUserId(currentUserPic);
    
    CustomersType customersType = null;
    try {
      PicsType picsType = new PicsType();
      picsType.getPic().addAll(pics);
      CustomerQueryCriteriaType customerQueryCriteria = new CustomerQueryCriteriaType();
      customerQueryCriteria.setPics(picsType);
      customersType = customerService.opQueryCustomers(customerQueryCriteria, customerAuditInfoType);
    } catch (fi.koku.services.entity.customer.v1.ServiceFault fault) {
      log.error("PyhDemoService.getUser: opGetCustomer raised a ServiceFault", fault);
      return persons;
    }
    
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
   */
  public void sendParentAdditionMessage(String communityId, String memberToAddPic, String requesterPic, CommunityRole role) {
    log.debug("calling sendParentAdditionMessage()");
    log.debug("communityId: " + communityId);
    log.debug("memberToAddPic: " + memberToAddPic);
    log.debug("requesterPic: " + requesterPic);
    log.debug("role: " + role.getRoleID());
    
    fi.koku.services.entity.community.v1.AuditInfoType communityAuditInfoType = new fi.koku.services.entity.community.v1.AuditInfoType();
    communityAuditInfoType.setComponent(componentName);
    communityAuditInfoType.setUserId(requesterPic); // requesterPic is PIC of current user
    
    MembershipApprovalType membershipApproval = new MembershipApprovalType();
    membershipApproval.setApproverPic(memberToAddPic);
    membershipApproval.setStatus(CommunityServiceConstants.MEMBERSHIP_REQUEST_STATUS_NEW);
    
    MembershipApprovalsType membershipApprovalsType = new MembershipApprovalsType();
    membershipApprovalsType.getApproval().add(membershipApproval);
    
    if (log.isDebugEnabled()) {
      log.debug("listing approvals:");
      Iterator<MembershipApprovalType> mi = membershipApprovalsType.getApproval().iterator();
      while (mi.hasNext()) {
        MembershipApprovalType approval = mi.next();
        log.debug("approval: " + approval.getApproverPic() + ", " + approval.getStatus());
      }
    }
    
    MembershipRequestType membershipRequest = new MembershipRequestType();
    membershipRequest.setCommunityId(communityId);
    membershipRequest.setMemberRole(role.getRoleID());
    membershipRequest.setMemberPic(memberToAddPic);
    membershipRequest.setRequesterPic(requesterPic);
    membershipRequest.setApprovals(membershipApprovalsType);
    
    try {
      communityService.opAddMembershipRequest(membershipRequest, communityAuditInfoType);
      //Log.getInstance().send(requesterPic, "", "pyh.membership.request", "Sending membership request to add person " + memberToAddPic + " into family");
    } catch (fi.koku.services.entity.community.v1.ServiceFault fault) {
      log.error("PyhDemoService.sendParentAdditionMessage: opAddMembershipRequest raised a ServiceFault", fault);
      throw new RuntimeException(fault);
    }
  }
  
  /**
   * Sends a new membership request for adding a member (not parent) into a family.
   */
  public void sendFamilyAdditionMessage(String communityId, List<String> recipients, String requesterPic, String memberToAddPic, CommunityRole role) {
    if (log.isDebugEnabled()) {
      log.debug("calling sendFamilyAdditionMessage()");
      log.debug("communityId: " + communityId);
      log.debug("recipients:");
      Iterator<String> ri = recipients.iterator();
      while (ri.hasNext()) {
        String recipientPic = ri.next();
        log.debug("recipient pic: " + recipientPic);
      }
      log.debug("requesterPic: " + requesterPic);
      log.debug("memberToAddPic: " + memberToAddPic);
      log.debug("role: " + role.getRoleID());
    }
    
    fi.koku.services.entity.community.v1.AuditInfoType communityAuditInfoType = new fi.koku.services.entity.community.v1.AuditInfoType();
    communityAuditInfoType.setComponent(componentName);
    communityAuditInfoType.setUserId(requesterPic); // requesterPic on kirjautunut käyttäjä
    
    MembershipApprovalsType membershipApprovalsType = new MembershipApprovalsType();
    
    Iterator<String> recipientsIterator = recipients.iterator();
    while (recipientsIterator.hasNext()) {
      String approverPic = recipientsIterator.next();
      
      MembershipApprovalType membershipApproval = new MembershipApprovalType();
      membershipApproval.setApproverPic(approverPic);
      membershipApproval.setStatus("new");
      
      membershipApprovalsType.getApproval().add(membershipApproval);
    }
    
    if (log.isDebugEnabled()) {
      log.debug("listing approvals:");
      Iterator<MembershipApprovalType> mi = membershipApprovalsType.getApproval().iterator();
      while (mi.hasNext()) {
        MembershipApprovalType approval = mi.next();
        log.debug("approval: " + approval.getApproverPic() + ", " + approval.getStatus());
      }
    }
    
    MembershipRequestType membershipRequest = new MembershipRequestType();
    membershipRequest.setCommunityId(communityId);
    membershipRequest.setMemberRole(role.getRoleID());
    membershipRequest.setMemberPic(memberToAddPic);
    membershipRequest.setRequesterPic(requesterPic);
    membershipRequest.setApprovals(membershipApprovalsType);
    
    try {
      communityService.opAddMembershipRequest(membershipRequest, communityAuditInfoType);
      //Log.getInstance().send(requesterPic, "", "pyh.membership.request", "Sending membership request to add person " + memberToAddPic + " into family");
    } catch (fi.koku.services.entity.community.v1.ServiceFault fault) {
      log.error("PyhDemoService.sendFamilyAdditionMessage: opAddMembershipRequest raised a ServiceFault", fault);
      throw new RuntimeException(fault);
    }
  }  
}
