package fi.koku.services.entity.customercommunication.v1;

/**
 * Factory for CustomerCommunication service
 * 
 * 
 * @author dkudinov
 *
 */

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.tampere.contract.municipalityportal.ccs.CustomerCommunicationService;
import fi.tampere.contract.municipalityportal.ccs.CustomerCommunicationServicePortType;

public class CustomerCommunicationServiceFactory {
    private String uid;
    private String pwd;
    private String endpointUrl;
    private final URL wsdlLocation = getClass().getClassLoader().getResource("wsdl/CustomerCommunicationService.wsdl");
    private static Logger log = LoggerFactory.getLogger(CustomerCommunicationServiceFactory.class);

    public CustomerCommunicationServiceFactory(String uid, String pwd, String endpointUrl) {
        this.uid = uid;
        this.pwd = pwd;
        this.endpointUrl = endpointUrl;
    }

    public CustomerCommunicationServicePortType getCustomerCommunicationService() {
        if (wsdlLocation == null)
            log.error("wsdllocation=null");
        CustomerCommunicationService service = new CustomerCommunicationService(wsdlLocation, new QName(
                "http://tampere.fi/contract/municipalityportal/ccs", "CustomerCommunicationService"));
        CustomerCommunicationServicePortType port = service.getCustomerCommunicationServicePort();
        String epAddr = endpointUrl;
        log.debug("ep addr: " + epAddr);

        ((BindingProvider) port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, epAddr);
        ((BindingProvider) port).getRequestContext().put(BindingProvider.USERNAME_PROPERTY, uid);
        ((BindingProvider) port).getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, pwd);

        try {
            // TODO: is there any better way to check if CXF Stack in use?
            final HTTPConduit httpConduit = (HTTPConduit) ClientProxy.getClient(port).getConduit();

            final TLSClientParameters tlsCP = new TLSClientParameters();

            final String keyStoreLoc = System.getProperty("javax.net.ssl.keyStore");
            final String keyPassword = System.getProperty("javax.net.ssl.keyStorePassword");
            final String keystoreType = System.getProperty("javax.net.ssl.keyStoreType");
            log.info("Keystore: type = " + keystoreType + ", location = " + keyStoreLoc);

            final KeyStore keyStore = KeyStore.getInstance(keystoreType);
            keyStore.load(new FileInputStream(keyStoreLoc), keyPassword.toCharArray());
            final KeyManager[] myKeyManagers = getKeyManagers(keyStore, keyPassword);
            tlsCP.setKeyManagers(myKeyManagers);

            final String trustStoreLoc = System.getProperty("javax.net.ssl.trustStore");
            final String trustStorePassword = System.getProperty("javax.net.ssl.trustStorePassword");
            final String trustStoreType = System.getProperty("javax.net.ssl.trustStoreType");
            log.info("TrustStore: type = " + trustStoreType + ", location = " + trustStoreLoc);

            final KeyStore trustStore = KeyStore.getInstance(trustStoreType);
            trustStore.load(new FileInputStream(trustStoreLoc), trustStorePassword.toCharArray());
            final TrustManager[] myTrustStoreKeyManagers = getTrustManagers(trustStore);
            tlsCP.setTrustManagers(myTrustStoreKeyManagers);

            httpConduit.setTlsClientParameters(tlsCP);
        } catch (Exception e) {
            log.info("Unable to setup CXF HTTPConduit for mutual authentication: " + e.getMessage(), e);
        }

        return port;

    }

    private static TrustManager[] getTrustManagers(KeyStore trustStore) throws NoSuchAlgorithmException, KeyStoreException {
        String alg = KeyManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory fac = TrustManagerFactory.getInstance(alg);
        fac.init(trustStore);
        return fac.getTrustManagers();
    }

    private static KeyManager[] getKeyManagers(KeyStore keyStore, String keyPassword) throws GeneralSecurityException, IOException {
        String alg = KeyManagerFactory.getDefaultAlgorithm();
        char[] keyPass = keyPassword != null ? keyPassword.toCharArray() : null;
        KeyManagerFactory fac = KeyManagerFactory.getInstance(alg);
        fac.init(keyStore, keyPass);
        return fac.getKeyManagers();
    }
}
