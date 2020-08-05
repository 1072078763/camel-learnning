package com.example.camel.getdatafromothersystem;

import com.alibaba.fastjson.JSONObject;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * 从系统1获取todoNum
 * 从系统2获取mailNum
 * 组装之后返回
 */
public class Main {
    public static void main(String[] args) throws Exception {
        final DefaultCamelContext defaultCamelContext=new DefaultCamelContext();
        defaultCamelContext.start();
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://0.0.0.0:8000/main")
                        //向系统1请求数据的前置
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                JSONObject result=new JSONObject();
                                ProducerTemplate producerTemplate=defaultCamelContext.createProducerTemplate();
                                //请求前用process处理请求需要带的参数
                                Exchange exchange1=producerTemplate.request("http://localhost:8001/system1", new Processor() {
                                    @Override
                                    public void process(Exchange exchange) throws Exception {
                                        exchange.getIn().setBody("1111");
                                    }
                                });
                                result.put("todoNum",exchange1.getMessage().getBody(String.class));
                                //直接设置body参数
                                String mailNum=producerTemplate.requestBody("http://localhost:8002/system2","123", String.class);
                                result.put("mailNum",mailNum);
                                exchange.getMessage().setBody(result.toJSONString());
                            }
                        });
            }
        });
    }
}
