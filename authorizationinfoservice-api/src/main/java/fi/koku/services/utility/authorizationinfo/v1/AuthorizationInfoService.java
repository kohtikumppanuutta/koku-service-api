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
/**
 * First draft of AuthorizationInfoService as plain java interface and impl-class
 * NOTICE: Constants -file contains 
 * 
 *  TBD: 
 *    How to use domain-parameters? 
 *    How to share that information between client and implementation?
 *    Exceptions
 * 
 */
package fi.koku.services.utility.authorizationinfo.v1;

import java.util.List;

import fi.koku.services.utility.authorizationinfo.v1.model.Group;
import fi.koku.services.utility.authorizationinfo.v1.model.OrgUnit;
import fi.koku.services.utility.authorizationinfo.v1.model.Registry;
import fi.koku.services.utility.authorizationinfo.v1.model.Role;
import fi.koku.services.utility.authorizationinfo.v1.model.User;

/**
 * @author mikkope
 *
 */
public interface AuthorizationInfoService {

  
  /**
   * Returns list of Registry-objects which user can access
   * 
   * @param uid = pic (hetu)
   * @return List containing Registry-objects or null if user has none
   */
  List<Registry> getUsersAuthorizedRegistries(String uid);
  
  
  /**
   * Returns list of Role-objects that user has in given domain.
   * #TODO# domain will probably be defined KEY-value for corresponding context/domain/system
   * 
   * @param domain, TBD
   * @param uid = pic (hetu)
   * @return List containing Role-objects or null if user has none
   */
  List<Role> getUsersRoles(String domain, String uid);
    
  
  /**
   * Returns list of OrgUnit-objects that is belongs to in given domain
   * #TODO# domain will probably be defined KEY-value for corresponding context/domain/system
   * 
   * @param domain, TBD
   * @param uid = pic (hetu)
   * @return List containing OrgUnit-objects or null if user has none
   */
  List<OrgUnit> getUsersOrgUnits(String domain, String uid);
  
  
  /**
   * Returns list of Group-objects that is belongs to in given domain
   * #TODO# domain will probably be defined KEY-value for corresponding context/domain/system
   * 
   * @param domain, TBD
   * @param uid = pic (hetu)
   * @return List containing Group-objects or null if user has none
   */
  List<Group> getUsersGroups(String domain, String uid);
  
  
  
  /**
   * Returns list of User-objects whom belong to given group in given domain
   * #TODO# domain will probably be defined KEY-value for corresponding context/domain/system
   * 
   * @param domain, TBD
   * @param gid = groupId
   * @return List containing User-objects or null if user has none
   */
  List<User> getGroupMembersByGroupId(String domain, String gid);
  
}
