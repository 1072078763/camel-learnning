package com.example.camel.dingpasstoekp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.camel.component.dingpass.DingPassComponent;
import com.example.camel.component.ekp.EkpComponent;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DingPassToEkpTest {
    public static DefaultCamelContext defaultCamelContext;
    public static void main(String[] args) throws Exception {
        defaultCamelContext=new DefaultCamelContext();
        defaultCamelContext.start();
        defaultCamelContext.addComponent("dingpass",new DingPassComponent());
        defaultCamelContext.addComponent("ekp",new EkpComponent("THJwwiVdcWKiDGW//eIUQJlth58="));
        defaultCamelContext.getRegistry().bind("test", new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                System.out.println("这是公用的process");
                Map<String, Object> params=exchange.getIn().getBody(Map.class);
                String loginName= (String) params.get("loginName");
                exchange.getMessage().setHeader("loginName",loginName);
            }
        });

        defaultCamelContext.addRoutes(new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                //访问的时候，需要在请求头带上参数
                //x-ddpaas-signature: WBJVmj+Y+z6QnqwvK8tYaEXxnS1akiVPhQzQO2tUJ2Q=
                //x-ddpaas-signature-timestamp: 1595925864847
                from("dingpass:http://0.0.0.0:8888/dsource")
                        .process("test")
                        .to("direct:toekpAndGetArticle");

                from("direct:toekpAndGetArticle")
                        .to("ekp:http://exp.xxx.com.cn:9503/ekp/sys/common/datajson.jsp?scope=no&rowsize=5&s_bean=sysNewsMainPortletService")
                        .process(new Processor() {
                            @Override
                            public void process(Exchange exchange) throws Exception {
                                String data=exchange.getIn().getBody(String.class);
                                JSONArray jsonArray= JSON.parseArray(data);
                                List<Map<String, String>> resultList=new ArrayList<>(jsonArray.size());
                                for(int i=0;i<jsonArray.size();i++){
                                    JSONObject json=jsonArray.getJSONObject(i);
                                    Map<String, String> result=new HashMap<>();
                                    result.put("image","https://s0.2mdn.net/simgad/2448605896373246918");
                                    result.put("title",json.getString("text"));
                                    result.put("dateTime",json.getString("publishTime"));
                                    result.put("source",json.getString("creator"));
                                    result.put("link","http://exp.xxx.com.cn:9503/ekp"+json.getString("href"));
                                    result.put("interaction","点击率："+json.getString("docReadCount"));
                                    resultList.add(result);
                                }
                                exchange.getMessage().setBody(JSON.toJSONString(resultList));
                            }
                        })
                        .end();

            }
        });

    }
}
