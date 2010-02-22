package gov.hhs.fha.nhinc.xdr.async.request.proxy;

import gov.hhs.fha.nhinc.common.nhinccommon.AssertionType;
import gov.hhs.fha.nhinc.common.nhinccommon.NhinTargetSystemType;
import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerCache;
import gov.hhs.fha.nhinc.connectmgr.ConnectionManagerException;
import gov.hhs.fha.nhinc.nhinclib.NhincConstants;
import gov.hhs.fha.nhinc.nhinclib.NullChecker;
import gov.hhs.fha.nhinc.saml.extraction.SamlTokenCreator;
import ihe.iti.xdr._2007.AcknowledgementType;
import ihe.iti.xds_b._2007.ProvideAndRegisterDocumentSetRequestType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import ihe.iti.xdr.async.request._2007.XDRRequestService;
import ihe.iti.xdr.async.request._2007.XDRRequestPortType;
import java.util.Map;
import javax.xml.ws.BindingProvider;

/**
 *
 * @author Neil Webb
 */
public class NhinXDRRequestWebServiceProxy implements NhinXDRRequestProxy
{
    private static Log log;
    private XDRRequestService service;

    public NhinXDRRequestWebServiceProxy()
    {
        log = createLogger();
        service = createService();
    }

    public AcknowledgementType provideAndRegisterDocumentSetBRequest(ProvideAndRegisterDocumentSetRequestType request, AssertionType assertion, NhinTargetSystemType targetSystem)
    {
        AcknowledgementType response = null;
        String url = getUrl(targetSystem);

        if (NullChecker.isNotNullish(url))
        {
            XDRRequestPortType port = getPort(url);

            setRequestContext(assertion, url, port);

            response = port.provideAndRegisterDocumentSetBRequest(request);
        }
        else
        {
            log.error("The URL for service: " + NhincConstants.NHINC_XDR_REQUEST_SERVICE_NAME + " is null");
        }

        return response;
    }

    protected void setRequestContext(AssertionType assertion, String url, XDRRequestPortType port)
    {
        SamlTokenCreator tokenCreator = new SamlTokenCreator();
        Map requestContext = tokenCreator.CreateRequestContext(assertion, url, NhincConstants.XDR_REQUEST_ACTION);

        ((BindingProvider) port).getRequestContext().putAll(requestContext);
    }

    protected XDRRequestService createService()
    {
        return new XDRRequestService();
    }

    protected Log createLogger()
    {
        return ((log != null) ? log : LogFactory.getLog(getClass()));
    }

    protected String getUrl(NhinTargetSystemType target)
    {
        String url = null;

        if (target != null)
        {
            try
            {
                url = ConnectionManagerCache.getEndpontURLFromNhinTarget(target, NhincConstants.NHINC_XDR_REQUEST_SERVICE_NAME);
            }
            catch (ConnectionManagerException ex)
            {
                log.error("Error: Failed to retrieve url for service: " + NhincConstants.NHINC_XDR_REQUEST_SERVICE_NAME);
                log.error(ex.getMessage());
            }
        }
        else
        {
            log.error("Target system passed into the proxy is null");
        }

        return url;
    }

    protected XDRRequestPortType getPort(String url)
    {
        XDRRequestPortType port = service.getXDRRequestPortSoap12();
        log.info("Setting endpoint address to Nhin XDR Request Service to " + url);
        ((BindingProvider) port).getRequestContext().put(javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        return port;
    }
}
