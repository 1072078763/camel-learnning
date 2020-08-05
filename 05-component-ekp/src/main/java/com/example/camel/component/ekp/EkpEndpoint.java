package com.example.camel.component.ekp;

import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.support.DefaultEndpoint;

import java.util.Map;

public class EkpEndpoint extends DefaultEndpoint {
    private final String remaining;

    private String loginName;
    private EkpComponent ekpComponent;

    private Map<String, Object> httpParameters;

    public EkpEndpoint(EkpComponent component, String uri, String remaining, Map<String, Object> parameters){
        super(uri,component);
        this.ekpComponent=component;
        this.remaining=remaining;
        this.setEndpointUri(uri);
        this.httpParameters=parameters;
    }

    @Override
    public Producer createProducer() throws Exception {
        return new EkpProducer(this);
    }

    @Override
    public boolean isLenientProperties() {
        return true;
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        throw new UnsupportedOperationException("EKP组件不能作为consumer");
    }

    public String getEndpointBaseUri() {
        String baseUri=this.remaining;
        boolean isAnd=false;
        for(Map.Entry<String, Object> entry:httpParameters.entrySet()){
            String key=entry.getKey();
            String value= String.valueOf(entry.getValue());
            String andChar="&";
            if(!isAnd){
                andChar="?";
                isAnd=true;
            }
            baseUri+=andChar+key+"="+value;
        }
        return baseUri;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    /**
     * 没有的话，自动还从全局配置获取一遍
     * @return
     */
    public String getSecurityKey() {
        return ekpComponent.getSecurityKey();
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
