package com.example.camel.helloworld;

import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.model.RoutesDefinition;
import org.apache.camel.spi.XMLRoutesDefinitionLoader;
import org.apache.camel.xml.jaxb.JaxbXMLRoutesDefinitionLoader;

import java.io.InputStream;

public class HelloWorld_Xml {
    public static void main(String[] args) throws Exception {
        DefaultCamelContext camelContext=new DefaultCamelContext();
        camelContext.start();
        //获取xml内容
        InputStream xmlStream=HelloWorld_Xml.class.getResourceAsStream("/route.xml");
        byte[] data=new byte[xmlStream.available()];
        xmlStream.read(data);
        xmlStream.close();
        String xml=new String(data);
        //通过xml字符串添加route
        XMLRoutesDefinitionLoader xmlRoutesDefinitionLoader=new JaxbXMLRoutesDefinitionLoader();
        RoutesDefinition routesDefinition=xmlRoutesDefinitionLoader.createModelFromXml(camelContext,xml, RoutesDefinition.class);
        camelContext.addRouteDefinitions(routesDefinition.getRoutes());
    }
}
