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
package fi.koku.services.entity.kks.v1;

import java.util.ArrayList;
import java.util.List;

/**
 * InfoGroup.
 * 
 * Simplified presentation of KksCollectionClass or KksGroup.
 * 
 * @author laukksa
 *
 */
public class InfoGroup {
  
  public InfoGroup(String id, String name) {
    super();
    this.id = id;
    this.name = name;
  }

  private String id;
  
  private String name;
  
  private List<InfoGroup> subGroups;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<InfoGroup> getSubGroups() {
    return subGroups;
  }
  
  public void addSubGroup(InfoGroup ig) {
    if (subGroups == null) {
      subGroups = new ArrayList<InfoGroup>();
    }
    subGroups.add(ig);
  }
  
  @Override
  public String toString() {
    return "InfoGroup [id=" + id + ", name=" + name + "]";
  }
  
  
}
