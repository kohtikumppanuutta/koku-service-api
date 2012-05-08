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
 * Wrapper class which contains dependants and family.
 * This class is used as a helper object to minimize remote service calls.
 * 
 * @author hurulmi
 */
public class DependantsAndFamily {
  
  private Family family;
  private List<Dependant> dependants;
  
  public DependantsAndFamily() {
  }
  
  public void setDependants(List<Dependant> dependants) {
    this.dependants = dependants;
  }
  
  public void setFamily(Family family) {
    this.family = family;
  }
  
  public List<Dependant> getDependants() {
    return dependants;
  }
  
  public Family getFamily() {
    return family;
  }
}
