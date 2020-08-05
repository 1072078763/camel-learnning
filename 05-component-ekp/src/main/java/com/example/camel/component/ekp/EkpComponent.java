package com.example.camel.component.ekp;

import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;

import java.util.Map;

public class EkpComponent extends DefaultComponent {

    private String securityKey;

    public EkpComponent(String securityKey){
        this.securityKey=securityKey;
    }

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        EkpEndpoint endpoint=new EkpEndpoint(this,uri,remaining,parameters);
        String loginName=getAndRemoveOrResolveReferenceParameter(parameters,"loginName", String.class);
        endpoint.setLoginName(loginName);
        return endpoint;
    }

    public String getSecurityKey() {
        return securityKey;
    }
}
