package com.example.camel.component.ekp;

import com.example.camel.component.ekp.token.LtpaTokenGenerator;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.support.DefaultProducer;

public class EkpProducer extends DefaultProducer {

    private final EkpEndpoint endpoint;

    public EkpProducer(EkpEndpoint ekpEndpoint) throws Exception {
        super(ekpEndpoint);
        this.endpoint=ekpEndpoint;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        ProducerTemplate producerTemplate=exchange.getContext().createProducerTemplate();
        String endpointUri=endpoint.getEndpointBaseUri();
        String loginName=endpoint.getLoginName();
        if(loginName==null||loginName.trim().length()==0){
            loginName=exchange.getIn().getHeader("loginName", String.class);
        }
        String content=requestForAutoLogin(endpoint.getSecurityKey(),loginName,producerTemplate,endpointUri);
        exchange.getMessage().setBody(content);
    }

    public String requestForAutoLogin(final String securityKey, final String loginName, ProducerTemplate producerTemplate, String endpointUri) throws Exception {
        Exchange exchange=producerTemplate.request(endpointUri, new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.getIn().setHeader("Cookie","LtpaToken="+ new LtpaTokenGenerator(securityKey).getLtpaTokenByLoginName(loginName));
            }
        });
        String body=exchange.getMessage().getBody(String.class);
        return body;



    }
}
