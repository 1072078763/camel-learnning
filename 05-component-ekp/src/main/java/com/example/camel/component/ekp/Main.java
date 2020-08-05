package com.example.camel.component.ekp;

import org.apache.camel.Component;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class Main {
    public static void main(String[] args) throws Exception {

        DefaultCamelContext defaultCamelContext=new DefaultCamelContext();
        //设置代理，用于fiddler抓包
//        defaultCamelContext.getGlobalOptions().put("http.proxyHost","127.0.0.1");
//        defaultCamelContext.getGlobalOptions().put("http.proxyPort","8888");
        defaultCamelContext.start();
        Component ekpComponent=new EkpComponent("THJwwiVdcWKiDGW//eIUQJlth58=");
        defaultCamelContext.addComponent("ekp",ekpComponent);
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://0.0.0.0:8000/ekpTest")
                        .to("ekp:http://exp.xxx.com.cn:9503/ekp/sys/news/sys_news_main/sysNewsMainIndex.do?method=listChildren&orderby=fdIsTop%3BfdTopTime%3BdocAlterTime&ordertype=down&__seq=1595555336078&s_ajax=true&loginName=admin");
            }
        });
    }
}
