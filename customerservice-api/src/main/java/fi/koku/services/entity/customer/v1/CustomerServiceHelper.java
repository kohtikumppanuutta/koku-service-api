/**
 * Helper class that simplifies data access from Customer Service
 */
package fi.koku.services.entity.customer.v1;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mikkope
 *
 */
public class CustomerServiceHelper {

  private static final Logger LOG = LoggerFactory.getLogger(CustomerServiceHelper.class);
  
  private CustomerServicePortType srv = null;
  private AuditInfoType auditInfo = null;
  
  public CustomerServiceHelper(CustomerServicePortType srv, AuditInfoType auditInfo){
    this.srv = srv;
    this.auditInfo = auditInfo;
  }
  
  
  /**
   * Gets customers with list of customer pics
   * 
   * @param customersPics, pics have to be in string format
   * @return list of CustomerType if found. Else return null.
   */
  public List<CustomerType> getCustomers(List<String> customersPics){ 
    
    List<CustomerType> customerList = null;
    
    if(customersPics!=null){
      customerList = new ArrayList<CustomerType>(customersPics.size());
      
      long queryStartTime = 0;
      if(LOG.isDebugEnabled()){
        LOG.debug("Starting to query customers");
        queryStartTime = System.currentTimeMillis();
      }
      
      //##TODO## Due to current CustomerServiceAPI limitations, we have to get users one by one
      //Consider updating actual CustomerServiceAPI to allow list of PICs to improve efficiency
      for (String pic : customersPics) {
        CustomerType c;
        try {
          c = srv.opGetCustomer(pic, auditInfo);
          if(c!=null){
            customerList.add(c);
          }else{
            //##TODO## Is error logging enough or should we raise an exception and return none?
            LOG.error("Could not get CustomerType with pic="+pic);//This should not be possible?
          }
        } catch (ServiceFault e) {
          LOG.error("ServiceFault: Could not get Customer type with pic="+pic,e);
        }
      }//...FOR  
      
      if(LOG.isDebugEnabled()){
        LOG.debug("Finished getting CustomerType-objects using customersPics.size="+customersPics.size()+
            ". It took "+(System.currentTimeMillis()-queryStartTime)+" ms.");
      }
      
    }//..IF
    return customerList;
    }

}
  

