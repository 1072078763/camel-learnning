package com.example.camel.httpjob;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * 通过camel定时发送一个http请求获取数据
 * 定时请求的客户端
 */
public class QuartzJobToHttp {
    public static void main(String[] args) throws Exception {
        DefaultCamelContext defaultCamelContext=new DefaultCamelContext();
        defaultCamelContext.start();
        //在上下文中注册处理器
        defaultCamelContext.getRegistry().bind("process", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println("这是请求后返回的结果："+exchange.getIn().getBody(String.class));
                exchange.getMessage().setBody("test result");
            }
        });
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                //1.每秒钟向服务器发送一条请求(设置了body内容)
                //2.将请求的结果放入自定义process中处理
                //3.将处理后的结果存入磁盘文件
                from("quartz://report?cron=* * * * * ?")
                        //路由id
                        .routeId("myRoute1")
                        //发送的HTTP请求的body数据
                        .setBody(constant("1+1-3"))
                        //发送到的URL地址
                        .to("http:127.0.0.1:8888/testServer")
                        .process("process")//处理器
                        //处理完之后保存到文件
                        .to("file:d:/test/?fileName=result.txt")
                        //转发
                        .to("direct:testd");
                //将转发的数据保存到文件
                from("direct:testd").to("file:d:/test/1.txt");
            }
        });
        //10秒钟之后停止
        Thread.sleep(10*1000);
        defaultCamelContext.stop();
    }
}
