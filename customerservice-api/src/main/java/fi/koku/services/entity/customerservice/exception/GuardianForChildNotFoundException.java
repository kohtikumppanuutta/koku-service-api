/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.entity.customerservice.exception;

/**
 * GuardianForChildNotFoundException.
 * 
 * @author hurulmi
 */
public class GuardianForChildNotFoundException extends Exception {
  
  private static final long serialVersionUID = -7358779846455584303L;

  public GuardianForChildNotFoundException(String message) {
    super(message);
  }
}
