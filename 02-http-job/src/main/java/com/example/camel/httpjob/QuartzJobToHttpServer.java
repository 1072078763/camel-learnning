package com.example.camel.httpjob;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

/**
 * 通过camel定时发送一个http请求获取数据
 * 定时请求的服务端，计算发过来的表达式的结果并返回
 */
public class QuartzJobToHttpServer {
    public static void main(String[] args) throws Exception {
        DefaultCamelContext defaultCamelContext=new DefaultCamelContext();
        defaultCamelContext.start();
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                //访问路径：
                // http://localhost:8888/testServer
                // http://127.0.0.1:8888/testServer
                // http://本机IP:8888/testServer
                //设置监听IP为0.0.0.0才能进行非本机的访问
//                from("jetty:http://localhost:8888/testServer")
                from("jetty:http://0.0.0.0:8888/testServer")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                ExpressionParser parser = new SpelExpressionParser();
                                String body=exchange.getIn().getBody(String.class);
                                Expression exp = parser.parseExpression(body);
                                String result=exp.getValue().toString();
                                exchange.getMessage().setBody(result);
                            }
                        });
            }
        });
    }
}
