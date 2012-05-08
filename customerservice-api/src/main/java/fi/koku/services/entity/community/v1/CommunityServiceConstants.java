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
package fi.koku.services.entity.community.v1;

/**
 * Constants for community service.
 * 
 * @author aspluma
 */
public class CommunityServiceConstants {
  
  final public static String COMMUNITY_TYPE_GUARDIAN_COMMUNITY = "guardian_community";
  final public static String COMMUNITY_TYPE_FAMILY = "family_community";
  
  final public static String COMMUNITY_ROLE_DEPENDANT = "dependant";
  final public static String COMMUNITY_ROLE_GUARDIAN = "guardian";
  final public static String COMMUNITY_ROLE_PARENT = "parent";
  final public static String COMMUNITY_ROLE_FATHER = "father";
  final public static String COMMUNITY_ROLE_MOTHER = "mother";
  final public static String COMMUNITY_ROLE_CHILD = "child";
  
  final public static String MEMBERSHIP_REQUEST_STATUS_NEW = "new";
  final public static String MEMBERSHIP_REQUEST_STATUS_APPROVED = "approved";
  final public static String MEMBERSHIP_REQUEST_STATUS_REJECTED = "rejected";
  
  private CommunityServiceConstants() {
  }
}