package com.example.camel.jdbc;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSON;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        DefaultCamelContext defaultCamelContext=new DefaultCamelContext();
        defaultCamelContext.start();
        DruidDataSource dataSource=new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://127.0.0.1/camel_test?useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2b8");
        dataSource.setUsername("root");
        dataSource.setPassword("lxz123");
        dataSource.setMinIdle(1);
        dataSource.setMaxActive(3);// 最大连接数
        defaultCamelContext.getRegistry().bind("DataSource",dataSource);
        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                from("jetty:http://0.0.0.0:8888/getJDBCData")
                        .to("jdbc:DataSource?outputType=SelectList")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                List<Map> result=exchange.getIn().getBody(List.class);
                                System.out.println("=======================");
                                System.out.println(JSON.toJSONString(result));
                                System.out.println("=======================");
                                exchange.getMessage().setBody(JSON.toJSONString(result));
                            }
                        });
                from("jetty:http://0.0.0.0:8888/saveDataForJDBC")
                        .to("jdbc:DataSource");
            }
        });
    }
}
