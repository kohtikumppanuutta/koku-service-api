/**
 * Class that holds Person related information
 */
package fi.koku.services.entity.person.v1;

/**
 * @author mikkope
 *
 */
public class Person {

  private String pic;
  private String uid;
  private String fname;
  private String sname;
  
  public Person() {  
  }
  
  public Person(String pic, String fname, String sname) {
    this.pic = pic;
    this.fname = fname;
    this.sname = sname;
  }


  public String getPic() {
    return pic;
  }

  public void setPic(String pic) {
    this.pic = pic;
  }

  public String getUid() {
    return uid;
  }

  public void setUid(String uid) {
    this.uid = uid;
  }

  public String getFname() {
    return fname;
  }

  public void setFname(String fname) {
    this.fname = fname;
  }

  public String getSname() {
    return sname;
  }

  public void setSname(String sname) {
    this.sname = sname;
  }

}
