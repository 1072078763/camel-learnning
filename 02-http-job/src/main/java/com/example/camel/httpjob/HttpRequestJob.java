package com.example.camel.httpjob;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class HttpRequestJob {
    public static void main(String[] args) throws Exception {
        CamelContext context = new DefaultCamelContext();
        context.start();

        context.addRoutes(new RouteBuilder() {
            public void configure() {
                //每秒钟执行一遍调度，往"https://www.baidu.com"发送请求，并将请求结果存入到"d:/test/atest.txt"
                from("quartz://report?cron=* * * * * ?")
                        .to("https://www.baidu.com")
                        .to("file:d:/test/?fileName=atest.txt");

                from("jetty:http://0.0.0.0:88/convert")
                        //这个接口将返回数据：{"datas":[{"id":1},{"id":2},{"id":3}],"name":"test"}
                        //由于本例是直接通过to("http://xxx")的方式获取http接口数据，所以需要开启bridgeEndpoint参数，实际使用可以通过ProducerTemplate的方式来调用http接口
                        .to("http://0.0.0.0:99/sourceData?bridgeEndpoint=true")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                //获取原始接口返回的数据
                                String resultData=exchange.getIn().getBody(String.class);
                                JSONObject jsonObject= JSON.parseObject(resultData);
                                JSONArray datas=jsonObject.getJSONArray("datas");
                                jsonObject.put("total",datas.size());
                                exchange.getMessage().setBody(jsonObject.toJSONString());
                            }
                        });

                //这是模拟的原始数据服务，不是转换过程的代码，这段代码是提供原始数据服务的
                from("jetty:http://0.0.0.0:99/sourceData")
                        .setBody(constant("{\"datas\":[{\"id\":1},{\"id\":2},{\"id\":3}],\"name\":\"test\"}"));
            }
        });
    }
}
