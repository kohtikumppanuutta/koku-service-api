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

import java.util.List;

/**
 * FamilyIdAndFamilyMembers data class.
 * 
 * @author hurulmi
 */
public class FamilyIdAndFamilyMembers {

    private String familyId;
    private List<FamilyMember> familyMembers;
    
    public FamilyIdAndFamilyMembers() {
    }
    
    public void setFamilyMembers(List<FamilyMember> members) {
      this.familyMembers = members;
    }
    
    public void setFamilyId(String id) {
      this.familyId = id;
    }
    
    public List<FamilyMember> getFamilyMembers() {
      return familyMembers;
    }
    
    public String getFamilyId() {
      return familyId;
    }
    
}
