/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.entity.customerservice.model;

/**
 * CommunityRole data class.
 * 
 * @author hurulmi
 */
public enum CommunityRole {
  
  FATHER("father"), MOTHER("mother"), FAMILY_MEMBER("family"), DEPENDANT("dependant"), CHILD("child"), PARENT("parent"),
  GUARDIAN("guardian");
  
  private String roleID;
  
  private CommunityRole(String roleID) {
    this.roleID = roleID;
  }

  public static CommunityRole create(String text) {
    for (CommunityRole r : values()) {
      if (r.toString().equals(text)) {
        return r;
      }
    }
    return CommunityRole.FAMILY_MEMBER;
  }
  
  public static CommunityRole createFromRoleID(String roleID) {
    for (CommunityRole r : values()) {
      if (r.getRoleID().equals(roleID)) {
        return r;
      }
    }
    return CommunityRole.FAMILY_MEMBER;
  }
  
  public String getRoleID() {
    return roleID;
  }
  
}
