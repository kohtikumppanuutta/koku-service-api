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
