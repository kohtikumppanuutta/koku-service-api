/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
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
