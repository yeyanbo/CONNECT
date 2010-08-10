/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.hhs.fha.nhinc.docquery.passthru.deferred.response;

import gov.hhs.fha.nhinc.async.AsyncMessageIdExtractor;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.BindingType;
import javax.xml.ws.WebServiceContext;

/**
 *
 * @author jhoppesc
 */
@WebService(serviceName = "NhincProxyDocQueryDeferredResponse", portName = "NhincProxyDocQueryDeferredResponsePortSoap", endpointInterface = "gov.hhs.fha.nhinc.nhincproxydocquerydeferredresponse.NhincProxyDocQueryDeferredResponsePortType", targetNamespace = "urn:gov:hhs:fha:nhinc:nhincproxydocquerydeferredresponse", wsdlLocation = "WEB-INF/wsdl/PassthruDocQueryDeferredResponseUnsecured/NhincProxyDocQueryDeferredResponse.wsdl")
@BindingType(value = "http://www.w3.org/2003/05/soap/bindings/HTTP/")
public class PassthruDocQueryDeferredResponseUnsecured {
    @Resource
    private WebServiceContext context;

    public gov.hhs.healthit.nhin.DocQueryAcknowledgementType respondingGatewayCrossGatewayQuery(gov.hhs.fha.nhinc.common.nhinccommonproxy.RespondingGatewayCrossGatewayQueryResponseType respondingGatewayCrossGatewayQueryRequest) {
        // Extract the message id value from the WS-Addressing Header and place it in the Assertion Class
        if (respondingGatewayCrossGatewayQueryRequest.getAssertion() != null) {
            AsyncMessageIdExtractor msgIdExtractor = new AsyncMessageIdExtractor();
            respondingGatewayCrossGatewayQueryRequest.getAssertion().setMessageId(msgIdExtractor.GetAsyncMessageId(context));
        }

        return new PassthruDocQueryDeferredResponseOrchImpl().respondingGatewayCrossGatewayQuery(respondingGatewayCrossGatewayQueryRequest.getAdhocQueryResponse(), respondingGatewayCrossGatewayQueryRequest.getAssertion(), respondingGatewayCrossGatewayQueryRequest.getNhinTargetSystem());
    }

}