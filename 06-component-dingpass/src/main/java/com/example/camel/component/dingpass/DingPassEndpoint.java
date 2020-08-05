package com.example.camel.component.dingpass;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;

public class DingPassEndpoint extends DefaultEndpoint {

    private String securityKey;

    private String baseUri;

    public DingPassEndpoint(String baseUri){
        this.baseUri=baseUri;
    }

    @Override
    public Producer createProducer() throws Exception {
        throw new UnsupportedOperationException("DingPass组件不能作为producer");
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new DingPassConsumer(this,processor);
    }

    @Override
    protected String createEndpointUri() {
        return this.baseUri;
    }

    public String getSecurityKey() {
        return securityKey;
    }

    public void setSecurityKey(String securityKey) {
        this.securityKey = securityKey;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
