package com.example.camel.getdatafromothersystem;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * 模拟系统1
 * 返回请求的数据+11
 */
public class System1 {
    public static void main(String[] args) throws Exception {
        DefaultCamelContext defaultCamelContext=new DefaultCamelContext();
        defaultCamelContext.start();
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://0.0.0.0:8001/system1")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                String num=exchange.getIn().getBody(String.class);
                                System.out.println("system1 request:"+num);
                                exchange.getMessage().setBody(String.valueOf(Integer.parseInt(num)+11));
                            }
                        });
            }
        });
    }
}
