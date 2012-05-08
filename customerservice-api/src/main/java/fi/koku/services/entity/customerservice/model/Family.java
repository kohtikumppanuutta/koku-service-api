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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fi.koku.services.entity.customerservice.model.CommunityRole;
import fi.koku.services.entity.community.v1.CommunityType;
import fi.koku.services.entity.community.v1.MemberType;
import fi.koku.services.entity.community.v1.MembersType;

/**
 * Family data class.
 * 
 * @author hurulmi
 */
public class Family {

  private CommunityType community;
  
  public Family() {
  }
  
  /**
   * Constructor for creating a Family from CommunityType and list of FamilyMembers
   */
  public Family(CommunityType community) {
    this.community = community;
  }
  
  public String getCommunityId() {
    return community.getId();
  }
  
  public String getCommunityType() {
    return community.getType();
  }
  
  public String getCommunityName() {
    return community.getName();
  }
  
  public MembersType getCommunityMembers() {
    return community.getMembers();
  }
  
  public CommunityType getCommunity() {
    return community;
  }
  
  public void combineFamily(Family family) {
    for (MemberType member : family.getAllMembers()) {
      addFamilyMember(member.getPic(), member.getRole());
    }
  }
  
  public MemberType getOtherParent(String notWantedParentPic) {
    for (MemberType m : getParents()) {
      if (!m.getPic().equals(notWantedParentPic)) {
        return m;
      }
    }
    return null;
  }
  
  public void addFamilyMember(String memberPic, String role) {
    MembersType membersType = community.getMembers();
    List<MemberType> members = membersType.getMember();
    
    MemberType newMember = new MemberType();
    newMember.setPic(memberPic);
    newMember.setRole(role);
    members.add(newMember);
  }
  
  public List<MemberType> getParents() {
    List<MemberType> parents = new ArrayList<MemberType>();
    MembersType membersType = community.getMembers();
    List<MemberType> members = membersType.getMember();
    Iterator<MemberType> mi = members.iterator();
    while (mi.hasNext()) {
      MemberType member = mi.next();
      CommunityRole memberRole = CommunityRole.createFromRoleID(member.getRole());
      if (CommunityRole.PARENT.equals(memberRole) || CommunityRole.MOTHER.equals(memberRole) || 
          CommunityRole.FATHER.equals(memberRole)) {
        parents.add(member);
      }
    }
    return parents;
  }
  
  public List<MemberType> getAllMembers() {
    MembersType membersType = community.getMembers();
    List<MemberType> members = membersType.getMember();
    return members;
  }
  
  public boolean isParentsSet() {
    return getParents().size() >= 2;
  }
  
}
