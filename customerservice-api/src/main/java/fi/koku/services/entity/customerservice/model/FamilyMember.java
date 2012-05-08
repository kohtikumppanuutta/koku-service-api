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


import fi.koku.services.entity.customerservice.model.CommunityRole;
import fi.koku.services.entity.customer.v1.CustomerType;

/**
 * FamilyMember data class.
 * 
 * @author hurulmi
 */
public class FamilyMember extends Person {

  private CommunityRole role;
  
  public FamilyMember(CustomerType customer, CommunityRole role) {
    super(customer);
    this.role = role;
  }
  
  /**
   * Returns member's role ID which is one of the following:
   * guardian, dependant, parent, father, mother
   */
  public String getRoleId() {
    return role.getRoleID();
  }
  
  public CommunityRole getRole() {
    return role;
  }
  
  @Override
  public String toString() {
    return super.toString() + " FamilyMember [role=" + role + "]";
  }
  
}
