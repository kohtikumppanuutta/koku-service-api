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

import fi.koku.services.entity.customer.v1.CustomerType;

/**
 * Person data class.
 * 
 * @author hurulmi
 */
public class Dependant extends Person {
  
  private boolean memberOfUserFamily;
  
  public Dependant(CustomerType customer) {
    super(customer);
    memberOfUserFamily = false;
  }
  
  public boolean getMemberOfUserFamily() {
    return memberOfUserFamily;
  }
  
  public void setMemberOfUserFamily(boolean isMember) {
    this.memberOfUserFamily = isMember;
  }
}
