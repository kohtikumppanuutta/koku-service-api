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
package fi.koku.services.entity.customerservice.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Message data class.
 * 
 * @author hurulmi
 */
public class Message {

  private static Logger log = LoggerFactory.getLogger(Message.class);
  
  private String id;
  // from is the PIC of the request sender
  private String from;
  private String role;
  private String text;
  private boolean twoParentsInFamily;
  private String memberToAddPic;

  public Message(String id, String fromUserPic, String memberToAddPic, String role, String text, boolean twoParentsInFamily) {
    log.debug("Message.constructor: creating a Message with parameters:");
    log.debug("id: " + id);
    log.debug("fromUserPic: " + fromUserPic);
    log.debug("role: " + role);
    log.debug("text: " + text);
    
    this.id = id;
    this.from = fromUserPic;
    this.role = role;
    this.text = text;
    this.twoParentsInFamily = twoParentsInFamily;
    this.memberToAddPic = memberToAddPic;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
  
  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }
  
  public String getRole() {
    return role;
  }
  
  public void setRole(String role) {
    this.role = role;
  }
  
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }
  
  public boolean getTwoParentsInFamily() {
    return twoParentsInFamily;
  }
  
  public String getMemberToAddPic() {
    return memberToAddPic;
  }
  
}
