/*
 * Copyright 2011 Ixonos Plc, Finland. All rights reserved.
 * 
 * You should have received a copy of the license text along with this program.
 * If not, please contact the copyright holder (http://www.ixonos.com/).
 * 
 */
package fi.koku.services.entity.kks.v1;

import java.util.ArrayList;
import java.util.List;

/**
 * GroupsHelper.
 * 
 * Hides the complexity of kksServicePortType.opGetKksCollectionClasses
 * 
 * @author laukksa
 *
 */
public class GroupsHelper {

  private KksServicePortType kksServicePortType;
  
  private final String component;
  
  public GroupsHelper(String uid, String pwd, String component, String endpointAddress) {
    this.component = component;
    this.kksServicePortType = new KksServiceFactory(uid, pwd, endpointAddress).getKksService();
  }
  
  /**
   * Get list of InfoGroups. Facade for kksServicePortType.opGetKksCollectionClasses
   * 
   * @param userPic
   * @return list of groups
   */
  public List<InfoGroup> getInfoGroups(String userPic) {
    AuditInfoType audit = new AuditInfoType();
    audit.setComponent(component);
    audit.setUserId(userPic);
    
    List<InfoGroup> groups = new ArrayList<InfoGroup>();
    try {
      KksCollectionClassesType collections = kksServicePortType.opGetKksCollectionClasses("scope", audit);
      
      for (KksCollectionClassType collection : collections.getKksCollectionClass()) {
        // Make collection as top level info group
        InfoGroup topLevelGroup = new InfoGroup("collection-" + collection.getId(), collection.getName());
        
        // Currently service layer can only return group hierarchy with depth of 2
        for (KksGroupType group : collection.getKksGroups().getKksGroup()) {
          
          boolean groupExist = ! "".equals(group.getName());
          InfoGroup infoGroup = groupExist ? new InfoGroup("group-" + group.getId(), group.getName()) : topLevelGroup;
          
          for (KksGroupType subGroup : group.getSubGroups().getKksGroup()) {
            boolean subGroupExist = ! "".equals(subGroup.getName());
            
            if ( subGroupExist ) {
              infoGroup.addSubGroup(new InfoGroup("group-" + subGroup.getId(), subGroup.getName()));
            }
          }
          
          if ( groupExist ) {
            topLevelGroup.addSubGroup(infoGroup);
          }
        }
        
        groups.add(topLevelGroup);
      }
    } catch (ServiceFault e) {
      throw new RuntimeException(e);
    }
    
    return groups;
  }
  
}
