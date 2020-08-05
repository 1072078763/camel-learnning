package com.example.camel.helloworld;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class HelloWorld_Java {
    public static void main(String[] args) throws Exception {
        DefaultCamelContext camelContext=new DefaultCamelContext();
        camelContext.start();
        camelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("quartz://report?cron=* * * * * ?")
                        .setBody(constant("Hello World"))
                        .to("log:mainInfo");
            }
        });
    }
}
