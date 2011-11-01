/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.entity.customerservice.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Message.
 * 
 * @author hurulmi
 *
 */
public class Message {

  private static Logger log = LoggerFactory.getLogger(Message.class);
  
  private String id;
  private String from;
  private String text;
  private boolean twoParentsInFamily;
  private String memberToAddPic;

  public Message(String id, String fromUserPic, String memberToAddPic, String text, boolean twoParentsInFamily) {
    log.debug("Message.constructor: creating a Message with parameters:");
    log.debug("id: " + id);
    log.debug("fromUserPic: " + fromUserPic);
    log.debug("text: " + text);
    
    this.id = id;
    this.from = fromUserPic;
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
