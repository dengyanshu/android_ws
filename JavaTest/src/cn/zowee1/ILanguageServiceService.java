
package cn.zowee1;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "ILanguageServiceService", targetNamespace = "http://service.yanshu.cn/", wsdlLocation = "http://localhost:8060/webservice/ws/languageService?wsdl")
public class ILanguageServiceService
    extends Service
{

    private final static URL ILANGUAGESERVICESERVICE_WSDL_LOCATION;
    private final static WebServiceException ILANGUAGESERVICESERVICE_EXCEPTION;
    private final static QName ILANGUAGESERVICESERVICE_QNAME = new QName("http://service.yanshu.cn/", "ILanguageServiceService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8060/webservice/ws/languageService?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        ILANGUAGESERVICESERVICE_WSDL_LOCATION = url;
        ILANGUAGESERVICESERVICE_EXCEPTION = e;
    }

    public ILanguageServiceService() {
        super(__getWsdlLocation(), ILANGUAGESERVICESERVICE_QNAME);
    }

    public ILanguageServiceService(WebServiceFeature... features) {
        super(__getWsdlLocation(), ILANGUAGESERVICESERVICE_QNAME, features);
    }

    public ILanguageServiceService(URL wsdlLocation) {
        super(wsdlLocation, ILANGUAGESERVICESERVICE_QNAME);
    }

    public ILanguageServiceService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, ILANGUAGESERVICESERVICE_QNAME, features);
    }

    public ILanguageServiceService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public ILanguageServiceService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * 
     * @return
     *     returns ILanguageService
     */
    @WebEndpoint(name = "ILanguageServicePort")
    public ILanguageService getILanguageServicePort() {
        return super.getPort(new QName("http://service.yanshu.cn/", "ILanguageServicePort"), ILanguageService.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ILanguageService
     */
    @WebEndpoint(name = "ILanguageServicePort")
    public ILanguageService getILanguageServicePort(WebServiceFeature... features) {
        return super.getPort(new QName("http://service.yanshu.cn/", "ILanguageServicePort"), ILanguageService.class, features);
    }

    private static URL __getWsdlLocation() {
        if (ILANGUAGESERVICESERVICE_EXCEPTION!= null) {
            throw ILANGUAGESERVICESERVICE_EXCEPTION;
        }
        return ILANGUAGESERVICESERVICE_WSDL_LOCATION;
    }

}
