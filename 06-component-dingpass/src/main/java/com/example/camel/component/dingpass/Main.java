package com.example.camel.component.dingpass;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        DefaultCamelContext defaultCamelContext=new DefaultCamelContext();
        defaultCamelContext.start();

        defaultCamelContext.addComponent("dingpass",new DingPassComponent());
        List<String> cs=defaultCamelContext.getComponentNames();
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                //访问的时候，需要在请求头带上参数
                //x-ddpaas-signature: WBJVmj+Y+z6QnqwvK8tYaEXxnS1akiVPhQzQO2tUJ2Q=
                //x-ddpaas-signature-timestamp: 1595925864847
                from("dingpass:http://0.0.0.0:88/dingPass")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                exchange.getMessage().setBody("result");
                            }
                        });
            }
        });
    }
}
