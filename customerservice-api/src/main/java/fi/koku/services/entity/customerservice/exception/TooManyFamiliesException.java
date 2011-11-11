/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.entity.customerservice.exception;

/**
 * TooManyFamiliesException.
 * 
 * @author hurulmi
 *
 */
public class TooManyFamiliesException extends Exception {
  
  private static final long serialVersionUID = 397898115505489078L;

  public TooManyFamiliesException(String message) {
    super(message);
  }
}
