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
