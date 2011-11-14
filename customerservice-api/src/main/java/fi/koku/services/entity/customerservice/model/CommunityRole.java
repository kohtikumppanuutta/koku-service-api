/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.entity.customerservice.model;

/**
 * CommunityRole.
 * 
 * @author hurulmi
 *
 */
public enum CommunityRole {
  
  FATHER("ui.pyh.father", "father"), MOTHER("ui.pyh.mother", "mother"), FAMILY_MEMBER("ui.pyh.family", "family"), 
  DEPENDANT("ui.pyh.dependant", "dependant"), CHILD("ui.pyh.child", "child"), PARENT("ui.pyh.parent", "parent"),
  GUARDIAN("ui.pyh.guardian", "guardian");
  
  private String bundleId;
  private String roleID;
  
  private CommunityRole(String text, String roleID) {
    this.bundleId = text;
    this.roleID = roleID;
  }

  public String getBundleId() {
    return bundleId;
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
