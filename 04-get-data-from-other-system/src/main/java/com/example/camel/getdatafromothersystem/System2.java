package com.example.camel.getdatafromothersystem;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * 模拟系统2
 * 返回请求的数据+22
 */
public class System2 {
    public static void main(String[] args) throws Exception {
        DefaultCamelContext defaultCamelContext=new DefaultCamelContext();
        defaultCamelContext.start();
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://localhost:8002/system2")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                String num=exchange.getIn().getBody(String.class);
                                System.out.println("system2 request:"+num);
                                exchange.getMessage().setBody(String.valueOf(Integer.parseInt(num)+22));
                            }
                        });
            }
        });
    }
}
