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
package fi.koku.services.entity.customerservice.exception;

/**
 * TooManyFamiliesException.
 * 
 * @author hurulmi
 */
public class TooManyFamiliesException extends Exception {
  
  private static final long serialVersionUID = 397898115505489078L;

  public TooManyFamiliesException(String message) {
    super(message);
  }
}
