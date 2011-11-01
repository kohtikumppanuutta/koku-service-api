/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.entity.customerservice.model;


import fi.koku.services.entity.customerservice.model.CommunityRole;
import fi.koku.services.entity.customer.v1.CustomerType;

/**
 * FamilyMember.
 * 
 * @author hurulmi
 *
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
