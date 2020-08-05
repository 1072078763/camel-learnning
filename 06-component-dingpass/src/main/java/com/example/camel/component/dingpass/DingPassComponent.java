package com.example.camel.component.dingpass;

import org.apache.camel.Endpoint;
import org.apache.camel.support.DefaultComponent;

import java.util.Map;

public class DingPassComponent extends DefaultComponent {

    @Override
    protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
        DingPassEndpoint endpoint=new DingPassEndpoint(remaining);
        return endpoint;
    }
}
