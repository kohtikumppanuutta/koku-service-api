/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.utility.authorizationinfo.v1.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.koku.KoKuFaultException;
import fi.koku.services.utility.authorization.v1.AuthorizationInfoServicePortType;
import fi.koku.services.utility.authorization.v1.GroupQueryCriteriaType;
import fi.koku.services.utility.authorization.v1.GroupType;
import fi.koku.services.utility.authorization.v1.GroupsType;
import fi.koku.services.utility.authorization.v1.MemberPicsType;
import fi.koku.services.utility.authorization.v1.ServiceFault;
import fi.koku.services.utility.authorizationinfo.v1.AuthorizationInfoService;
import fi.koku.services.utility.authorizationinfo.v1.model.Group;
import fi.koku.services.utility.authorizationinfo.v1.model.OrgUnit;
import fi.koku.services.utility.authorizationinfo.v1.model.Registry;
import fi.koku.services.utility.authorizationinfo.v1.model.Role;
import fi.koku.services.utility.authorizationinfo.v1.model.User;

/**
 * WS client implementation of authorization info service.
 * 
 * @author aspluma
 */
public class AuthorizationInfoServiceWSImpl implements AuthorizationInfoService {
  private Logger logger = LoggerFactory.getLogger(AuthorizationInfoServiceWSImpl.class);
  private AuthorizationInfoServicePortType authzService;

  public AuthorizationInfoServiceWSImpl(AuthorizationInfoServicePortType ep) {
    this.authzService = ep;
  }
  
  @Override
  public List<Registry> getUsersAuthorizedRegistries(String uid) {
    List<Registry> res = getGroups(null, "registry", uid, new GroupTypeMapper<Registry>() {
      @Override
      public Registry mapToSpecializedGroup(GroupType grp) {
        return new Registry(grp.getId(), grp.getName());
      }
    });
    return res;
  }

  @Override
  public List<Role> getUsersRoles(String domain, String uid) {
    List<Role> res = getGroups(domain, "role", uid, new GroupTypeMapper<Role>() {
      @Override
      public Role mapToSpecializedGroup(GroupType grp) {
        return new Role(grp.getId(), grp.getName());
      }
    });
    return res;
  }

  @Override
  public List<OrgUnit> getUsersOrgUnits(String domain, String uid) {
    GroupTypeMapper<OrgUnit> m = new GroupTypeMapper<OrgUnit>() {
      @Override
      public OrgUnit mapToSpecializedGroup(GroupType grp) {
        return new OrgUnit(grp.getId(), grp.getName());
      }
    };
    List<OrgUnit> res = getGroups(domain, "unit", uid, m); 
    return res;
  }

  @Override
  public List<Group> getUsersGroups(String domain, String uid) {
    List<Group> res = getGroups(domain, "group", uid, new GroupTypeMapper<Group>() {
      @Override
      public Group mapToSpecializedGroup(GroupType grp) {
        return new Group(grp.getId(), grp.getName());
      }
    });
    return res;
  }

  @Override
  public List<User> getGroupMembersByGroupId(String domain, String gid) {
    throw new KoKuFaultException(1234, "operation not implemented");
  }

  
  private static interface GroupTypeMapper<T> {
    public T mapToSpecializedGroup(GroupType grp);
  }
  
  private <T> List<T> getGroups(String domain, String groupClass, String uid, GroupTypeMapper<T> m) {
    List<T> res = new ArrayList<T>();
    GroupQueryCriteriaType qc = new GroupQueryCriteriaType();
    qc.setGroupClass(groupClass);
    if(domain != null) {
      qc.setDomain(domain);
    }
    qc.setMemberPics(new MemberPicsType());
    qc.getMemberPics().getMemberPic().add(uid);
    try {
      GroupsType groups = authzService.opQueryGroups(qc);
      for(GroupType g : groups.getGroup()) {
        res.add(m.mapToSpecializedGroup(g));
      }
    } catch (ServiceFault e) {
      logger.error("failed to get groups: "+uid, e);
      throw new KoKuFaultException(3000, "failed to get groups: "+uid);
    }
    return res;
  }
  
}
