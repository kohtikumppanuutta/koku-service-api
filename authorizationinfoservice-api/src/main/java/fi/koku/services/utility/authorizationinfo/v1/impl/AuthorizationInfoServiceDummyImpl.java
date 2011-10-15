/**
 * Dummy implementation of AuthorizationInfoService
 */
package fi.koku.services.entity.authorizationinfo.v1.impl;

import java.util.ArrayList;
import java.util.List;

import fi.koku.services.entity.authorizationinfo.v1.AuthorizationInfoService;
import fi.koku.services.entity.authorizationinfo.v1.Constants;
import fi.koku.services.entity.authorizationinfo.v1.model.Group;
import fi.koku.services.entity.authorizationinfo.v1.model.OrgUnit;
import fi.koku.services.entity.authorizationinfo.v1.model.Registry;
import fi.koku.services.entity.authorizationinfo.v1.model.Role;
import fi.koku.services.entity.authorizationinfo.v1.model.User;

/**
 * @author mikkope
 * 
 */
public class AuthorizationInfoServiceDummyImpl implements AuthorizationInfoService {

  public List<Registry> getUsersAuthorizedRegistries(String uid) {
    List<Registry> ret = new ArrayList<Registry>(2);

    if (uid.equals("777777-7777")) {
      ret.add(new Registry("daycareregistry", "Paivahoidon rekisteri"));
    } else if (uid.equals("888888-8888")) {
      ret.add(new Registry("healthcareregistry", "Potilastietorekisteri"));
    }

    return ret;
  }

  public List<Role> getUsersRoles(String context, String uid) {
    List<Role> ret = new ArrayList<Role>(1);

    if ("101010-1010".equals(uid)) {
      ret.add(Constants.ROLE_LOK_ADMIN);
    } else if ("121212-1212".equals(uid)) {
      ret.add(Constants.ROLE_LOK_LOG_ADMIN);
    }

    return ret;
  }

  public List<OrgUnit> getUsersOrgUnits(String context, String uid) {
    List<OrgUnit> ret = new ArrayList<OrgUnit>(1);

    if (uid.equals("777777-7777")) {
      ret.add(new OrgUnit("oid1", "Day care unit of Porolahti", "kk.servicearea.daycare"));
    } else if (uid.equals("888888-8888")) {
      ret.add(new OrgUnit("oid2", "Health care unit of Porolahti", "kk.servicearea.childHealth"));
    }

    ret.add(new OrgUnit("oid3", "Day care unit of Porolahti", "kk.servicearea.daycare"));

    return ret;
  }

  public List<Group> getUsersGroups(String context, String uid) {
    List<Group> ret = new ArrayList<Group>(1);
    ret.add(new Group("gid1",
        "City region wide day care workgroup - consists of people from serveral organization units"));

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
