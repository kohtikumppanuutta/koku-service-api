/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.entity.customerservice.exception;

/**
 * FamilyNotFoundException.
 * 
 * @author hurulmi
 */
public class FamilyNotFoundException extends Exception {
  
  private static final long serialVersionUID = 2054049990120358769L;

  public FamilyNotFoundException(String message) {
    super(message);
  }
}