package fi.koku.services.entity.person.v1;

import java.util.List;

/**
 * Interface that defines the person information access methods.
 * 
 * @author hanhian
 */
public interface PersonInfoProvider {

  List<Person> getPersonsFromCustomerDomainWithUidList(List<String> uids, String auditUserId, String auditComponentId);

  List<Person> getPersonsFromOfficerDomainWithUidList(List<String> uids, String auditUserId, String auditComponentId);

  List<Person> getPersonsFromCustomerDomainWithPicList(List<String> pics, String auditUserId, String auditComponentId);

  List<Person> getPersonsFromOfficerDomainWithPicList(List<String> pics, String auditUserId, String auditComponentId);
}