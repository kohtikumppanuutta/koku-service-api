/**
 * Dummy implementation of UserInfoService
 */
package fi.koku.service.entity.userinfo.v1.impl;

import java.util.ArrayList;
import java.util.List;

import fi.koku.services.entity.userinfo.v1.UserInfoService;
import fi.koku.services.entity.userinfo.v1.model.Group;
import fi.koku.services.entity.userinfo.v1.model.OrgUnit;
import fi.koku.services.entity.userinfo.v1.model.Registry;
import fi.koku.services.entity.userinfo.v1.model.Role;
import fi.koku.services.entity.userinfo.v1.model.User;

/**
 * @author mikkope
 *
 */
public class UserInfoServiceDummyImpl implements UserInfoService {

  public List<Registry> getUsersAuthorizedRegistries(String uid) {
    
    List<Registry> ret = new ArrayList<Registry>(2);
    
    Registry r1 = new Registry();
    r1.setId("healthcareregistry");
    r1.setName("Potilastietorekisteri");
    ret.add(r1);
    
    Registry r2 = new Registry();
    r2.setId("daycareregistry");
    r2.setName("Paivahoidon rekisteri");
    ret.add(r2);    
    
    return ret;
  }

  public List<Role> getUsersRoles(String context, String uid) {
    List<Role> ret = new ArrayList<Role>(1);

    Role r1 = new Role();
    r1.setId("LOG_ADMIN_ROLE");
    r1.setName("Lokin paakayttajaarooli");
    ret.add(r1);
    
    return ret;
  }

  public List<OrgUnit> getUsersOrgUnits(String context, String uid) {
    List<OrgUnit> ret = new ArrayList<OrgUnit>(1);
    
    OrgUnit o1 = new OrgUnit();
    o1.setId("oid1");
    o1.setName("Day care unit of Porolahti");
    ret.add(o1);
    
    return ret;
  }

  public List<Group> getUsersGroups(String context, String uid) {
    List<Group> ret = new ArrayList<Group>(1);
    
    Group g1 = new Group();
    g1.setId("gid1");
    g1.setName("City region wide day care workgroup - consists of people from serveral organization units");
    ret.add(g1);
    
    return ret;
  }

  public List<User> getGroupMembersByGroupId(String context, String gid) {
    List<User> ret = new ArrayList<User>(2);
    
    User u1 = new User();
    u1.setId("111111-1111");
    u1.setFirstname("etunimi1");
    u1.setLastname("sukunimi1");
    ret.add(u1);
    
    User u2 = new User();
    u2.setId("222222-2222");
    u2.setFirstname("etunimi2");
    u2.setLastname("sukunimi2");
    ret.add(u2);
    
    return ret;
  }

}
