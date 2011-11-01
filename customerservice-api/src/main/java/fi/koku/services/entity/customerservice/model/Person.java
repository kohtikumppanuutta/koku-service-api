/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.entity.customerservice.model;

import java.util.Iterator;
import java.util.List;

import fi.koku.services.entity.customer.v1.CustomerType;
import fi.koku.services.entity.customer.v1.ElectronicContactInfoType;
import fi.koku.services.entity.customer.v1.ElectronicContactInfosType;

/**
 * Message.
 * 
 * @author hurulmi
 *
 */
public class Person {

  private boolean requestPending;
  private CustomerType customer;
  
  public Person(CustomerType customer) {
    this.customer = customer;
  }
  
  public String getFirstname() {
    return customer.getEtuNimi();
  }
  
  public String getFirstnames() {
    return customer.getEtunimetNimi();
  }
  
  public String getSurname() {
    return customer.getSukuNimi();
  }

  public String getPic() {
    return customer.getHenkiloTunnus();
  }

  public String getBirthdate() {
    return customer.getSyntymaPvm().toString();
  }
  
  public String getEcontactinfo() {
    String electronicContactInfo = "";
    ElectronicContactInfosType contactInfoType = customer.getElectronicContactInfos();
    if (contactInfoType != null) {
      List<ElectronicContactInfoType> contactInfos = contactInfoType.getEContactInfo();
      Iterator<ElectronicContactInfoType> cii = contactInfos.iterator();
      while (cii.hasNext()) {
        ElectronicContactInfoType contactInfo = cii.next();
        String info = contactInfo.getContactInfo();
        electronicContactInfo += info + "\n";
      }
    }
    return electronicContactInfo;
  }
  
  public String getFullName() {
    return getFirstname() + " " + getSurname();
  }

  public String getCapFullName() {
    return (getFirstname() + " " + getSurname() + " " + getPic()).toUpperCase();
  }

  public boolean isRequestPending() {
    return this.requestPending;
  }
  
  public void setRequestPending(boolean requestPending) {
    this.requestPending = requestPending;
  }
  
  @Override
  public String toString() {
    return "Person [firstname=" + getFirstname() + ", surname=" + getSurname() + ", ssn=" + getPic() + "]";
  }
  
}
